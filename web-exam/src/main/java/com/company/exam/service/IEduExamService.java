package com.company.exam.service;

import java.util.List;
import java.util.Map;

import com.company.common.enums.business.QuestionTypeEnum;
import com.company.exam.domain.EduExam;
import com.company.exam.form.EduExamMergeForm;
import com.company.exam.form.EduExamQueryForm;
import com.company.exam.form.EduExamQuestionQueryForm;
import com.company.exam.vo.ExamQuestionVO;
import com.company.exam.vo.ExamineVO;

/**
 * 考核Service接口
 */
public interface IEduExamService {

    /**
     * 查询考核
     *
     * @param id 考核ID
     * @return 考核
     */
    EduExam selectEduExamById(Long id);

    /**
     * 查询考核列表
     *
     * @param queryForm 查询表单
     * @return 考核集合
     */
    List<EduExam> selectEduExamList(EduExamQueryForm queryForm);

    Map<QuestionTypeEnum, List<ExamQuestionVO>> selectExamQuestionList(EduExamQuestionQueryForm queryForm);

    Map<QuestionTypeEnum, List<ExamQuestionVO>> selectExamQuestionList(Long examId);

    ExamineVO selectExamineDetail(Long recordCollectId);

    ExamineVO selectExamineResult(Long recordCollectId);

    /**
     * 新增考核
     *
     * @param mergeForm 新增表单
     */
    void insertEduExam(EduExamMergeForm mergeForm);

    /**
     * 修改考核
     *
     * @param mergeForm 修改表单
     */
    void updateEduExam(EduExamMergeForm mergeForm);

    void updateTotalScoreAndDifficultyLevel(Long examId, Integer totalScore, Integer difficultyLevel);

    /**
     * 批量删除考核
     *
     * @param ids 需要删除的考核ID
     */
    void deleteEduExamByIds(Long[] ids);

    void checkExamOperateAllowable(Long examId, EduExam exam, Boolean deepCheck);

    void updateState(Long examId, String newState);

//    List<PaperQuestionStatisticsVO.ExamPaperDatePlan> statisticsExamPaperDatePlan(Long paperId, Integer days);
}
