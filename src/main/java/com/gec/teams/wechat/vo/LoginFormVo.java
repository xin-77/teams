package com.gec.teams.wechat.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
@ApiModel
@Data
public class LoginFormVo {
    @NotBlank(message = "临时授权码不能为空")
    private String code;
}
