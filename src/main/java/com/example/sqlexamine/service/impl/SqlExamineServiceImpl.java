package com.example.sqlexamine.service.impl;

import com.example.sqlexamine.config.SqlExamineConfig;
import com.example.sqlexamine.constant.ErrorCodeEnum;
import com.example.sqlexamine.constant.SqlOperationEnum;
import com.example.sqlexamine.entity.DdlSqlStrategy;
import com.example.sqlexamine.entity.DmlSqlStrategy;
import com.example.sqlexamine.entity.DqlSqlStrategy;
import com.example.sqlexamine.entity.SqlExamineBase;
import com.example.sqlexamine.service.SqlExamineService;
import com.example.sqlexamine.utils.Resp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author chenl
 * @Date 2022/3/23 11:26 上午
 */
@Service
public class SqlExamineServiceImpl implements SqlExamineService {
    private static boolean isEnable;


    private static final Map<String, SqlExamineBase> sqlStrategyMap = new HashMap<String, SqlExamineBase>();

    @Autowired
    public void setIsEnable(SqlExamineConfig sqlExamineConfig) {
        SqlExamineServiceImpl.isEnable = sqlExamineConfig.isEnable();
        sqlStrategyMap.put(SqlOperationEnum.SELECT.name(), new DqlSqlStrategy(isEnable));
        sqlStrategyMap.put(SqlOperationEnum.INSERT.name(), new DmlSqlStrategy(isEnable));
        sqlStrategyMap.put(SqlOperationEnum.UPDATE.name(), new DmlSqlStrategy(isEnable));
        sqlStrategyMap.put(SqlOperationEnum.DELETE.name(), new DmlSqlStrategy(isEnable));
        sqlStrategyMap.put(SqlOperationEnum.CREATE.name(), new DdlSqlStrategy(isEnable));
    }

//    private  static final Map<String, SqlExamineBase> sqlStrategyMap = new HashMap<String,SqlExamineBase>(){{
//        put(SqlOperationEnum.SELECT.name(),new DqlSqlStrategy(isEnable));
//        put(SqlOperationEnum.INSERT.name(),new DmlSqlStrategy(isEnable));
//        put(SqlOperationEnum.UPDATE.name(),new DmlSqlStrategy(isEnable));
//        put(SqlOperationEnum.DELETE.name(),new DmlSqlStrategy(isEnable));
//        put(SqlOperationEnum.CREATE.name(),new DdlSqlStrategy(isEnable));
//    }};
/**
 * SQL需要顶格写 先这么处理吧 还没想好怎么处理
 *
 * */
    @Override
    public Resp examine(String sqlString) {
        int i = sqlString.indexOf(" ");
        if (i == 0 ){
            return Resp.error(ErrorCodeEnum.SQL_NOT_ALLOWED.getCode(), ErrorCodeEnum.SQL_NOT_ALLOWED.getMsg());
        }
        String substring = sqlString.substring(0, i);
        SqlExamineBase sqlExamineBase = sqlStrategyMap.get(substring.toUpperCase());
        if (Objects.isNull(sqlExamineBase)){
            return Resp.error(ErrorCodeEnum.NOT_ALLOW_SQL_TYPE.getCode(),ErrorCodeEnum.NOT_ALLOW_SQL_TYPE.getMsg());
        }
        return sqlExamineBase.examine(sqlString);
    }


}
