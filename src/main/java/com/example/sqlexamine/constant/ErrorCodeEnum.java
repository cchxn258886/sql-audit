package com.example.sqlexamine.constant;

/**
 * @Author chenl
 * @Date 2022/3/25 3:41 下午
 */
public enum ErrorCodeEnum {
    SYSTEM_ERR(1001,"系统内部错误"),
    SQL_NOT_STANDARD(2002,"sql不规范,审核不通过"),
    NOT_ALLOW_SQL_TYPE(2003,"非规定的sql类型.非SELECT|CREATE|INSERT|DELETE|UPDATE"),
    SQL_NOT_ALLOWED(2004,"传入的SQL需要顶格写"),
    ;
    private String msg;
    private int code;
    ErrorCodeEnum(int code,String msg){
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }
}
