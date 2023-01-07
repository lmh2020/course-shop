package com.company.system.validator.handler;

import com.company.system.service.ISysDictTypeService;
import com.company.system.validator.annotation.ValidDict;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Description 判断是否是合法的系统字典值
 */

@Configurable(autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class DictDataValidHandler implements ConstraintValidator<ValidDict, String> {

    @Autowired
    private ISysDictTypeService dictTypeService;

    private String dictType;

    @Override
    public void initialize(ValidDict annotation) {
        this.dictType = annotation.type();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return StringUtils.isBlank(value) || dictTypeService.checkDictValueExist(dictType, value);
    }

}
