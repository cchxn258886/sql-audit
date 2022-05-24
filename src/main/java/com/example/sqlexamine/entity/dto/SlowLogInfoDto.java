package com.example.sqlexamine.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author chenl
 * @Date 2022/4/28 4:02 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SlowLogInfoDto {
    private String sqlText;
    private Long maxExecTime;
    private String dbName;



}
