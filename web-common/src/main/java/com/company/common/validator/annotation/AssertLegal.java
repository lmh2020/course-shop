package com.company.common.validator.annotation;



import com.company.common.validator.handler.FieldValueValidHandler;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @Description 判断被注解的属性值是否包含在某一类字典里，用作判断参数值是否合法
 */

@Documented
@Constraint(validatedBy = {FieldValueValidHandler.class})
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(AssertLegals.class)
public @interface AssertLegal {

    /* 属性对应在数据库的字段名，只有handler是service（非枚举）时才需要 */
    String fieldName() default "";

    /* 处理器类型，  支持枚举或者mybatis-plus的mapper类 */
    Class<?> handler();

    /* true:属性值存在时抛异常、 false：属性值不存在时抛异常 */
    boolean exist() default false;

    String message();

    //该属性必须添加，不添加启动报错
    Class<?>[] groups() default {};

    //该属性必须添加，不添加启动报错
    Class<? extends Payload>[] payload() default {};

}
