package com.company.system.form;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description
 * @Date 2021/4/5
 */
@Data
public class CourseOrderForm {

    /** 课程ID */
    @NotNull
    private Long courseId;

    /** 礼品 */
    @NotBlank
    private String gift;

    /** 发货地址 */
    @NotBlank
    private String address;

}
