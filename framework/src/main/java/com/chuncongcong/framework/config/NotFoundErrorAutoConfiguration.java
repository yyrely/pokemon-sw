package com.chuncongcong.framework.config;

import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static com.chuncongcong.framework.filter.TraceIdFilter.TRACE_ID;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class NotFoundErrorAutoConfiguration {

    @Component
    @ConditionalOnMissingBean(ErrorAttributes.class)
    public static class NotFoundErrorAttributes implements ErrorAttributes {

        @Override
        public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
            Integer status = (Integer) webRequest.getAttribute("javax.servlet.error.status_code", WebRequest.SCOPE_REQUEST);

            if (status != null && status == 404) {
                String path = (String) webRequest.getAttribute("javax.servlet.error.request_uri", WebRequest.SCOPE_REQUEST);
                return Map.of(
                        "code", 404,
                        "message", "接口不存在",
                        "path", path,
                        "traceId", MDC.get(TRACE_ID)
                );
            }

            // 其他错误，返回空交给 BasicErrorController 默认处理
            return Map.of();
        }

        @Override
        public Throwable getError(WebRequest webRequest) {
            return null;
        }
    }
}