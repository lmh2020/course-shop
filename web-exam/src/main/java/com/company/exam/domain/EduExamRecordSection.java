package com.company.exam.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.company.common.core.domain.BaseEntity;
import com.company.common.enums.business.QuestionExamineResultEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 考核结果记录对象【每道试题】 edu_exam_record_section
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EduExamRecordSection extends BaseEntity{

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long recordCollectId;

    private Long examQuestionId;

    private String answer;

    private Integer score;

    private QuestionExamineResultEnum result;

}
