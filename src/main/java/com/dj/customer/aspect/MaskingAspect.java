package com.dj.customer.aspect;

import com.dj.customer.annotation.Mask;
import com.dj.customer.util.MaskingUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Slf4j
@Aspect
@Component
public class MaskingAspect {

    @Before("execution(* com.dj.customer.controller..*(..))")
    public void maskRequestPayloads(JoinPoint joinPoint) {

        for (Object arg : joinPoint.getArgs()) {

            if (arg == null) continue;

            Field[] fields = arg.getClass().getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Mask.class)) {

                    field.setAccessible(true);
                    try {
                        String original = (String) field.get(arg);
                        Mask mask = field.getAnnotation(Mask.class);

                        String maskedValue = switch (mask.type()) {
                            case EMAIL -> MaskingUtil.maskEmail(original);
                            case MOBILE -> MaskingUtil.maskMobile(original);
                            default -> MaskingUtil.maskGeneric(original);
                        };

                        field.set(arg, maskedValue);

                    } catch (Exception ignored) {
                        log.warn("Masking field failed: {}", field.getName());
                    }
                }
            }
        }
    }
}
