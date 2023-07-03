package com.gec.teams.wechat.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("测试sayhello参数类")
public class TestSayHelloVo {

    @NotBlank
//    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,15}$",message = "姓名应为2-15中文")
    @ApiModelProperty("姓名：2-15中文")
    private String name;
}
