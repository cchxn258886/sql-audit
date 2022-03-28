package com.example.sqlexamine.entity;

import com.alibaba.druid.sql.SQLUtils;
import com.example.sqlexamine.utils.Resp;
import lombok.Data;

/**
 * @Author chenl
 * @Date 2022/3/23 11:31 上午
 */

public  interface SqlExamineBase {
    Resp examine(String sqlString);
}
