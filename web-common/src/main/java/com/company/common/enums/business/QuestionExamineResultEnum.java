package com.company.common.enums.business;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.company.common.enums.base.ICommonEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @desc 试题答题结果 枚举
 */

@Getter
@AllArgsConstructor
public enum QuestionExamineResultEnum implements ICommonEnum {

    NOT_DONE("NOT_DONE", "未批改"),

    CORRECT("CORRECT", "正确"),

    PART_CORRECT("PART_CORRECT", "部分正确"),

    INCORRECT("INCORRECT", "错误");

    //标记数据库存的值是code
    @EnumValue
    private String code;

    private String desc;

}
