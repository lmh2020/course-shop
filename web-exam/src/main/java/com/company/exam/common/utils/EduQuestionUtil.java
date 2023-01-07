package com.company.exam.common.utils;

import com.company.exam.service.base.QuestionSortable;
import com.company.common.utils.AssertUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description  试题通用工具类
 */
public class EduQuestionUtil {

    /**
     * @description 对查询出的试题数据排序（链表）  TODO  下面的ArrayList结构可更换成LinkedList效率更高
     * 思路：① 先随意从试题数据中取一条作为双向遍历的起点，然后分别用两个指针双向（以选取的起点正向、反向分别去检索每一个节点）
     *      ② 得到两个集合，组合成一个集合即得到排好序的试题集合
     */
    public static <Q extends QuestionSortable> List<Q> sortStoreQuestions(List<Q> questionList){
        /* 初始化正向集合 */
        List<Q> nextNodes = new ArrayList<>(questionList.size() / 2);
        /* 初始化反向集合 */
        List<Q> prevNodes = new ArrayList<>(questionList.size() / 2);
        /* 随意取一个数作为遍历起点 */
        Q startNode = questionList.get(0);
        Q nextNode = startNode;
        Q prevNode = startNode;
        nextNodes.add(nextNode);

        /* 以选取的起点去正向遍历所有数据 */
        while (nextNode != null){
            Boolean found = false;
            for (Q question : questionList) {
                if (question.getUniqueKey().equals(nextNode.getUniqueKey())){
                    continue;
                } else if(nextNode.getNextKey() == null){
                    break;
                } else if (question.getUniqueKey().equals(nextNode.getNextKey())){
                    nextNode = question;
                    found = true;
                    break;
                }
            }
            /* 找到了下一个节点，直接放进集合 */
            if (found){
                /* 这先判断一下，当前找到的元素是否已经添加过，避免出现环形链表（可能是代码逻辑有BUG导致添加的排序链表不正确）导致OOM */
                AssertUtil.isTrue(nextNodes.contains(nextNode), "答案排序出现异常！请联系管理员");
                nextNodes.add(nextNode);
            } else {
                nextNode = null;
            }
        }

        /* 以选取的起点去反向遍历所有数据 */
        while (prevNode != null){
            Boolean found = false;
            for (Q question : questionList) {
                if (question.getUniqueKey().equals(prevNode.getUniqueKey())){
                    continue;
                }
                if (prevNode.getUniqueKey().equals(question.getNextKey())){
                    prevNode = question;
                    found = true;
                    break;
                }
            }
            if (found){
                /* 这先判断一下，当前找到的元素是否已经添加过，避免出现环形链表（可能是代码逻辑导致添加的排序链表不正确）导致OOM */
                AssertUtil.isTrue(prevNodes.contains(prevNode), "答案排序出现异常！请联系管理员");
                prevNodes.add(prevNode);
            } else {
                prevNode = null;
            }
        }
//        if (CollectionUtils.isNotEmpty(prevNodes)){
            /* 反向遍历得到的数据需要倒序一下 */
            Collections.reverse(prevNodes);
            /* 合并两个集合 */
            prevNodes.addAll(nextNodes);
            /* 设置题目序号 */
        for (int i = 0; i < prevNodes.size(); i++) {
            prevNodes.get(i).setSortIndex(i + 1);
        }
            return prevNodes;
//        }
//        return nextNodes;
    }

}
