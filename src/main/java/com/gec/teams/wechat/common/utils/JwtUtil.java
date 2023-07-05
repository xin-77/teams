package com.gec.teams.wechat.config.shiro;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class JwtUtil {
    @Value("${teams.jwt.secret}")
    private String secret;

    @Value("${teams.jwt.cache-expire}")
    private int expire;

    /**
     * 编写一个根据用户的 编号产生用户的jwt令牌信息token的方法
     * 给每个用户生成令牌的方法
     * @param userId
     * @return
     */
    public String createToken(int userId) {
        Date date = DateUtil.offset(new Date(), DateField.DAY_OF_YEAR, expire);//获取过期时间
        Algorithm algorithm = Algorithm.HMAC256(secret);//生成密钥
        JWTCreator.Builder builder = JWT.create();
        String token = builder.withClaim("userId", userId).withExpiresAt(date).sign(algorithm);
        return token;
    }
    /**
     * 当用户拿着令牌 来我们的系统中的时候 要知道他是谁 我们的系统能够通过令牌识别出用户
     * 解密 获取获取用户信息
     * @param token
     * @return
     */
    public int getUserId(String token) {
        DecodedJWT jwt = JWT.decode(token);
        int userId = jwt.getClaim("userId").asInt();
        return userId;
    }
    /**
     * 对token信息进行校验的方法
     *
     * 校验token是否合法 密钥对不对
     */
    public void verifierToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        verifier.verify(token);//令牌错误或过期该方法抛出runtime异常
    }
}