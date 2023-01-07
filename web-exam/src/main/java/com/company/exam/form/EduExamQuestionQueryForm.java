package com.company.exam.form;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 考试试卷题目 [查询表单]
 */
@Data
public class EduExamQuestionQueryForm {

    /** 考核ID */
    @NotNull(message = "考核编号不能为空")
    private Long examId;

    /** 题目考核方向（类型） */
    private String questionCategoryCode;

    /** 题目 */
    private String question;

    /** 试题类型 */
    private String questionType;

    /** 困难等级（1~5） */
    private Integer difficultyLevel;

    /** 分值区间（1~50） */
    @Min(value = 1, message = "分值不能小于1")
    @Max(value = 50, message = "分值不能大于50")
    private Integer score;

}
