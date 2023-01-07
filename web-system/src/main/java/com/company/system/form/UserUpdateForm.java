package com.company.system.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description
 * @Date 2021/4/5
 */
@Data
public class UserUpdateForm {

    @NotBlank
    private String nickName;

    @NotBlank
    private String phonenumber;

    private String email;

    private String remark;

}
