package com.chuncongcong.sw.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "文件上传结果")
public class UploadVO {

    @Schema(description = "文件访问URL", example = "/pic/avatar/2025/01/15/a1b2c3d4e5f6.jpg")
    private String url;

    @Schema(description = "生成的文件名", example = "a1b2c3d4e5f6.jpg")
    private String fileName;

    @Schema(description = "原始文件名", example = "user_avatar.jpg")
    private String originalName;

    @Schema(description = "文件大小（字节）", example = "102400")
    private Long size;

    @Schema(description = "文件类型", example = "image/jpeg")
    private String contentType;

    @Schema(description = "文件分类", example = "avatar")
    private String category;

}
