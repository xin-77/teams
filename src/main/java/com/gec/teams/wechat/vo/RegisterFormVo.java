package com.gec.teams.wechat.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@ApiModel
public class RegisterFormVo  {
    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    /**
     * 头像网址
     */
    @NotBlank(message = "头像不能为空")
    private String photo;

    @NotBlank(message = "邀请码不能为空")
    @Pattern(regexp = "^[0-9]{6}$",message = "邀请码必须是六位数字")
    private String registerCode;

    @NotBlank(message = "微信临时授权不能为空")
    private String code;
}