package com.example.sqlexamine;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
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
    //TODO 目前筛选条件:是否走index,limit限制 2000,三表join,select * from tableName;
    //是否走index要走到对面的数据库才能看到 这样就要落表
    public void sql() throws SQLSyntaxErrorException {
        String sql = "select t.*,t.name, t.id, (select p.name from post p where p.id = t.post_id)" +
                "from acct t where t.id = 10 and exists (select r.id from role r where r.id = t.role_id) limit 1,10;";

        String dbType = "mysql";
        System.out.println("原始SQL 为 ： " + sql);
        String result = SQLUtils.format(sql, dbType);
        System.out.println(result);


        SQLSelectStatement statement = (SQLSelectStatement) parser(sql, dbType);
        SQLSelect select = statement.getSelect();
        SQLSelectQueryBlock query = (SQLSelectQueryBlock) select.getQuery();
        SQLLimit limit = query.getLimit();
        SQLExpr rowCount = limit.getRowCount();
        if (rowCount instanceof SQLIntervalExpr) {
            SQLIntegerExpr rowCount1 = (SQLIntegerExpr) rowCount;
            System.out.println(rowCount1.getNumber());
            if (rowCount1.getNumber().intValue() > 2000) {
                throw new RuntimeException("rowCount大于2000");
            }
        }
        SQLExpr offset = limit.getOffset();
        if (offset instanceof SQLIntegerExpr) {
            SQLIntegerExpr offset1 = (SQLIntegerExpr) offset;
            System.out.println("offset1：" + offset1.getNumber());
            int i = offset1.getNumber().intValue();
            if (i > 200) {
                throw new RuntimeException("offset1大于200");
            }
        }
        List<SQLSelectItem> selectList1 = query.getSelectList();
        for (SQLSelectItem sqlSelectItem : selectList1) {
            SQLExpr expr = sqlSelectItem.getExpr();
            if (expr instanceof SQLPropertyExpr) {
                SQLPropertyExpr expr1 = (SQLPropertyExpr) expr;
                if (expr1.getName().equals("*")) {
                    throw new RuntimeException("含有*");
                }
            }
        }

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
//        System.out.println("list:" + list);
        return list.get(0);
    }


    @Test
    public void ddl() {

//        String sql = "CREATE TABLE `train_consult` (`id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自动递增'," +
//                "`name` varchar(200) not null,`create_time` int(11) not null," +
//                " PRIMARY KEY (`id`) USING BTREE) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='站点-咨询表';";
        String sql = "select * from test1;\n" +
                "update test1 set name=\"adfadfadfasf\" where id = 1;";
        //假设
        String dbType = "mysql";
        System.out.println("原始SQL 为 ： " + sql);
        String result = SQLUtils.format(sql, dbType);
        System.out.println(result);
        String[] split = sql.split(";");
        System.out.println("split:" + split);
        SQLDDLStatement statement = null;
        try {
            statement = (SQLDDLStatement) parser(sql, dbType);
        } catch (SQLSyntaxErrorException e) {
            e.printStackTrace();
        }
        MySqlCreateTableStatement createTableStatement = (MySqlCreateTableStatement) statement;
        List<SQLTableElement> tableElementList = createTableStatement.getTableElementList();
        List<SQLAssignItem> tableOptions = createTableStatement.getTableOptions();
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
        CompletableFuture<Void> future01 = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello world");
            }
        }, executorService);
        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
            return "helloworld";
        });
//        future02.runAfterBoth()

//        CompletableFuture<Integer> objectCompletableFuture = CompletableFuture.supplyAsync();
//        objectCompletableFuture.whenComplete()
    }

    //static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    @Test
    public void sqlExplain() throws Exception {
        //select * from host_info;
        String dbUrl = "jdbc:mysql://39.108.91.67:3306/sqlexamine";

        Class.forName(JDBC_DRIVER);
        //php3 Dv75709806
        Connection connection = DriverManager.getConnection(dbUrl, "root", "123456");

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("explain select * from host_info");
        while (resultSet.next()) {
            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                resultSet.getMetaData().getColumnName(i);
                System.out.println(resultSet.getMetaData().getColumnName(i) + "result:" + resultSet.getObject(i));
            }

        }


    }

    @Test
    /**
     * #### select条件大部分都适用于DML
     * - 写成insert into tableName(col_name,col_name) values(xx,xx);
     * - insert的时候如果行数过多 使用批量插入。并且中间sleep
     * - 建议全部使用innodb
     * - 写到主库 非实时数据从从库获取。
     * - 除了小表(100行内)。其他都需要带where条件,且能走上index
     * - where条件注意隐式转换
     * - 要有where子句并且能走上index
     * - 禁止在INSERT|UPDATE|DELETE|REPLACE语句 中进行多表连接操作
     * - 批量操作数据时,需要控制事务处理间隔时间, 进行必要的sleep
     *
     *
     * */
    public void insert() throws SQLSyntaxErrorException {
        String sql = "insert into test2(id,name) values (1,\"adfadfasdfadf\");";
//        String sql = "insert into test2 values (1,\"adfadfasdfadf\");";

        String dbType = "mysql";
        System.out.println("原始SQL 为 ： " + sql);
        String result = SQLUtils.format(sql, dbType);
        System.out.println("result:" + result);
        SQLInsertStatement statement = (SQLInsertStatement) parser(sql, dbType);
        System.out.println("statement:" + statement);
        List<SQLExpr> columns = statement.getColumns();
        if (columns.size() == 0) {
            throw new RuntimeException("INSERT语句没有指定columnName");
        }
        for (SQLExpr column : columns) {
            if (column instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr column1 = (SQLIdentifierExpr) column;
                column1.getName();
            }
        }
    }

    @Test
    public void update() throws SQLSyntaxErrorException {
//      String sql = "update test2 set name=\"nihao2222\" where id=\"2aasdfasd\"; ";
        String sql = "update test2 set name=\"nihao2222\";";

        String dbType = "mysql";
        System.out.println("原始SQL 为 ： " + sql);
        String result = SQLUtils.format(sql, dbType);
        System.out.println("result:" + result);
        SQLUpdateStatement statement = (SQLUpdateStatement) parser(sql, dbType);
        System.out.println("statement:" + statement);
        SQLExpr where = statement.getWhere();
        if (Objects.isNull(where)) {
            throw new RuntimeException("where条件为空");
        }
    }

    @Test
    public void delete() throws SQLSyntaxErrorException {
//      String sql = "update test2 set name=\"nihao2222\" where id=\"2aasdfasd\"; ";
        String sql = "delete from test2 ";

        String dbType = "mysql";
        System.out.println("原始SQL 为 ： " + sql);
        String result = SQLUtils.format(sql, dbType);
        System.out.println("result:" + result);
        SQLDeleteStatement statement = (SQLDeleteStatement) parser(sql, dbType);
        System.out.println("statement:" + statement);
        SQLExpr where = statement.getWhere();
        if (Objects.isNull(where)) {
            throw new RuntimeException("where条件为空");
        }
    }

    @Test
    public void mergeSort() {
        //归并排序
        int[] args = new int[]{2,3,1,5,2,6};
        process(args,0,args.length -1 );
    }

    private void process(int[] args,int L,int R){
        if (L == R ){
            return ;
        }
        int mid = L + ((R -L) >> 1); //midpoint
        process(args,L,mid);
        process(args,mid+1, R);
        merge(args,mid,L,R);
        System.out.println("args:"+ Arrays.toString(args));
    }
    private void merge(int[] args,int mid,int L,int R){
        int[] help = new int[R - L +1];
        int i =0;
        int p1 = L;
        int p2 = mid + 1;
         while (p1 <= mid && p2 <= R){
             help[i++] = args[p1] <= args[p2]?args[p1++]: args[p2++];
         }
        while (p1 <= mid){
            help[i++] = args[p1++];
        }
        while (p2 <= R){
            help[i++] = args[p2++];
        }
        for (i=0;i<help.length;i++){
            args[L + i] = help[i];
        }
        System.out.println(Arrays.toString(help));
    }

    /**
     * 压力测试 sharding sphere 1000并发 300写 700读 看延迟
     */
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    public static final String DB_TYPE = "mysql";
    public static final String sqlString1 = "select * from t_order;";
    public static final String sqlString2 = "update t_order set user_id=0 where order_id=1;";
    public static final String sqlString3 = "insert into t_order(order_id,user_id,status) value (2,2,\"2\");";
    @Test
    public void testShardingSphere() throws Exception {
        String dbUrl = String.format("jdbc:%s://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai", DB_TYPE, "39.108.91.67", 13308, "sharding_db");
        String username = "root";
        String password = "123456";
        Class.forName(JDBC_DRIVER);
        Connection connection = DriverManager.getConnection(dbUrl, username, password);
        Statement statement = connection.createStatement();
        AtomicInteger selectNum = new AtomicInteger();
        AtomicInteger insertNum = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(1000);
        for (int i = 0; i < 1000; i++) {
            if (i % 2 == 0){
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ResultSet resultSet = statement.executeQuery(sqlString1);
                            System.out.println("result:"+resultSet);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            selectNum.addAndGet(1);
                        }
                        countDownLatch.countDown();
                    }
                });
                thread.start();
            } else {
                int finalI = i;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String sqlString = String.format("insert into t_order(order_id,user_id,status) value (%s,%s,\"%s\");", finalI, finalI, finalI);
                            int i1 = statement.executeUpdate(sqlString);
                            System.out.println(i1+":"+finalI);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            insertNum.addAndGet(1);
                        }
                        countDownLatch.countDown();
                    }
                });
                thread.start();

            }
        }
        countDownLatch.await();
        System.out.println("selectNum:"+selectNum);
        System.out.println("insertNum:"+insertNum);


//        while (resultSet.next()) {
//            System.out.println(resultSet.getString("order_id"));
//            System.out.println(resultSet.getString("user_id"));
//            System.out.println(resultSet.getString("status"));
//        }
    }


    @Test
    public void tttt(){
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0 ){
                System.out.println("0");
            } else {
                System.out.println("1");
            }
        }
    }
}
