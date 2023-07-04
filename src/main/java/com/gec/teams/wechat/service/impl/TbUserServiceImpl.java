package com.gec.teams.wechat.service.impl;


import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.teams.wechat.entity.TbUser;
import com.gec.teams.wechat.exception.TeamsException;
import com.gec.teams.wechat.mapper.TbUserMapper;
import com.gec.teams.wechat.service.TbUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/**
 * @author Administrator
 * @description 针对表【tb_user(用户表)】的数据库操作Service实现
 * @createDate 2023-07-03 17:04:31
 */
@Slf4j
@Service
@Scope("prototype")
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser> implements TbUserService {
    @Value("${wx.app-id}")
    private String appId;

    @Value("${wx.app-secret}")
    private String appSecret;

    @Autowired
    private TbUserMapper  tbUserMapper;


    @Override
    public String getOpenId(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        HashMap map = new HashMap();
        map.put("appid", appId);
        map.put("secret", appSecret);
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String response = HttpUtil.post(url, map);
        JSONObject json = JSONUtil.parseObj(response);
        String openId = json.getStr("openid");
        if (openId == null || openId.length() == 0) {
            throw new RuntimeException("临时登陆凭证错误");
        }
        return openId;
    }

    @Override
    public int registerUser(String registerCode, String code, String nickname, String photo) {
        boolean bool = tbUserMapper.haveRootUser();

        //如果不存在这在这个账户的话 就注册添加这个用户
        if (!bool) {
            String openId = getOpenId(code);

            //封装用户信息
            TbUser tbUser = new TbUser();
            tbUser.setOpenId(openId);
            tbUser.setNickname(nickname);
            tbUser.setPhoto(photo);
            //状态正常为1
            tbUser.setStatus(1);
            tbUser.setCreateTime(new Date());


            //TODO 可以新增管理员模块 来添加激活码什么的
            //我们默认的 超级系统管理员的账号是000000
            if (registerCode.equals("000000")) {
                //超级系统管理员注册
                //0 的角色是超级系统管理员
                tbUser.setRole("[0]");
                tbUser.setRoot(true);
            } else {
                //TODO 注册时候需要根据不同邀请码去分配不同的角色
                //没有这个邀请码的就是普通用户
                tbUser.setRole("[1]");
                tbUser.setRoot(false);
            }

            //调用mybatis plus 框架提供的添加方法添加数据到数据库中
            int insert = tbUserMapper.insert(tbUser);
            return tbUser.getId();
        } else {
            //如果 root账号被绑定了 就抛出异常
            throw new TeamsException("无法绑定超级管理员账号");
        }
    }

    @Override
    public Set<String> searchUserPermissions(int userId) {
        return tbUserMapper.searchUserPermissions(userId);
    }

    @Override
    public Integer login(String code) {
        String openId = getOpenId(code);
        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        //类似于where open_id = openId的sql语句
        queryWrapper.eq("open_id",openId);
        TbUser tbUser = tbUserMapper.selectOne(queryWrapper);
        if (tbUser == null) {
            throw new TeamsException("账号不存在");
        }
        //TODO 从消息队列中接收消息,转移到消息表
        return tbUser.getId();
    }


}






