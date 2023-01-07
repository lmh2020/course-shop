package com.company.exam.service;

import java.util.List;

import com.company.exam.domain.EduExamQuestion;
import com.company.exam.form.EduExamQuestionQueryForm;
import com.company.exam.vo.ExamQuestionVO;

/**
 * 考试试卷题目Service接口
 */
public interface IEduExamQuestionService {

    /**
     * 查询考试试卷题目
     *
     * @param id 考试试卷题目ID
     * @return 考试试卷题目
     */
    EduExamQuestion selectEduExamQuestionById(Long id);

    EduExamQuestion selectPrevEduExamQuestion(Long id);

    EduExamQuestion selectNextEduExamQuestionById(Long id);

    Long getExamQuestionCount(Long examId);

    List<ExamQuestionVO> selectEduExamQuestionList(Long examId);

    /**
     * 查询考试试卷题目列表
     *
     * @param queryForm 查询表单
     * @return 考试试卷题目集合
     */
    List<ExamQuestionVO> selectEduExamQuestionList(EduExamQuestionQueryForm queryForm);

    /**
     * 删除考试试卷题目信息
     *
     * @param id 考试试卷题目ID
     */
    void deleteEduExamQuestionById(Long id);

    void importByQuestionIds(Long examId, Long[] storeQuestionIds);


    /**
     * @description 直接拷贝某个试卷的所有试题到考核
     */
    void importByPaperQuestion(Long examId, Long paperId);

    void changeExamQuestionSort(String opt, Long fromId, Long toId);
}
