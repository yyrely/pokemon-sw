package com.chuncongcong.framework.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    private boolean success;
    private String traceId;
    private String path;

    private ApiResponse(int code, String message, T data, boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    private ApiResponse(int code, String message, T data, boolean success, String traceId, String path) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
        this.traceId = traceId;
        this.path = path;
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "OK", null, true);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "OK", data, true);
    }

    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<>(code, message, null, false);
    }

    public static <T> ApiResponse<T> fail(int code, String message, String traceId, String path) {
        return new ApiResponse<>(code, message, null, false, traceId, path);
    }
}

