package com.example.sqlexamine.controller;

import com.example.sqlexamine.service.DingRobotService;
import com.example.sqlexamine.utils.Resp;
import com.example.sqlexamine.vo.rqrs.DingRobotReqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author chenl
 * @Date 2022/4/28 11:29 上午
 */
@RestController
@RequestMapping("/api/dingRobot")
@Slf4j
public class DingRobotController {
    @Autowired
    DingRobotService dingRobotService;
    @PostMapping("/sendMsg")
    public Resp sendMessage(@RequestBody @Validated DingRobotReqVo<String> vo){
        dingRobotService.sendMsg(vo);
        return Resp.ok();
    }
    @PostMapping
    public void test(){
      log.info("测试日志");
    }
}
