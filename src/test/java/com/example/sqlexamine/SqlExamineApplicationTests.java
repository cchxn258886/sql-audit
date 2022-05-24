package com.example.sqlexamine;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.sqlexamine.entity.dto.SlowLogInfoDto;
import com.example.sqlexamine.entity.po.SlowLogTemplateEntity;
import com.example.sqlexamine.service.DingRobotService;
import com.example.sqlexamine.service.SlowLogTempService;
import com.example.sqlexamine.utils.RdsTimeUtils;
import com.example.sqlexamine.vo.rqrs.DingRobotReqVo;
import lombok.ToString;
import org.apache.commons.lang.time.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class SqlExamineApplicationTests {
    @Autowired
    DingRobotService dingRobotService;
    @Autowired
    SlowLogTempService slowLogTempService;

    @Test
    void contextLoads() {
        Date date = new Date();
        Date date1 = DateUtils.addDays(date, -1);
        Date todayZeroHour = RdsTimeUtils.getTodayZeroHour(date1);
        Date todayAllHour = RdsTimeUtils.getTodayAllHour(date1);
        QueryWrapper<SlowLogTemplateEntity> slowLogTemplateEntityQueryWrapper = new QueryWrapper<>();
        slowLogTemplateEntityQueryWrapper.between("create_time", todayZeroHour, todayAllHour);
        List<SlowLogInfoDto> slowLogInfo = slowLogTempService.getSlowLogInfo(todayZeroHour, todayAllHour);


        String s = dingRobotMsgFormat(slowLogInfo);
        DingRobotReqVo<String> dingRobotReqVo = new DingRobotReqVo<>();
        dingRobotReqVo.setMsgType("markdown");
        dingRobotReqVo.setMsg(s);
        dingRobotService.sendMsg(dingRobotReqVo);
    }

    /**
     * 【envname】- [dbName] - [
     * select * from tablename;
     * select * from fffff;
     * select * from ferqadfaf;
     * ]
     * <p>
     * [envname] - [dbname] - [select * from afadf];
     * [envname] - [dbname] - [select * from adfadfadfa]
     */
    private String dingRobotMsgFormat(List<SlowLogInfoDto> slowLogInfo) {
        StringBuilder sb = new StringBuilder("### 慢日志 \n");
        for (SlowLogInfoDto slowLogInfoDto : slowLogInfo) {
            sb.append(">");
            String resultString = String.format("【%s】 - 【%s】 -【%s】", "职培", slowLogInfoDto.getDbName(), slowLogInfoDto.getSqlText());
            sb.append(resultString).append("\n\n");
        }
        return sb.toString();
    }

    @Test
    public void send() {
        DingRobotReqVo<String> dingRobotReqVo = new DingRobotReqVo<>();
        dingRobotReqVo.setMsgType("markdown");
        ArrayList<String> strings = new ArrayList<>();
        strings.add("asfa");
        strings.add("asdfa");
        strings.add("asfa2");
        strings.add("asfa3");
        strings.add("asfa4");
        strings.add("asfa66666");
        StringBuilder stringBuilder = new StringBuilder("### 慢日志 \n ");
        for (String string : strings) {
            stringBuilder.append(">").append(string).append("\n\n");
        }
//        String format = String.format("[%s] - [%s] -[%s]","aa","dddd","select * from aa;");
//        dingRobotReqVo.setMsg("## hello \n " +
//                "> "+format);
        dingRobotReqVo.setMsg(stringBuilder.toString());
        dingRobotService.sendMsg(dingRobotReqVo);
    }
    @Test
    public void testAA(){
        Date date = new Date();
        Date date1 = DateUtils.addDays(date, -1);
        Date todayZeroHour = RdsTimeUtils.getTodayZeroHour(date1);
        Date todayAllHour = RdsTimeUtils.getTodayAllHour(date1);
        QueryWrapper<SlowLogTemplateEntity> slowLogTemplateEntityQueryWrapper = new QueryWrapper<>();
        slowLogTemplateEntityQueryWrapper.between("create_time", todayZeroHour, todayAllHour);
        List<SlowLogInfoDto> slowLogInfo = slowLogTempService.getSlowLogInfo(todayZeroHour, todayAllHour);
        System.out.println("slowLogINfo:"+slowLogInfo);
        String s = formatListDto(slowLogInfo);
        System.out.println("resutl:"+s);
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
