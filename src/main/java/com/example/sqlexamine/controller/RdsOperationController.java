package com.example.sqlexamine.controller;

import com.aliyun.rds20140815.models.DescribeDiagnosticReportListResponseBody;
import com.example.sqlexamine.exception.BizException;
import com.example.sqlexamine.service.rdsservice.AliyunRDSOperationService;
import com.example.sqlexamine.utils.Resp;
import com.example.sqlexamine.vo.rqrs.RdsReqVo;
import com.example.sqlexamine.vo.rqrs.RdsReqWithTimeVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author chenl
 * @Date 2022/4/21 10:40 上午
 */
@RestController
@RequestMapping("/api/rdsOperation")
public class RdsOperationController {
    @Autowired
    AliyunRDSOperationService aliyunRDSOperationService;

    @GetMapping("/describeDBInstances")
    public Resp describeDBInstances(@RequestParam String envName) {
        Map<String, String> map = aliyunRDSOperationService.describeDBInstances(envName);
        return Resp.ok().put("data", map);
    }

    @GetMapping("/getDescribeDiagnosticReportList")
    public Resp getDescribeDiagnosticReportList(@RequestParam String envName, String instanceId) {
        if (StringUtils.isEmpty(envName) || StringUtils.isEmpty(instanceId)) {
            throw new BizException("参数不能为空");
        }
        RdsReqVo rdsReqVo = new RdsReqVo();
        rdsReqVo.setEnvName(envName);
        rdsReqVo.setInstanceId(instanceId);
        List<DescribeDiagnosticReportListResponseBody.DescribeDiagnosticReportListResponseBodyReportList> describeDiagnosticReportList = aliyunRDSOperationService.getDescribeDiagnosticReportList(rdsReqVo);
        return Resp.ok().put("data", describeDiagnosticReportList);
    }

    @PostMapping("/getSlowLogTemplate")
    public Resp getSlowLogTemplate(@RequestBody @Validated RdsReqWithTimeVo rdsReqWithTimeVo) {
        if (StringUtils.isNotEmpty(rdsReqWithTimeVo.getInstanceId())) {
            aliyunRDSOperationService.getSlowLogTemplate(rdsReqWithTimeVo);
        }else {
            aliyunRDSOperationService.getOneEnvAllInstanceSlowLogTemplate(rdsReqWithTimeVo);
        }
        return Resp.ok();
    }


}
