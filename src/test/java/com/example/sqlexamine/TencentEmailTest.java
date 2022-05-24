package com.example.sqlexamine;

import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @Author chenl
 * @Date 2022/5/11 2:47 下午
 */
public class TencentEmailTest {
     public static final String username = "chenlong@hzwotu.com";
     public static final String password = "244776516chenL";
     public static final String host = "smtp.exmail.qq.com";

     public static final String port = "465";
     public static final String protocol = "smtp";// 协议

     @Test
    public void test1() throws Exception{
         JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
         javaMailSender.setHost(host);
         javaMailSender.setUsername(username);
         javaMailSender.setPassword(password);
         javaMailSender.setProtocol(protocol);
         javaMailSender.setPort(465);
         Properties properties = new Properties();
         properties.setProperty("mail.smtp.auth", "true");//开启认证
         properties.setProperty("mail.debug", "true");//启用调试
         properties.setProperty("mail.smtp.timeout", "1000");//设置链接超时
         properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        javaMailSender.setJavaMailProperties(properties);

         MimeMessage mimeMessage = javaMailSender.createMimeMessage();
         MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
         mimeMessageHelper.setTo("chenlong@hzwotu.com");
         mimeMessageHelper.setFrom("chenlong@hzwotu.com");
         mimeMessageHelper.setText("thank you");
         javaMailSender.send(mimeMessage);
     }

     @Test
    public void tabTest(){//SQLTEXT
         ArrayList<String> strings = new ArrayList<>();
         strings.add("a1");
         strings.add("a2");
         strings.add("a3");
         strings.add("b1");
         strings.add("b2");
         strings.add("c3");
         String title = "DBNAME\t"  + "MAXEXECTIME\t" +  "SQLTEXT\t" +"\n";
         StringBuilder stringBuilder = new StringBuilder(title);
         for (int i = 0; i < strings.size(); i++) {
             StringBuilder str = new StringBuilder(strings.get(i));
             if (str.length() < 6){
                 int i1 = 6 - str.length();
                 for (int j = 0; j < i1; j++) {
                     str.append(" ");
                 }
             }
             stringBuilder.append(str).append('\t');
             if ((i+1) % 3 ==0){
                 stringBuilder.append("\t\n");
             }
         }
         System.out.println(stringBuilder.toString());
     }
}
