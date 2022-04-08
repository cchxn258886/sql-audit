package com.example.sqlexamine.controller;

import com.example.sqlexamine.config.SqlExamineConfig;
import com.example.sqlexamine.service.SqlExamineService;
import com.example.sqlexamine.utils.Resp;
import com.example.sqlexamine.vo.SqlExamineReqDQLReqVo;
import com.example.sqlexamine.vo.SqlExamineReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author chenl
 * @Date 2022/3/23 11:51 上午
 */
@RestController
@RequestMapping("/api/sqlExamine")
public class SqlExamineController {
    @Autowired
    SqlExamineService sqlExamineService;


    @PostMapping("/examine")
    public Resp sqlExamineExec(@RequestBody SqlExamineReqDQLReqVo SqlExamineReqVo) {
        return sqlExamineService.examine(SqlExamineReqVo);
    }


}
