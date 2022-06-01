package com.example.sqlexamine.vo.rqrs;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author chenl
 * @Date 2022/5/12 11:10 上午
 */
@Data
public class SendMailReqVo {
//接收者的地址 如:abc@163.com
    @NotBlank
    private String to;
//    要发送的信息
    private String msg;
}
