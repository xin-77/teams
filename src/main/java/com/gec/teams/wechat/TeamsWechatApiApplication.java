package com.gec.teams.wechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class TeamsWechatApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamsWechatApiApplication.class, args);
    }

}
