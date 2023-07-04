package com.gec.teams.wechat.common.utils;

import org.apache.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

/**
 *  封装统一返回值
 */
public class R  extends HashMap<String,Object> {
    public R(){//默认绑定数据
        put("code", HttpStatus.SC_OK);//状态码
        put("msg","success");//消息
    }
    //声明方法绑定 通过他形成链式调用
    public R put(String key,Object value){
        super.put(key,value);
        return this;//返回当前this对象
    }
    //提供静态工厂方法
    public static R ok(){
        return new R();
    }//重载方法
    public static R ok(String msg){
        R r=new R();
        r.put("msg",msg);
        return r;
    }
    //数据可以定义到map 直接使用ok方法
    public static R ok(Map<String,Object> map){
        R r=new R();
        r.putAll(map);
        return r;
    }//声明请求失败返回对象
    public static R error(int code,String msg){
        R r=new R();
        r.put("code",code);
        r.put("msg",msg);
        return r;
    }
    public static R error(String msg){
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR,msg);
    }
    public static R error(){
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR,"未知异常，请联系管理员");
    }
}