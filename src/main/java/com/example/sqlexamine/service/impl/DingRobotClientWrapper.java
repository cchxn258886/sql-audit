package com.example.sqlexamine.service.impl;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.example.sqlexamine.utils.RdsTimeUtils;
import com.taobao.api.ApiException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @Author chenl
 * @Date 2022/4/27 3:45 下午
 */
@Slf4j
@AllArgsConstructor

public class DingRobotClientWrapper {
    private static String markdown = "markdown";
    private static String text = "text";
    private static String link = "link";
    private static final String robotUrlPrefix = "https://oapi.dingtalk.com/robot/send?access_token=";
    private DingTalkClient dingTalkClient;
    private OapiRobotSendRequest request;

    public static DingRobotClientWrapper dingTalkClientBuild(String accessToken, String sign) {
        String dingdingUrl = robotUrlPrefix + accessToken;
        long timestamp = System.currentTimeMillis();
        String s = null;
        DingTalkClient dingTalkClient = null;
        try {
            s = RdsTimeUtils.encodeDingRobotSign(timestamp, sign);
        } catch (Exception e) {
            log.error("dingdingRobot加密失败:"+e.getMessage());
            dingTalkClient = null;
        }
        String url = dingdingUrl + "&" + "timestamp=" + timestamp + "&" + "sign=" + s;
        dingTalkClient = new DefaultDingTalkClient(url);
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        return new DingRobotClientWrapper(dingTalkClient,request);
    }

    public void exec(String type,String msg) {
        if (type.equalsIgnoreCase(markdown)){
            execMarkDown(msg);
        }else if(type.equalsIgnoreCase(text)){

        }
    }

    private void execMarkDown(String msg)  {
        request.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("慢日志模版");
        markdown.setText(msg);
        request.setMarkdown(markdown);
        try {
            dingTalkClient.execute(request);
        } catch (ApiException e) {
            log.error("机器人发送消息失败");
        }
    }


}
