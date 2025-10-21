package com.chuncongcong.framework.response;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ApiListResponse<T> {
    private int code;
    private String message;
    private List<T> data;
    private boolean success;

    private ApiListResponse(int code, String message, List<T> data, boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    public static <T> ApiListResponse<T> success(List<T> data) {
        return new ApiListResponse<>(200, "OK", data != null ? data : Collections.emptyList(), true);
    }

    public static <T> ApiListResponse<T> fail(int code, String message) {
        return new ApiListResponse<>(code, message, Collections.emptyList(), false);
    }
}

