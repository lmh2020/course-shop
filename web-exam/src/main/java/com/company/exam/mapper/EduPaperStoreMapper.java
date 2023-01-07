package com.company.exam.mapper;

import com.company.common.core.mybaits.CustomBaseMapper;
import com.company.exam.domain.EduPaperStore;
import com.company.exam.domain.EduPaperQuestion;
import com.company.exam.domain.EduQuestionStore;
import com.company.exam.form.EduExamQuestionForExamQueryForm;
import com.company.exam.form.EduExamQuestionForPaperQueryForm;
import com.company.exam.vo.PaperQuestionStatisticsVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;


/**
 * 考核试卷基础信息Mapper接口
 */
public interface EduPaperStoreMapper extends CustomBaseMapper<EduPaperStore>{

    @Select("SELECT CASE COUNT(1) WHEN 0 THEN 0 ELSE 1 END FROM edu_paper_question WHERE paper_id = #{paperId} AND exam_question_store_id = #{storeQuestionId} LIMIT 1")
    Boolean checkPaperQuestionUnique(@Param("paperId") Long paperId, @Param("storeQuestionId") Long storeQuestionId);

    List<EduQuestionStore> selectPaperQuestionList(EduExamQuestionForPaperQueryForm queryForm);

    List<EduQuestionStore> selectStoreQuestionForPaper(EduExamQuestionForPaperQueryForm queryForm);

    List<EduQuestionStore> selectStoreQuestionForExam(EduExamQuestionForExamQueryForm queryForm);

    void batchRemovePaperQuestion(@Param("paperId") Long paperId, @Param("storeQuestionIds") Collection<Long> storeQuestionIds);

//    void batchImportQuestion(@Param("paperId") Long paperId, @Param("storeQuestionIds") Long[] storeQuestionIds);

    /**
     * @description 获取某试卷某试题类型的最后一道题
     */
    EduQuestionStore getLastPaperQuestionByQuestionType(@Param("paperId")Long paperId, @Param("questionType")String questionType);

    void updateLastPaperQuestion(@Param("paperId")Long paperId, @Param("questionType")String questionType, @Param("nextQuestionId")Long nextQuestionId);

    void batchImportQuestion(List<EduPaperQuestion> paperQuestionList);

    void updatePaperQuestionNextId(@Param("paperId")Long paperId, @Param("storeQuestionId")Long storeQuestionId, @Param("newNextId")Long newNextId);

    @Select("SELECT * FROM edu_paper_question WHERE paper_id = #{paperId} AND next_question_id = #{nextQuestionId} LIMIT 1")
    EduPaperQuestion selectPaperQuestionByNextQuestionId(@Param("paperId")Long paperId, @Param("nextQuestionId")Long nextQuestionId);

    @Select("SELECT * FROM edu_paper_question WHERE paper_id = #{paperId} AND exam_question_store_id = #{storeQuestionId} LIMIT 1")
    EduPaperQuestion selectPaperQuestionByQuestionId(@Param("paperId")Long paperId, @Param("storeQuestionId")Long storeQuestionId);

    EduQuestionStore selectPaperQuestion(@Param("paperId")Long paperId, @Param("storeQuestionId")Long storeQuestionId);

    /* 统计试卷各类型的数量与总分 */
    List<PaperQuestionStatisticsVO.QuestionPart> statisticsPaperQuestionPartCount(Long paperId);

    @Select("select COUNT(1) FROM edu_paper_question WHERE paper_id = #{paperId}")
    Long getPaperQuestionCount(Long paperId);


}
