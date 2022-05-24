package com.example.sqlexamine.service.impl;

import com.example.sqlexamine.entity.model.TencentSendMailWrapper;
import com.example.sqlexamine.exception.BizException;
import com.example.sqlexamine.service.MailSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Properties;

/**
 * @Author chenl
 * @Date 2022/5/11 4:14 下午
 *
 */
@Service
@Slf4j
public class MailSendServiceImpl implements MailSendService {
//    现在不需要调整 直接写死就好了 后面如果有需要 抽到application里面去
    private static final String username = "chenlong@hzwotu.com";
    private static final String password = "244776516chenL";
    private static final String host = "smtp.exmail.qq.com";

    @Override
    public void sendMail(String to,String msg) {
        TencentSendMailWrapper tencentSendMailWrapper = TencentSendMailWrapper.buildTencentSendMailWrapper(username,password,host);
        try {
            tencentSendMailWrapper.sendMail(to,msg);
        }catch (SocketTimeoutException e){
            e.printStackTrace();
        }catch (Exception e) {
            log.error("发送邮件异常:{}", e.getMessage());
            e.printStackTrace();
            throw new BizException("发送邮件异常");
        }
    }
}
