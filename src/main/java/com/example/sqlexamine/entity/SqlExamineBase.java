package com.example.sqlexamine.entity;

import com.example.sqlexamine.utils.Resp;
import com.example.sqlexamine.vo.SqlExamineReqDQLReqVo;
import com.example.sqlexamine.vo.SqlExamineReqVo;

/**
 * @Author chenl
 * @Date 2022/3/23 11:31 上午
 */

public interface SqlExamineBase {
    Resp examine(SqlExamineReqDQLReqVo sqlExamineReqVo);
}
