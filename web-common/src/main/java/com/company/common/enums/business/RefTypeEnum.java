package com.company.common.enums.business;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.company.common.enums.base.ICommonEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @desc 学校、医院类型枚举
 */

@Getter
@AllArgsConstructor
public enum RefTypeEnum implements ICommonEnum {

    SCHOOL("SCHOOL", "学校"),

    HOSPITAL("HOSPITAL", "医院");

    @EnumValue
    private String code;

    private String desc;

}
