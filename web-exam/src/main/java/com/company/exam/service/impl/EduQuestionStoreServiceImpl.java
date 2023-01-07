package com.company.exam.service.impl;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.exam.domain.EduQuestionStore;
import com.company.exam.mapper.EduQuestionStoreMapper;
import com.company.exam.service.IEduQuestionStoreService;
import com.company.common.enums.base.ICommonEnum;
import com.company.common.enums.business.QuestionTypeEnum;
import com.company.common.exception.CustomException;
import com.company.common.utils.AssertUtil;
import com.company.common.utils.StringUtils;
import com.company.common.utils.bean.BeanTransUtil;
import com.company.exam.domain.EduExam;
import com.company.exam.form.EduQuestionStoreMergeForm;
import com.company.exam.form.EduQuestionStoreQueryForm;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.CharUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 考试试卷题目Service业务层处理
 */
@Service
public class EduQuestionStoreServiceImpl extends ServiceImpl<EduQuestionStoreMapper, EduQuestionStore> implements IEduQuestionStoreService {

    /**
     * 查询考试试卷题目
     *
     * @param id 考试试卷题目ID
     * @return 考试试卷题目
     */
    @Override
    public EduQuestionStore selectEduQuestionStoreById(Long id){
        return this.getById(id);
    }

    /**
     * 查询考试试卷题目[列表]
     */
    @Override
    public List<EduQuestionStore> selectEduQuestionStoreList(EduQuestionStoreQueryForm queryForm){
        String[] questionCategoryCodes = null;
        if (StringUtils.isNotBlank(queryForm.getQuestionCategoryCodes())){
            questionCategoryCodes = queryForm.getQuestionCategoryCodes().split(",");
        }
        return this.list(new QueryWrapper<EduQuestionStore>().lambda()
            .in(ArrayUtils.isNotEmpty(questionCategoryCodes), EduQuestionStore::getQuestionCategoryCode, questionCategoryCodes)
            .like(StringUtils.isNotBlank(queryForm.getQuestion()), EduQuestionStore::getQuestion, queryForm.getQuestion())
            .eq(queryForm.getQuestionType() != null, EduQuestionStore::getQuestionType, queryForm.getQuestionType())
            .eq(queryForm.getDifficultyLevel() != null, EduQuestionStore::getDifficultyLevel, queryForm.getDifficultyLevel())
            .eq(queryForm.getScore() != null, EduQuestionStore::getScore, queryForm.getScore())
            .orderByDesc(EduQuestionStore::getCreateTime)
        );
    }

    /**
     * 获取所有引用了该试题的试卷信息
     */
    @Override
    public List<EduExam> getExamQuestionReferredExams(String questionId){
        return this.baseMapper.getExamQuestionReferredExams(questionId);
    }

    /**
     * 根据ID批量获取
     */
    @Override
    public List<EduQuestionStore> getByIds(Long[] ids){
        return this.list(new QueryWrapper<EduQuestionStore>().lambda().in(EduQuestionStore::getId, ids));
    }


    /**
     * 新增考试试卷题目
     *
     * @param mergeForm 考试试卷题目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertEduQuestionStore(EduQuestionStoreMergeForm mergeForm){
        QuestionTypeEnum questionTypeEnum = ICommonEnum.assertContainsAndGet(QuestionTypeEnum.class, mergeForm.getQuestionType());
        this.checkQuestionBegerMerge(mergeForm, questionTypeEnum);

        EduQuestionStore eduQuestionStore = EduQuestionStore.builder()
                .analysis(mergeForm.getAnalysis())
                .answers(CollectionUtils.isEmpty(mergeForm.getAnswers()) ? null : JSON.toJSONString(mergeForm.getAnswers()))
                .correctAnswer(CollectionUtils.isEmpty(mergeForm.getCorrectAnswer()) ? null : JSON.toJSONString(mergeForm.getCorrectAnswer()))
                .difficultyLevel(mergeForm.getDifficultyLevel())
                .question(mergeForm.getQuestion())
                .questionCategoryCode(mergeForm.getQuestionCategoryCode())
                .questionType(questionTypeEnum)
                .score(mergeForm.getScore())
                .totalReachedCount(0)
                .totalUsedCount(0)
                .version(0)
                .build();
        this.save(eduQuestionStore);
    }

    /**
     * 修改考试试卷题目
     *
     * @param mergeForm 考试试卷题目
     * @return 结果
     */
    @Override
    public void updateEduQuestionStore(EduQuestionStoreMergeForm mergeForm){
        EduQuestionStore examQuestion = Optional.ofNullable(this.getById(mergeForm.getId())).orElseThrow(() -> new CustomException("试卷信息不存在"));

        QuestionTypeEnum questionTypeEnum = ICommonEnum.assertContainsAndGet(QuestionTypeEnum.class, mergeForm.getQuestionType());
        this.checkQuestionBegerMerge(mergeForm, questionTypeEnum);

        BeanTransUtil.copy(mergeForm, examQuestion, "answers", "correctAnswer", "questionType");

        examQuestion.setAnswers(CollectionUtils.isEmpty(mergeForm.getAnswers()) ? null : JSON.toJSONString(mergeForm.getAnswers()));
        examQuestion.setCorrectAnswer(CollectionUtils.isEmpty(mergeForm.getCorrectAnswer()) ? null : JSON.toJSONString(mergeForm.getCorrectAnswer()));
        examQuestion.setQuestionType(questionTypeEnum);
        examQuestion.setTotalReachedCount(0);
        examQuestion.setTotalUsedCount(0);
        examQuestion.setVersion(examQuestion.getVersion() + 1);
        this.updateById(examQuestion);
    }


    /**
     * @description 新增/修改试题前进行各种检查
     */
    private void checkQuestionBegerMerge(EduQuestionStoreMergeForm mergeForm, QuestionTypeEnum questionTypeEnum){
        List<EduQuestionStoreMergeForm.ChoiceAnswer> answers = mergeForm.getAnswers();
        Set<String> correctAnswer = mergeForm.getCorrectAnswer();
        switch (questionTypeEnum){
            case SINGLE_CHOICE:
            case MULTIPLE_CHOICE:
            case JUDGE:
                AssertUtil.isTrue(questionTypeEnum != QuestionTypeEnum.JUDGE && (answers == null || answers.size() < 2),
                        "可选答案不能少于2项");
                AssertUtil.isEmpty(correctAnswer, "正确答案不能为空");
                if (questionTypeEnum == QuestionTypeEnum.MULTIPLE_CHOICE){
                    AssertUtil.isTrue(correctAnswer.size() < 2, "多选题正确答案不能少于两项");
                } else {
                    AssertUtil.isFalse(correctAnswer.size() == 1, "正确答案不能多于一项");
                }

                List<Character> choiceKeys = new ArrayList<>(answers.size());
                if (QuestionTypeEnum.JUDGE == questionTypeEnum){
                    if(CollectionUtils.isEmpty(answers)){
                        answers = new ArrayList<>();
                    }
                    /* 判断题的可选答案系统自动指定 */
                    Collections.addAll(answers, new EduQuestionStoreMergeForm.ChoiceAnswer('A', "对"), new EduQuestionStoreMergeForm.ChoiceAnswer('B', "错"));
                    mergeForm.setAnswers(answers);
                    Collections.addAll(choiceKeys, 'A', 'B');
                } else {
                    AssertUtil.notEquels(answers.get(0).getKey(), 'A', "可选答案不是从A开始");
                    choiceKeys.add(answers.get(0).getKey());
                    for (int i = 1; i < answers.size(); i++) {
                        EduQuestionStoreMergeForm.ChoiceAnswer answer = answers.get(i);
                        AssertUtil.isFalse(CharUtils.isAsciiAlphaUpper(answer.getKey()), "可选答案标识不是大写字母：" + answer.getKey());
                        AssertUtil.isFalse(answer.getKey() - answers.get(i - 1).getKey() == 1, "可选答案标识不是连续的大写字母");
                        choiceKeys.add(answer.getKey());
                    }
                }
                AssertUtil.isTrue(correctAnswer.stream().anyMatch(answer -> !choiceKeys.contains(answer.charAt(0))), "正确答案" + JSON.toJSONString(correctAnswer) + "标识不存在于答案标识中");
                break;
//            case FILL_INPUT:
//                AssertUtil.isFalse(correctAnswer.size() == 0 || correctAnswer.size() == StringUtils.getChildCharCount(mergeForm.getQuestion(), "${i}"),
//                        "填空个数与给定答案个数不一致");
//                mergeForm.setAnswers(null);
//                break;
        }
    }

    /**
     * 批量删除考试试卷题目
     *
     * @param ids 需要删除的考试试卷题目ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEduQuestionStoreByIds(Long[] ids){
        AssertUtil.isEmpty(ids, "编号不能为空");
        this.removeByIds(Arrays.asList(ids));
    }

    /**
     * 删除考试试卷题目信息
     *
     * @param id 考试试卷题目ID
     */
    @Override
    public void deleteEduQuestionStoreById(Long id){
        this.removeById(id);
    }


}
