package com.company.exam.mapper;

import com.company.common.core.mybaits.CustomBaseMapper;
import com.company.exam.domain.EduExamRecordSection;

import java.util.Collection;


/**
 */
public interface EduExamRecordSectionMapper extends CustomBaseMapper<EduExamRecordSection>{

    void batchSaveEduExamRecordSection(Collection<EduExamRecordSection> recordSections);

    void batchUpdateEduExamRecordSection(Collection<EduExamRecordSection> recordSections);

}
