package com.company.exam.form;

import com.company.common.validator.group.ValidGroup;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * 考试试卷题目对象 [新增/修改表单]
 */
@Data
public class EduExamQuestionMergeForm {

    /** 主键 */
    @NotNull(groups = ValidGroup.Update.class, message = "编号不能为空")
    private Long id;

    /** 题目考核方向（类型） */
    @NotEmpty(groups = ValidGroup.Merge.class, message = "题目考核方向不能为空")
    private String questionCategoryCode;

    /** 题目 */
    @NotEmpty(groups = ValidGroup.Merge.class, message = "题目不能为空")
    private String question;

    /** 试题类型 */
    @NotEmpty(groups = ValidGroup.Merge.class, message = "试题类型不能为空")
    private String questionType;

    /** 答案 */
    private String answers;

    /** 正确答案 */
    private String correctAnswer;

    /** 答案分析 */
    private String analysis;

    /** 困难等级（1~5） */
    @NotNull(groups = ValidGroup.Merge.class, message = "困难等级不能为空")
    private Integer difficultyLevel;

    /** 题目总使用次数 */
    private Integer totalUsedCount;

    /** 达标/正确次数 */
    private Integer totalReachedCount;

    /** 分值 */
    @NotNull(groups = ValidGroup.Merge.class, message = "分值不能为空")
    private Integer score;

}
