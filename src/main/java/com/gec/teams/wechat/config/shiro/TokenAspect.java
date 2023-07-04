package com.gec.teams.wechat.aop;

import com.gec.teams.wechat.common.utils.R;
import com.gec.teams.wechat.config.shiro.ThreadLocalToken;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect //声明切面类
@Component
public class TokenAspect {
    @Autowired
    private ThreadLocalToken threadLocalToken;

    //设置切入点
    @Pointcut("execution(public * com.gec.teams.wechat.controller.*.*(..))") //拦截所有controller类方法
    public void aspect() {

    }

    //设置环绕事件
    @Around("aspect()") //使用上面设置的切入点
    public Object around(ProceedingJoinPoint point) throws Throwable {
        R r = (R) point.proceed();
        String token = threadLocalToken.getToken();
        if (token != null) {
            r.put("token", token);
            threadLocalToken.clear();
        }
        return r;
    }
}