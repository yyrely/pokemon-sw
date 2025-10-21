package com.chuncongcong.framework.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ApiPageResponse<T> {


    private int code;
    private String message;
    private PageData<T> data;
    private boolean success;

    private ApiPageResponse(int code, String message, PageData<T> data, boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    public static <T> ApiPageResponse<T> success(long total, int pageNum, int pageSize, List<T> list) {
        return new ApiPageResponse<>(200, "OK", new PageData<>(total, pageNum, pageSize, list), true);
    }

    public static <T> ApiPageResponse<T> fail(int code, String message) {
        return new ApiPageResponse<>(code, message, new PageData<>(0, 0, 0, Collections.emptyList()), false);
    }

    public static <T> ApiPageResponse<T> ofPage(IPage<T> page) {
        return ApiPageResponse.success(
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize(),
                page.getRecords());
    }
}

