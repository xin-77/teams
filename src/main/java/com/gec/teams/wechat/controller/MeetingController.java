package com.gec.teams.wechat.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.gec.teams.wechat.common.utils.R;
import com.gec.teams.wechat.config.shiro.JwtUtil;
import com.gec.teams.wechat.entity.TbMeeting;
import com.gec.teams.wechat.exception.TeamsException;
import com.gec.teams.wechat.service.TbMeetingService;
import com.gec.teams.wechat.vo.InsertMeetingFormVo;
import com.gec.teams.wechat.vo.SearchMyMeetingListByPageVo;
import com.gec.teams.wechat.vo.UpdateMeetingFormVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author xin
 * @since 2023/7/25 15:36
 */

@RestController
@RequestMapping("/meeting")
@Api("会议模块网络接口")
@Slf4j
public class MeetingController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TbMeetingService meetingService;

    @PostMapping("/searchMyMeetingListByPage")
    @ApiOperation("查询会议列表分页数据")
    public R searchMyMeetingListByPage(@Valid @RequestBody SearchMyMeetingListByPageVo form,
                                       @RequestHeader("token") String token){
        int userId=jwtUtil.getUserId(token);
        int page=form.getPage();
        int length=form.getLength();
        long start=(page-1)*length;
        HashMap map=new HashMap();
        map.put("userId",userId);
        map.put("start",start);
        map.put("length",length);
        ArrayList list=meetingService.searchMyMeetingListByPage(map);
        return R.ok().put("result",list);
    }


    @PostMapping("/insertMeeting")
    @ApiOperation("添加会议")
    @RequiresPermissions(value = {"ROOT", "MEETING:INSERT"},logical = Logical.OR)
    public R insertMeeting(@Valid @RequestBody InsertMeetingFormVo form, @RequestHeader("token")
    String token){
        if(form.getType()==2&&(form.getPlace()==null||form.getPlace().length()==0)){
            throw new TeamsException("线下会议地点不能为空");
        }
        DateTime d1= DateUtil.parse(form.getDate()+" "+form.getStart()+":00");
        DateTime d2= DateUtil.parse(form.getDate()+" "+form.getEnd()+":00");
        if(d2.isBeforeOrEquals(d1)){
            throw new TeamsException("结束时间必须大于开始时间");
        }
        if(!JSONUtil.isJsonArray(form.getMembers())){
            throw new TeamsException("members不是JSON数组");
        }
        TbMeeting entity=new TbMeeting();
        entity.setUuid(UUID.randomUUID().toString());
        entity.setTitle(form.getTitle());
        entity.setCreatorId((long)jwtUtil.getUserId(token));
        entity.setDate(form.getDate());
        entity.setPlace(form.getPlace());
        entity.setStart(form.getStart() + ":00");
        entity.setEnd(form.getEnd() + ":00");
        entity.setType(form.getType());
        entity.setMembers(form.getMembers());
        entity.setDesc(form.getDesc());
        entity.setStatus(1);
        entity.setRoomId(new Date().getTime());
        meetingService.insertMeeting(entity);
        return R.ok().put("result","success");
    }

    @GetMapping("/searchMeetingById/{id}")
    @ApiOperation("根据ID查询会议")
    @RequiresPermissions(value = {"ROOT", "MEETING:SELECT"}, logical = Logical.OR)
    public R searchMeetingById(@PathVariable("id") Integer id){
        HashMap map=meetingService.searchMeetingById(id);
        return R.ok().put("result",map);
    }

    @PostMapping("/updateMeetingInfo")
    @ApiOperation("更新会议")
    @RequiresPermissions(value = {"ROOT", "MEETING:UPDATE"}, logical = Logical.OR)
    public R updateMeetingInfo(@Valid @RequestBody UpdateMeetingFormVo form){
        if(form.getType()==2&&(form.getPlace()==null||form.getPlace().length()==0)){
            throw new TeamsException("线下会议地点不能为空");
        }
        DateTime d1= DateUtil.parse(form.getDate()+" "+form.getStart()+":00");
        DateTime d2= DateUtil.parse(form.getDate()+" "+form.getEnd()+":00");
        if(d2.isBeforeOrEquals(d1)){
            throw new TeamsException("结束时间必须大于开始时间");
        }
        if(!JSONUtil.isJsonArray(form.getMembers())){
            throw new TeamsException("members不是JSON数组");
        }
        HashMap param=new HashMap();
        param.put("title", form.getTitle());
        param.put("date", form.getDate());
        param.put("place", form.getPlace());
        param.put("start", form.getStart() + ":00");
        param.put("end", form.getEnd() + ":00");
        param.put("type", form.getType());
        param.put("members", form.getMembers());
        param.put("desc", form.getDesc());
        param.put("id", form.getId());
        meetingService.updateMeetingInfo(param);
        return R.ok().put("result","success");
    }

    @DeleteMapping("/deleteMeetingById/{id}")
    @ApiOperation("根据ID删除会议")
    @RequiresPermissions(value = {"ROOT", "MEETING:DELETE"}, logical = Logical.OR)
    public R deleteMeetingById(@PathVariable Integer id){
        meetingService.deleteMeetingById(id);
        return R.ok().put("result","success");
    }



}
