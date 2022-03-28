package com.example.sqlexamine.entity;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDDLStatement;
import com.example.sqlexamine.constant.ErrorCodeEnum;
import com.example.sqlexamine.utils.Resp;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLSyntaxErrorException;
import java.util.List;

/**
 * @Author chenl
 * @Date 2022/3/23 11:33 上午
 * <p>
 * 是否走index,limit限制,select * from tableName
 * <p>
 * 有表用来存数据库信息。
 */
@Slf4j
public class DqlSqlStrategy implements SqlExamineBase {
    private boolean isDatetimeJudgeEnable;
    private static final String dbType = "mysql";

    public DqlSqlStrategy(boolean isDatetimeJudgeEnable) {
        this.isDatetimeJudgeEnable = isDatetimeJudgeEnable;
    }

    @Override
    public Resp examine(String sqlString) {
        //TODO 目前筛选条件:是否走index,limit限制,三表join,select * from tableName;
        log.info("原始sql:{}", sqlString);
        SQLUtils.format(sqlString, dbType);
        SQLDDLStatement statement = null;
        try {
            statement = (SQLDDLStatement) parser(sqlString, dbType);
        } catch (SQLSyntaxErrorException e) {
            return Resp.error(ErrorCodeEnum.SYSTEM_ERR.getCode(), e.getMessage());
        }

        return Resp.ok();
    }

    private SQLStatement parser(String sql, String dbType) throws SQLSyntaxErrorException {
        List<SQLStatement> list = SQLUtils.parseStatements(sql, dbType);
        if (list.size() > 1) {
            throw new SQLSyntaxErrorException("MultiQueries is not supported,use single query instead ");
        }
        return list.get(0);
    }

    public boolean limitConditionJudge() {
        return false;
    }

    public boolean allConditionJudge() {
        return false;
    }

}
