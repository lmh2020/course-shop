package com.company.exam.form;

import com.company.common.validator.group.ValidGroup;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * 考核对象 [新增/修改表单]
 */
@Data
public class EduExamMergeForm {

    /** 编号 */
    @NotNull(groups = ValidGroup.Update.class, message = "编号不能为空")
    private Long id;

    /** 考核标题 */
    @NotBlank(groups = ValidGroup.Merge.class, message = "考核标题不能为空")
    private String title;

    /** 封面 */
    private String coverImg;

    /** 备注说明 */
    private String remark;

    @NotNull(groups = ValidGroup.Update.class, message = "课程编号不能为空")
    private Long courseId;

}
