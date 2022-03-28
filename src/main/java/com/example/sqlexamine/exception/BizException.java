package com.example.sqlexamine.exception;

/**
 * @Author chenl
 * @Date 2022/3/23 4:30 下午
 */

public class BizException extends RuntimeException {
    public BizException(String message){
        super(message);
    }
}
