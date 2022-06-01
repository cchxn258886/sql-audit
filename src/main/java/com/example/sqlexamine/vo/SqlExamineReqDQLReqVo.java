package com.example.sqlexamine.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author chenl
 * @Date 2022/3/29 5:42 下午
 */

@Data
public class SqlExamineReqDQLReqVo  extends SqlExamineReqVo{
    @ApiModelProperty(required = true,name = "数据库名")
    private String databaseName;
    @ApiModelProperty(required = true,name = "数据库ip地址")
    private String ipAddress;
    @ApiModelProperty(required = true,name = "数据库用户密码")
    private String password;
    @ApiModelProperty(required = true,name = "数据库用户名")
    private String username;
}
