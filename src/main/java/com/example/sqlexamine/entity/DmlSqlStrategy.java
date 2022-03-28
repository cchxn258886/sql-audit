package com.example.sqlexamine.entity;

import com.example.sqlexamine.utils.Resp;
import org.springframework.stereotype.Component;

/**
 * @Author chenl
 * @Date 2022/3/23 11:32 上午
 */
public class DmlSqlStrategy implements SqlExamineBase{
    private  boolean isDatetimeJudgeEnable;

    public DmlSqlStrategy(boolean isDatetimeJudgeEnable) {
        this.isDatetimeJudgeEnable = isDatetimeJudgeEnable;
    }
    @Override
    public Resp examine(String sqlString) {
        return Resp.ok();
    }
}
