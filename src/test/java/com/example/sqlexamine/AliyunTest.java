package com.example.sqlexamine;


import com.aliyun.rds20140815.Client;
import com.aliyun.rds20140815.models.*;
import com.aliyun.teaopenapi.models.Config;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.example.sqlexamine.config.DingRobotConfig;
import com.example.sqlexamine.utils.RdsTimeUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author chenl
 * @Date 2022/4/18 2:04 下午
 */
public class AliyunTest {
    @Autowired
    DingRobotConfig dingRobotConfig;
    private static String AccessKeyID = "LTAI5tHDVAoessXQ5GVNjXZg";
    private static String AccessKeySecret = "UMTPwvJNHG51NRpRiddKSVdNwTf5aD";
    private static String regionId = "cn-hangzhou";
    private static String endpoint = "rm-bp1b476e1uxk2uo8goo.mysql.rds.aliyuncs.com";//测试数据库
    private static String endPoint2 = "rds.aliyuncs.com";

    @Test
    public void aliyunTest() {
        Config config = new Config();
        config.setAccessKeyId(AccessKeyID);
        config.setAccessKeySecret(AccessKeySecret);
        config.setEndpoint(endPoint2);
        try {
            Client client = new Client(config);


            DescribeDBInstancesRequest describeDBInstancesRequest = new DescribeDBInstancesRequest();
            describeDBInstancesRequest.setRegionId(regionId);
            DescribeDBInstancesResponse resp = client.describeDBInstances(describeDBInstancesRequest);


            DescribeDBInstancesResponseBody body = resp.getBody();
            List<DescribeDBInstancesResponseBody.DescribeDBInstancesResponseBodyItemsDBInstance> dbInstances = body.getItems().getDBInstance();
            List<String> instanceidList = dbInstances.stream().filter(
                    (item) -> {
                        return (item.getDBInstanceDescription().contains("生产") && "Running".equals(item.getDBInstanceStatus()));
                    }).map((item) -> {
                return item.getDBInstanceId();
            }).collect(Collectors.toList());
            System.out.println("result:" + instanceidList);
//            JSONObject reqJson = (JSONObject) JSONObject.toJSON(body);
//            System.out.println(reqJson);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Client getRdsClient() throws Exception {
        Config config = new Config();
        config.setAccessKeyId(AccessKeyID);
        config.setAccessKeySecret(AccessKeySecret);
        config.setEndpoint(endPoint2);
        Client client = new Client(config);
        return client;
    }

    @Test
//    rm-bp1b476e1uxk2uo8g
    public void getSlowLogFromRds() {
        String format = FastDateFormat.getInstance("yyyy-MM-dd HH:mm").format(new Date());
        System.out.println(format);
        Client rdsClient = null;
        try {
            rdsClient = getRdsClient();
        } catch (Exception e) {
            throw new RuntimeException("生成rdsclient失败:" + e.getMessage());
        }
        DescribeSlowLogsRequest describeSlowLogsRequest = new DescribeSlowLogsRequest();
        describeSlowLogsRequest.setDBInstanceId("rm-bp1ee618st66nzm8g")
                .setStartTime("2022-04-21Z")
                .setEndTime("2022-04-21Z");
        try {
            DescribeSlowLogsResponse describeSlowLogsResponse = rdsClient.describeSlowLogs(describeSlowLogsRequest);
            DescribeSlowLogsResponseBody.DescribeSlowLogsResponseBodyItems items = describeSlowLogsResponse.getBody().getItems();
            List<DescribeSlowLogsResponseBody.DescribeSlowLogsResponseBodyItemsSQLSlowLog> sqlSlowLogs = items.getSQLSlowLog();
            for (DescribeSlowLogsResponseBody.DescribeSlowLogsResponseBodyItemsSQLSlowLog sqlSlowLog : sqlSlowLogs) {
                System.out.println(sqlSlowLog);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSlowLogRecord() {
        String format = FastDateFormat.getInstance("yyyy-MM-dd HH:mm").format(new Date());
        System.out.println(format);
        Client rdsClient = null;
        try {
            rdsClient = getRdsClient();
        } catch (Exception e) {
            throw new RuntimeException("生成rdsclient失败:" + e.getMessage());
        }
        DescribeSlowLogRecordsRequest describeSlowLogRecordsRequest = new DescribeSlowLogRecordsRequest();
        describeSlowLogRecordsRequest.setDBInstanceId("rm-bp1ee618st66nzm8g")
                .setStartTime("2022-04-21T00:00Z")
                .setEndTime("2022-04-21T12:00Z");
        try {
            DescribeSlowLogRecordsResponse describeSlowLogRecordsResponse = rdsClient.describeSlowLogRecords(describeSlowLogRecordsRequest);

            DescribeSlowLogRecordsResponseBody.DescribeSlowLogRecordsResponseBodyItems items = describeSlowLogRecordsResponse.getBody().getItems();
            List<DescribeSlowLogRecordsResponseBody.DescribeSlowLogRecordsResponseBodyItemsSQLSlowRecord> sqlSlowRecord = items.getSQLSlowRecord();
            for (DescribeSlowLogRecordsResponseBody.DescribeSlowLogRecordsResponseBodyItemsSQLSlowRecord describeSlowLogRecordsResponseBodyItemsSQLSlowRecord : sqlSlowRecord) {
                System.out.println(describeSlowLogRecordsResponseBodyItemsSQLSlowRecord);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void timeTest() {
        String rdsNeedTimeNoHour = RdsTimeUtils.createRdsNeedTimeNoHourMinSecond(new Date());
        System.out.println(rdsNeedTimeNoHour);
        String rdsNeedTimeWithHour = RdsTimeUtils.createRdsNeedTimeWithHourMinNoSecond(new Date());
        System.out.println(rdsNeedTimeWithHour);

        String rdsNeedTimeSplit = RdsTimeUtils.createRdsNeedTimeSplit("2020/10/01 10:00:00");
        System.out.println(rdsNeedTimeSplit);
    }

    @Test
    public void dingRobotTest() throws Exception {
        String dingdingUrl = "https://oapi.dingtalk.com/robot/send?access_token=3c4606fc148ea6a433109c1d1cd5a2f3dc971ab68afd8e5a58a3cb3ab3e89f3c";
        String accessToken = "SECdc6c768a124d76416d904aa6396664e45c2f28f7fb8705d8a695d125e634fb52";
        Long timestamp = System.currentTimeMillis();
        String s = encodeSign(timestamp);
        String url = dingdingUrl + "&" + "timestamp=" + timestamp + "&" + "sign=" + s;
        System.out.println("url:" + url);
        DingTalkClient client = new DefaultDingTalkClient(url);
        OapiRobotSendRequest request = new OapiRobotSendRequest();

/*        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent("输入内容");
        request.setText(text);*/

//        request.setMsgtype("markdown");
//        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
//        markdown.setTitle("# asfasdfa");
//        markdown.setText("## ccacadfafa");
//        request.setMarkdown(markdown);


        request.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("杭州天气");
        markdown.setText("#### 杭州天气 @156xxxx8827\n" +
                "> 9度，西北风1级，空气良89，相对温度73%\n\n" +
                "> ![screenshot](https://gw.alicdn.com/tfs/TB1ut3xxbsrBKNjSZFpXXcXhFXa-846-786.png)\n"  +
                "> ###### 10点20分发布 [天气](http://www.thinkpage.cn/) \n");
        request.setMarkdown(markdown);
        OapiRobotSendResponse response = client.execute(request);
        System.out.println(response.getErrorCode());
    }

    private String encodeSign(Long timestamp) throws Exception {
        String accessToken = "SECdc6c768a124d76416d904aa6396664e45c2f28f7fb8705d8a695d125e634fb52";
        String stringToSign = timestamp + "\n" + accessToken;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(accessToken.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
        return sign;
    }

    @Test
    public void testTime(){
        Date date = new Date();
        Date date1 = DateUtils.addDays(date, -1);
        String format = DateFormatUtils.format(date1, "yyyy-MM-dd 00:00:00");
        String format1 = DateFormatUtils.format(date1, "yyyy-MM-dd 23:59:59");

        System.out.println(format);;
        System.out.println(format1);;

    }


}

