package com.example.sqlexamine.entity.dto;

import com.example.sqlexamine.utils.Resp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author chenl
 * @Date 2022/4/14 11:23 上午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrategyDto  {
    private int code;
    private String msg;
    private String sqlString;
    private Map<String,String> data;

}
