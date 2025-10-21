package com.chuncongcong.sw.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "文件上传参数")
public class UploadParam {

    @NotNull(message = "请选择要上传的文件")
    @Schema(description = "上传的文件", required = true)
    private MultipartFile file;

    @Size(max = 50, message = "分类名称不能超过50个字符")
    @Schema(description = "文件分类目录", example = "avatar")
    private String category;

}
