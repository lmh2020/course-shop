package com.company.exam.service;

import java.util.List;
import java.util.Map;

import com.company.exam.domain.EduQuestionStore;
import com.company.common.enums.business.QuestionTypeEnum;
import com.company.exam.domain.EduPaperStore;
import com.company.exam.form.EduExamQuestionForExamQueryForm;
import com.company.exam.form.EduPaperStoreMergeForm;
import com.company.exam.form.EduPaperStoreQueryForm;
import com.company.exam.form.EduExamQuestionForPaperQueryForm;
import com.company.exam.vo.PaperQuestionStatisticsVO;
import com.company.exam.vo.StoreQuestionVO;

/**
 * 考核试卷基础信息Service接口
 */
public interface IEduPaperStoreService {

    /**
     * 查询考核试卷基础信息
     *
     * @param id 考核试卷基础信息ID
     * @return 考核试卷基础信息
     */
    EduPaperStore selectEduPaperStoreById(Long id);

    /**
     * 查询考核试卷基础信息列表
     *
     * @param queryForm 查询表单
     * @return 考核试卷基础信息集合
     */
    List<EduPaperStore> selectEduPaperStoreList(EduPaperStoreQueryForm queryForm);

    EduQuestionStore getLastQuestion(Long paperId, QuestionTypeEnum questionType);

    Long getPaperQuestionCount(Long paperId);

    List<EduQuestionStore> selectPaperQuestionList(EduExamQuestionForPaperQueryForm queryForm);

    /**
     * 查询某试卷的所有试题[按试题分类进行分组]
     *
     * @param queryForm 查询参数
     * @return
     */
    Map<QuestionTypeEnum, List<EduQuestionStore>> selectPaperQuestionListWithGroup(EduExamQuestionForPaperQueryForm queryForm);

    /**
     * 从题库里查询可导入试题【用于某试卷！！】
     *
     * @param queryForm 查询参数
     */
    List<EduQuestionStore> selectStoreQuestionForPaper(EduExamQuestionForPaperQueryForm queryForm);

    /**
     * 从题库里查询可导入试题【用于某考核！！】
     *
     * @param queryForm 查询参数
     * @return
     */
    List<StoreQuestionVO> selectStoreQuestionForExam(EduExamQuestionForExamQueryForm queryForm);

    /**
     * 新增考核试卷基础信息
     *
     * @param mergeForm 新增表单
     */
    void insertEduPaperStore(EduPaperStoreMergeForm mergeForm);

    /**
     * 修改考核试卷基础信息
     *
     * @param mergeForm 修改表单
     */
    void updateEduPaperStore(EduPaperStoreMergeForm mergeForm);

    /**
     * 批量删除考核试卷基础信息
     *
     * @param ids 需要删除的考核试卷基础信息ID
     */
    void deleteEduPaperStoreByIds(Long[] ids);

    void importQuestion(Long paperId, Long[] storeQuestionIds);

    /**
     * 从试卷里移除多个试题
     *
     * @param storeQuestionId 题库试题的ID
     */
    void removePaperQuestion(Long paperId, Long storeQuestionId);

    void changePaperQuestionSort(String opt, Long paperId, Long fromId, Long toId);

    PaperQuestionStatisticsVO statisticsPaperInfo(Long paperId, String[] searchMode);
}
