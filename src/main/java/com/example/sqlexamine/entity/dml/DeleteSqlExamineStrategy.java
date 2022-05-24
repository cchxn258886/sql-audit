package com.example.sqlexamine.entity.dml;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.example.sqlexamine.constant.ErrorCodeEnum;
import com.example.sqlexamine.entity.SqlExamineBase;
import com.example.sqlexamine.entity.SqlStrategyBase;
import com.example.sqlexamine.entity.dto.StrategyDto;
import com.example.sqlexamine.exception.BizException;
import com.example.sqlexamine.utils.Resp;
import com.example.sqlexamine.vo.SqlExamineReqDQLReqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;
import java.util.Objects;

/**
 * @Author chenl
 * @Date 2022/4/1 2:42 下午
 */
@Component
@Slf4j
public class DeleteSqlExamineStrategy extends SqlStrategyBase implements SqlExamineBase {
    @Override
    public StrategyDto examine(SqlExamineReqDQLReqVo sqlExamineReqVo) {
        String sqlString = sqlExamineReqVo.getSqlString();
        sqlString = sqlString.replace("\n","");
        log.info("原始sql:{}", sqlString);
        SQLUtils.format(sqlString, SqlStrategyBase.DB_TYPE);
        SQLDeleteStatement statement = null;
        try {
            statement = (SQLDeleteStatement) super.parser(sqlString, SqlStrategyBase.DB_TYPE);
        } catch (SQLSyntaxErrorException e) {
            throw new BizException(e.getMessage());
        }
        SQLExpr where = statement.getWhere();
        if (Objects.isNull(where)) {
            throw new RuntimeException("where条件为空");
        }
        boolean useIndex = super.isUseIndex(sqlExamineReqVo);
        if (!useIndex) {
            StrategyDto strategyDto = new StrategyDto(ErrorCodeEnum.SQL_NOT_STANDARD.getCode(), ErrorCodeEnum.SQL_NOT_STANDARD.getMsg(), sqlString, new HashMap<>());
            strategyDto.getData().put("data", "SQL没有走上索引");
            return strategyDto;
            //.put("data", "SQL没有走上索引")
        }
        return new StrategyDto(0,"success",sqlString,null);
    }
}
