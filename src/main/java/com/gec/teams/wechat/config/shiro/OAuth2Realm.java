package com.gec.teams.wechat.config.shiro;

import com.gec.teams.wechat.entity.TbUser;
import com.gec.teams.wechat.service.TbUserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class OAuth2Realm extends AuthorizingRealm {
    @Autowired
    private com.gec.teams.wechat.config.shiro.JwtUtil jwtUtil;
    @Autowired
    private TbUserService tbUserService;

    //验证token 是否合法
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    //授权(验证权限时调用)
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection collection) {
        TbUser user = (TbUser) collection.getPrimaryPrincipal();
        Integer userId = user.getId();
        //获取用户权限列表
        Set<String> permsSet = tbUserService.searchUserPermissions(userId);
        // 为info设置权限列表
        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
        info.setStringPermissions(permsSet);
        return info;
    }

    //认证(验证登录时调用)
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws  AuthenticationException{
        //1.从令牌中获取 userId,然后检查该账户是否被冻结
        String accessToken = (String) token.getPrincipal();
        int userId = jwtUtil.getUserId(accessToken);
        //2.查询用户信息
        TbUser user = tbUserService.getById(userId);
        if(user==null){//如果查询当前用户状态不为1 账号被锁定
            throw new LockedAccountException("账号已被锁定,请联系管理员");
        }
        SimpleAuthenticationInfo info=new SimpleAuthenticationInfo(user,accessToken,getName());
        //TODO 往info对象中添加用户信息 token字符串
        return info;
    }
}