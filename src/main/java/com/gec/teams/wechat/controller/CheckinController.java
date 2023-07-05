package com.gec.teams.wechat.controller;

import cn.hutool.core.date.DateUtil;
import com.gec.teams.wechat.common.utils.R;
import com.gec.teams.wechat.config.shiro.JwtUtil;
import com.gec.teams.wechat.service.TbCheckinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/checkin")
@Slf4j
@Api("签到模块Web层")
public class CheckinController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TbCheckinService checkinService;

    @GetMapping("/validCanCheckin")
    @ApiOperation("查看用户今天是否可以签到")
    public R validCanCheckin(@RequestHeader("token")String token){
        int userId = jwtUtil.getUserId(token);
        String result = checkinService.validCanCheckin(userId, DateUtil.today());
        return  R.ok(result);
    }
}
