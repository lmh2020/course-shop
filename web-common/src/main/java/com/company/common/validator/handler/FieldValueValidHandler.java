package com.company.common.validator.handler;

import com.company.common.core.mybaits.CustomBaseMapper;
import com.company.common.enums.base.ICommonEnum;
import com.company.common.exception.ServiceException;
import com.company.common.utils.AssertUtil;
import com.company.common.utils.spring.SpringUtils;
import com.company.common.validator.annotation.AssertLegal;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 判断被AssertContains注解的属性值是否被包含。用于判断参数是否合法
 */

public class FieldValueValidHandler implements ConstraintValidator<AssertLegal, String> {

    /* 处理器本地缓存 */
    private Map<Class, CustomBaseMapper> mapperMap = new HashMap<>();

    /* 处理器类型。枚举类型、或者获取继承ServiceImpl的mybaits-plus处理器 */
    private Class mapperClazz;

    /* 被AssertContains注解的属性在数据库对应的字段 */
    private String fieldName;

    private boolean contains;

    @Override
    public void initialize(AssertLegal constraintAnnotation) {
        this.mapperClazz = constraintAnnotation.handler();
        this.fieldName = constraintAnnotation.fieldName();
        this.contains = constraintAnnotation.exist();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)){
            return true;
        }
        if (CustomBaseMapper.class.isAssignableFrom(mapperClazz)){
            AssertUtil.isBlank(this.fieldName, "字段名不能为空");
            CustomBaseMapper handler = mapperMap.get(mapperClazz);
            if (handler == null){
                handler = (CustomBaseMapper) SpringUtils.getBean(mapperClazz);
                AssertUtil.isNull(handler, "未获取到处理器：" + mapperClazz.getName());
                mapperMap.put(mapperClazz, handler);
            }
            return handler.exist(this.fieldName, value) != contains;
        } else if(ICommonEnum.class.isAssignableFrom(mapperClazz)){
            return ICommonEnum.contains(mapperClazz, value);
        } else{
            throw new ServiceException("不支持的处理器类型：" + mapperClazz.getName());
        }
    }

}
