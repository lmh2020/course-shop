package com.company.common.enums.business;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.company.common.enums.base.ICommonEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @desc 试题类型 枚举
 */

@Getter
@AllArgsConstructor
public enum QuestionTypeEnum implements ICommonEnum {

    SINGLE_CHOICE("SINGLE_CHOICE", Boolean.TRUE, 0, "单选题"),

    MULTIPLE_CHOICE("MULTIPLE_CHOICE", Boolean.TRUE, 1, "多选题"),

    JUDGE("JUDGE", Boolean.TRUE, 2, "判断题");

//    FILL_INPUT("FILL_INPUT", Boolean.FALSE, 3, "填空题"),

//    SUBJECTIVE("SUBJECTIVE", Boolean.FALSE, 4, "主观题");

//    DEVELOPMENT("DEVELOPMENT", Boolean.TRUE, 5, "编程题");

    //标记数据库存的值是code
    @EnumValue
    private String code;

    /* 该题型试题是否由系统自动批改 */
    private Boolean autoCorrect;

    /* 排序 */
    private Integer sort;

    private String desc;

}
