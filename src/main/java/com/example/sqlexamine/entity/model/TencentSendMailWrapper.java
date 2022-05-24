package com.example.sqlexamine.entity.model;

import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.net.SocketTimeoutException;
import java.util.Properties;

/**
 * @Author chenl
 * @Date 2022/5/11 5:33 下午
 */
@AllArgsConstructor
public class TencentSendMailWrapper {
    //    现在不需要调整 直接写死就好了 后面如果有需要 抽到application里面去
//    private static final String username = "chenlong@hzwotu.com";
//    private static final String password = "244776516chenL";
//    private static final String host = "smtp.exmail.qq.com";
    private static final String protocol = "smtp";// 协议
    private JavaMailSenderImpl javaMailSender;
    private String username;

    public static TencentSendMailWrapper buildTencentSendMailWrapper(String username,String password,String host) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);
        javaMailSender.setProtocol(protocol);
        javaMailSender.setPort(465);
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");//开启认证
//        properties.setProperty("mail.debug", "true");//启用调试
        properties.put("mail.smtp.ssl.enable", "true");
        properties.setProperty("mail.smtp.timeout", "25000");//设置链接超时
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        javaMailSender.setJavaMailProperties(properties);
        return new TencentSendMailWrapper(javaMailSender,username);
    }

    public void sendMail(String to,  String msg) throws Exception {
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject("阿里云慢日志模版");
        mimeMessageHelper.setFrom(username);
        mimeMessageHelper.setText(msg);
        javaMailSender.send(mimeMessage);
    }
}
