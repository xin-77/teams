package com.gec.teams.wechat.exception;

import lombok.Data;

@Data
public class TeamsException extends RuntimeException{
    private String msg; //异常消息
    private int code = 500;

    public TeamsException(String msg) {//异常类构造器
        super(msg);//调用父类构造器
        this.msg = msg;//变量赋值
    }

    public TeamsException(String msg, Throwable e) {
        super(msg, e);//重载构造器
        this.msg = msg;
    }

    public TeamsException(String msg, int code) {
        super(msg); //设置异常信息及转台码
        this.msg = msg;
        this.code = code;
    }

    public TeamsException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }
}
