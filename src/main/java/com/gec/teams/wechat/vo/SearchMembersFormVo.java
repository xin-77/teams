package com.gec.teams.wechat.vo;

/**
 * @author xin
 * @since 2023/7/26 14:22
 */

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class SearchMembersFormVo {
    @NotBlank
    private String members;
}

