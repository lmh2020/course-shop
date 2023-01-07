package com.company.exam.service;


import com.company.exam.domain.EduExamRecordSection;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public interface IEduExamRecordSectionService {

    List<EduExamRecordSection> selectRecordSectionListByRecordCollectId(Long recordCollectId);

    void batchSaveEduExamRecordSection(Collection<EduExamRecordSection> recordSections);

    void batchUpdateEduExamRecordSection(Collection<EduExamRecordSection> recordSections);

    void clearExamRecordSections(Long recordCollectId);

}
