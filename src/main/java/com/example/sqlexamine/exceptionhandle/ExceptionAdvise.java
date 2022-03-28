package com.example.sqlexamine.exceptionhandle;

import com.example.sqlexamine.constant.ErrorCodeEnum;
import com.example.sqlexamine.exception.BizException;
import com.example.sqlexamine.utils.Resp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.BindException;

/**
 * @Author chenl
 * @Date 2022/3/25 3:44 下午
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.example.sqlexamine.controller")
public class ExceptionAdvise  {
    @ExceptionHandler(value = BizException.class)
    public Resp bizExceptionHandler(BizException e){
        log.error("数据校验出现问题:{},异常类型:{}",e.getMessage(),e.getClass());
        return Resp.error(ErrorCodeEnum.SQL_NOT_STANDARD.getCode(),ErrorCodeEnum.SQL_NOT_STANDARD.getMsg()).put("data",e.getMessage());
    }
}
