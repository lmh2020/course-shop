package com.company.exam.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.company.exam.domain.EduExamQuestion;
import com.company.exam.domain.EduQuestionStore;
import com.company.exam.mapper.EduExamQuestionMapper;
import com.company.exam.service.IEduExamQuestionService;
import com.company.exam.service.IEduExamService;
import com.company.exam.service.IEduPaperStoreService;
import com.company.exam.service.IEduQuestionStoreService;
import com.company.common.enums.business.QuestionTypeEnum;
import com.company.common.exception.CustomException;
import com.company.common.exception.SystemException;
import com.company.common.utils.AssertUtil;
import com.company.common.utils.StringUtils;
import com.company.common.utils.bean.BeanTransUtil;
import com.company.exam.domain.EduExam;
import com.company.exam.domain.EduPaperStore;
import com.company.exam.form.EduExamQuestionForPaperQueryForm;
import com.company.exam.form.EduExamQuestionQueryForm;
import com.company.exam.dto.QuestionAnswersDTO;
import com.company.exam.vo.ExamQuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 考试试卷题目Service业务层处理
 */
@Slf4j
@Service
public class EduExamQuestionServiceImpl extends ServiceImpl<EduExamQuestionMapper, EduExamQuestion> implements IEduExamQuestionService {

    @Lazy
    @Autowired
    private IEduExamService examService;

    @Autowired
    private IEduQuestionStoreService questionStoreService;

    @Autowired
    private IEduPaperStoreService paperStoreService;

    /**
     * 查询考试试卷题目
     *
     * @param id 考试试卷题目ID
     * @return 考试试卷题目
     */
    @Override
    public EduExamQuestion selectEduExamQuestionById(Long id) {
        return this.getById(id);
    }

    /**
     * 查询上一道题
     *
     * @param id 考试试卷题目ID
     * @return 上一道考试试卷题目
     */
    @Override
    public EduExamQuestion selectPrevEduExamQuestion(Long id) {
        return this.getOne(new QueryWrapper<EduExamQuestion>().lambda()
            .eq(EduExamQuestion::getNextExamQuestionId, id));
    }

    /**
     * 查询下一道题
     *
     * @param id 考试试卷题目ID
     * @return 下一道考试试卷题目
     */
    @Override
    public EduExamQuestion selectNextEduExamQuestionById(Long id) {
        return this.baseMapper.selectNextEduExamQuestion(id);
    }


    /**
     * @description 获取某考核总试题数目
     */
    @Override
    public Long getExamQuestionCount(Long examId){
        return this.baseMapper.getExamQuestionCount(examId);
    }

    /**
     * 查询某考核的所有试题
     * @return
     */
    @Override
    public List<ExamQuestionVO> selectEduExamQuestionList(Long examId){
        List<EduExamQuestion> list = this.list(new QueryWrapper<EduExamQuestion>().lambda()
                .eq(EduExamQuestion::getExamId, examId));
        return this.convertExamQuestion(list);
    }

    /**
     * 查询考核试卷的所有题目列表
     *
     * @param queryForm 查询表单
     * @return 考试试卷题目
     */
    @Override
    public List<ExamQuestionVO> selectEduExamQuestionList(EduExamQuestionQueryForm queryForm) {
        List<EduExamQuestion> list = this.list(new QueryWrapper<EduExamQuestion>().lambda()
                .eq(queryForm.getExamId() != null, EduExamQuestion::getExamId, queryForm.getExamId())
                .eq(StringUtils.isNotEmpty(queryForm.getQuestionCategoryCode()), EduExamQuestion::getQuestionCategoryCode, queryForm.getQuestionCategoryCode())
                .eq(StringUtils.isNotEmpty(queryForm.getQuestion()), EduExamQuestion::getQuestion, queryForm.getQuestion())
                .eq(StringUtils.isNotEmpty(queryForm.getQuestionType()), EduExamQuestion::getQuestionType, queryForm.getQuestionType())
                .eq(queryForm.getDifficultyLevel() != null, EduExamQuestion::getDifficultyLevel, queryForm.getDifficultyLevel())
                .eq(queryForm.getScore() != null, EduExamQuestion::getScore, queryForm.getScore())
        );
        return this.convertExamQuestion(list);
    }


    /**
     * @description 转换考核试卷信息（主要转换里面的答案）
     */
    private List<ExamQuestionVO> convertExamQuestion(List<EduExamQuestion> list){
        if (CollectionUtils.isNotEmpty(list)){
            return list.stream()
                    .map(question -> {
                        ExamQuestionVO examQuestionVO = BeanTransUtil.copy(question, ExamQuestionVO.class);
                        if (StringUtils.isNotBlank(question.getAnswers())) {
                            examQuestionVO.setAnswers(JSON.parseArray(question.getAnswers(), QuestionAnswersDTO.class));
                        }
                        if (StringUtils.isNotBlank(question.getCorrectAnswer())) {
                            List<String> correctAnswer = JSON.parseArray(question.getCorrectAnswer(), String.class);
                            examQuestionVO.setCorrectAnswer(correctAnswer);
                        }
                        return examQuestionVO;
                    })
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 删除考试试卷题目信息
     * 删除前后试题编号的next指向变化（删除B）：
     *  删除前：A → B → C
     *  删除后：A → C
     *
     * @param id 考试试卷题目ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEduExamQuestionById(Long id) {
        /* 1. 查询被删除试题信息 */
        EduExamQuestion deleteQestion = Optional.ofNullable(this.selectEduExamQuestionById(id))
                .orElseThrow(() -> new CustomException("试题信息不存在"));

        EduExam exam = Optional.ofNullable(examService.selectEduExamById(deleteQestion.getExamId()))
                .orElseThrow(() -> new CustomException("考核信息不存在"));

        examService.checkExamOperateAllowable(deleteQestion.getExamId(), exam, Boolean.TRUE);

        /* 2. 判断该试题是否排在末尾（同类型） */
        if (deleteQestion.getNextExamQuestionId() != null){
            /* 3. 存在下一题（同类型），继续查询是否有上一道题 */
//            EduExamQuestion prevQuestion = this.getOne(new QueryWrapper<EduExamQuestion>().lambda()
//                    .eq(EduExamQuestion::getNextExamQuestionId, deleteQestion.getId()));
            EduExamQuestion prevQuestion = this.selectPrevEduExamQuestion(id);
            /* 4. 如果存在上一道题,修改其指向为被删除试题的原指向 */
            if (prevQuestion != null){
                /* TODO 冗余判断（避免新增试题出现BUG，方便查询问题）  项目成熟后可删除 */
                AssertUtil.notEquels(prevQuestion.getQuestionType(), deleteQestion.getQuestionType(), "试题类型不一致！[" + id + "、" + prevQuestion.getId() + "]");
                prevQuestion.setNextExamQuestionId(deleteQestion.getNextExamQuestionId());
                this.updateById(prevQuestion);
            }
        }
        /* 5. 更新考核总分数、难度等级 */
        if (exam.getTotalScore() < 0){
            log.warn("删除考核试题时发生异常：考核【{}】总分数{}小于试卷【{}】总分数{}", exam.getId(), exam.getTotalScore(), id, deleteQestion.getScore());
            throw new SystemException("系统异常！试卷分数大于考核总分数");
        }
        Long count = this.getExamQuestionCount(exam.getId());
        examService.updateTotalScoreAndDifficultyLevel(exam.getId(), exam.getTotalScore() - deleteQestion.getScore(),
                exam.getDifficultyLevel() - (int)(deleteQestion.getDifficultyLevel() / count));

        /* 6. 删除试题 */
        this.removeById(id);
    }

    /**
     * 更新某考核下某试题类型的最后一道题的next指向
     *
     * @param examId       考核试卷ID
     * @param questionType 题目类型
     * @param newNextId    排在当前试题类型最后一位的新指针（指针指向被新添加进来的第一个元素，以此让排序链表再次连接起来）
     */
    private void updateExamLastQuestionNextId(Long examId, QuestionTypeEnum questionType, Long newNextId) {
        this.update(new UpdateWrapper<EduExamQuestion>().lambda()
                .set(EduExamQuestion::getNextExamQuestionId, newNextId)
                .eq(EduExamQuestion::getExamId, examId)
                .eq(EduExamQuestion::getQuestionType, questionType)
                .isNull(EduExamQuestion::getNextExamQuestionId));
    }


    /**
     * @description 修改该试题指向的下一道题
     */
    private void updateExamQuestionNextId(Long examQuestionId, Long newNextId){
        this.update(new UpdateWrapper<EduExamQuestion>().lambda()
            .set(EduExamQuestion::getNextExamQuestionId, newNextId)
            .eq(EduExamQuestion::getId, examQuestionId));
    }


    /**
     * 从题库导入试题【批量导入试题】
     *
     * @param examId           考核ID
     * @param storeQuestionIds 题库试题ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importByQuestionIds(Long examId, Long[] storeQuestionIds) {
        AssertUtil.isEmpty(storeQuestionIds, "题库试题编号不能为空");
        EduExam exam = Optional.ofNullable(examService.selectEduExamById(examId))
                .orElseThrow(() -> new CustomException("考核信息不存在：" + examId));
        examService.checkExamOperateAllowable(examId, exam, Boolean.TRUE);

        List<EduQuestionStore> questionList = questionStoreService.getByIds(storeQuestionIds);
        if (CollectionUtils.isEmpty(questionList)) {
            throw new CustomException("题库试题信息不存在：" + JSON.toJSONString(storeQuestionIds));
        } else if (questionList.size() != storeQuestionIds.length) {
            List<Long> ids = Arrays.asList(storeQuestionIds);
            ids.removeAll(questionList.stream().map(EduQuestionStore::getId).collect(Collectors.toList()));
            throw new CustomException("以下题库试题信息不存在：" + JSON.toJSONString(ids));
        }

        Integer totalScore = exam.getTotalScore();
        Integer difficultyLevels = 0;
        List<EduExamQuestion> examQuestionList = new ArrayList<>(storeQuestionIds.length);
        Map<QuestionTypeEnum, List<EduQuestionStore>> questionGroupMap = questionList.stream().collect(Collectors.groupingBy(EduQuestionStore::getQuestionType));
        for (Map.Entry<QuestionTypeEnum, List<EduQuestionStore>> questionEntry : questionGroupMap.entrySet()) {
            List<EduQuestionStore> questions = questionEntry.getValue();
            /* 当前试题ID */
            Long currExamQuestionId;
            /* 提前生成下一道题的ID */
            Long nextExamQuestionId = IdWorker.getId();
            /* 新增的该类型所有题中，获取第一道题，用于拼接排序链表 */
            Long firstQuestionId = null;
            for (int i = 0; i < questions.size(); i++) {
                EduQuestionStore storeQuestion = questions.get(i);
                Optional.ofNullable(questionStoreService.selectEduQuestionStoreById(storeQuestion.getId()))
                        .orElseThrow(() -> new CustomException("题库试题信息不存在：" + storeQuestion.getId()));
                AssertUtil.isTrue(this.baseMapper.checkExamQuestionUnique(examId, storeQuestion.getId()),
                        "试题【" + storeQuestion.getId() + "】已存在于该考核试卷中，请勿重复添加");
                currExamQuestionId = nextExamQuestionId;
                /* 最后一道题的指向是NULL */
                nextExamQuestionId = (i == questions.size() - 1) ? null : IdWorker.getId();

                /* 获取该试题类型下新增这一批试题的第一道题，拼接到该类型其他试题的尾巴上（链表接上） */
                if (firstQuestionId == null){
                    firstQuestionId = currExamQuestionId;
                }

                EduExamQuestion examQuestion = BeanTransUtil.copy(storeQuestion, EduExamQuestion.class, "id", "nextQuestionId", "createBy", "createTime", "updateBy", "updateTime");
                examQuestion.setId(currExamQuestionId);
                examQuestion.setExamId(examId);
                examQuestion.setQuestionStoreId(storeQuestion.getId());
                examQuestion.setNextExamQuestionId(nextExamQuestionId);
                examQuestionList.add(examQuestion);

                totalScore += storeQuestion.getScore();
                difficultyLevels += storeQuestion.getDifficultyLevel();
            }
            this.updateExamLastQuestionNextId(examId, questionEntry.getKey(), firstQuestionId);
        }
        this.saveBatch(examQuestionList);

        /* 重新计算试卷难度等级、总分数 */
        examService.updateTotalScoreAndDifficultyLevel(exam.getId(), totalScore, (exam.getDifficultyLevel() + difficultyLevels / storeQuestionIds.length) / 2);
    }

    /**
     * 将某试卷的所有试题导入到该考核
     *  @param examId  考核ID
     * @param paperId 试卷ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importByPaperQuestion(Long examId, Long paperId) {
        /* 检查考核状态  TODO */
        EduExam exam = examService.selectEduExamById(examId);
        AssertUtil.isNull(exam, "考核信息不存在：" + examId);

        examService.checkExamOperateAllowable(examId, exam, Boolean.TRUE);
        EduPaperStore storePaper = paperStoreService.selectEduPaperStoreById(paperId);
        AssertUtil.isNull(storePaper, "试卷信息不存在：" + paperId);

        EduExamQuestionForPaperQueryForm queryForm = new EduExamQuestionForPaperQueryForm();
        queryForm.setPaperId(paperId);

        List<EduQuestionStore> questionStoreList = paperStoreService.selectPaperQuestionList(queryForm);
        AssertUtil.isEmpty(questionStoreList, "试卷【" + storePaper.getTitle() + "】下没有添加任何试题，请先去添加试题");

        List<EduExamQuestion> examQuestionList = new ArrayList<>(questionStoreList.size());
        List<String> excludeStoreQuestionList = new ArrayList<>();
        Integer totalScore = exam.getTotalScore();
        Integer difficultyLevels = 0;
        Map<QuestionTypeEnum, List<EduQuestionStore>> questionGroupMap = questionStoreList.stream().collect(Collectors.groupingBy(EduQuestionStore::getQuestionType));
        for (Map.Entry<QuestionTypeEnum, List<EduQuestionStore>> questionEntry : questionGroupMap.entrySet()) {
            List<EduQuestionStore> questions = questionEntry.getValue();
            /* 当前试题ID */
            Long currExamQuestionId;
            /* 提前生成下一道题的ID */
            Long nextExamQuestionId = IdWorker.getId();
            /* 新增的该类型所有题中，获取第一道题，用于拼接排序链表 */
            Long firstQuestionId = null;
            for (int i = 0; i < questions.size(); i++) {
                EduQuestionStore storeQuestion = questions.get(i);
                Optional.ofNullable(questionStoreService.selectEduQuestionStoreById(storeQuestion.getId()))
                        .orElseThrow(() -> new CustomException("题库试题信息不存在：" + storeQuestion.getId()));
                if (this.baseMapper.checkExamQuestionUnique(examId, storeQuestion.getId())){
                    /* 排除已经存在的试题 */
                    excludeStoreQuestionList.add(storeQuestion.getQuestion());
                    continue;
                }
                currExamQuestionId = nextExamQuestionId;
                /* 最后一道题的指向是NULL */
                nextExamQuestionId = (i == questions.size() - 1) ? null : IdWorker.getId();

                /* 获取该试题类型下新增这一批试题的第一道题，拼接到该类型其他试题的尾巴上（链表接上） */
                if (firstQuestionId == null){
                    firstQuestionId = currExamQuestionId;
                }

                EduExamQuestion examQuestion = BeanTransUtil.copy(storeQuestion, EduExamQuestion.class, "id", "nextQuestionId", "createBy", "createTime", "updateBy", "updateTime");
                examQuestion.setId(currExamQuestionId);
                examQuestion.setExamId(examId);
                examQuestion.setQuestionStoreId(storeQuestion.getId());
                examQuestion.setNextExamQuestionId(nextExamQuestionId);
                examQuestionList.add(examQuestion);

                totalScore += storeQuestion.getScore();
                difficultyLevels += storeQuestion.getDifficultyLevel();
            }
            this.updateExamLastQuestionNextId(examId, questionEntry.getKey(), firstQuestionId);
        }

        /* 重新计算试卷难度等级、总分数 */
        Integer newDifficultyLevel = (exam.getDifficultyLevel() + difficultyLevels / questionStoreList.size()) / 2;
        examService.updateTotalScoreAndDifficultyLevel(exam.getId(), totalScore, newDifficultyLevel);

        if (CollectionUtils.isNotEmpty(examQuestionList)){
            this.saveBatch(examQuestionList);
        }
    }


    /**
     * 为考核试卷试题排序（页面上/下拖拽）
     *
     * @param opt 操作类型：UP（拖拽元素向上移动）、DOWM（拖拽元素向下移动）
     * @param fromId 被拖拽元素ID
     * @param toId 拖拽目标位置元素ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeExamQuestionSort(String opt, Long fromId, Long toId){
        AssertUtil.equels(fromId, toId, "请正确移动试题排序");
        EduExamQuestion fromQuestion = Optional.ofNullable(this.selectEduExamQuestionById(fromId))
                .orElseThrow(() -> new CustomException("试题信息不存在：" + fromId));
        EduExamQuestion toQuestion = Optional.ofNullable(this.selectEduExamQuestionById(toId))
                .orElseThrow(() -> new CustomException("试题信息不存在：" + toId));
        AssertUtil.notEquels(fromQuestion.getQuestionType(), toQuestion.getQuestionType(), "试题类型不匹配，操作失败");
        AssertUtil.notEquels(fromQuestion.getExamId(), toQuestion.getExamId(), "调换的试题不在同一份考核试卷下，操作失败");

        examService.checkExamOperateAllowable(fromQuestion.getExamId(), null, Boolean.TRUE);

        if ("UP".equals(opt)){
            /* 一定要先查出来下面两条，否则数据更改后导致后面查询结果不一致 */
            EduExamQuestion fromPrevQuestion = this.selectPrevEduExamQuestion(fromId);
            EduExamQuestion toPrevQuestion = this.selectPrevEduExamQuestion(toId);
            /** 更改拖拽元素的上个节点指向，改为指向拖拽元素的下个元素，即：
             *       拖拽前： B → C（拖拽元素） → D
             *       拖拽后： B → E，
             */
            if (fromPrevQuestion != null){
                this.updateExamQuestionNextId(fromPrevQuestion.getId(), fromQuestion.getNextExamQuestionId());
            }
            /** 更改原本指向目标位置元素的节点其指向，改为指向拖拽元素，即：
             *       拖拽前： A → B（目标位置） → C（拖拽元素） → D
             *       拖拽后： A → C（拖拽元素），
             */
            if (toPrevQuestion != null){
                this.updateExamQuestionNextId(toPrevQuestion.getId(), fromId);
            }
            /** 更改拖拽节点的指向，改为指向目标元素，即：
             *       拖拽前： B（目标位置） → C（拖拽元素） → D
             *       拖拽后： C（拖拽元素） → B（目标位置），
             */
            this.updateExamQuestionNextId(fromId, toId);
        } else if ("DOWM".equals(opt)){
            /* 一定要先查出来下面两条，否则后面数据更改后查询结果不一致 */
            EduExamQuestion fromPrevQuestion = this.selectPrevEduExamQuestion(fromId);
            /** 更改拖拽元素的上个节点指向，改为指向拖拽元素的下个元素，即：
             *       拖拽前： B → C（拖拽元素） → D
             *       拖拽后： B → D，
             */
            if (fromPrevQuestion != null){
                this.updateExamQuestionNextId(fromPrevQuestion.getId(), fromQuestion.getNextExamQuestionId());
            }
            /** 更改目标位置元素的指向，改为指向拖拽元素，即：
             *       拖拽前： A → B（目标位置） → C（拖拽元素） → D
             *       拖拽后： A → C（拖拽元素），
             */
            this.updateExamQuestionNextId(toId, fromId);
            /** 更改拖拽节点的指向，改为原目标位置元素的指向，即：
             *       拖拽前： A → B（拖拽元素） → C（目标位置） → D
             *       拖拽后： A → C（目标位置） → B（拖拽元素），
             */
            this.updateExamQuestionNextId(fromId, toQuestion.getNextExamQuestionId());
        } else {
            throw new CustomException("未知的操作类型：" + opt);
        }
    }


}
