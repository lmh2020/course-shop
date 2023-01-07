package com.company.exam.form;

import com.company.common.validator.group.ValidGroup;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * 考核试卷基础信息对象 [新增/修改表单]
 */
@Data
public class EduPaperStoreMergeForm {

    @NotNull(groups = ValidGroup.Update.class, message = "试卷编号不能为空")
    private Long id;

    /** 标题 */
    @NotEmpty(groups = ValidGroup.Merge.class, message = "标题不能为空")
    private String title;

    /** 试卷说明 */
    private String detail;

}
