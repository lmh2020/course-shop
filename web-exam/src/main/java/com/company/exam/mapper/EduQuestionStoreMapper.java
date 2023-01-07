package com.company.exam.mapper;

import java.util.List;

import com.company.common.core.mybaits.CustomBaseMapper;
import com.company.exam.domain.EduExam;
import com.company.exam.domain.EduQuestionStore;
import org.apache.ibatis.annotations.Select;

/**
 * 考试试卷题目Mapper接口
 */
public interface EduQuestionStoreMapper extends CustomBaseMapper<EduQuestionStore> {

    /**
     * 获取所有引用该试题的试卷
     */
    @Select("SELECT exam.* FROM edu_exam exam LEFT JOIN edu_exam_question exam_question ON exam_question.exam_id = exam.id " +
            "WHERE exam.del_flag = '0' AND exam_question.question_store_id = #{questionId} GROUP BY exam.id")
    List<EduExam> getExamQuestionReferredExams(String questionId);

}
