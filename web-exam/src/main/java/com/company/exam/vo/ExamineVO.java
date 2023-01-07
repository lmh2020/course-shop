package com.company.exam.vo;

import com.company.common.enums.business.QuestionTypeEnum;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Description
 */
@Data
public class ExamineVO {

    private UserExamineRecordVO exam;

    private Map<Long, List<String>> answersCache;

    private Map<QuestionTypeEnum, List<ExamQuestionVO>> questions;

}
