package com.company.exam.form;

import lombok.Data;

/**
 * 考核试卷基础信息对象 [查询表单]
 */
@Data
public class EduPaperStoreQueryForm {

    /** 标题 */
    private String title;

    /** 困难等级（1~5） */
    private Integer difficultyLevel;

}
