package com.example.sqlexamine.entity;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.example.sqlexamine.entity.dao.HostInfoDao;
import com.example.sqlexamine.vo.SqlExamineReqDQLReqVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.List;

/**
 * @Author chenl
 * @Date 2022/3/29 2:53 下午
 * 一些公共的方法就放到父类里面来
 * 比如判断是否走上index
 * 这种东西子类每个都去实现的话麻烦
 * explain 生成的东西
 * 1,SIMPLE,host_info,,ALL,,,,,2,100,
 */

@Component
@Slf4j
public class SqlStrategyBase {
    @Autowired
    private HostInfoDao hostInfoDao;
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String DB_TYPE = "mysql";

    protected boolean isUseIndex(SqlExamineReqDQLReqVo vo) {
        // HostInfoDto hostInfoEntity = hostInfoDao.getById(uuid);
//        if (Objects.isNull(hostInfoEntity)) {
//            log.error("传入的uuid有误:" + uuid);
//            throw new BizException("传入的uuid有误,传入的uuid为:" + uuid);
//        }
        String finalSqlString = "explain" + " " + vo.getSqlString();
        log.info("送审sqlString:" + vo.getSqlString());
        try {
            Class.forName(JDBC_DRIVER);
            if (!StringUtils.isEmpty(vo.getIpAddress())) {
                if (!getScanType(vo.getDatabaseName(), vo.getIpAddress(), vo.getUsername(), vo.getPassword(), finalSqlString)) {
                    return false;
                }
            }
//            if (!hostInfoEntity.getCloudHostInfo().equals("")) {
//                if (!getScanType(dbName, hostInfoEntity.getCloudHostInfo(), hostInfoEntity.getUsername(), hostInfoEntity.getHostPassword(), finalSqlString)) {
//                    return false;
//                }
//            } else {
//                throw new BizException("数据库数据有错误");
//            }

        } catch (Exception e) {
            //基本上不会发生。做个兜底吧
            e.printStackTrace();
            log.error("异常:{},类型:{}", e.getMessage(), e.getClass().toString());
            return false;
        }

        return true;
    }

    private boolean getScanType(String dbName, String ipAddr, String username, String password, String finalSqlString) {
        Connection connection = null;
        String dbUrl = String.format("jdbc:%s://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai", DB_TYPE, ipAddr, 3306, dbName);
        //String dbUrl = "jdbc:mysql://39.108.91.67:3306/sqlexamine";
        log.info("dburl:" + dbUrl);
        try {
            connection = DriverManager.getConnection(dbUrl, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(finalSqlString);
            while (resultSet.next()) {
                int columnCount = resultSet.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i);
                    if (columnName.equalsIgnoreCase("type")) {
                        String scanType = (String) resultSet.getObject(i);
                        if (scanType.equalsIgnoreCase("ALL")) {
                            String tableName = (String) resultSet.getObject(3);
                            log.info("表或者表的别名:{}，没有走上index", tableName);
                            return false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            return false;
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    protected SQLStatement parser(String sql, String dbType) throws SQLSyntaxErrorException {
        List<SQLStatement> list = SQLUtils.parseStatements(sql, dbType);
        if (list.size() > 1) {
            throw new SQLSyntaxErrorException("MultiQueries is not supported,use single query instead ");
        }
        return list.get(0);
    }

    //TODO 获取表行数 当作一个判断条件和是否走index联合判断 当表行数小于100行的时候不会返false;
    private boolean getTableRowNum() {
        return true;
    }
}
