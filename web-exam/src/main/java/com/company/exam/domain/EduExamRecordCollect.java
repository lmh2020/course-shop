package com.company.exam.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.company.common.core.domain.BaseEntity;
import com.company.common.enums.business.ExamRecordStateEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 考核结果记录对象【基础、汇总数据】 edu_exam_record_collect
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EduExamRecordCollect extends BaseEntity{

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long examId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime enrollTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime touchTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishTime;

    private Integer score;

    private Boolean noticeEnable;

    private ExamRecordStateEnum state;

    private String remark;

//    private transient EduExam exam;

}
