package com.company.exam.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description  某个试题 用户提交的答案
 */
@Data
public class UserAnswer {

    @NotNull(message = "考核试题编号不能为空")
    private Long examQuestionId;

    private String[] answer;

}
