package com.company.exam.form;

import com.company.common.enums.business.ExamStateEnum;
import lombok.Data;

/**
 * 考核对象 [查询表单]
 */
@Data
public class EduExamQueryForm {

    /** 考核标题 */
    private String examTitle;

    private ExamStateEnum state;

}
