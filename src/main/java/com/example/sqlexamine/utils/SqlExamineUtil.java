package com.example.sqlexamine.utils;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;

import java.sql.SQLSyntaxErrorException;
import java.util.List;

/**
 * @Author chenl
 * @Date 2022/3/23 11:06 上午
 */
public class SqlExamineUtil {
    public static List<SQLStatement> parser(String sql, String dbType)  {
        List<SQLStatement> list = SQLUtils.parseStatements(sql, dbType);
        return list;
    }
//244776516chenL

}
