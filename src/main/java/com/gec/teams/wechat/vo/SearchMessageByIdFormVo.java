package com.gec.teams.wechat.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel
@Data
public class SearchMessageByIdFormVo {
    @NotBlank
    private String id;
}
