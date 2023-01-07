package com.company.system.validator.annotation;


import com.company.system.validator.handler.DictDataValidHandler;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @Description 判断是否是合法的系统字典值
 */

@Documented
@Constraint(validatedBy = {DictDataValidHandler.class})
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDict {

    /* 字典类型值（唯一标识，对应字典表sys_dict_type中的dict_type字段） */
    String type();

    String message();

    //该属性必须添加，不添加启动报错
    Class<?>[] groups() default {};

    //该属性必须添加，不添加启动报错
    Class<? extends Payload>[] payload() default {};

}
