package com.example.sqlexamine.entity;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.example.sqlexamine.constant.ErrorCodeEnum;
import com.example.sqlexamine.entity.dto.StrategyDto;
import com.example.sqlexamine.vo.SqlExamineReqDQLReqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @Author chenl
 * @Date 2022/3/23 11:33 上午
 * <p>
 * 是否走index,limit限制,select * from tableName
 * <p>
 * 有表用来存数据库信息。
 */
@Slf4j
@Component
public class DqlSqlStrategy extends SqlStrategyBase implements SqlExamineBase {
    private static final String dbType = "mysql";


    @Override
    public StrategyDto examine(SqlExamineReqDQLReqVo sqlExamineReqVo) {
        //TODO 目前筛选条件:是否走index,limit限制,三表join,select * from tableName;
        String sqlString = sqlExamineReqVo.getSqlString();
        log.info("原始sql:{}", sqlString);
        SQLUtils.format(sqlString, dbType);
        SQLSelectStatement statement = null;
        try {
//                    SQLSelectStatement statement = (SQLSelectStatement) parser(sql, dbType);
            statement = (SQLSelectStatement) super.parser(sqlString, dbType);
        } catch (SQLSyntaxErrorException e) {
            return new StrategyDto(ErrorCodeEnum.SYSTEM_ERR.getCode(), e.getMessage(),sqlString,null);
        }

        SQLSelect select = statement.getSelect();
        SQLSelectQueryBlock query = (SQLSelectQueryBlock) select.getQuery();
        //判断SQL LIMIT条件是否符合要求
        boolean isTrueOfLimit = limitConditionJudge(query);
        if (!isTrueOfLimit) {
            StrategyDto strategyDto = new StrategyDto(ErrorCodeEnum.SQL_NOT_STANDARD.getCode(), ErrorCodeEnum.SQL_NOT_STANDARD.getMsg(), sqlString,new HashMap<>());
            strategyDto.getData().put("data","LIMIT条件不符合规范");
            return strategyDto;
        }

        boolean isAllCondition = allConditionJudge(query);
        if (!isAllCondition) {
            StrategyDto strategyDto = new  StrategyDto(ErrorCodeEnum.SQL_NOT_STANDARD.getCode(), ErrorCodeEnum.SQL_NOT_STANDARD.getMsg(), sqlString,new HashMap<>());
            strategyDto.getData().put("data", "sql含有*号");
            return strategyDto;
        }

        boolean useIndex = super.isUseIndex(sqlExamineReqVo);
        if (!useIndex) {
            StrategyDto strategyDto = new StrategyDto(ErrorCodeEnum.SQL_NOT_STANDARD.getCode(), ErrorCodeEnum.SQL_NOT_STANDARD.getMsg(), sqlString,new HashMap<>());
            strategyDto.getData().put("data", "SQL中存在全表扫");
            return strategyDto;
        }
        return new StrategyDto(0, "success",sqlString,null);

    }

//    private SQLStatement parser(String sql, String dbType) throws SQLSyntaxErrorException {
//        List<SQLStatement> list = SQLUtils.parseStatements(sql, dbType);
//        if (list.size() > 1) {
//            throw new SQLSyntaxErrorException("MultiQueries is not supported,use single query instead ");
//        }
//        return list.get(0);
//    }

    private boolean limitConditionJudge(SQLSelectQueryBlock query) {
        SQLLimit limit = query.getLimit();
        if (Objects.isNull(limit)) {
            return true;
        }
        SQLExpr rowCount = limit.getRowCount();
        if (rowCount instanceof SQLIntegerExpr) {
            SQLIntegerExpr rowCountValue = (SQLIntegerExpr) rowCount;
            if (rowCountValue.getNumber().intValue() > 2000) {
                log.error("rowCountValue超过2000");
                return false;
            }
        }

        SQLExpr offset = limit.getOffset();
        if (offset instanceof SQLIntegerExpr) {
            SQLIntegerExpr offsetValue = (SQLIntegerExpr) offset;
            int i = offsetValue.getNumber().intValue();
            if (i > 200) {
                log.error("rowCountValue超过2000");
                return false;
            }
        }
        return true;
    }

    private boolean allConditionJudge(SQLSelectQueryBlock query) {
        List<SQLSelectItem> selectConditionList = query.getSelectList();
        for (SQLSelectItem sqlSelectItem : selectConditionList) {
            SQLExpr expr = sqlSelectItem.getExpr();
            if (expr instanceof SQLAllColumnExpr) {
                log.error("SQL含有*号");
                return false;
            }
            if (expr instanceof SQLPropertyExpr) {
                SQLPropertyExpr expr1 = (SQLPropertyExpr) expr;
                if (expr1.getName().equals("*")) {
                    log.error("SQL含有*号");
                    return false;
                }
            }
        }
        return true;
    }


}
