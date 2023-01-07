package com.company.exam.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.company.common.enums.business.ExamRecordStateEnum;
import com.company.common.enums.business.ExamStateEnum;
import com.company.common.enums.business.QuestionExamineResultEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description
 */
@Data
public class UserExamineRecordVO {

    /** 主键 */
    private Long examId;

    /** 考核标题 */
    private String title;

    /** 封面 */
    private String coverImg;

    /** 总分 */
    private Integer totalScore;

    /** 困难等级 */
    private Integer difficultyLevel;

    /** 状态 */
    private ExamStateEnum examState;

    private String examRemark;

    private RecordCollect examineRecord;

    private List<RecordSection> recordSections;

    /* 报名信息 */
    @Data
    public static class RecordCollect {

        /** 主键 */
        private Long recordCollectId;

        private Long userId;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime enrollTime;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime touchTime;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime finishTime;

        private Integer score;

        private Boolean noticeEnable;

        private ExamRecordStateEnum examRecordState;

        private String examRecordRemark;

    }

    /* 考题考生答案记录 */
    @Data
    public static class RecordSection {

        private Long examQuestionId;

        private String userAnswer;

        private Integer userScore;

        private QuestionExamineResultEnum result;

    }

}
