package com.gec.teams.wechat.controller;

import com.gec.teams.wechat.common.utils.R;
import com.gec.teams.wechat.config.shiro.JwtUtil;
import com.gec.teams.wechat.service.MessageService;
import com.gec.teams.wechat.task.MessageTask;
import com.gec.teams.wechat.vo.DeleteMessageRefByIdFormVo;
import com.gec.teams.wechat.vo.SearchMessageByIdFormVo;
import com.gec.teams.wechat.vo.SearchMessageByPageFormVo;
import com.gec.teams.wechat.vo.UpdateUnreadMessageFormVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/message")
@Api("消息模块网络接口")
public class MessageController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageTask messageTask;



    @PostMapping("/searchMessageByPage")
    @ApiOperation("获取分页消息列表")
    public R searchMessageByPage(@Valid @RequestBody SearchMessageByPageFormVo form,
                                 @RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        int page = form.getPage();
        int length = form.getLength();
        long start = (page - 1) * length;
        List<HashMap> list = messageService.searchMessageByPage(userId, start, length);
        return R.ok().put("result", list);
    }
    @PostMapping("/searchMessageById")
    @ApiOperation("根据消息Id查询消息")
    public R SearchMessageById(@Valid@RequestBody SearchMessageByIdFormVo
                                       form,@RequestHeader("token")String token){
        HashMap map = messageService.searchMessageById(form.getId());
        return R.ok().put("result",map);
    }
    @PostMapping("/updateUnreadMessage")
    @ApiOperation("未读消息更新成已读消息")
    public R updateUnreadMessage(@Valid @RequestBody UpdateUnreadMessageFormVo form) {
        long rows = messageService.updateUnreadMessage(form.getId());
        return R.ok().put("result", rows == 1 ? true : false);
    }

    @PostMapping("/deleteMessageRefById")
    @ApiOperation("删除消息")
    public R deleteMessageRefById(@Valid @RequestBody DeleteMessageRefByIdFormVo form) {
        long rows = messageService.deleteMessageRefById(form.getId());
        return R.ok().put("result", rows == 1 ? true : false);
    }

    @GetMapping("/refreshMessage")
    @ApiOperation("刷新用户消息")
    public R refreshMessage(@RequestHeader("token") String token){
        int userId=jwtUtil.getUserId(token);
        messageTask.receiveAsync(userId+"");
        long lastRows=messageService.searchLastCount(userId);
        long unreadRows=messageService.searchUnreadCount(userId);
        return R.ok().put("lastRows",lastRows).put("unreadRows",unreadRows);
    }
}
