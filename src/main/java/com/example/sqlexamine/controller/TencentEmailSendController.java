package com.example.sqlexamine.controller;

import com.example.sqlexamine.service.MailSendService;
import com.example.sqlexamine.utils.Resp;
import com.example.sqlexamine.vo.rqrs.SendMailReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author chenl
 * @Date 2022/5/12 10:50 上午
 */
@RestController
@RequestMapping("/api/sendEmail")
public class TencentEmailSendController {
    @Autowired
    MailSendService mailSendService;
    @PostMapping("/tencentEmail")
    public Resp emailSend(@RequestBody @Validated  SendMailReqVo sendMailReqVo){
        mailSendService.sendMail(sendMailReqVo.getTo(),sendMailReqVo.getMsg());
        return Resp.ok();
    }

}
