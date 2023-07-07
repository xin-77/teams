package com.gec.teams.wechat.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.gec.teams.wechat.common.utils.R;
import com.gec.teams.wechat.config.SystemConstants;
import com.gec.teams.wechat.config.shiro.JwtUtil;
import com.gec.teams.wechat.exception.TeamsException;
import com.gec.teams.wechat.service.TbCheckinService;
import com.gec.teams.wechat.service.TbUserService;
import com.gec.teams.wechat.vo.CheckinFormVo;
import com.gec.teams.wechat.vo.SearchMonthCheckinVo;
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
    public R validCanCheckin(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        String result = checkinService.validCanCheckin(userId, DateUtil.today());
        return R.ok(result);
    }

    @PostMapping("/checkin")
    @ApiOperation("签到")
    public R checkin(@Valid CheckinFormVo form, @RequestParam("photo") MultipartFile file, @RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        if (file == null) {
            return R.error("没有上传文件");
        } else {
            HashMap param = new HashMap();
            param.put("userId", userId);
            param.put("city", form.getCity());
            param.put("district", form.getDistrict());
            param.put("address", form.getAddress());
            param.put("country", form.getCountry());
            param.put("province", form.getProvince());
            param.put("file", file);
            checkinService.checkin(param);
            return R.ok("签到成功");
        }
    }

    @PostMapping("/createFaceModel")
    @ApiOperation("创建人脸模型")
    public R createFaceModel(@RequestParam("photo") MultipartFile file, @RequestHeader("token") String token) {
        if (file == null) {
            return R.error("没有上传文件");
        }
        int userId = jwtUtil.getUserId(token);
        checkinService.createFaceModel(userId, file);
        return R.ok("人脸模型创建成功");
    }

    @GetMapping("/searchTodayCheckin")
    @ApiOperation("查询用户当日签到数据")
    public R searchTodayCheckin(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        HashMap map = checkinService.searchTodayCheckin(userId);
        log.info(systemConstants.attendanceTime);
        map.put("attendanceTime", systemConstants.attendanceTime);
        map.put("closingTime", systemConstants.closingTime);
        long days = checkinService.searchCheckinDays(userId);
        map.put("checkinDays", days);

        DateTime hiredate = DateUtil.parse(userService.searchUserHiredate(userId));
        DateTime startDate = DateUtil.beginOfWeek(DateUtil.date());
        if (startDate.isBefore(hiredate)) {
            startDate = hiredate;
        }
        DateTime endDate = DateUtil.endOfWeek(DateUtil.date());
        HashMap param = new HashMap();
        param.put("startDate", startDate.toString());
        param.put("endDate", endDate.toString());
        param.put("userId", userId);
        ArrayList<HashMap> list = checkinService.searchWeekCheckin(param);
        map.put("weekCheckin", list);
        return R.ok().put("result", map);
    }

    @PostMapping("/searchMonthCheckin")
    @ApiOperation("查询用户某月签到数据")
    public R searchMonthCheckin(@Valid @RequestBody SearchMonthCheckinVo form, @RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        DateTime hiredate = DateUtil.parse(userService.searchUserHiredate(userId));
        //月份处理成双数字
        String month = form.getMonth() < 10 ? "0" + form.getMonth() : form.getMonth().toString();
        //移动端选择的起始日期 某年某月第一天
        DateTime startDate = DateUtil.parse(form.getYear() + "-" + month + "-01");
        //如果查询月份早于员工入职日期月份抛出异常
        if (startDate.isBefore(DateUtil.beginOfMonth(hiredate))) {
            throw new TeamsException("只能查询考勤之后日期的数据");
        }//查询月份与入职月份恰好是同月,查询考勤开始日期设置成入职日期
        if (startDate.isBefore(hiredate)) {
            startDate = hiredate;
        }
        //本月结束日期
        DateTime endDate = DateUtil.endOfMonth(startDate);
        HashMap param = new HashMap();
        param.put("userId", userId);
        param.put("startDate", startDate.toString());
        param.put("endDate", endDate.toString());
        //查询并封装数据
        ArrayList<HashMap> list = checkinService.searchWeekCheckin(param);
        int sum_1 = 0, sum_2 = 0, sum_3 = 0;
        //统计当月正常、迟到、缺勤数据
        for (HashMap<String, String> one : list) {
            String type = one.get("type");
            String status = one.get("status");
            if ("工作日".equals(type)) {
                if ("正常".equals(status)) {
                    sum_1++;
                } else if ("迟到".equals(status)) {
                    sum_2++;
                } else if ("缺勤".equals(status)) {
                    sum_3++;
                }
            }
        }
        return R.ok().put("list", list).put("sum_1", sum_1).put("sum_2", sum_2).put("sum_3", sum_3);
    }
}
