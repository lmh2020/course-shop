package com.company.exam.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.company.common.core.domain.BaseEntity;
import com.company.common.enums.business.QuestionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 试卷与试题的中间表 edu_paper_question
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EduPaperQuestion extends BaseEntity{

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long paperId;

    private Long examQuestionStoreId;

    private QuestionTypeEnum examQuestionType;

    private Long nextQuestionId;


}
