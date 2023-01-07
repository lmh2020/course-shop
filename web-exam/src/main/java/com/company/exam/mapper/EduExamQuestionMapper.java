package com.company.exam.mapper;

import com.company.common.core.mybaits.CustomBaseMapper;
import com.company.exam.domain.EduExamQuestion;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 考试试卷题目Mapper接口
 */
public interface EduExamQuestionMapper extends CustomBaseMapper<EduExamQuestion>{

    @Select("SELECT CASE COUNT(1) WHEN 0 THEN 0 ELSE 1 END FROM edu_exam_question WHERE exam_id = #{examId} AND question_store_id = #{storeQuestionId} LIMIT 1")
    Boolean checkExamQuestionUnique(@Param("examId") Long examId, @Param("storeQuestionId") Long storeQuestionId);

    @Select("SELECT q1.* FROM edu_exam_question q1 WHERE q1.id = (SELECT q2.id FROM edu_exam_question q2 WHERE q2.id = #{id}) AND q1.del_flag = '0' LIMIT 1")
    EduExamQuestion selectNextEduExamQuestion(Long id);

    @Select("select COUNT(1) FROM edu_exam_question WHERE exam_id = #{exmaId} AND del_flag = '0'")
    Long getExamQuestionCount(Long exmaId);
}
