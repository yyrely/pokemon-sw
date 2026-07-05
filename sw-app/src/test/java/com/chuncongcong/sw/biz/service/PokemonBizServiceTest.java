package com.chuncongcong.sw.biz.service;

import com.chuncongcong.framework.exception.ServiceException;
import com.chuncongcong.framework.util.ContextHolder;
import com.chuncongcong.sw.bean.param.PokemonParam;
import com.chuncongcong.sw.bean.param.UserPokemonCollectParam;
import com.chuncongcong.sw.bean.vo.PokemonStatsVO;
import com.chuncongcong.sw.bean.vo.PokemonVO;
import com.chuncongcong.sw.bean.vo.UserPokemonCollectVO;
import com.chuncongcong.sw.biz.service.impl.PokemonBizServiceImpl;
import com.chuncongcong.sw.entity.CollectibleTypeDO;
import com.chuncongcong.sw.entity.ItemDO;
import com.chuncongcong.sw.entity.PokemonDO;
import com.chuncongcong.sw.entity.SubItemDO;
import com.chuncongcong.sw.entity.UserPokemonCollectDO;
import com.chuncongcong.sw.service.CollectibleTypeService;
import com.chuncongcong.sw.service.ItemService;
import com.chuncongcong.sw.service.PokemonScaleMappingService;
import com.chuncongcong.sw.service.PokemonService;
import com.chuncongcong.sw.service.PokemonTypeService;
import com.chuncongcong.sw.service.SubItemService;
import com.chuncongcong.sw.service.UserItemCollectService;
import com.chuncongcong.sw.service.UserPokemonCollectService;
import com.chuncongcong.sw.service.UserSubItemCollectService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PokemonBizServiceTest {

    @Mock
    private PokemonService pokemonService;
    @Mock
    private PokemonTypeService pokemonTypeService;
    @Mock
    private UserPokemonCollectService userPokemonCollectService;
    @Mock
    private CollectibleTypeService collectibleTypeService;
    @Mock
    private PokemonScaleMappingService pokemonScaleMappingService;
    @Mock
    private ItemService itemService;
    @Mock
    private SubItemService subItemService;
    @Mock
    private UserItemCollectService userItemCollectService;
    @Mock
    private UserSubItemCollectService userSubItemCollectService;

    private PokemonBizService pokemonBizService;

    @BeforeEach
    void setUp() {
        pokemonBizService = new PokemonBizServiceImpl();
        ReflectionTestUtils.setField(pokemonBizService, "pokemonService", pokemonService);
        ReflectionTestUtils.setField(pokemonBizService, "pokemonTypeService", pokemonTypeService);
        ReflectionTestUtils.setField(pokemonBizService, "userPokemonCollectService", userPokemonCollectService);
        ReflectionTestUtils.setField(pokemonBizService, "collectibleTypeService", collectibleTypeService);
        ReflectionTestUtils.setField(pokemonBizService, "pokemonScaleMappingService", pokemonScaleMappingService);
        ReflectionTestUtils.setField(pokemonBizService, "itemService", itemService);
        ReflectionTestUtils.setField(pokemonBizService, "subItemService", subItemService);
        ReflectionTestUtils.setField(pokemonBizService, "userItemCollectService", userItemCollectService);
        ReflectionTestUtils.setField(pokemonBizService, "userSubItemCollectService", userSubItemCollectService);
        ContextHolder.setUserId(7L);
    }

    @AfterEach
    void tearDown() {
        ContextHolder.clear();
    }

    @Test
    void listFiltersUncollectedPokemonWhenCollectedOnlyIsFalse() {
        PokemonDO pikachu = new PokemonDO();
        pikachu.setId(25L);
        pikachu.setRegionId(1L);
        pikachu.setNameZh("Pikachu");

        PokemonDO raichu = new PokemonDO();
        raichu.setId(26L);
        raichu.setRegionId(1L);
        raichu.setNameZh("Raichu");

        when(pokemonService.listByRegionId(1L, null)).thenReturn(List.of(pikachu, raichu));
        when(pokemonTypeService.listAll()).thenReturn(List.of());

        UserPokemonCollectDO collect = new UserPokemonCollectDO();
        collect.setPokemonId(25L);
        when(userPokemonCollectService.listByUserIdAndPokemonIds(7L, List.of(25L, 26L)))
                .thenReturn(List.of(collect));

        PokemonParam param = new PokemonParam();
        param.setRegionId(1L);
        param.setCollectedOnly(false);

        List<PokemonVO> result = pokemonBizService.list(param);

        assertThat(result)
                .extracting(PokemonVO::getId)
                .containsExactly(26L);
    }

    @Test
    void listReturnsPokemonCollectPriceTotal() {
        PokemonDO pikachu = new PokemonDO();
        pikachu.setId(25L);
        pikachu.setRegionId(1L);
        pikachu.setNameZh("Pikachu");

        when(pokemonService.listByRegionId(1L, null)).thenReturn(List.of(pikachu));
        when(pokemonTypeService.listAll()).thenReturn(List.of());

        UserPokemonCollectDO firstCollect = new UserPokemonCollectDO();
        firstCollect.setPokemonId(25L);
        firstCollect.setCollectPrice(new BigDecimal("12.30"));
        UserPokemonCollectDO secondCollect = new UserPokemonCollectDO();
        secondCollect.setPokemonId(25L);
        secondCollect.setCollectPrice(new BigDecimal("7.70"));
        when(userPokemonCollectService.listByUserIdAndPokemonIds(7L, List.of(25L)))
                .thenReturn(List.of(firstCollect, secondCollect));

        PokemonParam param = new PokemonParam();
        param.setRegionId(1L);
        param.setCollectedOnly(true);

        List<PokemonVO> result = pokemonBizService.list(param);

        assertThat(result)
                .extracting("collectPriceTotal")
                .containsExactly(new BigDecimal("20.00"));
    }

    @Test
    void statsCountsScaleWorldSourceCollectPriceOnceWhenOneCollectMapsToMultiplePokemon() {
        PokemonDO charmander = new PokemonDO();
        charmander.setId(4L);
        charmander.setRegionId(1L);
        charmander.setNameZh("Charmander");

        PokemonDO squirtle = new PokemonDO();
        squirtle.setId(7L);
        squirtle.setRegionId(1L);
        squirtle.setNameZh("Squirtle");

        when(pokemonService.listByRegionId(1L, null)).thenReturn(List.of(charmander, squirtle));

        UserPokemonCollectDO firstMappedCollect = new UserPokemonCollectDO();
        firstMappedCollect.setPokemonId(4L);
        firstMappedCollect.setCollectibleTypeCode("scale_world");
        firstMappedCollect.setSourceType("scale_item");
        firstMappedCollect.setSourceCollectId(101L);
        firstMappedCollect.setCollectPrice(new BigDecimal("100.00"));

        UserPokemonCollectDO secondMappedCollect = new UserPokemonCollectDO();
        secondMappedCollect.setPokemonId(7L);
        secondMappedCollect.setCollectibleTypeCode("scale_world");
        secondMappedCollect.setSourceType("scale_item");
        secondMappedCollect.setSourceCollectId(101L);
        secondMappedCollect.setCollectPrice(new BigDecimal("100.00"));

        when(userPokemonCollectService.listByUserIdAndPokemonIds(7L, List.of(4L, 7L)))
                .thenReturn(List.of(firstMappedCollect, secondMappedCollect));

        PokemonParam param = new PokemonParam();
        param.setRegionId(1L);

        PokemonStatsVO result = pokemonBizService.stats(param);

        assertThat(result.getCollectPriceTotal()).isEqualByComparingTo(new BigDecimal("100.00"));
    }
    @Test
    void userCollectListResolvesScaleWorldSourceNames() {
        CollectibleTypeDO scaleWorldType = new CollectibleTypeDO();
        scaleWorldType.setCode("scale_world");
        scaleWorldType.setName("Scale World");
        when(collectibleTypeService.listEnabled()).thenReturn(List.of(scaleWorldType));

        UserPokemonCollectDO itemRecord = new UserPokemonCollectDO();
        itemRecord.setId(101L);
        itemRecord.setPokemonId(25L);
        itemRecord.setCollectibleTypeCode("scale_world");
        itemRecord.setSourceType("scale_item");
        itemRecord.setSourceId(55L);

        UserPokemonCollectDO subItemRecord = new UserPokemonCollectDO();
        subItemRecord.setId(102L);
        subItemRecord.setPokemonId(25L);
        subItemRecord.setCollectibleTypeCode("scale_world");
        subItemRecord.setSourceType("scale_sub_item");
        subItemRecord.setSourceId(66L);

        when(userPokemonCollectService.listByUserIdAndPokemonId(7L, 25L))
                .thenReturn(List.of(itemRecord, subItemRecord));

        ItemDO item = new ItemDO();
        item.setId(55L);
        item.setName("Scale item");
        when(itemService.listByIds(List.of(55L))).thenReturn(List.of(item));

        SubItemDO subItem = new SubItemDO();
        subItem.setId(66L);
        subItem.setName("Scale sub item");
        when(subItemService.listByIds(List.of(66L))).thenReturn(List.of(subItem));

        UserPokemonCollectParam param = new UserPokemonCollectParam();
        param.setPokemonId(25L);

        List<UserPokemonCollectVO> result = pokemonBizService.userCollectList(param);

        assertThat(result)
                .extracting("sourceName")
                .containsExactly("Scale item", "Scale sub item");
    }

    @Test
    void userExchangeUpdatesOwnedPokemonCollectRecord() {
        UserPokemonCollectDO record = new UserPokemonCollectDO();
        record.setId(101L);
        record.setUserId(7L);
        record.setExchange(false);
        when(userPokemonCollectService.getById(101L)).thenReturn(record);

        UserPokemonCollectParam param = new UserPokemonCollectParam();
        param.setId(101L);
        param.setExchange(true);
        param.setExchangeRemark("duplicate figure");

        pokemonBizService.userExchange(param);

        verify(userPokemonCollectService).updateById(argThat(updated ->
                updated instanceof UserPokemonCollectDO
                        && Boolean.TRUE.equals(((UserPokemonCollectDO) updated).getExchange())
                        && "duplicate figure".equals(((UserPokemonCollectDO) updated).getExchangeRemark())
        ));
    }

    @Test
    void userExchangeRejectsOtherUsersPokemonCollectRecord() {
        UserPokemonCollectDO record = new UserPokemonCollectDO();
        record.setId(101L);
        record.setUserId(8L);
        when(userPokemonCollectService.getById(101L)).thenReturn(record);

        UserPokemonCollectParam param = new UserPokemonCollectParam();
        param.setId(101L);
        param.setExchange(true);

        assertThatThrownBy(() -> pokemonBizService.userExchange(param))
                .isInstanceOf(ServiceException.class);
    }
}