package com.company.exam.mapper;

import com.company.common.core.mybaits.CustomBaseMapper;
import com.company.exam.domain.EduExam;
import com.company.exam.form.EduExamQueryForm;

import java.util.List;

/**
 * 考核Mapper接口
 */
public interface EduExamMapper extends CustomBaseMapper<EduExam>{


//    List<PaperQuestionStatisticsVO.ExamPaperDatePlan> statisticsExamPaperDatePlan(@Param("paperId") Long paperId, @Param("days") Integer days);

    List<EduExam> selectEduExamList(EduExamQueryForm queryForm);

}
