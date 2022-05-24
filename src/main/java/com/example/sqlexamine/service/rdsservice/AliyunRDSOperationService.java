package com.example.sqlexamine.service.rdsservice;

import com.aliyun.rds20140815.models.DescribeDiagnosticReportListResponseBody.DescribeDiagnosticReportListResponseBodyReportList;
import com.example.sqlexamine.vo.rqrs.RdsReqVo;
import com.example.sqlexamine.vo.rqrs.RdsReqWithTimeVo;

import java.util.List;
import java.util.Map;

/**
 * @Author chenl
 * @Date 2022/4/19 4:30 下午
 */
public interface AliyunRDSOperationService {
    void createDiagnosticReport(String envName,String startTime,String endTime);
    List<DescribeDiagnosticReportListResponseBodyReportList> getDescribeDiagnosticReportList(RdsReqVo rdsReqVo);
    Map<String,String> describeDBInstances(String envName);
    void getSlowLogRecord(RdsReqVo rdsReqVo);
    void getSlowLogTemplate(RdsReqWithTimeVo rdsReqVo);
    void getOneEnvAllInstanceSlowLogTemplate(RdsReqWithTimeVo rdsReqVo);
}
