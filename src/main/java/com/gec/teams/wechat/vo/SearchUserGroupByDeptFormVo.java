package com.gec.teams.wechat.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @author xin
 * @since 2023/7/25 20:03
 */
@Data
@ApiModel
public class SearchUserGroupByDeptFormVo {
    //参数为中文1~15个字符
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,15}$")
    private String keyword;
}
