package com.company.exam.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.company.exam.service.base.QuestionSortable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.company.common.annotation.Excel;
import com.company.common.core.domain.BaseEntity;
import com.company.common.enums.business.QuestionTypeEnum;
import lombok.Data;

import java.util.Objects;

/**
 * 考试试卷题目对象 edu_exam_question
 */
@Data
public class EduExamQuestion extends BaseEntity implements QuestionSortable {

    /** 主键 */
    @TableId
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    /** 试卷ID */
    private Long examId;

    /** 题库试题ID */
    private Long questionStoreId;

    /**
     * 排在当前通类型试题的下一位的试题ID（做试卷试题排序用）
     */
    private Long nextExamQuestionId;

    /** 题目考核方向（类型） */
    @Excel(name = "题目考核方向", readConverterExp = "类=型")
    private String questionCategoryCode;

    /** 题目 */
    @Excel(name = "题目")
    private String question;

    /** 试题类型 */
    @Excel(name = "试题类型")
    private QuestionTypeEnum questionType;

    /** 答案 */
    private String answers;

    /** 正确答案 */
    private String correctAnswer;

    /** 答案分析 */
    private String analysis;

    /** 困难等级（1~5） */
    @Excel(name = "困难等级", readConverterExp = "1=~5")
    private Integer difficultyLevel;

    /** 分值 */
    @Excel(name = "分值")
    private Integer score;

    /** 版本号（对应题库中的版本号） */
    @Excel(name = "版本号", readConverterExp = "对应题库中的版本号")
    private Integer version;

    /** 删除标志（0代表存在 2代表删除） */
    @TableLogic
    private String delFlag;

    /**
     * 排序
     */
    @TableField(exist = false)
    private Integer sort;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EduExamQuestion)) return false;
        EduExamQuestion that = (EduExamQuestion) o;
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
     * @description 获取当前同一类型试题的下一个试题ID。注意：这里使用的是题库试题ID作为排序的唯一标识，因为nextStoreQuestionId取的是题库试题ID
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
        return this.getNextExamQuestionId();
    }

}
