package com.gec.teams.wechat;

import cn.hutool.core.util.StrUtil;
import com.gec.teams.wechat.config.SystemConstants;
import com.gec.teams.wechat.entity.SysConfig;
import com.gec.teams.wechat.mapper.SysConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.List;

@SpringBootApplication
@ServletComponentScan
@MapperScan("com.gec.teams.wechat.mapper")
@Slf4j
@EnableAsync
public class TeamsWechatApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamsWechatApiApplication.class, args);
    }

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private SystemConstants constants;
    @PostConstruct
    public void init(){
        List<SysConfig> list=sysConfigMapper.selectAllParam();
        list.forEach(one->{
            String key=one.getParamKey();
            key= StrUtil.toCamelCase(key);
            String value=one.getParamValue();
            try{
                Field field=constants.getClass().getDeclaredField(key);
                field.set(constants,value);
            }catch (Exception e){
                log.error("执行异常",e);
            }
        });
        System.out.println(constants);
    }
}
