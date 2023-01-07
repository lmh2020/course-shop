package com.company.exam.form;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 考试试卷题目对象[用于查询某#考核#可导入试题] [查询表单]
 */
@Data
public class EduExamQuestionForExamQueryForm {

    /** 考核ID */
    @NotNull(message = "试卷编号不能为空")
    private Long examId;

    /**
     * 用于标识当前试题是否已经添加过(传Y或者N)
     **/
    private String added;

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
