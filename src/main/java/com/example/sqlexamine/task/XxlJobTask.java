package com.example.sqlexamine.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.sqlexamine.entity.dto.SlowLogInfoDto;
import com.example.sqlexamine.entity.po.SlowLogTemplateEntity;
import com.example.sqlexamine.exception.BizException;
import com.example.sqlexamine.service.DingRobotService;
import com.example.sqlexamine.service.MailSendService;
import com.example.sqlexamine.service.SlowLogTempService;
import com.example.sqlexamine.service.rdsservice.AliyunRDSOperationService;
import com.example.sqlexamine.utils.RdsTimeUtils;
import com.example.sqlexamine.vo.rqrs.DingRobotReqVo;
import com.example.sqlexamine.vo.rqrs.RdsReqWithTimeVo;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Author chenl
 * @Date 2022/4/25 2:50 下午
 * xxl-job调度的定时任务类
 */
@Component
@Slf4j
public class XxlJobTask {
    @Autowired
    AliyunRDSOperationService aliyunRDSOperationService;
    @Autowired
    SlowLogTempService slowLogTempService;

    @Autowired
    DingRobotService dingRobotService;

    @Autowired
    MailSendService mailSendService;

    @XxlJob("demoJobHandler")
    public void demoJobHandler() throws Exception {
        XxlJobHelper.log("XXL-JOB, Hello World.");
        String param = XxlJobHelper.getJobParam();
        String[] methodParams = param.split(",");
        System.out.println(Arrays.toString(methodParams));

    }

    @XxlJob("createSlowLog")
    public void createSlowLog() throws Exception {
        String param = XxlJobHelper.getJobParam();
        if (StringUtils.isBlank(param)) {
            throw new BizException("定时任务传入的参数不能为空");
        }
        String[] methodParams = param.split(",");
        RdsReqWithTimeVo rdsReqWithTimeVo = new RdsReqWithTimeVo();
        rdsReqWithTimeVo.setEnvName(methodParams[0]);
        aliyunRDSOperationService.getOneEnvAllInstanceSlowLogTemplate(rdsReqWithTimeVo);
    }

    /**
     * 当天晚上11：30 生成慢日志模版存入数据库
     */
    @XxlJob("createSlowLogTest")
    public void createSlowLogTest() throws Exception {
        String param = XxlJobHelper.getJobParam();
        if (StringUtils.isBlank(param)) {
            throw new BizException("定时任务传入的参数不能为空");
        }
        String[] methodParams = param.split(",");
        RdsReqWithTimeVo rdsReqWithTimeVo = new RdsReqWithTimeVo();
        rdsReqWithTimeVo.setEnvName(methodParams[0]);
        log.info("参数1:{}", methodParams[0]);
        rdsReqWithTimeVo.setInstanceId(methodParams[1]);
        log.info("参数1:{}", methodParams[1]);
        aliyunRDSOperationService.getSlowLogTemplate(rdsReqWithTimeVo);
    }

    /**
     * 第二天 早上9点发送慢日志模版 消息
     */
    @XxlJob("sendMsgToDDRobot")
    public void sendMsgToDDRobot() {
        Date date = new Date();
        Date date1 = DateUtils.addDays(date, -1);
        Date todayZeroHour = RdsTimeUtils.getTodayZeroHour(date1);
        Date todayAllHour = RdsTimeUtils.getTodayAllHour(date1);
//        QueryWrapper<SlowLogTemplateEntity> slowLogTemplateEntityQueryWrapper = new QueryWrapper<>();
//        slowLogTemplateEntityQueryWrapper.between("create_time",todayZeroHour,todayAllHour);
//        List<SlowLogTemplateEntity> slowLogTemplateEntities = slowLogTempService.getBaseMapper().selectList(slowLogTemplateEntityQueryWrapper);
        DingRobotReqVo<String> dingRobotReqVo = new DingRobotReqVo<>();
        dingRobotReqVo.setMsgType("markdown");
        dingRobotReqVo.setMsg(todayZeroHour + "====" + todayAllHour);
        dingRobotService.sendMsg(dingRobotReqVo);
    }


    @XxlJob("sendMail")
    public void sendMail() {
        String param = XxlJobHelper.getJobParam();
        if (StringUtils.isBlank(param)) {
            throw new BizException("定时任务传入的参数不能为空");
        }
        String[] methodParams = param.split(",");
        Date date = new Date();
        Date date1 = DateUtils.addDays(date, -1);
        Date todayZeroHour = RdsTimeUtils.getTodayZeroHour(date1);
        Date todayAllHour = RdsTimeUtils.getTodayAllHour(date1);
        QueryWrapper<SlowLogTemplateEntity> slowLogTemplateEntityQueryWrapper = new QueryWrapper<>();
        slowLogTemplateEntityQueryWrapper.between("create_time", todayZeroHour, todayAllHour);
        List<SlowLogInfoDto> slowLogInfo = slowLogTempService.getSlowLogInfo(todayZeroHour, todayAllHour);
        log.info("slowLogInfo:{}",slowLogInfo);
        if (slowLogInfo.isEmpty()) {
            mailSendService.sendMail(methodParams[0], "昨日没有慢日志产生");
            return;
        }
        for (String methodParam : methodParams) {
            mailSendService.sendMail(methodParam, formatListDto(slowLogInfo));
        }
    }


    private String formatListDto(List<SlowLogInfoDto> listDto) {
        String title = "DBNAME\t" + "MAXEXECTIME\t" + "SQLTEXT\t" + "\n";
        StringBuilder sb = new StringBuilder(title);
        for (int i = 0; i < listDto.size(); i++) {
            SlowLogInfoDto slowLogInfoDto = listDto.get(i);
            sb.append(slowLogInfoDto.getDbName()).append("\t").append(slowLogInfoDto.getMaxExecTime()).append("\t")
                    .append(slowLogInfoDto.getSqlText()).append("\t\n");
            if ((i + 1) % 3 == 0) {
                sb.append("\n\n");
            }
        }
        return sb.toString();
    }
}
