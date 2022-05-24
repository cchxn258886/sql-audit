package com.example.sqlexamine.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author chenl
 * @Date 2022/3/23 11:58 上午
 */
@Data
public class SqlExamineReqVo {
    @ApiModelProperty(required= true,name = "数据库类型默认mysql",example = "mysql")
    private String dbType;
    @ApiModelProperty(required= true,name = "sql语句")
    private String sqlString;
}
