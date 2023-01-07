package com.company.common.enums.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.company.common.exception.ServiceException;

import java.util.EnumSet;

/**
 * @Description
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public interface ICommonEnum {

    String getCode();

    static <E extends Enum<E> & ICommonEnum> Boolean contains(Class<E> enumClass, String code){
        return EnumSet.allOf(enumClass).stream().anyMatch(item -> item.getCode().equals(code));
    }

    static <E extends Enum<E> & ICommonEnum> E getByCode(Class<E> enumClass, String code){
        return EnumSet.allOf(enumClass).stream().filter(item -> item.getCode().equals(code)).findFirst().orElse(null);
    }

    static <E extends Enum<E> & ICommonEnum> E assertContainsAndGet(Class<E> enumClass, String code){
        E result = getByCode(enumClass, code);
        if (result == null){
            throw new ServiceException("未知的" + enumClass.getName() + "编码：" + code);
        }
        return result;
    }

}
