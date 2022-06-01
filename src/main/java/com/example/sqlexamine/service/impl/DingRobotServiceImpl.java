package com.example.sqlexamine.service.impl;

import com.example.sqlexamine.config.DingRobotConfig;
import com.example.sqlexamine.entity.dao.SlowLogTemplateDao;
import com.example.sqlexamine.exception.BizException;
import com.example.sqlexamine.service.DingRobotService;
import com.example.sqlexamine.vo.rqrs.DingRobotReqVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @Author chenl
 * @Date 2022/4/27 2:46 下午
 */
@Service
@Slf4j
public class DingRobotServiceImpl implements DingRobotService {

    @Autowired
    DingRobotConfig dingRobotConfig;
    @Autowired
    SlowLogTemplateDao slowLogTemplateDao;

    @Override
    public <T> void sendMsg(DingRobotReqVo<T> vo) {
        DingRobotClientWrapper dingRobotClientWrapper = DingRobotClientWrapper.dingTalkClientBuild(dingRobotConfig.getAccessToken(), dingRobotConfig.getSign());
        dingRobotClientWrapper.exec(vo.getMsgType(),vo.getMsg().toString());
    }

}
