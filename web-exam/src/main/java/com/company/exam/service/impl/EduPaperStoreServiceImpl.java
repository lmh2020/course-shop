package com.company.exam.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.company.exam.domain.EduPaperQuestion;
import com.company.exam.domain.EduQuestionStore;
import com.company.exam.mapper.EduPaperStoreMapper;
import com.company.exam.service.IEduPaperStoreService;
import com.company.exam.service.IEduQuestionStoreService;
import com.company.common.enums.business.QuestionTypeEnum;
import com.company.common.exception.CustomException;
import com.company.common.exception.SystemException;
import com.company.common.utils.AssertUtil;
import com.company.common.utils.StringUtils;
import com.company.common.utils.bean.BeanTransUtil;
import com.company.exam.dto.QuestionAnswersDTO;
import com.company.exam.form.EduExamQuestionForExamQueryForm;
import com.company.exam.form.EduExamQuestionForPaperQueryForm;
import com.company.exam.common.utils.EduQuestionUtil;
import com.company.exam.vo.PaperQuestionStatisticsVO;
import com.company.exam.vo.StoreQuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.company.exam.domain.EduPaperStore;
import com.company.exam.form.EduPaperStoreQueryForm;
import com.company.exam.form.EduPaperStoreMergeForm;

/**
 * 考核试卷基础信息Service业务层处理
 */
@Slf4j
@Service
public class EduPaperStoreServiceImpl extends ServiceImpl<EduPaperStoreMapper, EduPaperStore> implements IEduPaperStoreService {

    @Autowired
    private IEduQuestionStoreService questionStoreService;

    /**
     * 查询考核试卷基础信息
     *
     * @param id 考核试卷基础信息ID
     * @return 考核试卷基础信息
     */
    @Override
    public EduPaperStore selectEduPaperStoreById(Long id) {
        return this.getById(id);
    }

    /**
     * 查询考核试卷基础信息列表
     *
     * @param queryForm 查询表单
     * @return 考核试卷基础信息
     */
    @Override
    public List<EduPaperStore> selectEduPaperStoreList(EduPaperStoreQueryForm queryForm) {
        List<EduPaperStore> list = this.list(new QueryWrapper<EduPaperStore>().lambda()
                .like(StringUtils.isNotEmpty(queryForm.getTitle()), EduPaperStore::getTitle, queryForm.getTitle())
                .eq(queryForm.getDifficultyLevel() != null, EduPaperStore::getDifficultyLevel, queryForm.getDifficultyLevel())
        );
        return list;
    }

    /**
     * 查询考试试卷题目
     *
     * @param paperId 试卷ID
     * @param questionType 试卷类型
     * @return 考试试卷题目
     */
    @Override
    public EduQuestionStore getLastQuestion(Long paperId, QuestionTypeEnum questionType){
        return this.baseMapper.getLastPaperQuestionByQuestionType(paperId, questionType.getCode());
    }


    /**
     * 获取某试卷的试题总数
     */
    @Override
    public Long getPaperQuestionCount(Long paperId){
        return this.baseMapper.getPaperQuestionCount(paperId);
    }

    /**
     * 查询某试卷的所有试题
     *
     * @param queryForm 查询参数
     * @return
     */
    @Override
    public List<EduQuestionStore> selectPaperQuestionList(EduExamQuestionForPaperQueryForm queryForm){
        return this.baseMapper.selectPaperQuestionList(queryForm);
    }

    /**
     * 查询某试卷的所有试题[按试题分类进行分组]
     *
     * @param queryForm 查询参数
     * @return
     */
    @Override
    public Map<QuestionTypeEnum, List<EduQuestionStore>> selectPaperQuestionListWithGroup(EduExamQuestionForPaperQueryForm queryForm){
        List<EduQuestionStore> questionList = this.selectPaperQuestionList(queryForm);
        if (CollectionUtils.isNotEmpty(questionList)) {
            Map<QuestionTypeEnum, List<EduQuestionStore>> collect = questionList.stream().collect(Collectors.groupingBy(EduQuestionStore::getQuestionType));
            for (Map.Entry<QuestionTypeEnum, List<EduQuestionStore>> questionEntry : collect.entrySet()) {
                List<EduQuestionStore> sortedList = EduQuestionUtil.sortStoreQuestions(questionEntry.getValue());
                collect.put(questionEntry.getKey(), sortedList);
            }
            return collect;
        }
        return null;
    }

    /**
     * 从题库里查询某【试卷！！！！】的可导入试题（对题库结果进行了封装）
     *
     * @param queryForm 查询参数
     */
    @Override
    public List<EduQuestionStore> selectStoreQuestionForPaper(EduExamQuestionForPaperQueryForm queryForm){
        return this.baseMapper.selectStoreQuestionForPaper(queryForm);
    }

    /**
     * 从题库里查询某【考核！！！！】的可导入试题（对题库结果进行了封装）
     *
     * @param queryForm 查询参数
     * @return
     */
    @Override
    public List<StoreQuestionVO> selectStoreQuestionForExam(EduExamQuestionForExamQueryForm queryForm){
        List<EduQuestionStore> questionStoreList = this.baseMapper.selectStoreQuestionForExam(queryForm);
        return this.convertStoreQuestion(questionStoreList);
    }

    /**
     * @description 转换考核试卷信息（主要转换里面的答案）
     */
    private List<StoreQuestionVO> convertStoreQuestion(List<EduQuestionStore> list){
        if (CollectionUtils.isNotEmpty(list)){
            return list.stream()
                    .map(question -> {
                        StoreQuestionVO storeQuestionVO = BeanTransUtil.copy(question, StoreQuestionVO.class);
                        if (StringUtils.isNotBlank(question.getAnswers())) {
                            storeQuestionVO.setAnswers(JSON.parseArray(question.getAnswers(), QuestionAnswersDTO.class));
                        }
                        if (StringUtils.isNotBlank(question.getCorrectAnswer())) {
                            storeQuestionVO.setCorrectAnswer(JSON.parseArray(question.getCorrectAnswer()).toArray(new String[0]));
                        }
                        return storeQuestionVO;
                    })
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 新增考核试卷基础信息
     *
     * @param mergeForm 新增表单
     * @return 结果
     */
    @Override
    public void insertEduPaperStore(EduPaperStoreMergeForm mergeForm) {
        AssertUtil.isTrue(this.baseMapper.exist("title", mergeForm.getTitle()), "当前试卷标题已存在");
        EduPaperStore eduPaperStore = BeanTransUtil.copy(mergeForm, EduPaperStore.class);
        eduPaperStore.setTotalScore(0);
        eduPaperStore.setDifficultyLevel(0);
        this.save(eduPaperStore);
    }

    /**
     * 修改考核试卷基础信息
     *
     * @param mergeForm 修改表单
     * @return 结果
     */
    @Override
    public void updateEduPaperStore(EduPaperStoreMergeForm mergeForm) {
        AssertUtil.isNotNull(this.getOne(new QueryWrapper<EduPaperStore>().lambda()
                .ne(EduPaperStore::getId, mergeForm.getId())
                .eq(EduPaperStore::getTitle, mergeForm.getTitle())
                .last("limit 1")), "当前试卷标题已存在");
        EduPaperStore eduPaperStore = BeanTransUtil.copy(mergeForm, EduPaperStore.class);
        eduPaperStore.setDifficultyLevel(0);
        this.updateById(eduPaperStore);
    }

    /**
     * 批量删除考核试卷基础信息
     *
     * @param ids [批量]需要删除的考核试卷基础信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEduPaperStoreByIds(Long[] ids) {
        AssertUtil.isEmpty(ids, "编号不能为空");
        this.removeByIds(Arrays.asList(ids));
    }

    /**
     * 从题库导入试题
     *  @param paperId 试卷ID
     * @param storeQuestionIds 题库试题ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importQuestion(Long paperId, Long[] storeQuestionIds){
        AssertUtil.isEmpty(storeQuestionIds, "题库试题编号不能为空");
        EduPaperStore paper = Optional.ofNullable(this.selectEduPaperStoreById(paperId))
                .orElseThrow(() -> new CustomException("试卷信息不存在：" + paperId));

        List<EduQuestionStore> questionList = questionStoreService.getByIds(storeQuestionIds);
        if (CollectionUtils.isEmpty(questionList)){
            throw new CustomException("题库试题信息不存在：" + JSON.toJSONString(storeQuestionIds));
        } else if (questionList.size() != storeQuestionIds.length){
            List<Long> ids = Arrays.asList(storeQuestionIds);
            ids.removeAll(questionList.stream().map(EduQuestionStore::getId).collect(Collectors.toList()));
            throw new CustomException("以下题库试题信息不存在：" + JSON.toJSONString(ids));
        }
        /* 注意，这里要计算的是新添加这一批试题的困难程度总和（不包括试卷中已经添加的试题） */
        Integer newTotalDifficultyLevel = paper.getDifficultyLevel();
        Integer totalScore = paper.getTotalScore();
        List<EduPaperQuestion> paperQuestionList = new ArrayList<>(storeQuestionIds.length);
        Map<QuestionTypeEnum, List<EduQuestionStore>> questionGroupMap = questionList.stream().collect(Collectors.groupingBy(EduQuestionStore::getQuestionType));
        for (Map.Entry<QuestionTypeEnum, List<EduQuestionStore>> questionEntry : questionGroupMap.entrySet()) {
            List<EduQuestionStore> questions = questionEntry.getValue();
            for (int i = questions.size() - 1; i >= 0; i--) {
                EduQuestionStore question = questions.get(i);
                Optional.ofNullable(questionStoreService.selectEduQuestionStoreById(question.getId()))
                        .orElseThrow(() -> new CustomException("题库试题信息不存在：" + question.getId()));

                AssertUtil.isTrue(this.baseMapper.checkPaperQuestionUnique(paperId, question.getId()),
                        "试题【" + question.getId() + "】已存在于该试卷中，请勿重复添加");

                Long nextQuestionId = i > 0 ? questions.get(i - 1).getId() : null;
                paperQuestionList.add(new EduPaperQuestion(null, paperId, question.getId(), question.getQuestionType(), nextQuestionId));

                /* 累加试卷总分数 */
                totalScore += question.getScore();

                /* 累加试题困难度 */
                newTotalDifficultyLevel += question.getDifficultyLevel();
            }
            this.baseMapper.updateLastPaperQuestion(paperId, questionEntry.getKey().getCode(), questions.get(questions.size() - 1).getId());
        }
        /* 更新试卷总分数 */
        paper.setTotalScore(totalScore);
        paper.setDifficultyLevel(((newTotalDifficultyLevel / questionList.size()) + paper.getDifficultyLevel()) / 2);
        this.updateById(paper);

        /* 批量保存新增的试卷 */
        this.baseMapper.batchImportQuestion(paperQuestionList);

        /* 检查当前该试卷是否正在被用于考试  TODO */

        /* 重新计算试卷难度等级  TODO */
    }

    /**
     * 从试卷里移除某个试题
     *  @param paperId 试卷ID
     * @param storeQuestionId 题库试题ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removePaperQuestion(Long paperId, Long storeQuestionId) {
        EduPaperStore paper = Optional.ofNullable(this.selectEduPaperStoreById(paperId))
                .orElseThrow(() -> new CustomException("试卷信息不存在：" + paperId));
        /* 需要删除的试题 */
        EduPaperQuestion paperQuestion = Optional.ofNullable(this.baseMapper.selectPaperQuestionByQuestionId(paperId, storeQuestionId))
                .orElseThrow(() -> new CustomException("试卷试题信息不存在：" + storeQuestionId));
        EduQuestionStore questionStore = Optional.ofNullable(questionStoreService.selectEduQuestionStoreById(storeQuestionId))
                .orElseThrow(() -> new SystemException("题库试题新词不存在：" + storeQuestionId));

        /* 更新试卷总分数 */
        if (paper.getTotalScore() < questionStore.getScore()){
            log.warn("试卷【{}】中试题【{}】删除后更新总分数时发生异常！被删除的试题分数{}大于试卷总分数{}", paperId, storeQuestionId, questionStore.getScore(), paper.getTotalScore());
            throw new SystemException("试卷总分数异常！请联系管理员");
        }

        Long paperQuestionCount = this.getPaperQuestionCount(paperId);

        paper.setTotalScore(paper.getTotalScore() - questionStore.getScore());
        paper.setDifficultyLevel(paper.getDifficultyLevel() - (int) (questionStore.getDifficultyLevel() / paperQuestionCount));
        this.updateById(paper);

        /* 被删除试题的上一个试题 */
        EduPaperQuestion prevPaperQuestion = this.baseMapper.selectPaperQuestionByNextQuestionId(paperId, storeQuestionId);
        if (prevPaperQuestion != null){
        /* 检查一下上个试题与当前试题是否是同类型（一般都都是同类型，但前期冗余一些判断，方便在系统测试阶段查找问题） */
        AssertUtil.notEquels(paperQuestion.getExamQuestionType(), prevPaperQuestion.getExamQuestionType(), "系统异常：试题类型不匹配：" + storeQuestionId);
            /* 如果存在上个试题（也就是被删除的试题不是处于第一个位置，则需要重新修改排序链表） */
            this.baseMapper.updatePaperQuestionNextId(paperId, prevPaperQuestion.getExamQuestionStoreId(), paperQuestion.getNextQuestionId());
        }
        /* 删除被移除的试题 */
        this.baseMapper.batchRemovePaperQuestion(paperId, Collections.singletonList(storeQuestionId));
    }

    /**
     * 为试卷试题排序（页面上/下拖拽）
     *
     * @param opt 操作类型：UP（拖拽元素向上移动）、DOWM（拖拽元素向下移动）
     * @param paperId 试卷ID
     * @param fromId 被拖拽元素ID
     * @param toId 拖拽目标位置元素ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePaperQuestionSort(String opt, Long paperId, Long fromId, Long toId){
        AssertUtil.equels(fromId, toId, "请正确移动试题排序");
        EduQuestionStore fromQuestion = Optional.ofNullable(this.baseMapper.selectPaperQuestion(paperId, fromId))
                .orElseThrow(() -> new CustomException("试题信息不存在：" + fromId));

        EduQuestionStore toQuestion = Optional.ofNullable(this.baseMapper.selectPaperQuestion(paperId, toId))
                .orElseThrow(() -> new CustomException("试题信息不存在：" + toId));
        AssertUtil.notEquels(fromQuestion.getQuestionType(), toQuestion.getQuestionType(), "试题类型不匹配，操作失败");

        if ("UP".equals(opt)){
            /* 一定要先查出来下面两条，否则数据更改后导致后面查询结果不一致 */
            EduPaperQuestion fromPrevQuestion = this.baseMapper.selectPaperQuestionByNextQuestionId(paperId, fromId);
            EduPaperQuestion toPrevQuestion = this.baseMapper.selectPaperQuestionByNextQuestionId(paperId, toId);
            /** 更改拖拽元素的上个节点指向，改为指向拖拽元素的下个元素，即：
             *       拖拽前： B → C（拖拽元素） → D
             *       拖拽后： B → E，
             */
            if (fromPrevQuestion != null){
                this.baseMapper.updatePaperQuestionNextId(paperId, fromPrevQuestion.getExamQuestionStoreId(), fromQuestion.getNextQuestionId());
            }
            /** 更改原本指向目标位置元素的节点其指向，改为指向拖拽元素，即：
             *       拖拽前： A → B（目标位置） → C（拖拽元素） → D
             *       拖拽后： A → C（拖拽元素），
             */
            if (toPrevQuestion != null){
                this.baseMapper.updatePaperQuestionNextId(paperId, toPrevQuestion.getExamQuestionStoreId(), fromId);
            }
            /** 更改拖拽节点的指向，改为指向目标元素，即：
             *       拖拽前： B（目标位置） → C（拖拽元素） → D
             *       拖拽后： C（拖拽元素） → B（目标位置），
             */
            this.baseMapper.updatePaperQuestionNextId(paperId, fromId, toId);
        } else if ("DOWM".equals(opt)){
            /* 一定要先查出来下面两条，否则后面数据更改后查询结果不一致 */
            EduPaperQuestion fromPrevQuestion = this.baseMapper.selectPaperQuestionByNextQuestionId(paperId, fromId);
            /** 更改拖拽元素的上个节点指向，改为指向拖拽元素的下个元素，即：
             *       拖拽前： B → C（拖拽元素） → D
             *       拖拽后： B → E，
             */
            if (fromPrevQuestion != null){
                this.baseMapper.updatePaperQuestionNextId(paperId, fromPrevQuestion.getExamQuestionStoreId(), fromQuestion.getNextQuestionId());
            }
            /** 更改目标位置元素的指向，改为指向拖拽元素，即：
             *       拖拽前： A → B（目标位置） → C（拖拽元素） → D
             *       拖拽后： A → C（拖拽元素），
             */
            this.baseMapper.updatePaperQuestionNextId(paperId, toId, fromId);
            /** 更改拖拽节点的指向，改为原目标位置元素的指向，即：
             *       拖拽前： B（目标位置） → C（拖拽元素） → D
             *       拖拽后： C（拖拽元素） → B（目标位置），
             */
            this.baseMapper.updatePaperQuestionNextId(paperId, fromId, toQuestion.getNextQuestionId());
        } else {
            throw new CustomException("未知的操作类型：" + opt);
        }
    }


    /**
     * @description 试卷统计
     */
    @Override
    public PaperQuestionStatisticsVO statisticsPaperInfo(Long paperId, String[] searchMode){
        PaperQuestionStatisticsVO paperQuestionStatisticsVO = new PaperQuestionStatisticsVO();

        /* 计算试卷中各类型试题的分布情况 */
        if (ArrayUtils.contains(searchMode, "questionPart")){
            List<PaperQuestionStatisticsVO.QuestionPart> questionParts = this.baseMapper.statisticsPaperQuestionPartCount(paperId);
            paperQuestionStatisticsVO.setQuestionParts(questionParts);
        }

        /* 计算该试题被引用的考核时间安排 */
//        if (ArrayUtils.contains(searchMode, "examPaperDatePlan")) {
//            List<PaperQuestionStatisticsVO.ExamPaperDatePlan> examPaperDatePlans = examService.statisticsExamPaperDatePlan(paperId, 30);
//            paperQuestionStatisticsVO.setExamPaperDatePlans(examPaperDatePlans);
//        }

        /* 计算该试题自上次修改以来的及格率 TODO*/
        if (ArrayUtils.contains(searchMode, "examPassRate")) {
            paperQuestionStatisticsVO.setTotalExamPeopleCount(10);
            paperQuestionStatisticsVO.setTotalExamPassedCount(6);
        }
        return paperQuestionStatisticsVO;
    }

}
