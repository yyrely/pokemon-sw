package com.chuncongcong.sw.controller;

import com.chuncongcong.framework.exception.ServiceException;
import com.chuncongcong.framework.response.ApiResponse;
import com.chuncongcong.framework.util.ContextHolder;
import com.chuncongcong.sw.bean.param.UploadParam;
import com.chuncongcong.sw.bean.vo.UploadVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Tag(name = "上传")
@Slf4j
@Validated
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${file.path}")
    private String filePath;

    @Value("${file.pattern-prefix}")
    private String filePatternPrefix;
    
    // 允许的文件类型
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png"
    );
    
    // 允许的MIME类型
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg", "image/png"
    );
    
    // 文件头魔数检查
    private static final Map<String, byte[]> FILE_SIGNATURES = Map.of(
            "jpg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF},
            "jpeg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF},
            "png", new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A}
    );

    // 最大文件大小（字节）- 1MB
    private static final long MAX_FILE_SIZE = (long) 1024 * 1024;
    
    // 上传频率限制 - 每用户每分钟最多上传5次
    private static final int MAX_UPLOADS_PER_MINUTE = 5;
    private static final Map<Long, Map<Long, AtomicInteger>> uploadCountMap = new ConcurrentHashMap<>();

    @Operation(summary = "文件上传")
    @PostMapping()
    public ApiResponse<UploadVO> upload(@Valid @ModelAttribute UploadParam uploadParam) {
        try {
            // 检查上传频率限制
            checkUploadRateLimit();
            
            MultipartFile file = uploadParam.getFile();
            String category = uploadParam.getCategory();
            // 验证文件
            validateFile(file);
            // 确保上传目录存在
            String uploadDir = createUploadDirectory(category);
            // 生成唯一文件名
            String fileName = generateFileName(file.getOriginalFilename());
            // 保存文件
            Path path = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), path);
            // 生成访问URL
            String accessUrl = generateAccessUrl(category, fileName);

            // 封装返回结果
            UploadVO result = new UploadVO();
            result.setUrl(accessUrl);
            result.setFileName(fileName);
            result.setOriginalName(file.getOriginalFilename());
            result.setSize(file.getSize());
            result.setContentType(file.getContentType());
            result.setCategory(category);
            
            return ApiResponse.success(result);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new ServiceException("文件上传失败");
        }
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("请选择要上传的文件");
        }
        
        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ServiceException("文件大小不能超过1MB");
        }
        
        // 检查文件名
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new ServiceException("文件名不能为空");
        }
        
        // 检查文件名是否包含危险字符
        if (containsDangerousCharacters(originalFilename)) {
            throw new ServiceException("文件名包含非法字符");
        }
        
        // 检查文件扩展名
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new ServiceException("不支持的文件类型，仅支持: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
        
        // 检查MIME类型
        String contentType = file.getContentType();
        if (!ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new ServiceException("不支持的文件格式，MIME类型不匹配");
        }
        
        // 检查文件头（魔数验证）
        try {
            if (!validateFileSignature(file, extension)) {
                throw new ServiceException("文件内容与扩展名不匹配，可能是恶意文件");
            }
        } catch (IOException e) {
            log.error("文件头验证失败", e);
            throw new ServiceException("文件验证失败");
        }
    }

    /**
     * 创建上传目录
     */
    private String createUploadDirectory(String category) throws IOException {
        // 验证category参数，防止路径遍历攻击
        if (StringUtils.hasText(category)) {
            category = sanitizeCategory(category);
        }
        
        // 按日期分组存储
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        
        String uploadDir;
        if (StringUtils.hasText(category)) {
            uploadDir = filePath + category + "/" + dateDir;
        } else {
            uploadDir = filePath + dateDir;
        }
        
        // 验证最终路径是否在允许的目录范围内
        Path basePath = Paths.get(filePath).normalize().toAbsolutePath();
        Path finalPath = Paths.get(uploadDir).normalize().toAbsolutePath();
        
        if (!finalPath.startsWith(basePath)) {
            throw new ServiceException("非法的文件路径");
        }
        
        if (!Files.exists(finalPath)) {
            Files.createDirectories(finalPath);
        }
        
        return uploadDir;
    }
    
    /**
     * 清理分类参数，防止路径遍历
     */
    private String sanitizeCategory(String category) {
        if (!StringUtils.hasText(category)) {
            return "";
        }
        
        // 移除危险字符
        category = category.replaceAll("[.\\\\/:*?\"<>|]", "");
        
        // 限制长度
        if (category.length() > 50) {
            category = category.substring(0, 50);
        }
        
        // 只允许字母、数字、下划线、连字符
        if (!category.matches("^[a-zA-Z0-9_-]+$")) {
            throw new ServiceException("分类名称只能包含字母、数字、下划线和连字符");
        }
        
        return category;
    }
    
    /**
     * 检查上传频率限制
     */
    private void checkUploadRateLimit() {
        Long userId = ContextHolder.getContext().getUserId();
        long currentMinute = System.currentTimeMillis() / 60000; // 当前分钟时间戳
        
        uploadCountMap.putIfAbsent(userId, new ConcurrentHashMap<>());
        Map<Long, AtomicInteger> userUploads = uploadCountMap.get(userId);
        
        // 清理过期的计数器（超过1分钟的）
        userUploads.entrySet().removeIf(entry -> entry.getKey() < currentMinute - 1);
        
        // 获取当前分钟的上传次数
        AtomicInteger currentCount = userUploads.computeIfAbsent(currentMinute, k -> new AtomicInteger(0));
        
        if (currentCount.incrementAndGet() > MAX_UPLOADS_PER_MINUTE) {
            throw new ServiceException("上传过于频繁，请稍后再试");
        }
    }

    /**
     * 生成唯一文件名
     */
    private String generateFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString().replace("-", "") + "." + extension;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
    
    /**
     * 检查文件名是否包含危险字符
     */
    private boolean containsDangerousCharacters(String filename) {
        // 检查路径遍历字符
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return true;
        }
        
        // 检查其他危险字符
        String[] dangerousChars = {"<", ">", ":", "\"", "|", "?", "*", "\0"};
        for (String dangerousChar : dangerousChars) {
            if (filename.contains(dangerousChar)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 验证文件头（魔数）
     */
    private boolean validateFileSignature(MultipartFile file, String extension) throws IOException {
        byte[] expectedSignature = FILE_SIGNATURES.get(extension.toLowerCase());
        if (expectedSignature == null) {
            return false;
        }
        
        try (InputStream inputStream = file.getInputStream()) {
            byte[] fileHeader = new byte[expectedSignature.length];
            int bytesRead = inputStream.read(fileHeader);
            
            if (bytesRead < expectedSignature.length) {
                return false;
            }
            
            return Arrays.equals(fileHeader, expectedSignature);
        }
    }

    /**
     * 生成访问URL
     */
    private String generateAccessUrl(String category, String fileName) {
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        
        if (StringUtils.hasText(category)) {
            return filePatternPrefix + category + "/" + dateDir + "/" + fileName;
        } else {
            return filePatternPrefix + dateDir + "/" + fileName;
        }
    }
}


