package com.example.sqlexamine.service;

import com.example.sqlexamine.utils.Resp;

/**
 * @Author chenl
 * @Date 2022/3/23 11:25 上午
 */
public interface SqlExamineService {
    Resp examine(String sqlString);
}
