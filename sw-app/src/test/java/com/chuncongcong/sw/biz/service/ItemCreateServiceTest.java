package com.chuncongcong.sw.biz.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.chuncongcong.framework.config.GlobalExceptionHandler;
import com.chuncongcong.framework.config.JacksonAutoConfiguration;
import com.chuncongcong.framework.config.MybatisPlusConfig;
import com.chuncongcong.framework.exception.ServiceException;
import com.chuncongcong.framework.interceptor.JwtInterceptor;
import com.chuncongcong.framework.util.ContextHolder;
import com.chuncongcong.sw.bean.param.ItemCreateParam;
import com.chuncongcong.sw.bean.vo.ItemCreateVO;
import com.chuncongcong.sw.controller.ItemCreateController;
import com.chuncongcong.sw.mapper.ItemMapper;
import com.chuncongcong.sw.service.impl.ItemInfoServiceImpl;
import com.chuncongcong.sw.service.impl.ItemServiceImpl;
import com.chuncongcong.sw.service.impl.RegionServiceImpl;
import com.chuncongcong.sw.service.impl.SubItemServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/** Uses real MyBatis services and an isolated database; never connects to application databases. */
@SpringJUnitConfig(ItemCreateServiceTest.TestConfig.class)
class ItemCreateServiceTest {
    @Autowired private ItemCreateService service;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private LocalValidatorFactoryBean validator;
    private MockMvc mvc;
    private ObjectMapper json;

    @BeforeEach
    void setUp() {
        for (String table : List.of("sw_sub_item", "sw_item_info", "sw_item", "sw_region")) {
            jdbc.execute("DELETE FROM " + table);
        }
        jdbc.update("INSERT INTO sw_region (id, name, code, deleted) VALUES (1, '密阿雷市', 'LumioseCity', 0)");
        ContextHolder.setUserId(99L);
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        new JacksonAutoConfiguration().jacksonCustomizer().customize(builder);
        json = builder.build();
        mvc = MockMvcBuilders.standaloneSetup(new ItemCreateController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(json))
                .setValidator(validator)
                .addInterceptors(new JwtInterceptor(userId -> true)).build();
    }

    @AfterEach
    void cleanContext() {
        ContextHolder.clear();
    }

    @Test
    void createsAllTablesWithGeneratedLinksAndAuditFields() {
        ItemCreateParam param = validParam();
        ItemCreateParam.Info reprint = new ItemCreateParam.Info();
        reprint.setDate(LocalDate.of(2027, 1, 1));
        reprint.setType(4);
        reprint.setPrice(" 5,500 日元 ");
        param.setInfoList(List.of(param.getInfoList().get(0), reprint));
        ItemCreateVO result = service.create(param);

        assertNotNull(result.getItemId());
        assertEquals(2, result.getInfoIds().size());
        assertEquals(1, result.getSubItemIds().size());
        assertEquals("测试物品", jdbc.queryForObject("SELECT name FROM sw_item", String.class));
        assertEquals(99L, jdbc.queryForObject("SELECT create_user FROM sw_item", Long.class));
        assertNotNull(jdbc.queryForObject("SELECT create_time FROM sw_item", java.sql.Timestamp.class));
        assertEquals(2, jdbc.queryForObject("SELECT COUNT(*) FROM sw_item_info WHERE item_id = ?", Integer.class, result.getItemId()));
        assertEquals(result.getItemId(), jdbc.queryForObject("SELECT item_id FROM sw_sub_item", Long.class));
        assertEquals(1L, jdbc.queryForObject("SELECT region_id FROM sw_sub_item", Long.class));
        assertEquals("皮卡丘", jdbc.queryForObject("SELECT name FROM sw_sub_item", String.class));
        assertEquals("5,500 日元", jdbc.queryForObject("SELECT price FROM sw_item_info WHERE type = 4", String.class));
    }

    @Test
    void rollsBackParentAndEarlierChildrenWhenLaterChildInsertFails() {
        ItemCreateParam param = validParam();
        ItemCreateParam.SubItem invalid = new ItemCreateParam.SubItem();
        invalid.setName("失败的子物品");
        invalid.setCode("FAIL");
        invalid.setSort(-1); // Test database CHECK constraint fails after earlier inserts.
        param.setSubItemList(List.of(param.getSubItemList().get(0), invalid));
        assertThrows(RuntimeException.class, () -> service.create(param));
        assertTablesEmpty();
    }

    @Test
    void rollsBackParentWhenInfoInsertFails() {
        ItemCreateParam param = validParam();
        param.getInfoList().get(0).setType(9);
        assertThrows(RuntimeException.class, () -> service.create(param));
        assertTablesEmpty();
    }

    @Test
    void rejectsMissingAndDeletedRegions() {
        jdbc.update("UPDATE sw_region SET deleted = 1 WHERE id = 1");
        ServiceException error = assertThrows(ServiceException.class, () -> service.create(validParam()));
        assertEquals(400, error.getCode());
        assertTablesEmpty();
    }

    @Test
    void rejectsExistingCodeInSameRegion() {
        service.create(validParam());
        assertThrows(ServiceException.class, () -> service.create(validParam()));
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM sw_item", Integer.class));
    }

    @Test
    void rejectsDuplicateChildCodesBeforeAnyInsert() {
        ItemCreateParam param = validParam();
        ItemCreateParam.SubItem duplicate = new ItemCreateParam.SubItem();
        duplicate.setName("重复编码");
        duplicate.setCode(" sub-001 ");
        param.setSubItemList(List.of(param.getSubItemList().get(0), duplicate));
        assertThrows(ServiceException.class, () -> service.create(param));
        assertTablesEmpty();
    }

    @Test
    void rejectsEmptyOrNullNestedEntriesAndUnknownTypes() {
        ItemCreateParam param = validParam();
        assertTrue(validator.validate(param).isEmpty());
        param.setInfoList(List.of());
        assertFalse(validator.validate(param).isEmpty());
        param = validParam();
        param.setSubItemList(java.util.Arrays.asList((ItemCreateParam.SubItem) null));
        assertFalse(validator.validate(param).isEmpty());
        param = validParam();
        param.getInfoList().get(0).setType(5);
        assertFalse(validator.validate(param).isEmpty());
        param = validParam();
        param.getSubItemList().get(0).setName(" ");
        assertFalse(validator.validate(param).isEmpty());
    }

    @Test
    void controllerRejectsMissingVerificationCodeWithoutWriting() throws Exception {
        ItemCreateParam param = validParam();
        param.setVerificationCode(null);
        mvc.perform(post("/item/create").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsBytes(param)))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.success").value(false));
        assertTablesEmpty();
    }

    @Test
    void rejectsWrongVerificationCodeEvenWithoutLogin() throws Exception {
        ItemCreateParam param = validParam();
        param.setVerificationCode("wrong");
        mvc.perform(post("/item/create").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsBytes(param)))
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.success").value(false));
        assertThrows(ServiceException.class, () -> service.create(param));
        assertTablesEmpty();
    }

    @Test
    void generatesCodesFromRegionAndSortWhenCodesAreBlank() {
        ItemCreateParam param = validParam();
        param.setCode(" ");
        param.getSubItemList().get(0).setCode(null);
        assertEquals(1, param.getSort());
        assertEquals(1, param.getSubItemList().get(0).getSort());
        service.create(param);
        assertEquals("LumioseCity_01", jdbc.queryForObject("SELECT code FROM sw_item", String.class));
        assertEquals("LumioseCity_01_01", jdbc.queryForObject("SELECT code FROM sw_sub_item", String.class));
    }

    @Test
    void blankImagesUseRegionAndFinalCodes() {
        ItemCreateParam param = validParam();
        param.setCode(null);
        param.setSort(5);
        param.getSubItemList().get(0).setCode(null);
        service.create(param);
        assertEquals("/file/pic/region/LumioseCity/LumioseCity_05.jpg", jdbc.queryForObject("SELECT img FROM sw_item", String.class));
        assertEquals("/file/pic/region/LumioseCity/sub/LumioseCity_05_01.jpg", jdbc.queryForObject("SELECT img FROM sw_sub_item", String.class));
    }

    @Test
    void explicitImageAddressesArePreserved() {
        ItemCreateParam param = validParam();
        param.setImg(" https://example.com/main.png ");
        param.getSubItemList().get(0).setImg("/file/existing-child.jpg");
        service.create(param);
        assertEquals("https://example.com/main.png", jdbc.queryForObject("SELECT img FROM sw_item", String.class));
        assertEquals("/file/existing-child.jpg", jdbc.queryForObject("SELECT img FROM sw_sub_item", String.class));
    }

    @Test
    void childAutoCodeUsesCustomParentCodeAndActualSort() {
        ItemCreateParam param = validParam();
        param.getSubItemList().get(0).setCode(" ");
        param.getSubItemList().get(0).setSort(12);
        service.create(param);
        assertEquals("SW-001_12", jdbc.queryForObject("SELECT code FROM sw_sub_item", String.class));
    }

    @Test
    void autoCodeDoesNotTruncateSortAboveTwoDigits() {
        ItemCreateParam param = validParam();
        param.setCode(null);
        param.setSort(100);
        service.create(param);
        assertEquals("LumioseCity_100", jdbc.queryForObject("SELECT code FROM sw_item", String.class));
    }

    @Test
    void rejectsCollisionBetweenGeneratedAndManualChildCode() {
        ItemCreateParam param = validParam();
        param.getSubItemList().get(0).setCode(null);
        ItemCreateParam.SubItem second = new ItemCreateParam.SubItem();
        second.setName("碰撞测试");
        second.setCode("sw-001_01");
        second.setSort(2);
        param.setSubItemList(List.of(param.getSubItemList().get(0), second));
        assertThrows(ServiceException.class, () -> service.create(param));
        assertTablesEmpty();
    }

    @Test
    void rejectsOverlongGeneratedCodeBeforeWriting() {
        jdbc.update("UPDATE sw_region SET code = ? WHERE id = 1", "R".repeat(64));
        ItemCreateParam param = validParam();
        param.setCode(null);
        assertThrows(ServiceException.class, () -> service.create(param));
        assertTablesEmpty();
    }

    @Test
    void acceptsEmptySubItems() {
        ItemCreateParam param = validParam();
        param.setSubItemList(List.of());
        assertTrue(validator.validate(param).isEmpty());
        ItemCreateVO result = service.create(param);
        assertTrue(result.getSubItemIds().isEmpty());
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM sw_sub_item", Integer.class));
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM sw_item_info", Integer.class));
    }

    @Test
    void acceptsNullSubItems() {
        ItemCreateParam param = validParam();
        param.setSubItemList(null);
        assertTrue(validator.validate(param).isEmpty());
        assertTrue(service.create(param).getSubItemIds().isEmpty());
    }

    @Test
    void controllerAcceptsOmittedSubItemsAndCodesWithoutLogin() throws Exception {
        ContextHolder.clear();
        com.fasterxml.jackson.databind.node.ObjectNode body = json.valueToTree(validParam());
        body.remove(List.of("subItemList", "code", "sort"));
        mvc.perform(post("/item/create").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsBytes(body)))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.subItemIds.length()").value(0));
        assertEquals("LumioseCity_01", jdbc.queryForObject("SELECT code FROM sw_item", String.class));
        assertNull(jdbc.queryForObject("SELECT create_user FROM sw_item", Long.class));
    }

    @Test
    void rejectsSortBelowOne() {
        ItemCreateParam param = validParam();
        param.setSort(0);
        assertFalse(validator.validate(param).isEmpty());
        param = validParam();
        param.getSubItemList().get(0).setSort(0);
        assertFalse(validator.validate(param).isEmpty());
    }

    @Test
    void controllerValidatesNestedRowsBeforeWriting() throws Exception {
        ItemCreateParam param = validParam();
        param.getSubItemList().get(0).setName(" ");
        mvc.perform(post("/item/create")
                        .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(param)))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.success").value(false));
        assertTablesEmpty();
    }

    @Test
    void controllerCreatesCompleteRecord() throws Exception {
        ContextHolder.clear();
        mvc.perform(post("/item/create")
                        .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(validParam())))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.itemId").exists())
                .andExpect(jsonPath("$.data.subItemIds.length()").value(1));
    }

    private ItemCreateParam validParam() {
        ItemCreateParam param = new ItemCreateParam();
        param.setVerificationCode("a1314257");
        param.setName(" 测试物品 ");
        param.setRegionId(1L);
        param.setCode(" SW-001 ");
        ItemCreateParam.Info info = new ItemCreateParam.Info();
        info.setDate(LocalDate.of(2026, 11, 6));
        info.setType(1);
        info.setPrice("100 日元");
        param.setInfoList(List.of(info));
        ItemCreateParam.SubItem sub = new ItemCreateParam.SubItem();
        sub.setName(" 皮卡丘 ");
        sub.setCode("SUB-001");
        param.setSubItemList(List.of(sub));
        return param;
    }

    private void assertTablesEmpty() {
        for (String table : List.of("sw_item", "sw_item_info", "sw_sub_item")) {
            assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class), table);
        }
    }

    @Configuration
    @EnableTransactionManagement
    @MapperScan(basePackageClasses = ItemMapper.class)
    @Import({ItemCreateService.class, ItemServiceImpl.class, ItemInfoServiceImpl.class,
            SubItemServiceImpl.class, RegionServiceImpl.class})
    static class TestConfig {
        @Bean
        DataSource dataSource() {
            JdbcDataSource dataSource = new JdbcDataSource();
            dataSource.setURL("jdbc:h2:mem:item-create;MODE=MySQL;DB_CLOSE_DELAY=-1");
            JdbcTemplate jdbc = new JdbcTemplate(dataSource);
            String audit = ", deleted INT DEFAULT 0, create_user BIGINT, create_time TIMESTAMP, update_user BIGINT, update_time TIMESTAMP";
            jdbc.execute("CREATE TABLE sw_region (id BIGINT PRIMARY KEY, name VARCHAR(100), code VARCHAR(64), generation VARCHAR(32), img VARCHAR(255), sort INT" + audit + ")");
            jdbc.execute("CREATE TABLE sw_item (id BIGINT AUTO_INCREMENT PRIMARY KEY, region_id BIGINT, name VARCHAR(100), code VARCHAR(64), img VARCHAR(255), sort INT" + audit + ")");
            jdbc.execute("CREATE TABLE sw_item_info (id BIGINT AUTO_INCREMENT PRIMARY KEY, item_id BIGINT REFERENCES sw_item(id), date DATE, type INT CHECK(type BETWEEN 1 AND 4), price VARCHAR(64)" + audit + ")");
            jdbc.execute("CREATE TABLE sw_sub_item (id BIGINT AUTO_INCREMENT PRIMARY KEY, region_id BIGINT, item_id BIGINT REFERENCES sw_item(id), name VARCHAR(100), code VARCHAR(64), img VARCHAR(255), sort INT CHECK(sort >= 1)" + audit + ")");
            return dataSource;
        }

        @Bean
        SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
            MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
            factory.setDataSource(dataSource);
            factory.setConfiguration(new MybatisConfiguration());
            factory.setGlobalConfig(new GlobalConfig().setMetaObjectHandler(new MybatisPlusConfig().metaObjectHandler()));
            return factory.getObject();
        }

        @Bean
        DataSourceTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        LocalValidatorFactoryBean validator() {
            return new LocalValidatorFactoryBean();
        }
    }
}
