package com.example.sqlexamine;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @Author chenl
 * @Date 2022/3/23 9:13 上午
 * CREATE TABLE `train_consult` (
 * `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自动递增',
 * `name` varchar(32) NOT NULL COMMENT '名称',
 * `unit` varchar(64) NOT NULL COMMENT '单位',
 * `phone` varchar(32) NOT NULL COMMENT '电话',
 * `address` varchar(64) NOT NULL COMMENT '地址',
 * `email` varchar(32) NOT NULL COMMENT '邮箱',
 * `sort` tinyint(4) unsigned NOT NULL DEFAULT '1' COMMENT '排序',
 * `create_user` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '操作人UID',
 * `create_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '创建时间',
 * `update_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '更新时间',
 * `delete_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '删除时间',
 * PRIMARY KEY (`id`) USING BTREE
 * ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='站点-咨询表';
 * selectList select条件
 * from表名
 * where
 * <p>
 * <p>
 * 判断的依据
 * varchar(2000)
 * - int(11)
 * - tinyint(4)
 * - smallint(6)
 * - mediumint(9)
 * - bigint(20)
 * xxx_time datetime
 * xxx_id int
 * 没有id列主键
 * xxx_uuid
 */
/*
*
* - int(11)
- tinyint(4)
- smallint(6)
- mediumint(9)
- bigint(20)
*
* */
public class SQLGrammerParseTreeTest {
    private static final String sqlString = "CREATE TABLE `train_consult` (\n" +
            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自动递增',\n" +
            "  `name` varchar(32) NOT NULL COMMENT '名称',\n" +
            "  `unit` varchar(64) NOT NULL COMMENT '单位',\n" +
            "  `phone` varchar(32) NOT NULL COMMENT '电话',\n" +
            "  `address` varchar(64) NOT NULL COMMENT '地址',\n" +
            "  `email` varchar(32) NOT NULL COMMENT '邮箱',\n" +
            "  `sort` tinyint(4) unsigned NOT NULL DEFAULT '1' COMMENT '排序',\n" +
            "  `create_user` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '操作人UID',\n" +
            "  `create_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '创建时间',\n" +
            "  `update_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '更新时间',\n" +
            "  `delete_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '删除时间',\n" +
            "  PRIMARY KEY (`id`) USING BTREE\n" +
            ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='站点-咨询表';";

    @Test
    public void sql2Tree() throws SQLSyntaxErrorException {
        String sql = "select t.name, t.id, (select p.name from post p where p.id = t.post_id)" +
                "from acct t where t.id = 10 and exists (select r.id from role r where r.id = t.role_id) ;";

        String dbType = "mysql";
        System.out.println("原始SQL 为 ： " + sql);
        String result = SQLUtils.format(sql, dbType);
        System.out.println(result);


        SQLSelectStatement statement = (SQLSelectStatement) parser(sql, dbType);
        SQLSelect select = statement.getSelect();
        SQLSelectQueryBlock query = (SQLSelectQueryBlock) select.getQuery();
        System.out.println("query:" + query);
        List<SQLSelectItem> selectList = query.getSelectList();
        SQLBinaryOpExpr where = (SQLBinaryOpExpr) query.getWhere();
        where.getOperator();


        SQLExprTableSource tableSource = (SQLExprTableSource) query.getFrom();
        System.out.println("tableSource:" + tableSource);
        String s = tableSource.getExpr().toString();
        System.out.println("表名:" + s);
    }

    public SQLStatement parser(String sql, String dbType) throws SQLSyntaxErrorException {
        List<SQLStatement> list = SQLUtils.parseStatements(sql, dbType);
        if (list.size() > 1) {
            throw new SQLSyntaxErrorException("MultiQueries is not supported,use single query instead ");
        }
        System.out.println("list:" + list);
        return list.get(0);
    }


    @Test
    public void ddl() {
//
        String sql = "CREATE TABLE `train_consult` (`id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自动递增'," +
                "`name` varchar(200) not null,`create_time` int(11) not null," +
                " PRIMARY KEY (`id`) USING BTREE) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='站点-咨询表';";
        String dbType = "mysql";
        System.out.println("原始SQL 为 ： " + sql);
        String result = SQLUtils.format(sql, dbType);
        System.out.println(result);
        SQLDDLStatement statement = null;
        try {
            statement = (SQLDDLStatement) parser(sql, dbType);
        } catch (SQLSyntaxErrorException e) {
            e.printStackTrace();
        }
        MySqlCreateTableStatement createTableStatement = (MySqlCreateTableStatement) statement;
        List<SQLTableElement> tableElementList = createTableStatement.getTableElementList();
        List<SQLAssignItem> tableOptions = createTableStatement.getTableOptions();
        //TODO
        Map<String, String> collect = tableOptions.stream().collect(Collectors.toMap(
                (item) -> {
                    SQLIdentifierExpr target = (SQLIdentifierExpr) item.getTarget();
                    return target.getName();
                }, (item) -> {
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
        String engine = collect.get("ENGINE");
        String charset = collect.get("CHARSET");
        //判断建表是否包含pk 不包含直接false掉
        if (!judgePkOnDDL(tableElementList)) {
            throw new RuntimeException("不存在主键");
        }

        for (SQLTableElement sqlTableElement : tableElementList) {
//            System.out.println(sqlTableElement.getAttributes());
            if (sqlTableElement instanceof SQLColumnDefinition) {
                SQLColumnDefinition columnDefinition = (SQLColumnDefinition) sqlTableElement;
                String columnName = columnDefinition.getColumnName();
                SQLDataType dataType = columnDefinition.getDataType();
                List<SQLColumnConstraint> constraints = columnDefinition.getConstraints();
                if (!extracted(constraints)) {
                    throw new RuntimeException("不符合规范,有字段声明为null");
                }
                if (columnName.contains("time")) {
                    String name = dataType.getName();
                    if (!"datetime".equalsIgnoreCase(name)) {
                        throw new RuntimeException("带有time字段名需要datetime类型");
                    }
                }
                if (columnName.contains("id")) {
                    String name = dataType.getName();
                    if (!"int".equalsIgnoreCase(name) && !"bigint".equalsIgnoreCase(name)) {
                        throw new RuntimeException("带有id字段需要IntOrBigint");
                    }
                }


                String name = dataType.getName();
                if ("int".equalsIgnoreCase(name)) {
                    List<SQLExpr> arguments = dataType.getArguments();
                    SQLExpr sqlExpr = arguments.get(0);
                    SQLIntegerExpr sqlExpr1 = (SQLIntegerExpr) sqlExpr;
                    Integer value = (Integer) sqlExpr1.getValue();
                    System.out.println("sqlExpr:" + value);
                    if (value != 11) {
                        throw new RuntimeException("int类型与要求不符");
                    }
                }
                if ("varchar".equals(name)) {
                    List<SQLExpr> arguments = dataType.getArguments();
                    SQLExpr sqlExpr = arguments.get(0);
                    SQLIntegerExpr sqlExpr1 = (SQLIntegerExpr) sqlExpr;
                    Integer value = (Integer) sqlExpr1.getValue();
                    System.out.println("varchar:" + value);
                    if (value > 2000) {
                        throw new RuntimeException("varchar类型与要求不符");
                    }

                }


                System.out.println("columnName:" + columnName + "dataType:" + dataType);
            }
        }
        //SQLColumnDefinition MySqlPrimaryKey MySqlTableIndex

    }

    //判断传入的约束是否包含非空限制 考虑做成插件式 类似k8s源码的实现
    private boolean extracted(List<SQLColumnConstraint> constraints) {
        for (SQLColumnConstraint constraint : constraints) {
            if (constraint instanceof SQLNotNullConstraint) {
//                System.out.println("非空包含");
                return true;
            }
        }
        return false;
    }

    private String judgeSqlType(String sqlString) {
        int i = sqlString.indexOf(" ");
        String substring = sqlString.substring(0, i);
        if ("SELECT".equalsIgnoreCase(substring)) {
            System.out.println("select");
        } else if ("UPDATE".equalsIgnoreCase(substring)) {
            System.out.println("DML");
        } else if ("CREATE".equalsIgnoreCase(substring)) {
            System.out.println("DDL");
        } else if ("INSERT".equalsIgnoreCase(substring)) {
            System.out.println("DML");
        } else if ("DELETE".equalsIgnoreCase(substring)) {
            System.out.println("DML");
        } else if ("TRUNCATE".equalsIgnoreCase(substring)) {
            return "";
        }
        return "";
    }

    private boolean judgePkOnDDL(List<SQLTableElement> list) {
        ArrayList<String> strings = new ArrayList<>();
        for (SQLTableElement sqlTableElement : list) {
            strings.add(sqlTableElement.getClass().toString());
//            if (sqlTableElement instanceof MySqlPrimaryKey){
//                MySqlPrimaryKey mySqlPrimaryKey = (MySqlPrimaryKey)sqlTableElement;
//                SQLIndexDefinition indexDefinition = mySqlPrimaryKey.getIndexDefinition();
//                SQLIndexOptions options = indexDefinition.getOptions();
//                if (!options.getIndexType().equals("BTREE")){
//                    return false;
//                }
//            }
        }
        return strings.stream().anyMatch((item) -> {
            return item.contains("MySqlPrimaryKey");
        });
    }


    @Value("${examine.datetime.judge.enable}")
    private boolean dateTimeJudgeEnable;

    @Test
    public void testValue() {
        System.out.println("dateTimeJudgeEnable:" + dateTimeJudgeEnable);
    }


    @Test
    public void compFutereTest() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        CompletableFuture<Void> hello_world = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello world");
            }
        }, executorService);
//        CompletableFuture<Integer> objectCompletableFuture = CompletableFuture.supplyAsync();
//        objectCompletableFuture.whenComplete()
    }

}
