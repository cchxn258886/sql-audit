package com.example.sqlexamine.entity;

import com.example.sqlexamine.entity.dto.StrategyDto;
import com.example.sqlexamine.vo.SqlExamineReqDQLReqVo;

/**
 * @Author chenl
 * @Date 2022/3/23 11:31 上午
 */

public interface SqlExamineBase {
    StrategyDto examine(SqlExamineReqDQLReqVo sqlExamineReqVo);
}
