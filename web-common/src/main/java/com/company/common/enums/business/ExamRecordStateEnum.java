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
public enum ExamRecordStateEnum implements ICommonEnum {

    AWAITING("AWAITING", "未开始"),

    UNDERWAY("UNDERWAY", "进行中"),

    FINISHED("FINISHED", "已结束");

    //标记数据库存的值是code
    @EnumValue
    private String code;

    private String desc;

}
