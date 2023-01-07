package com.company.exam.vo;

import com.company.common.enums.business.QuestionTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * @Description  试卷试题统计
 */
@Data
public class PaperQuestionStatisticsVO {

    private List<QuestionPart> questionParts;

    private List<ExamPaperDatePlan> examPaperDatePlans;

    /* 该试卷总考试人数 */
    private Integer totalExamPeopleCount;

    /* 该试卷总通过人数 */
    private Integer totalExamPassedCount;


    /* 试题类型数量分布 */
    @Data
    public static class QuestionPart {

        /* 试题类型 */
        private QuestionTypeEnum questionType;

        /* 试题总分 */
        private Integer questionScore;

        /* 试题总数 */
        private Integer questionCount;

    }

    /* 当前试卷被引用的考核时间安排 */
    @Data
    public static class ExamPaperDatePlan {

        /* 考核标题 */
        private String title;

        /* 考核日期 */
        private String examDate;

        /* 考核总数 */
        private Integer examCount;

    }

}
