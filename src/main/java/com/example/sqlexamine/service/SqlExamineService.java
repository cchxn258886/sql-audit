package com.example.sqlexamine.service;

import com.example.sqlexamine.entity.dto.StrategyDto;
import com.example.sqlexamine.vo.SqlExamineReqDQLReqVo;

import java.util.List;

/**
 * @Author chenl
 * @Date 2022/3/23 11:25 上午
 */
public interface SqlExamineService {
    List<StrategyDto> examine(SqlExamineReqDQLReqVo sqlExamineReqVo);
}
