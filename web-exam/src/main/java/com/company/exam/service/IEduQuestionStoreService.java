package com.company.exam.service;

import java.util.List;

import com.company.exam.domain.EduQuestionStore;
import com.company.exam.domain.EduExam;
import com.company.exam.form.EduQuestionStoreMergeForm;
import com.company.exam.form.EduQuestionStoreQueryForm;

/**
 * 考试试卷题目Service接口
 */
public interface IEduQuestionStoreService {

    /**
     * 查询考试试卷题目
     *
     * @param id 考试试卷题目ID
     * @return 考试试卷题目
     */
    EduQuestionStore selectEduQuestionStoreById(Long id);

    /**
     * 查询考试试卷题目列表
     *
     * @param queryForm 考试试卷题目
     * @return 考试试卷题目集合
     */
    List<EduQuestionStore> selectEduQuestionStoreList(EduQuestionStoreQueryForm queryForm);

    List<EduExam> getExamQuestionReferredExams(String questionId);

    List<EduQuestionStore> getByIds(Long[] ids);

    /**
     * 新增考试试卷题目
     *
     * @param mergeForm 考试试卷题目
     */
    void insertEduQuestionStore(EduQuestionStoreMergeForm mergeForm);

    /**
     * 修改考试试卷题目
     *
     * @param eduQuestionStore 考试试卷题目
     */
    void updateEduQuestionStore(EduQuestionStoreMergeForm eduQuestionStore);

    /**
     * 批量删除考试试卷题目
     *
     * @param ids 需要删除的考试试卷题目ID
     */
    void deleteEduQuestionStoreByIds(Long[] ids);

    /**
     * 删除考试试卷题目信息
     *
     * @param id 考试试卷题目ID
     */
    void deleteEduQuestionStoreById(Long id);

}
