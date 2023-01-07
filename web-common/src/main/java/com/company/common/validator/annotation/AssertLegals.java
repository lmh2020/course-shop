package com.company.common.validator.annotation;


import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AssertLegals {

    AssertLegal[] value();

}
