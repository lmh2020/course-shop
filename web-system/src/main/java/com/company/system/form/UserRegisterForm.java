package com.company.system.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description
 * @Date 2021/4/6
 */
@Data
public class UserRegisterForm {

    @NotBlank
    private String userName;

    @NotBlank
    private String nickName;

    @NotBlank
    private String phonenumber;

    @NotBlank
    private String password;

}
