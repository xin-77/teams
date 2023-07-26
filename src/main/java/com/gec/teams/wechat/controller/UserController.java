package com.gec.teams.wechat.controller;


import cn.hutool.json.JSONUtil;
import com.gec.teams.wechat.common.utils.R;
import com.gec.teams.wechat.config.shiro.JwtUtil;
import com.gec.teams.wechat.exception.TeamsException;
import com.gec.teams.wechat.service.TbUserService;
import com.gec.teams.wechat.vo.LoginFormVo;
import com.gec.teams.wechat.vo.RegisterFormVo;
import com.gec.teams.wechat.vo.SearchMembersFormVo;
import com.gec.teams.wechat.vo.SearchUserGroupByDeptFormVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/user")
@ApiModel("用户模块Web接口")
public class UserController {

    @Autowired
    private TbUserService tbUserService;

    //用来产生令牌的
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    //设置令牌过期时间
    @Value("${teams.jwt.cache-expire}")
    private int cacheExpire;

    @ApiOperation("注册用户")
    @PostMapping("/register")
    public R register(@Valid @RequestBody RegisterFormVo registerFormVo){
        //1、注册用户、返回用户主键
        int userId = tbUserService.registerUser(registerFormVo.getRegisterCode(), registerFormVo.getCode(),
                registerFormVo.getNickname(), registerFormVo.getPhoto());
        //2、通过注册的用户信息生成主键 生成访问令牌
        String token = jwtUtil.createToken(userId);
        //3、将令牌缓存到redis中
        saveCacheToken(token,userId);
        //4、通过用户id查询用户权限
        Set<String> permsSet = tbUserService.searchUserPermissions(userId);
        //5、响应给前端 页面 令牌 以及用户权限
        return R.ok("用户注册成功").put("token",token).put("permission",permsSet);
    }

    @PostMapping("/searchMembers")
    @ApiOperation("查询成员")
    @RequiresPermissions(value = {"ROOT", "MEETING:INSERT", "MEETING:UPDATE"},logical =
            Logical.OR)
    public R searchMembers(@Valid @RequestBody SearchMembersFormVo form){
        if(!JSONUtil.isJsonArray(form.getMembers())){
            throw new TeamsException("members不是JSON数组");
        }
        List param=JSONUtil.parseArray(form.getMembers()).toList(Integer.class);
        ArrayList list=tbUserService.searchMembers(param);
        return R.ok().put("result",list);
    }


    /**
     * 封装 将token值存放到redis缓存数据库的代码
     *
     * @param token
     * @param userId
     */
    private void saveCacheToken(String token, int userId) {
        //将token值和用户的id 一起存放进去 并且设置过期时间
        redisTemplate.opsForValue().set(token,userId+"",cacheExpire);
    }


    @PostMapping("/login")
    @ApiOperation("用户登录")
    public R login(@Valid@RequestBody LoginFormVo form){

        Integer id = tbUserService.login(form.getCode());
        String token = jwtUtil.createToken(id);
        Set<String> permsSet = tbUserService.searchUserPermissions(id);
        saveCacheToken(token,id);
        return R.ok("用户登录成功").put("token",token).put("permission",permsSet);
    }

    @PostMapping("/addUser")
    @ApiOperation("添加用户")
    @RequiresPermissions(value = {"ROOT","USER:INSERT"},logical = Logical.OR)
    public R addUser(){
        return R.ok("用户添加成功");
    }

    @GetMapping("/searchUserSummary")
    @ApiOperation("查询用户摘要信息")
    public R searchUserSummary(@RequestHeader("token") String token){
        int userId = jwtUtil.getUserId(token);
        HashMap hashMap = tbUserService.searchUserSummary(userId);
        return R.ok().put("result", hashMap);
    }

    @PostMapping("/searchUserGroupByDept")
    @ApiOperation("查询员工列表，按照部门分组排列")
    @RequiresPermissions(value = {"ROOT","EMPLOYEE:SELECT"},logical = Logical.OR)
    public R searchUserGroupByDept(@Valid @RequestBody SearchUserGroupByDeptFormVo form){
        ArrayList<HashMap> list=tbUserService.searchUserGroupByDept(form.getKeyword());
        return R.ok().put("result",list);
    }
}
