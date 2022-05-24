package com.example.sqlexamine.service.rdsservice.impl;

import com.aliyun.rds20140815.Client;
import com.aliyun.rds20140815.models.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.sqlexamine.entity.AccessInfoEntity;
import com.example.sqlexamine.entity.dao.AccessInfoDao;
import com.example.sqlexamine.entity.dao.SlowLogTempTextDao;
import com.example.sqlexamine.entity.dao.SlowLogTemplateDao;
import com.example.sqlexamine.entity.po.SlowLogTempTextEntity;
import com.example.sqlexamine.entity.po.SlowLogTemplateEntity;
import com.example.sqlexamine.exception.BizException;
import com.example.sqlexamine.service.rdsservice.AliyunRDSOperationService;
import com.example.sqlexamine.service.rdsservice.cache.RdsCache;
import com.example.sqlexamine.service.rdsservice.context.RdsClientWrapper;
import com.example.sqlexamine.service.rdsservice.context.RdsConfigContext;
import com.example.sqlexamine.utils.RdsTimeUtils;
import com.example.sqlexamine.vo.rqrs.RdsReqVo;
import com.example.sqlexamine.vo.rqrs.RdsReqWithTimeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @Author chenl
 * @Date 2022/4/19 4:31 下午
 */
@Service
@Slf4j
public class AliyunRDSOperationServiceImpl implements AliyunRDSOperationService {
    private static final String envName1 = "职培";
    private static final String envName2 = "创培";
    private static final String rdsTimeSymbol = "Z";

    @Autowired
    RdsCache rdsCache;
    @Autowired
    AccessInfoDao accessInfoDao;
    @Autowired
    SlowLogTemplateDao slowLogTemplateDao;
    @Autowired
    SlowLogTempTextDao slowLogTempTextDao;
    @Autowired
    ExecutorService threadPool;

    /**
     * 阿里云rds的接口名字相同 性能诊断
     */
    @Override
    public void createDiagnosticReport(String envName, String startTime, String endTime) {
//        传入的时间:yyyy-mm-dd hh-mm
        String startTimeString = RdsTimeUtils.createRdsNeedTimeSplit(startTime);
        String endTimeString = RdsTimeUtils.createRdsNeedTimeSplit(endTime);
        List<String> instanceIdList = rdsCache.getInstanceList(envName);
        RdsConfigContext rdsConfigContext = rdsCache.getRdsConfigContext(envName);
        Client rdsClient = null;
        try {
            rdsClient = RdsClientWrapper.rdsClientWrapper(rdsConfigContext, 50000);
        } catch (Exception e) {
            log.error("获取rds客户端失败:{}", e.getMessage());
            throw new BizException("获取rds客户端失败");
        }
        CreateDiagnosticReportRequest createDiagnosticReportRequest = new CreateDiagnosticReportRequest();
        for (String s : instanceIdList) {
            createDiagnosticReportRequest.setDBInstanceId(s);
            createDiagnosticReportRequest.setStartTime(startTimeString);
            createDiagnosticReportRequest.setEndTime(endTimeString);
        }

        try {
            rdsClient.createDiagnosticReport(createDiagnosticReportRequest);
        } catch (Exception e) {
            log.error("生成诊断报告失败:{}", e.getMessage());
        }
    }

    @Override
    public List<DescribeDiagnosticReportListResponseBody.DescribeDiagnosticReportListResponseBodyReportList> getDescribeDiagnosticReportList(RdsReqVo rdsReqVo) {
        //  List<String> instanceIdList = rdsCache.getInstanceList(rdsReqVo.getEnvName());
        RdsConfigContext rdsConfigContext = rdsCache.getRdsConfigContext(rdsReqVo.getEnvName());
        Client rdsClient = null;
        try {
            rdsClient = RdsClientWrapper.rdsClientWrapper(rdsConfigContext);
        } catch (Exception e) {
            log.error("获取rds客户端失败:{}", e.getMessage());
            throw new BizException("获取rds客户端失败");
        }
        DescribeDiagnosticReportListRequest describeDiagnosticReportListRequest = new DescribeDiagnosticReportListRequest();
        describeDiagnosticReportListRequest.setDBInstanceId(rdsReqVo.getInstanceId());
        DescribeDiagnosticReportListResponse describeDiagnosticReportListResponse = null;
        try {
            describeDiagnosticReportListResponse = rdsClient.describeDiagnosticReportList(describeDiagnosticReportListRequest);
        } catch (Exception e) {
            log.error("rds-client获取诊断报告列表失败:{}", e.getMessage());
            throw new BizException("rds-client获取诊断报告列表失败");
        }
        List<DescribeDiagnosticReportListResponseBody.DescribeDiagnosticReportListResponseBodyReportList> reportList = describeDiagnosticReportListResponse.getBody().getReportList();
        return reportList;
    }

    /**
     * 从阿里云获取信息:key rdsName:value instanceId;
     */
    @Override
    public Map<String, String> describeDBInstances(String envName) {
        QueryWrapper<AccessInfoEntity> accessInfoEntityQueryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(envName)) {
            accessInfoEntityQueryWrapper.eq("description", envName);
        }
        AccessInfoEntity accessInfoEntity = accessInfoDao.selectOne(accessInfoEntityQueryWrapper);
        RdsConfigContext rdsConfigContext = new RdsConfigContext();
        rdsConfigContext.setAccessKeySecret(accessInfoEntity.getAccessKeySecret());
        rdsConfigContext.setAccessKeyID(accessInfoEntity.getAccessId());
        Client rdsClient = null;
        try {
            rdsClient = RdsClientWrapper.rdsClientWrapper(rdsConfigContext);
        } catch (Exception e) {
            log.error("获取rds client失败:{}", e.getMessage());
            throw new BizException("获取rds-client失败");
        }
        DescribeDBInstancesRequest describeDBInstancesRequest = new DescribeDBInstancesRequest();
        describeDBInstancesRequest.setRegionId(RdsClientWrapper.regionId);
        DescribeDBInstancesResponse response = null;
        try {
            response = rdsClient.describeDBInstances(describeDBInstancesRequest);
        } catch (Exception e) {
            log.error("rds获取resp失败:{}", e.getMessage());
            throw new BizException("rds-client获取response失败");
        }
        //item.getDBInstanceDescription().contains("生产") &&
        List<DescribeDBInstancesResponseBody.DescribeDBInstancesResponseBodyItemsDBInstance> dbInstanceList = response.getBody().getItems().getDBInstance();
        Map<String, String> resultMap = dbInstanceList.stream().filter((item) -> {
            return "Primary".equals(item.getDBInstanceType()) && "Running".equals(item.getDBInstanceStatus());
        }).collect(Collectors.toMap(
                DescribeDBInstancesResponseBody.DescribeDBInstancesResponseBodyItemsDBInstance::getDBInstanceDescription,
                (item) -> {
                    return item.getDBInstanceId();
                }
        ));
        return resultMap;
    }

    /**
     * 从rds instance 慢日志明细 获取插入到数据库
     * TODO
     */
    @Override
    public void getSlowLogRecord(RdsReqVo rdsReqVo) {

    }

    /**
     * 从rds instance 获取慢日志模版 插入到数据库slow_log_template
     * 2020-01-01 13:58:12
     */
    @Override
    @Transactional
    public void getSlowLogTemplate(RdsReqWithTimeVo rdsReqVo) {
        log.info("instanceId:{}", rdsReqVo.getInstanceId());
        RdsReqWithTimeVo rdsReqWithTimeVo = initRdsReqVo(rdsReqVo);
        instanceListHasReqInstanceId(rdsReqWithTimeVo);

        Date createTime = new Date();
        QueryWrapper<SlowLogTemplateEntity> slowLogTemplateEntityQueryWrapper = new QueryWrapper<>();
        slowLogTemplateEntityQueryWrapper.between("create_time", RdsTimeUtils.getTodayZeroHour(createTime), RdsTimeUtils.getTodayAllHour(createTime));
        Integer count = slowLogTemplateDao.selectCount(slowLogTemplateEntityQueryWrapper);
        //TODO 这里存在bug 如果两个任务运行的时候
        if (count != 0) {
            throw new BizException("当天慢日志模版无需再次生成。");
        }
        exec(rdsReqWithTimeVo, createTime, rdsReqVo.getInstanceId());

    }

    @Transactional
    public void exec(RdsReqWithTimeVo rdsReqWithTimeVo, Date createTime, String instanceId) {
        log.info("instanceId:{}", instanceId);
        RdsConfigContext rdsConfigContext = rdsCache.getRdsConfigContext(rdsReqWithTimeVo.getEnvName());
        Client rdsClient = null;
        try {
            rdsClient = RdsClientWrapper.rdsClientWrapper(rdsConfigContext);
        } catch (Exception e) {
            log.error("获取rds-client失败:{}", e.getMessage());
            throw new BizException("获取rds-client失败");
        }
        String startTime = rdsReqWithTimeVo.getStartTime();
        String endTime = rdsReqWithTimeVo.getEndTime();

        DescribeSlowLogsRequest describeSlowLogsRequest = new DescribeSlowLogsRequest();
        describeSlowLogsRequest.setDBInstanceId(instanceId)
                .setStartTime(startTime)
                .setEndTime(endTime);
        ArrayList<SlowLogTemplateEntity> slowLogTemplateEntities = new ArrayList<>();
        ArrayList<SlowLogTempTextEntity> slowLogTempTextEntities = new ArrayList<>();

        try {
            DescribeSlowLogsResponse describeSlowLogsResponse = rdsClient.describeSlowLogs(describeSlowLogsRequest);
            DescribeSlowLogsResponseBody.DescribeSlowLogsResponseBodyItems items = describeSlowLogsResponse.getBody().getItems();
            List<DescribeSlowLogsResponseBody.DescribeSlowLogsResponseBodyItemsSQLSlowLog> sqlSlowLogs = items.getSQLSlowLog();
            if (sqlSlowLogs.size() == 0) {
                log.info("envName:{},instanceId:{},没有慢日志", rdsReqWithTimeVo.getEnvName(), instanceId);
                return;
            }
            for (DescribeSlowLogsResponseBody.DescribeSlowLogsResponseBodyItemsSQLSlowLog sqlSlowLog : sqlSlowLogs) {
                String uuid = UUID.randomUUID().toString().replace("-", "");
                if (sqlSlowLog.maxExecutionTime == 0) {
                    log.info("sqlMaxExecutionTime为0");
                    continue;
                }
                SlowLogTemplateEntity slowLogTemplateEntity = new SlowLogTemplateEntity(
                        uuid,
                        createTime,
                        sqlSlowLog.getSQLHASH(),
                        sqlSlowLog.getMaxExecutionTime(),
                        sqlSlowLog.getDBName(),
                        instanceId
                );
                SlowLogTempTextEntity slowLogTempTextEntity = new SlowLogTempTextEntity();
                slowLogTempTextEntity.setId(UUID.randomUUID().toString().replace("-",""));
                slowLogTempTextEntity.setSqlTextHash(sqlSlowLog.SQLHASH);
                slowLogTempTextEntity.setSqlText(sqlSlowLog.getSQLText());
                slowLogTempTextEntity.setCreateTime(createTime);
                slowLogTemplateEntities.add(slowLogTemplateEntity);
                slowLogTempTextEntities.add(slowLogTempTextEntity);

            }
            log.info("slowLogTemplateEntities:{}", slowLogTemplateEntities);
            log.info("slowLogTempTextEntities:{}", slowLogTempTextEntities);
            slowLogTemplateDao.insertList(slowLogTemplateEntities);
            slowLogTempTextDao.insertList(slowLogTempTextEntities);
        } catch (Exception e) {
            log.error("慢日志模版写入数据库失败:{}", e.getMessage());
            throw new BizException("慢日志模版写入数据库错误.");
        }
    }

    /**
     * 初始化 reqVo
     */
    private RdsReqWithTimeVo initRdsReqVo(RdsReqWithTimeVo rdsReqVo) {
        if (StringUtils.isBlank(rdsReqVo.getStartTime())) {
            String rdsNeedTimeNoHourMinSecond = RdsTimeUtils.createRdsNeedTimeNoHourMinSecond(new Date());
            rdsReqVo.setStartTime(rdsNeedTimeNoHourMinSecond);
        } else {
            rdsReqVo.setStartTime(rdsReqVo.getStartTime() + rdsTimeSymbol);
        }
        if (StringUtils.isBlank(rdsReqVo.getEndTime())) {
            String rdsNeedTimeNoHourMinSecond = RdsTimeUtils.createRdsNeedTimeNoHourMinSecond(new Date());
            rdsReqVo.setEndTime(rdsNeedTimeNoHourMinSecond);
        } else {
            rdsReqVo.setEndTime(rdsReqVo.getEndTime() + rdsTimeSymbol);
        }
        return rdsReqVo;
    }

    /**
     * 判断这个envName 是否包含传入的instanceID
     */
    private void instanceListHasReqInstanceId(RdsReqWithTimeVo rdsReqVo) {
        List<String> instanceList = rdsCache.getInstanceList(rdsReqVo.getEnvName());
        if (!instanceList.contains(rdsReqVo.getInstanceId())) {
            log.error("传入的vo:{},instanceList:{}", rdsReqVo.toString(), instanceList);
            throw new BizException("envName中不包含这个instance");
        }
    }


    /**
     * 获取当前环境下的所有的instance slowlogTemp
     */
    @Override
    public void getOneEnvAllInstanceSlowLogTemplate(RdsReqWithTimeVo rdsReqVo) {
        if (rdsReqVo.getIsAsync()) {
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    getOneEnvAllInstanceSlowLogTemp(rdsReqVo);
                }
            });
        } else {
            getOneEnvAllInstanceSlowLogTemp(rdsReqVo);
        }
    }

    private void getOneEnvAllInstanceSlowLogTemp(RdsReqWithTimeVo rdsReqVo) {
        RdsReqWithTimeVo rdsReqWithTimeVo = initRdsReqVo(rdsReqVo);
//        instanceListHasReqInstanceId(rdsReqWithTimeVo);

/*        Date createTime = new Date();
        QueryWrapper<SlowLogTemplateEntity> slowLogTemplateEntityQueryWrapper = new QueryWrapper<>();
        slowLogTemplateEntityQueryWrapper.between("create_time", RdsTimeUtils.getTodayZeroHour(createTime), RdsTimeUtils.getTodayAllHour(createTime));
        Integer count = slowLogTemplateDao.selectCount(slowLogTemplateEntityQueryWrapper);
        if (count != 0) {
            throw new BizException("当天慢日志模版无需再次生成。");
        }*/
        List<String> instanceList = rdsCache.getInstanceList(rdsReqVo.getEnvName());
        if (Objects.isNull(instanceList) || instanceList.size() == 0 ){
            log.error("instanceList 为空");
            throw new BizException("instanceList为空");
        }
        for (String s : instanceList) {
            Date createTime = new Date();
            QueryWrapper<SlowLogTemplateEntity> slowLogTemplateEntityQueryWrapper = new QueryWrapper<>();
            slowLogTemplateEntityQueryWrapper.eq("instance_id",s);
            slowLogTemplateEntityQueryWrapper.between("create_time", RdsTimeUtils.getTodayZeroHour(createTime), RdsTimeUtils.getTodayAllHour(createTime));
            Integer count = slowLogTemplateDao.selectCount(slowLogTemplateEntityQueryWrapper);
            if (count != 0) {
                throw new BizException("当天慢日志模版无需再次生成。");
            }
            exec(rdsReqVo, createTime, s);
        }
    }
}
