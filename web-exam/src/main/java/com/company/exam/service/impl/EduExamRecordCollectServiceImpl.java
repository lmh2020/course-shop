package com.company.exam.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.exam.domain.EduExamRecordCollect;
import com.company.exam.domain.EduExamRecordSection;
import com.company.exam.mapper.EduExamRecordCollectMapper;
import com.company.exam.service.IEduExamQuestionService;
import com.company.exam.service.IEduExamRecordCollectService;
import com.company.exam.service.IEduExamRecordSectionService;
import com.company.exam.service.IEduExamService;
import com.company.common.constant.CacheKeyConstants;
import com.company.common.core.domain.entity.SysUser;
import com.company.common.core.redis.RedisCache;
import com.company.common.enums.CommonBooleanEnum;
import com.company.common.enums.business.*;
import com.company.common.exception.CustomException;
import com.company.common.exception.SystemException;
import com.company.common.utils.*;
import com.company.exam.common.properties.EduExamConfig;
import com.company.exam.domain.EduExam;
import com.company.exam.form.EduUserExamineQueryForm;
import com.company.exam.form.SubmitExamineForm;
import com.company.exam.vo.ExamQuestionVO;
import com.company.exam.vo.UserExamineRecordVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 */
@Slf4j
@Service
public class EduExamRecordCollectServiceImpl extends ServiceImpl<EduExamRecordCollectMapper, EduExamRecordCollect> implements IEduExamRecordCollectService {

    @Autowired
    private EduExamConfig examConfig;

    @Lazy
    @Autowired
    private IEduExamService examService;

    @Autowired
    private IEduExamQuestionService examQuestionService;

    @Autowired
    private IEduExamRecordSectionService examRecordSectionService;

    @Autowired
    private RedisCache redisCache;

    @Override
    public EduExamRecordCollect getEduExamRecordCollectById(Long examRecordId){
        return this.getById(examRecordId);
    }

    /**
     * 查询【我的】所有考核列表
     *
     * @param queryForm 查询表单
     * @return 考核
     */
    @Override
    public List<UserExamineRecordVO> selectExamRecordCollectList(EduUserExamineQueryForm queryForm){
        return this.baseMapper.selectExamRecordCollectList(queryForm);
    }


    /**
     * @description 获取考核详情（用于考试）
     */
    @Override
    public UserExamineRecordVO selectExamineDetail(Long recordCollectId){
        return this.baseMapper.selectExamineDetail(recordCollectId);
    }

    /**
     * @description 获取所有报名了某考核的记录信息
     */
    @Override
    public List<EduExamRecordCollect> selectExamEnrolledRecordList(Long examId){
        return this.list(new QueryWrapper<EduExamRecordCollect>().lambda()
            .eq(EduExamRecordCollect::getExamId, examId)
            .in(EduExamRecordCollect::getState, ExamRecordStateEnum.AWAITING, ExamRecordStateEnum.UNDERWAY));
    }

    /**
     * @description 考核报名
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enrollExam(Long examId, Boolean noticeEnable){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        /* 先获取要报名的考核信息 */
        EduExam exam = Optional.ofNullable(examService.selectEduExamById(examId))
                .orElseThrow(() -> new CustomException("考核信息不存在"));

        /* 先检查是否允许报名 */
        this.checkExam(exam, user.getUserId());

        /* 预存储考核基本信息、试题信息 */
        this.saveExamEnrollRecord(examId, user.getUserId(), noticeEnable);
    }


    /**
     * @description 保存考核报名信息
     */
    private void saveExamEnrollRecord(Long examId, Long userId, Boolean noticeEnable){
        /* 查询该考核所有试题 */
        List<ExamQuestionVO> questionList = examQuestionService.selectEduExamQuestionList(examId);
        AssertUtil.isEmpty(questionList, "该考核试卷试题为空！请联系管理员");

        /* 预生成考核记录基础汇总表信息 */
        EduExamRecordCollect recordCollect = EduExamRecordCollect.builder()
                .userId(userId)
                .examId(examId)
                .enrollTime(LocalDateTime.now())
                .noticeEnable(BooleanUtils.isTrue(noticeEnable))
                .state(ExamRecordStateEnum.AWAITING)
                .build();
        this.save(recordCollect);

        /* 预存储所有试题的考核记录基本信息 */
        List<EduExamRecordSection> recordSectionList = questionList.stream()
                .map(examQuestion -> EduExamRecordSection.builder()
                        .recordCollectId(recordCollect.getId())
                        .examQuestionId(examQuestion.getId())
                        .build())
                .collect(Collectors.toList());
        examRecordSectionService.batchSaveEduExamRecordSection(recordSectionList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdate(Collection<EduExamRecordCollect> recordCollects){
        this.updateBatchById(recordCollects);
    }


    /**
     * @description 更新状态
     */
    @Override
    public void updateState(Long recordCollectId, ExamRecordStateEnum newState){
        this.update(new UpdateWrapper<EduExamRecordCollect>().lambda()
            .set(EduExamRecordCollect::getState, newState)
            .eq(EduExamRecordCollect::getId, recordCollectId));
    }


    /**
     * @description 考试前检查一下  是否允许考试
     */
    @Override
    public void checkBeforeExamine(Long recordCollectId){
        EduExamRecordCollect examRecordCollect = Optional.ofNullable(this.getEduExamRecordCollectById(recordCollectId))
                .orElseThrow(() -> new CustomException("考核报名信息不存在"));
        if (ExamRecordStateEnum.AWAITING != examRecordCollect.getState() && ExamRecordStateEnum.UNDERWAY != examRecordCollect.getState()){
            throw new CustomException("考核" + examRecordCollect.getState().getDesc());
        }

        EduExam exam = Optional.ofNullable(examService.selectEduExamById(examRecordCollect.getExamId()))
                .orElseThrow(() -> new CustomException("考核信息不存在"));
        AssertUtil.notEquels(ExamStateEnum.NORMAL, exam.getState(), "考核状态为：" + exam.getState().getDesc());
    }


    /**
     * @description 撤销考核报名
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeExam(Long recordCollectId){
        EduExamRecordCollect recordCollect = Optional.ofNullable(this.getEduExamRecordCollectById(recordCollectId))
                .orElseThrow(() -> new CustomException("报名信息不存在"));
        Optional.ofNullable(examService.selectEduExamById(recordCollect.getExamId()))
                .orElseThrow(() -> new CustomException("考核信息不存在"));
        /* 直接删除子考核记录的试题信息 */
        examRecordSectionService.clearExamRecordSections(recordCollectId);

        /* 删除报名记录 */
        this.removeById(recordCollectId);

        /* 清除本地缓存答案 */
        this.clearUserAnswersCache(SecurityUtils.getLoginUserId());
    }


    /**
     * @description 提交/定时保存试卷
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitExamine(SubmitExamineForm form) {
        String opt = CommonBooleanEnum.TRUE == form.getAutomaticSubmit() ? "自动" : "";
        EduExamRecordCollect recordCollect = Optional.ofNullable(this.getEduExamRecordCollectById(form.getRecordCollectId()))
                .orElseThrow(() -> new SystemException("未找到该考核[" + form.getRecordCollectId() + "]的报名记录，请通知管理员"));
        /* 1. 判断是否已经提交过试卷 */
        AssertUtil.notEquels(ExamRecordStateEnum.UNDERWAY, recordCollect.getState(), opt + "提交失败！当前考核状态为：" + recordCollect.getState().getDesc());

        /* 2. 先判断交卷时间是否允许 */
        EduExam exam = examService.selectEduExamById(recordCollect.getExamId());

        /* 3. 如果是自动交卷操作，则再更新一下用户答案缓存 */
        if (CommonBooleanEnum.TRUE == form.getAutomaticSubmit()){
            this.cacheUserAnswers(SecurityUtils.getLoginUserId(), form.getUserAnswers());
        }

        /* 4. 计算客观题得分，并保存答案信息， */
        Integer totalObjectiveScore = this.correctExamQuestions(exam.getId(), recordCollect.getId(), form.getUserAnswers());

        /* 5. 保存考核记录基本信息 */
        recordCollect.setFinishTime(LocalDateTime.now());
        recordCollect.setState(ExamRecordStateEnum.FINISHED);
        recordCollect.setScore(totalObjectiveScore);
        this.updateById(recordCollect);
    }


    /**
     * @description 系统自动批改主观试题（单选、多选、判断）
     * @return Integer  返回客观题总得分
     */
    private Integer correctExamQuestions(Long examId, Long recordCollectId, Map<Long, List<String>> userAnswers){
        /* 获取当前考核的所有试卷试题信息，用于批改试卷 */
        List<ExamQuestionVO> questionList = examQuestionService.selectEduExamQuestionList(examId);

        /* 获取用户上一次提交保存的答案 */
        Map<Long, List<String>> prevUserAnswers = this.getUserAnswersCache(SecurityUtils.getLoginUserId());
        /* 不为空，非第一次保存，去和上一次保存的答案对比，找出发生变更的答案保存进数据库 */
        List<EduExamRecordSection> recordSectionList = new ArrayList<>();
        /* 客观题总得分 */
        Integer totalObjectiveScore = 0;
        for (Map.Entry<Long, List<String>> newAnswer : userAnswers.entrySet()) {
            ExamQuestionVO examQuestion = questionList.stream().filter(q -> q.getId().equals(newAnswer.getKey())).findFirst().get();
            Collection<String> oldAnswer = prevUserAnswers.get(newAnswer.getKey());

            /* 第一次保存或与上一次答案不一样 */
            if (!ListUtils.isEqualList(oldAnswer, newAnswer.getValue())){
                EduExamRecordSection recordSection = new EduExamRecordSection();
                /* 只批改支持自动批改的试题，其他的将后续由老师手动批改（调用其他接口） */
                if(examQuestion.getQuestionType().getAutoCorrect()){
                    this.correctQuestionUserAnswer(examQuestion.getCorrectAnswer(), newAnswer.getValue(), recordSection, examQuestion.getScore());
                    totalObjectiveScore += recordSection.getScore();
                }
                recordSection.setRecordCollectId(recordCollectId);
                recordSection.setExamQuestionId(newAnswer.getKey());
                recordSection.setAnswer(CollectionUtils.isEmpty(newAnswer.getValue()) ? null : JSON.toJSONString(newAnswer.getValue()));
                recordSectionList.add(recordSection);
            }
        }
        if (recordSectionList.size() > 0){
            examRecordSectionService.batchUpdateEduExamRecordSection(recordSectionList);
        }
        return totalObjectiveScore;
    }



    /**
     * @description 自动批改用户答案（客观题）
     * 批改规则（不管是什么题型，答案都规范为数组格式（且不管是否填写答案，数组长度都固定））：
     *      1. 不管任何题型，只要用户答案为空，则判定为错误答案，0分；
     *      2. 如果正确答案与用户答案数组长度不一致（一般不会发送，可能是程序错误），则错误并给0分；
     *      3. 如果部分答案正确（比如多选题、填空题），按正确比例给分；
     *      4. 如果用户答案全错，则给0分；
     */
    private void correctQuestionUserAnswer(Collection<String> collectAnswer, Collection<String> userAnswer, EduExamRecordSection recordSection, Integer totalScore){
        QuestionExamineResultEnum result;
        int score;
        if(CollectionUtils.isEmpty(userAnswer)){
            result = QuestionExamineResultEnum.INCORRECT;
            score = 0;
        } else if (collectAnswer.size() != userAnswer.size()){
            log.error("系统自动批改用户试卷时发生错误：系统答案：{} 与用户上传答案：{} 数组长度不一致");
            result = QuestionExamineResultEnum.INCORRECT;
            score = 0;
        } else {
            int correctCount = 0;
            Iterator<String> correctIterator = collectAnswer.iterator();
            Iterator<String> userIterator = userAnswer.iterator();
            while (correctIterator.hasNext()){
                if (Objects.equals(correctIterator.next(), userIterator.next())){
                    correctCount++;
                }
            }
            if (correctCount == collectAnswer.size()){
                result = QuestionExamineResultEnum.CORRECT;
                score = totalScore;
            } else if (correctCount == 0){
                result = QuestionExamineResultEnum.INCORRECT;
                score = 0;
            } else {
                result = QuestionExamineResultEnum.PART_CORRECT;
                score = totalScore * (correctCount / collectAnswer.size());
            }
        }
        recordSection.setScore(score);
        recordSection.setResult(result);
    }


    /**
     * 从缓存获取上一次保存的用户填写的答案
     */
    @Override
    public Map<Long, List<String>> getUserAnswersCache(Long userId){
        Map<Long, List<String>> userAnswers = redisCache.getCacheObject(CacheKeyConstants.USER_ANSWER + userId);
        return MapUtils.emptyIfNull(userAnswers);
    }

    /**
     * 缓存用户填写的答案
     */
    private void cacheUserAnswers(Long userId, Map<Long, List<String>> userAnswers){
        /* 过期时间默认设置为总考试时间的2倍（考试结束也会直接主动删除该缓存） */
        redisCache.setCacheObject(CacheKeyConstants.USER_ANSWER + userId, userAnswers, 60, TimeUnit.MINUTES);
    }

    /**
     * 清除用户填写的答案缓存
     */
    private void clearUserAnswersCache(Long userId){
        redisCache.deleteObject(CacheKeyConstants.USER_ANSWER + userId);
    }

    /**
     * @description 检查是否允许报名考核
     */
    private void checkExam(EduExam exam, Long userId){
        AssertUtil.notEquels(ExamStateEnum.NORMAL, exam.getState(), "操作失败！考核当前状态为：" + exam.getState().getDesc());
        /* 只要有未练习结束（未开考、考试中）的考核，都不准再次报名 */
        AssertUtil.isNotNull(this.getOne(new QueryWrapper<EduExamRecordCollect>().lambda()
                .eq(EduExamRecordCollect::getUserId, userId)
                .eq(EduExamRecordCollect::getExamId, exam.getId())
                .ne(EduExamRecordCollect::getState, ExamRecordStateEnum.FINISHED)
                .last("limit 1")), "操作失败！您在该考核下仍存在一条练习未结束");
    }


}
