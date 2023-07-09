package com.gec.teams.wechat.service.impl;


import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gec.teams.wechat.entity.MessageEntity;
import com.gec.teams.wechat.entity.TbUser;
import com.gec.teams.wechat.exception.TeamsException;
import com.gec.teams.wechat.mapper.TbUserMapper;
import com.gec.teams.wechat.service.TbUserService;
import com.gec.teams.wechat.task.MessageTask;
import com.gec.teams.wechat.vo.TbUserVo;
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
public class TbUserServiceImpl implements TbUserService {
    @Value("${wx.app-id}")
    private String appId;

    @Value("${wx.app-secret}")
    private String appSecret;

    @Autowired
    private TbUserMapper tbUserMapper;

    @Autowired
    private MessageTask messageTask;


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
        if (registerCode.equals("000000")) {
            boolean bool = tbUserMapper.haveRootUser();
            if (!bool) {
                String openId = getOpenId(code);

                TbUserVo tbUserVo = new TbUserVo();
                tbUserVo.setOpenId(openId);
                tbUserVo.setNickname(nickname);
                tbUserVo.setName(nickname);
                tbUserVo.setPhoto(photo);
                tbUserVo.setRole("[0]");
                tbUserVo.setStatus(1);
                tbUserVo.setCreateTime(new Date());
                tbUserVo.setRoot(true);
                tbUserMapper.insert(tbUserVo);

                MessageEntity entity = new MessageEntity();
                entity.setSenderId(0);//系统发送设为0
                entity.setSenderName("系统消息");
                entity.setUuid(IdUtil.simpleUUID());
                entity.setMsg("欢迎您注册成为超级管理员,请及时更新你的员工个人信息");
                entity.setSendTime(new Date());
                messageTask.sendAsync(tbUserVo.getId() + "", entity);

                return tbUserVo.getId();
            } else {//如果root账号被绑定了抛出异常
                throw new TeamsException("无法绑定超级管理员账号");
            }
        } else {
            //TODO普通员工注册
            String openId = getOpenId(code);

            TbUserVo tbUserVo = new TbUserVo();
            tbUserVo.setOpenId(openId);
            tbUserVo.setNickname(nickname);
            tbUserVo.setName(nickname);
            tbUserVo.setPhoto(photo);
            tbUserVo.setRole("[1, 2, 3, 4, 5, 6, 7, 8]");
            tbUserVo.setStatus(1);
            tbUserVo.setCreateTime(new Date());
            tbUserVo.setRoot(false);
            tbUserVo.setHiredate(new Date("2023-07-01"));
            tbUserMapper.insert(tbUserVo);

            MessageEntity entity = new MessageEntity();
            entity.setSenderId(0);//系统发送设为0
            entity.setSenderName("系统消息");
            entity.setUuid(IdUtil.simpleUUID());
            entity.setMsg("欢迎您注册成为Teams用户,请及时更新你的员工个人信息");
            entity.setSendTime(new Date());
            messageTask.sendAsync(tbUserVo.getId() + "", entity);

            return tbUserVo.getId();
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
        queryWrapper.eq("open_id", openId);
        TbUser tbUser = tbUserMapper.selectOne(queryWrapper);
        if (tbUser == null) {
            throw new TeamsException("账号不存在");
        }
        // 从消息队列中接收消息,转移到消息表
        Integer id = tbUser.getId();
        messageTask.receiveAsync(id+"");
        return id;
    }

    @Override
    public String searchUserHiredate(int userId) {
        return tbUserMapper.searchUserHiredate(userId);
    }

    @Override
    public HashMap searchUserSummary(int userId) {
        return tbUserMapper.searchUserSummary(userId);
    }


}






