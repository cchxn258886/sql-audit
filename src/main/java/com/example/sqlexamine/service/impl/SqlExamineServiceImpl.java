package com.example.sqlexamine.service.impl;

import com.example.sqlexamine.constant.ErrorCodeEnum;
import com.example.sqlexamine.constant.SqlOperationEnum;
import com.example.sqlexamine.entity.DdlSqlStrategy;
import com.example.sqlexamine.entity.DqlSqlStrategy;
import com.example.sqlexamine.entity.SqlExamineBase;
import com.example.sqlexamine.entity.dml.DeleteSqlExamineStrategy;
import com.example.sqlexamine.entity.dml.InsertSqlExamineStrategy;
import com.example.sqlexamine.entity.dml.UpdateSqlExamineStrategy;
import com.example.sqlexamine.entity.dto.StrategyDto;
import com.example.sqlexamine.service.SqlExamineService;
import com.example.sqlexamine.utils.Resp;
import com.example.sqlexamine.vo.SqlExamineReqDQLReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author chenl
 * @Date 2022/3/23 11:26 上午
 */
@Service
public class SqlExamineServiceImpl implements SqlExamineService {
    @Autowired
    DqlSqlStrategy dqlSqlStrategy;
    @Autowired
    InsertSqlExamineStrategy insertSqlExamineStrategy;
    @Autowired
    UpdateSqlExamineStrategy updateSqlExamineStrategy;
    @Autowired
    DeleteSqlExamineStrategy deleteSqlExamineStrategy;
    @Autowired
    DdlSqlStrategy ddlSqlStrategy;

    private static final Map<String, SqlExamineBase> sqlStrategyMap = new HashMap<String, SqlExamineBase>();

    @Autowired
    public void setIsEnable() {
//        sqlStrategyMap.put(SqlOperationEnum.SELECT.name(), new DqlSqlStrategy(isEnable));
//        sqlStrategyMap.put(SqlOperationEnum.INSERT.name(), new DmlSqlStrategy(isEnable));
//        sqlStrategyMap.put(SqlOperationEnum.UPDATE.name(), new DmlSqlStrategy(isEnable));
//        sqlStrategyMap.put(SqlOperationEnum.DELETE.name(), new DmlSqlStrategy(isEnable));
//        sqlStrategyMap.put(SqlOperationEnum.CREATE.name(), new DdlSqlStrategy(isEnable));
        sqlStrategyMap.put(SqlOperationEnum.SELECT.name(), dqlSqlStrategy);
        sqlStrategyMap.put(SqlOperationEnum.INSERT.name(), insertSqlExamineStrategy);
        sqlStrategyMap.put(SqlOperationEnum.UPDATE.name(), updateSqlExamineStrategy);
        sqlStrategyMap.put(SqlOperationEnum.DELETE.name(), deleteSqlExamineStrategy);
        sqlStrategyMap.put(SqlOperationEnum.CREATE.name(), ddlSqlStrategy);
    }



    /**
     * SQL需要顶格写 先这么处理吧 还没想好怎么处理
     */
    @Override
    public List<StrategyDto> examine(SqlExamineReqDQLReqVo sqlExamineReqVo) {
        String sqlString = sqlExamineReqVo.getSqlString();
        int i = sqlString.indexOf(" ");
        ArrayList<StrategyDto> result = new ArrayList<>();

        String[] split = sqlString.split(";");
        for (String s : split) {
            if (i == 0) {
                StrategyDto put = new StrategyDto(ErrorCodeEnum.SQL_NOT_STANDARD.getCode(), ErrorCodeEnum.SQL_NOT_STANDARD.getMsg(), s,new HashMap<>());
                put.getData().put("data", "SQL需要顶格写");
                result.add(put);
                return result;
            }

            String substring = s.substring(0, i);
            substring = substring.replaceAll("\n","");
            SqlExamineBase sqlExamineBase = sqlStrategyMap.get(substring.toUpperCase());
            if (Objects.isNull(sqlExamineBase)) {
                StrategyDto put = new StrategyDto(ErrorCodeEnum.NOT_ALLOW_SQL_TYPE.getCode(), ErrorCodeEnum.NOT_ALLOW_SQL_TYPE.getMsg(), s,new HashMap<>());
                put.getData().put("data", "不支持的SQL_TYPE");
                result.add(put);
                return result;
            }
            sqlExamineReqVo.setSqlString(s);
            StrategyDto examine = sqlExamineBase.examine(sqlExamineReqVo);
            result.add(examine);
        }
        return result;
    }
}
