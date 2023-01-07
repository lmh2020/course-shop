package com.company.exam.mapper;

import com.company.common.core.mybaits.CustomBaseMapper;
import com.company.exam.domain.EduExamRecordCollect;
import com.company.exam.form.EduUserExamineQueryForm;
import com.company.exam.vo.UserExamineRecordVO;

import java.util.List;


/**
 * 考核记录
 */
public interface EduExamRecordCollectMapper extends CustomBaseMapper<EduExamRecordCollect>{

    List<UserExamineRecordVO> selectExamRecordCollectList(EduUserExamineQueryForm queryForm);

    UserExamineRecordVO selectExamineDetail(Long recordCollectId);

}
