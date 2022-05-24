package com.example.sqlexamine.vo.rqrs;

import com.example.sqlexamine.exception.BizException;

/**
 * @Author chenl
 * @Date 2022/4/28 11:04 上午
 * 数据校验全部由实现类的自我实现
 */
public interface ApiRQContext {
    void check() throws BizException;
}
