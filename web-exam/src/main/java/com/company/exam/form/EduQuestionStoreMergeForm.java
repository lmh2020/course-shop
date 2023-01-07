package com.company.exam.form;

import com.company.common.validator.group.ValidGroup;
import com.company.system.validator.annotation.ValidDict;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.List;
import java.util.Set;

/**
 * @Description
 */
@Data
public class EduQuestionStoreMergeForm {

    @NotNull(groups = ValidGroup.Update.class, message = "编号不能为空")
    private Long id;

    @ValidDict(groups = ValidGroup.Merge.class, type = "edu_exam_mode", message = "未知的考核方向编码")
    @NotBlank(groups = ValidGroup.Merge.class, message = "考核方向不能为空")
    private String questionCategoryCode;

    @NotBlank(groups = ValidGroup.Merge.class, message = "题目问题不能为空")
    private String question;

    @NotBlank(groups = ValidGroup.Merge.class, message = "试题类型不能为空")
    private String questionType;

    /* 可选答案，用于单选、多选、判断题 */
    private List<ChoiceAnswer> answers;

    private String analysis;

    /* 正确答案集合，用于所有类型。主观题/编程题等只有唯一答案时，该Set集合传一个参数即可 */
    private Set<String> correctAnswer;

    @NotNull(groups = ValidGroup.Merge.class, message = "难度等级不能为空")
    @Min(value = 1, groups = ValidGroup.Merge.class, message = "难度等级不能小于1")
    @Max(value = 5, groups = ValidGroup.Merge.class, message = "难度等级不能大于5")
    private Integer difficultyLevel;

    @NotNull(groups = ValidGroup.Merge.class, message = "分数不能为空")
    @Min(value = 1, groups = ValidGroup.Merge.class, message = "分数不能小于1")
    private Integer score;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChoiceAnswer{

        private Character key;

        private String val;

    }

}
