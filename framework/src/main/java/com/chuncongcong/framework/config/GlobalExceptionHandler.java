package com.chuncongcong.framework.config;


import com.chuncongcong.framework.exception.ServiceException;
import com.chuncongcong.framework.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;


import static com.chuncongcong.framework.filter.TraceIdFilter.TRACE_ID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public ApiResponse<Void> handleBusinessException(ServiceException ex, HttpServletRequest request) {
        return getFail(ex.getCode(), ex.getMessage(), request);
    }

    /**
     * 运行时异常（包括空指针、数组越界等）
     */
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<Void> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        // 可以记录日志
        log.error(ex.getMessage(), ex);
        return getFail(500, ex.getMessage(), request);
    }

    /**
     * 校验异常处理 - 用于@RequestBody对象
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = "参数校验失败";
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (fieldError != null && fieldError.getDefaultMessage() != null) {
            errorMessage = fieldError.getDefaultMessage();
        }
        return getFail(400, errorMessage, request);
    }

    /**
     * 校验异常处理 - 用于@RequestParam/@PathVariable的校验
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<Void> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("参数校验失败");
        return getFail(400, errorMessage, request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ApiResponse<Void> handleNotFound(NoResourceFoundException ex, HttpServletRequest request) {
        return getFail(404, ex.getMessage(), request);
    }

    /**
     * 捕获其他异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception ex, HttpServletRequest request) {
        log.error("系统异常: {}", ex.getMessage(), ex);
        return getFail(500, "系统异常：" + ex.getMessage(), request);
    }

    private ApiResponse<Void> getFail(int code, String errorMessage, HttpServletRequest request) {
        return ApiResponse.fail(code, errorMessage, request.getRequestURI(), MDC.get(TRACE_ID));
    }
}

