package com.company.exam.form;

import com.company.common.enums.business.QuestionTypeEnum;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @Description
 */
@Data
public class EduQuestionStoreQueryForm {

    /**
     * 考核方向
     */
    private String questionCategoryCodes;

    /**
     * 题目
     */
    private String question;

    /**
     * 试题类型
     */
    private QuestionTypeEnum questionType;

    /** 困难等级（1~5） */
    private Integer difficultyLevel;

    /** 分值区间（1~50） */
    @Min(value = 1, message = "分值不能小于1")
    @Max(value = 50, message = "分值不能大于50")
    private Integer score;

}
