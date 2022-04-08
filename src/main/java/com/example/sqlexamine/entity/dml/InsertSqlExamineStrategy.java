package com.example.sqlexamine.entity.dml;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.example.sqlexamine.constant.ErrorCodeEnum;
import com.example.sqlexamine.entity.SqlExamineBase;
import com.example.sqlexamine.entity.SqlStrategyBase;
import com.example.sqlexamine.exception.BizException;
import com.example.sqlexamine.utils.Resp;
import com.example.sqlexamine.vo.SqlExamineReqDQLReqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.SQLSyntaxErrorException;

/**
 * @Author chenl
 * @Date 2022/4/1 2:29 下午
 */
@Component
@Slf4j
public class InsertSqlExamineStrategy  extends SqlStrategyBase implements SqlExamineBase {

    @Override
    public Resp examine(SqlExamineReqDQLReqVo sqlExamineReqVo) {
        String sqlString = sqlExamineReqVo.getSqlString();
        log.info("原始sql:{}", sqlString);
        SQLUtils.format(sqlString,SqlStrategyBase.DB_TYPE);
        SQLInsertStatement insertStatement = null;
        try {
             insertStatement = (SQLInsertStatement) super.parser(sqlString, SqlStrategyBase.DB_TYPE);
        } catch (SQLSyntaxErrorException e) {
            throw new BizException(e.getMessage());
        }
        /*INSERT 只是判断是否使用了 INSERT INTO aa values(xx) 这里的逻辑 需要包含全列名*/
        if (insertStatement.getColumns().size() == 0){
            return Resp.error(ErrorCodeEnum.SQL_NOT_STANDARD.getCode(),ErrorCodeEnum.SQL_NOT_STANDARD.getMsg()).put("data","INSERT语句没有指定columnName");
        }
        return Resp.ok();
    }
}
