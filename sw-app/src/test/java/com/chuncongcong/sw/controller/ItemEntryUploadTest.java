package com.chuncongcong.sw.controller;

import com.chuncongcong.framework.config.GlobalExceptionHandler;
import com.chuncongcong.framework.interceptor.JwtInterceptor;
import com.chuncongcong.framework.util.ContextHolder;
import com.chuncongcong.sw.entity.RegionDO;
import com.chuncongcong.sw.service.RegionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class ItemEntryUploadTest {
    @TempDir Path directory;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        ContextHolder.clear();
        UploadController controller = new UploadController();
        ReflectionTestUtils.setField(controller, "filePath", directory.toString() + "/");
        ReflectionTestUtils.setField(controller, "filePatternPrefix", "/file/");
        RegionService regions = mock(RegionService.class);
        RegionDO region = new RegionDO();
        region.setId(1L);
        region.setCode("LumioseCity");
        when(regions.getById(1L)).thenReturn(region);
        ReflectionTestUtils.setField(controller, "regionService", regions);
        ((java.util.Map<?, ?>) ReflectionTestUtils.getField(UploadController.class, "uploadCountMap")).clear();
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addInterceptors(new JwtInterceptor(userId -> true)).build();
    }

    @Test
    void missingOrWrongVerificationCannotWriteFiles() throws Exception {
        mvc.perform(itemUpload())
                .andExpect(jsonPath("$.code").value(403));
        mvc.perform(itemUpload().param("verificationCode", "wrong"))
                .andExpect(jsonPath("$.code").value(403));
        try (var files = Files.list(directory)) {
            assertEquals(0, files.count());
        }
    }

    @Test
    void mainUploadUsesRegionCodeAndSortAndPreservesPngBytes() throws Exception {
        mvc.perform(itemUpload()
                        .param("verificationCode", "a1314257").param("category", "headImg"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.url").value("/file/pic/region/LumioseCity/LumioseCity_05.png"))
                .andExpect(jsonPath("$.data.fileName").value("LumioseCity_05.png"))
                .andExpect(jsonPath("$.data.category").value("pic/region/LumioseCity"))
                .andExpect(jsonPath("$.data.contentType").value("image/png"));
        try (var files = Files.walk(directory)) {
            assertEquals(1, files.filter(Files::isRegularFile).count());
        }
        Path image = directory.resolve("pic/region/LumioseCity/LumioseCity_05.png");
        byte[] bytes = Files.readAllBytes(image);
        assertArrayEquals(image().getBytes(), bytes);
        assertTrue(ImageIO.read(image.toFile()).getColorModel().hasAlpha());
    }

    @ParameterizedTest
    @ValueSource(strings = {"jpg", "jpeg"})
    void preservesJpegBytesAndOriginalExtension(String extension) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "jpg", bytes);
        MockMultipartFile jpeg = new MockMultipartFile("file", "source." + extension, "image/jpeg", bytes.toByteArray());
        mvc.perform(multipart("/upload/item/pic").file(jpeg).param("regionId", "1").param("sort", "5")
                        .param("verificationCode", "a1314257"))
                .andExpect(jsonPath("$.data.url").value("/file/pic/region/LumioseCity/LumioseCity_05." + extension))
                .andExpect(jsonPath("$.data.contentType").value("image/jpeg"));
        assertArrayEquals(jpeg.getBytes(), Files.readAllBytes(directory.resolve("pic/region/LumioseCity/LumioseCity_05." + extension)));
    }

    @Test
    void subUploadUsesParentAndSubSort() throws Exception {
        mvc.perform(itemUpload().param("verificationCode", "a1314257").param("subSort", "1"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.url").value("/file/pic/region/LumioseCity/sub/LumioseCity_05_01.png"))
                .andExpect(jsonPath("$.data.category").value("pic/region/LumioseCity/sub"));
        assertArrayEquals(image().getBytes(), Files.readAllBytes(directory.resolve("pic/region/LumioseCity/sub/LumioseCity_05_01.png")));
        assertFalse(Files.exists(directory.resolve("pic/region/LumioseCity/LumioseCity_05_01.png")));
    }

    @Test
    void uploadRespectsCustomCodes() throws Exception {
        mvc.perform(itemUpload().param("verificationCode", "a1314257")
                        .param("code", "CUSTOM").param("subSort", "2").param("subCode", "CUSTOM_CHILD"))
                .andExpect(jsonPath("$.data.url").value("/file/pic/region/LumioseCity/sub/CUSTOM_CHILD.png"));
        assertTrue(Files.exists(directory.resolve("pic/region/LumioseCity/sub/CUSTOM_CHILD.png")));
    }

    @Test
    void mainAndChildWithSameCodeUseSeparateDirectories() throws Exception {
        mvc.perform(itemUpload().param("verificationCode", "a1314257").param("code", "SAME_CODE"))
                .andExpect(jsonPath("$.data.url").value("/file/pic/region/LumioseCity/SAME_CODE.png"));
        mvc.perform(itemUpload().param("verificationCode", "a1314257")
                        .param("subSort", "1").param("subCode", "SAME_CODE"))
                .andExpect(jsonPath("$.data.url").value("/file/pic/region/LumioseCity/sub/SAME_CODE.png"));
        assertTrue(Files.exists(directory.resolve("pic/region/LumioseCity/SAME_CODE.png")));
        assertTrue(Files.exists(directory.resolve("pic/region/LumioseCity/sub/SAME_CODE.png")));
    }

    @Test
    void existingImageIsNotOverwritten() throws Exception {
        Path destination = directory.resolve("pic/region/LumioseCity/LumioseCity_05.png");
        Files.createDirectories(destination.getParent());
        byte[] original = new byte[]{1, 2, 3};
        Files.write(destination, original);
        mvc.perform(itemUpload().param("verificationCode", "a1314257"))
                .andExpect(jsonPath("$.code").value(409));
        assertArrayEquals(original, Files.readAllBytes(destination));
    }

    @Test
    void rejectsPathTraversalBeforeWritingFiles() throws Exception {
        mvc.perform(itemUpload().param("verificationCode", "a1314257").param("code", "../other"))
                .andExpect(jsonPath("$.code").value(400));
        try (var files = Files.list(directory)) {
            assertEquals(0, files.count());
        }
    }

    @Test
    void sharedUploadEndpointStillRequiresItsExistingLogin() throws Exception {
        mvc.perform(multipart("/upload/pic").file(image()).param("verificationCode", "a1314257"))
                .andExpect(jsonPath("$.success").value(false));
        try (var files = Files.list(directory)) {
            assertEquals(0, files.count());
        }
    }

    private MockHttpServletRequestBuilder itemUpload() throws Exception {
        return multipart("/upload/item/pic").file(image()).param("regionId", "1").param("sort", "5");
    }

    private MockMultipartFile image() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB), "png", bytes);
        return new MockMultipartFile("file", "item.png", "image/png", bytes.toByteArray());
    }
}
