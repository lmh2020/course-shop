package com.company.exam.form;

import com.company.common.enums.CommonBooleanEnum;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Description  交卷
 */
@Data
public class SubmitExamineForm {

    @NotNull(message = "考核报名记录编号不能为空")
    private Long recordCollectId;

    @NotNull(message = "是否自动保存标识不能为空")
    private CommonBooleanEnum automaticSubmit;

//    private List<UserAnswer> userAnswers;

    @NotEmpty(message = "考核答案不能为空")
    private Map<Long, List<String>> userAnswers;

}
