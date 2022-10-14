package com.example.emos.wx.controller.form;


import io.swagger.annotations.ApiModel;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@ApiModel
@Data
public class LoginForm {

    @NotBlank(message = "临时授权不能为空")
    private String code;

}
