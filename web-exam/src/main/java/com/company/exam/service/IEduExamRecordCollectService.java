package com.company.exam.service;


import com.company.common.enums.business.ExamRecordStateEnum;
import com.company.exam.domain.EduExamRecordCollect;
import com.company.exam.form.EduUserExamineQueryForm;
import com.company.exam.form.SubmitExamineForm;
import com.company.exam.vo.UserExamineRecordVO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface IEduExamRecordCollectService {

    EduExamRecordCollect getEduExamRecordCollectById(Long id);

    List<UserExamineRecordVO> selectExamRecordCollectList(EduUserExamineQueryForm queryForm);

    UserExamineRecordVO selectExamineDetail(Long recordCollectId);

    List<EduExamRecordCollect> selectExamEnrolledRecordList(Long examId);

    void enrollExam(Long examId, Boolean noticeEnable);

    void batchUpdate(Collection<EduExamRecordCollect> recordCollects);

    void updateState(Long recordCollectId, ExamRecordStateEnum newState);

    void checkBeforeExamine(Long recordCollectId);

    void revokeExam(Long recordCollectId);

    void submitExamine(SubmitExamineForm form);

    Map<Long, List<String>> getUserAnswersCache(Long userId);
}
