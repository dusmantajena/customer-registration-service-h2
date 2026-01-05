package com.dj.customer.advice;

import com.dj.customer.annotation.Mask;
import com.dj.customer.util.MaskingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Field;

@Slf4j
@ControllerAdvice
public class MaskingResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true; // apply to all responses
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  org.springframework.http.server.ServerHttpRequest request,
                                  org.springframework.http.server.ServerHttpResponse response) {

        if (body == null) return null;

        try {
            Field[] fields = body.getClass().getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Mask.class)) {
                    field.setAccessible(true);

                    Mask mask = field.getAnnotation(Mask.class);
                    String original = (String) field.get(body);

                    if (original == null) continue;

                    String masked = switch (mask.type()) {
                        case EMAIL -> MaskingUtil.maskEmail(original);
                        case MOBILE -> MaskingUtil.maskMobile(original);
                        default -> MaskingUtil.maskGeneric(original);
                    };

                    field.set(body, masked);
                }
            }

        } catch (Exception e) {
            log.error("Failed to mask response", e);
        }
        log.info("Masked Response Body: {}", body);

        return body;
    }
}
