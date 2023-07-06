package com.gec.teams.wechat.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class CheckinFormVo {
    private String address;
    private String country;
    private String province;
    private String city;
    private String district;
}
