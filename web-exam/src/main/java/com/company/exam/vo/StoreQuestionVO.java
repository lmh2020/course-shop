package com.company.exam.vo;

import com.company.exam.dto.QuestionAnswersDTO;
import com.company.exam.service.base.QuestionSortable;
import com.company.common.core.domain.BaseEntity;
import com.company.common.enums.business.QuestionTypeEnum;
import lombok.Data;

import java.util.Collection;
import java.util.Objects;

/**
 * 考试试卷题目对象VO edu_exam_question
 */
@Data
public class StoreQuestionVO extends BaseEntity implements QuestionSortable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 考核方向
     */
    private String questionCategoryCode;

    /**
     * 题目
     */
    private String question;

    /**
     * 试题类型
     */
    private QuestionTypeEnum questionType;

    /** 答案 */
    private Collection<QuestionAnswersDTO> answers;

    /** 正确答案(answers里的key) */
    private String[] correctAnswer;

    /**
     * 答案分析
     */
    private String analysis;


    /**
     * 难度
     */
    private Integer difficultyLevel;

    /** 题目总使用次数 */
    private Integer totalUsedCount;

    /** 达标/正确次数 */
    private Integer totalReachedCount;

    /**
     * 分值
     */
    private Integer score;

    /**
     * 版本号（每次修改会递增）
     */
    private Integer version;

    /**
     * 排在当前试题下一位的试题ID（做试卷试题排序用）
     */
    private Long nextQuestionId;

    /**
     * 是否已被添加到某试卷（Y代表存在 N代表未添加）
     */
    private String added;

    /**
     * 排序
     */
    private Integer sort;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoreQuestionVO)) return false;
        StoreQuestionVO that = (StoreQuestionVO) o;
        return this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    @Override
    public void setSortIndex(Integer sortIndex) {
        this.setSort(sortIndex);
    }

    /**
     * @description 获取试题唯一标识
     */
    @Override
    public Long getUniqueKey() {
        return this.getId();
    }

    /**
     * @description 获取当前同一类型试题的下一个试题ID
     */
    @Override
    public Long getNextKey() {
        return this.getNextQuestionId();
    }

}
