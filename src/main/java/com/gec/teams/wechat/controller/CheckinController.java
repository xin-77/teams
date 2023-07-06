package com.gec.teams.wechat.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.gec.teams.wechat.common.utils.R;
import com.gec.teams.wechat.config.SystemConstants;
import com.gec.teams.wechat.config.shiro.JwtUtil;
import com.gec.teams.wechat.service.TbCheckinService;
import com.gec.teams.wechat.service.TbUserService;
import com.gec.teams.wechat.vo.CheckinFormVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/checkin")
@Slf4j
@Api("签到模块Web层")
public class CheckinController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TbCheckinService checkinService;

    @Autowired
    private SystemConstants systemConstants;

    @Autowired
    private TbUserService userService;

    @GetMapping("/validCanCheckin")
    @ApiOperation("查看用户今天是否可以签到")
    public R validCanCheckin(@RequestHeader("token")String token){
        int userId = jwtUtil.getUserId(token);
        String result = checkinService.validCanCheckin(userId, DateUtil.today());
        return  R.ok(result);
    }

    @PostMapping("/checkin")
    @ApiOperation("签到")
    public R checkin(@Valid CheckinFormVo form, @RequestParam("photo") MultipartFile file, @RequestHeader("token")String token){
        int userId=jwtUtil.getUserId(token);
        if(file==null){
            return R.error("没有上传文件");
        } else{
            HashMap param=new HashMap();
            param.put("userId",userId);
            param.put("city",form.getCity());
            param.put("district",form.getDistrict());
            param.put("address",form.getAddress());
            param.put("country",form.getCountry());
            param.put("province",form.getProvince());
            param.put("file",file);
            checkinService.checkin(param);
            return R.ok("签到成功");
        }
    }

    @PostMapping("/createFaceModel")
    @ApiOperation("创建人脸模型")
    public R createFaceModel(@RequestParam("photo") MultipartFile file,@RequestHeader("token") String token){
        if(file==null){
            return R.error("没有上传文件");
        }
        int userId=jwtUtil.getUserId(token);
        checkinService.createFaceModel(userId,file);
        return R.ok("人脸模型创建成功");
    }

    @GetMapping("/searchTodayCheckin")
    @ApiOperation("查询用户当日签到数据")
    public R searchTodayCheckin(@RequestHeader("token") String token){
        int userId=jwtUtil.getUserId(token);
        HashMap map=checkinService.searchTodayCheckin(userId);
        log.info(systemConstants.attendanceTime);
        map.put("attendanceTime",systemConstants.attendanceTime);
        map.put("closingTime",systemConstants.closingTime);
        long days=checkinService.searchCheckinDays(userId);
        map.put("checkinDays",days);

        DateTime hiredate=DateUtil.parse(userService.searchUserHiredate(userId));
        DateTime startDate=DateUtil.beginOfWeek(DateUtil.date());
        if(startDate.isBefore(hiredate)){
            startDate=hiredate;
        }
        DateTime endDate=DateUtil.endOfWeek(DateUtil.date());
        HashMap param=new HashMap();
        param.put("startDate",startDate.toString());
        param.put("endDate",endDate.toString());
        param.put("userId",userId);
        ArrayList<HashMap> list=checkinService.searchWeekCheckin(param);
        map.put("weekCheckin",list);
        return R.ok().put("result",map);
    }
}
