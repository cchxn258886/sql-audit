package com.example.sqlexamine.entity;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.example.sqlexamine.config.SqlExamineConfig;
import com.example.sqlexamine.constant.ErrorCodeEnum;
import com.example.sqlexamine.exception.BizException;
import com.example.sqlexamine.utils.Resp;
import com.example.sqlexamine.vo.SqlExamineReqDQLReqVo;
import com.example.sqlexamine.vo.SqlExamineReqVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author chenl
 * @Date 2022/3/23 11:33 上午
 * 几个筛选条件:
 * 必须包含pk
 * xxx_time 字段的必须使用datetime类型
 * - int(11)
 * - tinyint(4)
 * - smallint(6)
 * - mediumint(9)
 * - bigint(20)
 *
 * 针对create SQL
 * truncate目前不支持审核
 */
@Slf4j
@Component
public class DdlSqlStrategy implements SqlExamineBase {
    @Autowired
    SqlExamineConfig sqlExamineConfig;

    private final static String dbType = "mysql";


    //private final boolean isDatetimeJudgeEnable;

//    public DdlSqlStrategy(boolean isDatetimeJudgeEnable) {
//        this.isDatetimeJudgeEnable = isDatetimeJudgeEnable;
//    }

    @Override
    public Resp examine(SqlExamineReqDQLReqVo sqlExamineReqVo) {
        String sqlString = sqlExamineReqVo.getSqlString();
        log.info("原始sql:{}", sqlString);
        SQLUtils.format(sqlString, dbType);
        SQLDDLStatement statement = null;
        try {
            statement = (SQLDDLStatement) parser(sqlString, dbType);
        } catch (SQLSyntaxErrorException e) {
            return Resp.error(ErrorCodeEnum.SYSTEM_ERR.getCode(),e.getMessage());
        }
        MySqlCreateTableStatement createTableStatement = (MySqlCreateTableStatement) statement;
        assert createTableStatement != null;

        List<SQLAssignItem> tableOptions = createTableStatement.getTableOptions();
        if (tableOptions.size() != 0){
            Map<String, String> map = engineAndCharsetJudge(tableOptions);
            if ((!map.get("ENGINE").equalsIgnoreCase("INNODB")) && (!map.get("ENGINE").isEmpty())){
                throw new BizException("engine必须为innodb");
            }
            if (!map.get("CHARSET").equalsIgnoreCase("UTF8MB4") && !(map.get("CHARSET").isEmpty())){
                throw new BizException("CHARSET 必须为utf8mb4");
            }
        }

        List<SQLTableElement> tableElementList = createTableStatement.getTableElementList();
        if (!judgePkOnDDL(tableElementList)) {
            log.info("createTable语句不包含主键");
            throw new BizException("不存在主键");
        }
        for (SQLTableElement sqlTableElement : tableElementList) {
            if (sqlTableElement instanceof SQLColumnDefinition) {
                sqlDefinitionHandle((SQLColumnDefinition) sqlTableElement);
            }
        }
        return Resp.ok();
    }

    private void sqlDefinitionHandle(SQLColumnDefinition sqlTableElement) {
        SQLColumnDefinition columnDefinition = sqlTableElement;
        String columnName = columnDefinition.getColumnName();
        SQLDataType dataType = columnDefinition.getDataType();
        List<SQLColumnConstraint> constraints = columnDefinition.getConstraints();
        if (!constraintJudge(constraints)) {
            throw new BizException("不符合规范,有字段声明为null");
        }

        //由于历史原因需要先关闭这个判断条件

        if (sqlExamineConfig.isEnable()) {
            columnWithTimeJudge(columnName, dataType);
        }
        if (columnName.contains("id")) {
            String name = dataType.getName();
            if (!"int".equalsIgnoreCase(name) && !"bigint".equalsIgnoreCase(name)) {
                throw new BizException("带有id字段需要IntOrBigint");
            }
        }

        String name = dataType.getName();
        switch (name) {
            case "int":
                columnLimit(dataType, "int", 11);
                break;
            case "varchar":
                columnLimitVarchar(dataType);
                break;
            case "tinyint":
                columnLimit(dataType, "tinyint", 4);
                break;
            case "smallint":
                columnLimit(dataType, "smallint", 6);
                break;
            case "mediumint":
                columnLimit(dataType, "mediumint", 9);
                break;
            case "bigint":
                columnLimit(dataType, "bigint", 20);
                break;
        }


    }

    //    private void columnLimitTinyint(SQLDataType dataType) {
//        List<SQLExpr> arguments = dataType.getArguments();
//        SQLIntegerExpr sqlExpr = (SQLIntegerExpr) arguments.get(0);
//        Integer value = (Integer) sqlExpr.getValue();
//        if (value != 4) {
//            throw new RuntimeException("tinyint不符合规范");
//        }
//    }
//
    private void columnLimitVarchar(SQLDataType dataType) {
        List<SQLExpr> arguments = dataType.getArguments();
        SQLIntegerExpr sqlExpr = (SQLIntegerExpr) arguments.get(0);
        Integer value = (Integer) sqlExpr.getValue();
        if (value > 2000) {
            throw new BizException("varchar不符合规范");
        }
    }
//
//
//    private void columnLimitInt(SQLDataType dataType) {
//        List<SQLExpr> arguments = dataType.getArguments();
//        SQLIntegerExpr sqlExpr = (SQLIntegerExpr) arguments.get(0);
//        Integer value = (Integer) sqlExpr.getValue();
//        if (value != 11) {
//            throw new RuntimeException("int不符合规范");
//        }
//    }

    private void columnLimit(SQLDataType dataType, String columnType, Integer limit) {
        List<SQLExpr> arguments = dataType.getArguments();
        SQLIntegerExpr sqlExpr = (SQLIntegerExpr) arguments.get(0);
        Integer value = (Integer) sqlExpr.getValue();
        if (!Objects.equals(value, limit)) {
            throw new BizException(columnType + "不符合规范");
        }
    }

    private void columnWithTimeJudge(String columnName, SQLDataType dataType) {
        if (columnName.contains("time")) {
            String typeName = dataType.getName();
            if (!"datetime".equalsIgnoreCase(typeName)) {
                throw new BizException("带有time的字段名类型非datetime");
            }
        }
    }


    private SQLStatement parser(String sql, String dbType) throws SQLSyntaxErrorException {
        List<SQLStatement> list = SQLUtils.parseStatements(sql, dbType);
        if (list.size() > 1) {
            throw new SQLSyntaxErrorException("MultiQueries is not supported,use single query instead ");
        }
//        System.out.println("list:" + list);
        return list.get(0);
    }

    //判断主键
    private boolean judgePkOnDDL(List<SQLTableElement> list) {
        ArrayList<String> strings = new ArrayList<>();
        for (SQLTableElement sqlTableElement : list) {
            strings.add(sqlTableElement.getClass().toString());
        }
        return strings.stream().anyMatch((item) -> {
            return item.contains("MySqlPrimaryKey");
        });
    }

    //判断传入的约束是否包含非空限制 考虑做成插件式 类似k8s源码的实现
    private boolean constraintJudge(List<SQLColumnConstraint> constraints) {
        for (SQLColumnConstraint constraint : constraints) {
            if (constraint instanceof SQLNotNullConstraint) {
                return true;
            }
        }
        return false;
    }
    //判断是否是innodb 判断字符集是否是utf-8mb4
    private Map<String, String> engineAndCharsetJudge(List<SQLAssignItem> tableOptions){
        Map<String, String> resultMap = tableOptions.stream().collect(Collectors.toMap(
                (item) -> {
                    SQLIdentifierExpr target = (SQLIdentifierExpr) item.getTarget();
                    return target.getName();
                },
                (item) -> {
                    if (item.getValue() instanceof SQLIdentifierExpr) {
                        SQLIdentifierExpr value = (SQLIdentifierExpr) item.getValue();
                        return value.getName();
                    }
                    if (item.getValue() instanceof SQLIntegerExpr) {
                        SQLIntegerExpr value = (SQLIntegerExpr) item.getValue();
                        return value.getNumber().toString();
                    }
                    return "version";
                }
        ));
        return resultMap;
    }
}
