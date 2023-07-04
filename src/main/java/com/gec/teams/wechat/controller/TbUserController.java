package com.gec.teams.wechat.controller;


import com.gec.teams.wechat.common.utils.R;
import com.gec.teams.wechat.config.shiro.JwtUtil;
import com.gec.teams.wechat.service.TbUserService;
import com.gec.teams.wechat.vo.RegisterFormVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/user")
@ApiModel("用户模块Web接口")
public class TbUserController {

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


}
