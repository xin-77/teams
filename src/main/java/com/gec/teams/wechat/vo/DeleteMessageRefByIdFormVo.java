package com.gec.teams.wechat.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class DeleteMessageRefByIdFormVo {
    @NotNull
    private String id;
}
