package com.dj.customer.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Mask {
    MaskType type() default MaskType.GENERIC;

    enum MaskType {
        EMAIL,
        MOBILE,
        GENERIC
    }
}
