package com.company.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.exam.domain.EduExamRecordSection;
import com.company.exam.mapper.EduExamRecordSectionMapper;
import com.company.exam.service.IEduExamRecordSectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;


/**
 * @description 考核记录（试题）
 */
@Service
public class EduExamRecordSectionServiceImpl extends ServiceImpl<EduExamRecordSectionMapper, EduExamRecordSection> implements IEduExamRecordSectionService {


    @Override
    public List<EduExamRecordSection> selectRecordSectionListByRecordCollectId(Long recordCollectId){
        return this.list(new QueryWrapper<EduExamRecordSection>().lambda()
            .eq(EduExamRecordSection::getRecordCollectId, recordCollectId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveEduExamRecordSection(Collection<EduExamRecordSection> recordSections){
        this.baseMapper.batchSaveEduExamRecordSection(recordSections);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateEduExamRecordSection(Collection<EduExamRecordSection> recordSections){
        this.baseMapper.batchUpdateEduExamRecordSection(recordSections);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearExamRecordSections(Long recordCollectId){
        this.remove(new QueryWrapper<EduExamRecordSection>().lambda()
            .eq(EduExamRecordSection::getRecordCollectId, recordCollectId));
    }



}
