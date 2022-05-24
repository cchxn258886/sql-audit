package com.example.sqlexamine.vo.rqrs;

import com.example.sqlexamine.exception.BizException;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author chenl
 * @Date 2022/4/28 11:30 上午
 */
@Data

public class DingRobotReqVo<T> implements ApiRQContext {
    @NotNull
    private String msgType;

    private T msg;

    @Override
    public void check() throws BizException {

    }

    @Override
    public String toString() {
        return "DingRobotReqVo{" +
                "msgType='" + msgType + '\'' +
                ", msg=" + msg +
                '}';
    }
}
