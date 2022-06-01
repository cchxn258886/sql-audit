package com.example.sqlexamine.service;

import com.example.sqlexamine.vo.rqrs.DingRobotReqVo;

/**
 * @Author chenl
 * @Date 2022/4/27 2:46 下午
 */
public interface DingRobotService {
    <T> void sendMsg(DingRobotReqVo<T> vo);
}
