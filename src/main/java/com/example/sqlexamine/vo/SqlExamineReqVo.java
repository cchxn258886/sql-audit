package com.example.sqlexamine.vo;

import lombok.Data;

/**
 * @Author chenl
 * @Date 2022/3/23 11:58 上午
 */
@Data
public class SqlExamineReqVo {
    private String dbType;
    private String sqlString;
}
