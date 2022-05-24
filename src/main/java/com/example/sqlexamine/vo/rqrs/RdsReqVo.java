package com.example.sqlexamine.vo.rqrs;

import com.example.sqlexamine.exception.BizException;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author chenl
 * @Date 2022/4/21 9:59 上午
 * 请求rds包装类
 */
@Data
public class RdsReqVo implements ApiRQContext{

    /**
     * 环境名
     */
    @NotNull
    String envName;

    /**
     * 阿里云的instanceid rm-bp1b476e1uxk2uo8g
     */
    String instanceId;

    @Override
    public void check() throws BizException {

    }
}
