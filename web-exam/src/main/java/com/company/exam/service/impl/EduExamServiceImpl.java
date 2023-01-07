package com.company.exam.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.company.common.exception.ServiceException;
import com.company.exam.mapper.EduExamMapper;
import com.company.exam.service.IEduExamQuestionService;
import com.company.exam.service.IEduExamRecordCollectService;
import com.company.exam.service.IEduExamService;
import com.company.common.enums.base.ICommonEnum;
import com.company.common.enums.business.*;
import com.company.common.exception.CustomException;
import com.company.common.utils.AssertUtil;
import com.company.common.utils.SecurityUtils;
import com.company.common.utils.bean.BeanTransUtil;
import com.company.exam.common.properties.EduExamConfig;
import com.company.exam.form.EduExamQuestionQueryForm;
import com.company.exam.common.utils.EduQuestionUtil;
import com.company.exam.vo.ExamQuestionVO;
import com.company.exam.vo.ExamineVO;
import com.company.exam.vo.UserExamineRecordVO;
import com.company.system.domain.Course;
import com.company.system.service.ICourseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.exam.domain.EduExam;
import com.company.exam.form.EduExamQueryForm;
import com.company.exam.form.EduExamMergeForm;

/**
 * 考核Service业务层处理
 */
@Slf4j
@Service
public class EduExamServiceImpl extends ServiceImpl<EduExamMapper, EduExam> implements IEduExamService {

    @Autowired
    private IEduExamQuestionService examQuestionService;

    @Autowired
    private IEduExamRecordCollectService examRecordCollectService;

    @Autowired
    private ICourseService courseService;

    /**
     * 查询考核
     *
     * @param id 考核ID
     * @return 考核
     */
    @Override
    public EduExam selectEduExamById(Long id){
        return this.getById(id);
    }

    /**
     * 查询所有考核列表
     *
     * @param queryForm 查询表单
     * @return 考核
     */
    @Override
    public List<EduExam> selectEduExamList(EduExamQueryForm queryForm){
        return this.baseMapper.selectEduExamList(queryForm);
    }

    @Override
    public Map<QuestionTypeEnum, List<ExamQuestionVO>> selectExamQuestionList(EduExamQuestionQueryForm queryForm){
        List<ExamQuestionVO> questionList = examQuestionService.selectEduExamQuestionList(queryForm);
        return this.groupAndSortExamQuestionList(questionList);
    }

    @Override
    public Map<QuestionTypeEnum, List<ExamQuestionVO>> selectExamQuestionList(Long examId){
        List<ExamQuestionVO> questionList = examQuestionService.selectEduExamQuestionList(examId);
        return this.groupAndSortExamQuestionList(questionList);
    }

    /**
     * 查询某考核的所有试题列表
     *
     * @param questionList 需要排序的试题
     * @return
     */
    private Map<QuestionTypeEnum, List<ExamQuestionVO>> groupAndSortExamQuestionList(List<ExamQuestionVO> questionList){
        if (CollectionUtils.isNotEmpty(questionList)) {
            Map<QuestionTypeEnum, List<ExamQuestionVO>> collect = questionList.stream().collect(Collectors.groupingBy(ExamQuestionVO::getQuestionType));
            for (Map.Entry<QuestionTypeEnum, List<ExamQuestionVO>> questionEntry : collect.entrySet()) {
                List<ExamQuestionVO> sortedList = EduQuestionUtil.sortStoreQuestions(questionEntry.getValue());
                collect.put(questionEntry.getKey(), sortedList);
            }
            return collect;
        }
        return null;
    }

    /**
     * 查询考试内容（试题信息）
     */
    @Override
    public ExamineVO selectExamineDetail(Long recordCollectId){
        UserExamineRecordVO examineRecord = Optional.ofNullable(examRecordCollectService.selectExamineDetail(recordCollectId))
                .orElseThrow(() -> new CustomException("考核信息不存在"));
        UserExamineRecordVO.RecordCollect recordCollect = examineRecord.getExamineRecord();
        AssertUtil.isNull(recordCollect, "没有找到您的报名记录");
        AssertUtil.notEquels(ExamStateEnum.NORMAL, examineRecord.getExamState(), "当前考核试卷不可用");

        ExamineVO examineVO = new ExamineVO();
        /* 说明是第一次请求，也就是正式开始考试，去更新考试进度状态 */
        if (ExamRecordStateEnum.AWAITING == recordCollect.getExamRecordState()){
            examRecordCollectService.updateState(recordCollectId, ExamRecordStateEnum.UNDERWAY);
        } else {
            if (ExamRecordStateEnum.UNDERWAY != recordCollect.getExamRecordState()) {
                throw new CustomException("操作失败！您报名的考核状态为：" + recordCollect.getExamRecordState().getDesc());
            }
            /* 加载用户已填写的答案（防止用户刷新页面丢失已答试题数据丢失） */
            Map<Long, List<String>> userAnswers = examRecordCollectService.getUserAnswersCache(SecurityUtils.getLoginUserId());
            if (MapUtils.isNotEmpty(userAnswers)){
                examineVO.setAnswersCache(userAnswers);
            }
        }
        examineVO.setExam(examineRecord);
        Map<QuestionTypeEnum, List<ExamQuestionVO>> questions = null;
        List<ExamQuestionVO> questionList = examQuestionService.selectEduExamQuestionList(examineRecord.getExamId());
        if (CollectionUtils.isNotEmpty(questionList)) {
            for (ExamQuestionVO examQuestion : questionList) {
                /* 去除考试答案信息 */
                examQuestion.setAnalysis(null);
                examQuestion.setCorrectAnswer(null);
            }
            questions = questionList
                    .stream()
                    .collect(Collectors.groupingBy(ExamQuestionVO::getQuestionType))/* 先根据试题类型分组 */
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.comparingInt(QuestionTypeEnum::getSort)))/* 根据试题类型排序 */
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));/* 重组（注意需要用LinkedHashMap） */
            for (Map.Entry<QuestionTypeEnum, List<ExamQuestionVO>> questionEntry : questions.entrySet()) {
                /* 试题排序 */
                List<ExamQuestionVO> sortedList = EduQuestionUtil.sortStoreQuestions(questionEntry.getValue());
                questions.put(questionEntry.getKey(), sortedList);
            }
        }
        examineVO.setQuestions(questions);
        return examineVO;
    }


    /**
     * @description 获取考试结果信息
     */
    @Override
    public ExamineVO selectExamineResult(Long recordCollectId){
        UserExamineRecordVO examineRecord = Optional.ofNullable(examRecordCollectService.selectExamineDetail(recordCollectId))
                .orElseThrow(() -> new CustomException("考核信息不存在"));
        UserExamineRecordVO.RecordCollect recordCollect = examineRecord.getExamineRecord();
        AssertUtil.isNull(recordCollect, "没有找到您的报名记录");
        AssertUtil.notEquels(ExamRecordStateEnum.FINISHED, recordCollect.getExamRecordState(), "查询失败！您的考核记录状态为：" + recordCollect.getExamRecordState().getDesc());

        ExamineVO examineVO = new ExamineVO();
        examineVO.setExam(examineRecord);

        Map<QuestionTypeEnum, List<ExamQuestionVO>> questions = null;
        List<ExamQuestionVO> questionList = examQuestionService.selectEduExamQuestionList(examineRecord.getExamId());
        if (CollectionUtils.isNotEmpty(questionList)) {
            questions = questionList
                    .stream()
                    .collect(Collectors.groupingBy(ExamQuestionVO::getQuestionType))/* 先根据试题类型分组 */
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.comparingInt(QuestionTypeEnum::getSort)))/* 根据试题类型排序 */
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));/* 重组（注意需要用LinkedHashMap） */
            for (Map.Entry<QuestionTypeEnum, List<ExamQuestionVO>> questionEntry : questions.entrySet()) {
                /* 试题排序 */
                List<ExamQuestionVO> sortedList = EduQuestionUtil.sortStoreQuestions(questionEntry.getValue());
                questions.put(questionEntry.getKey(), sortedList);
            }
        }
        examineVO.setQuestions(questions);
        return examineVO;
    }


    /**
     * 新增考核
     *
     * @param mergeForm 新增表单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertEduExam(EduExamMergeForm mergeForm){
        AssertUtil.isTrue(this.baseMapper.exist("title", mergeForm.getTitle()), "当前考核标题已存在");

        Course course = Optional.ofNullable(courseService.selectCourseById(mergeForm.getCourseId()))
                .orElseThrow(() -> new ServiceException("课程信息不存在"));

        /* 判断该课程是否已经绑定了考核 */
        AssertUtil.isNotNull(course.getExamId(), "该课程已经绑定了考核，请勿重复操作");

        EduExam eduExam = BeanTransUtil.copy(mergeForm, EduExam.class);
        eduExam.setTotalScore(0);
        eduExam.setCoverImg("https://qn.ee1.net.cn/timg.jpg");//TODO
        eduExam.setDifficultyLevel(0);
        eduExam.setState(ExamStateEnum.NORMAL);
        this.save(eduExam);

        course.setExamId(eduExam.getId());
        courseService.updateCourse(course);
    }

    /**
     * 修改考核
     *
     * @param mergeForm 修改表单
     * @return 结果
     */
    @Override
    public void updateEduExam(EduExamMergeForm mergeForm){
        AssertUtil.isNotNull(this.getOne(new QueryWrapper<EduExam>().lambda()
                .ne(EduExam::getId, mergeForm.getId())
                .eq(EduExam::getTitle, mergeForm.getTitle())
                .last("limit 1")), "当前考核标题已存在");
        EduExam exam = Optional.ofNullable(this.selectEduExamById(mergeForm.getId()))
                .orElseThrow(() -> new CustomException("考试信息不存在"));
        this.checkExamOperateAllowable(exam.getId(), exam, Boolean.TRUE);
        BeanTransUtil.copy(mergeForm, exam);
        exam.setCoverImg("https://qn.ee1.net.cn/timg.jpg");//TODO
        this.updateById(exam);
    }


    /**
     * @description 更新总分数、难度等级
     */
    @Override
    public void updateTotalScoreAndDifficultyLevel(Long examId, Integer totalScore, Integer difficultyLevel){
        this.update(new UpdateWrapper<EduExam>().lambda()
            .set(EduExam::getTotalScore, totalScore)
            .set(EduExam::getDifficultyLevel, difficultyLevel)
            .eq(EduExam::getId, examId));
    }

    /**
     * 批量删除考核
     *
     * @param ids 需要删除的考核ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEduExamByIds(Long[] ids){
        AssertUtil.isEmpty(ids, "编号不能为空");
        for (Long id : ids) {
            this.checkExamOperateAllowable(id, null, Boolean.TRUE);
        }
        this.removeByIds(Arrays.asList(ids));
    }

    /**
     * @description 校验某考核当前时间是否允许操作（如编辑、删除、新增/修改/删除试题...等）
     * 注：考核信息（包括考核试题）修改不当的话，可能会影响：
     *      1. 临近开考，修改考核试题没有经过详细审查，导致的导入的试题不合理等情况；
     *      2. 当前考核正在进行中，修改会导致影响考生考试结果；
     *      3. 考核已经结束，修改会导致成绩核对、错题回顾等；
     *   所以：只允许：
     */
    @Override
    public void checkExamOperateAllowable(Long examId, EduExam exam, Boolean deepCheck){
        AssertUtil.isNull(examId, "考核ID不能为空");
    }

    @Override
    public void updateState(Long examId, String newState) {
        ExamStateEnum examStateEnum = ICommonEnum.assertContainsAndGet(ExamStateEnum.class, newState);
        EduExam exam = Optional.ofNullable(this.selectEduExamById(examId))
                .orElseThrow(() -> new CustomException("考核信息不存在"));
        AssertUtil.equels(examStateEnum, exam.getState(), "操作失败！当前考核已经是[" + exam.getState().getDesc() + "]状态");
        this.checkExamOperateAllowable(examId, exam, Boolean.FALSE);
        exam.setState(examStateEnum);
        this.updateById(exam);
    }


}
