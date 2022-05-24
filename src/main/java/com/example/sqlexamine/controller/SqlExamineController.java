package com.example.sqlexamine.controller;

import com.example.sqlexamine.entity.dto.StrategyDto;
import com.example.sqlexamine.service.SqlExamineService;
import com.example.sqlexamine.vo.SqlExamineReqDQLReqVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author chenl
 * @Date 2022/3/23 11:51 上午
 */
@RestController
@RequestMapping("/api/sqlExamine")
public class SqlExamineController {
    @Autowired
    SqlExamineService sqlExamineService;

    @ApiOperation("审核引擎核心总入口")
    @PostMapping("/examine")
    public List<StrategyDto> sqlExamineExec(@RequestBody SqlExamineReqDQLReqVo SqlExamineReqVo) {
        return sqlExamineService.examine(SqlExamineReqVo);
    }


}
