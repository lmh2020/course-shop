package com.company.exam.service.base;


/**
 * @Description  试题排序处理器
 */
public interface QuestionSortable<Q> {

    void setSortIndex(Integer sortIndex);

    /**
     * @description 获取试题ID
     */
    Long getUniqueKey();

    /**
     * @description 获取当前同一类型试题的下一个试题ID
     */
    Long getNextKey();

    /**
     * @description 用于判断两个试题对象是否是同一个（判断ID是否一样即可）
     */
    default Boolean isSameQuestion(QuestionSortable<Q> question){
        return this.getUniqueKey().equals(question.getUniqueKey());
    }


}
