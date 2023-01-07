package com.company.common.enums.business;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.company.common.enums.base.ICommonEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @desc 考核状态 枚举
 */

@Getter
@AllArgsConstructor
public enum ExamStateEnum implements ICommonEnum {

    NORMAL("NORMAL", "正常"),

    FREEZE("FREEZE", "冻结");

    //标记数据库存的值是code
    @EnumValue
    private String code;

    private String desc;

}
