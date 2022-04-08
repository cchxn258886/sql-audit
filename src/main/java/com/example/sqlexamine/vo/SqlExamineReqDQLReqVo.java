package com.example.sqlexamine.vo;

import lombok.Data;

/**
 * @Author chenl
 * @Date 2022/3/29 5:42 下午
 */

@Data
public class SqlExamineReqDQLReqVo  extends SqlExamineReqVo{
    private String uuid;
    private String databaseName;
    private String ipAddress;
    private String password;
    private String username;
}
