package com.example.sqlexamine.utils;

import org.springframework.http.HttpStatus;

import java.util.HashMap;

/**
 * @Author chenl
 * @Date 2022/3/25 3:46 下午
 */
public class Resp extends HashMap<String,Object> {
    private static final long serialVersionUID = 1L;
    public Resp(){
        put("code",0);
        put("msg","success");
    }
    public static Resp ok(String message){
        Resp resp = new Resp();
        resp.put("message",message);
        return resp;
    }
    public static Resp ok(){
        return new Resp();
    }
    public static Resp error(String msg){
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(),msg);
    }
    public static Resp error(){
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "未知错误.查看日志");
    }
    public static Resp error(int code,String msg){
        Resp resp = new Resp();
        resp.put("code",code);
        resp.put("msg",msg);
        return resp;
    }


    @Override
    public Resp put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
