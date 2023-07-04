package com.gec.teams.wechat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
@MapperScan("com.gec.teams.wechat.mapper")
public class TeamsWechatApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamsWechatApiApplication.class, args);
    }

}
