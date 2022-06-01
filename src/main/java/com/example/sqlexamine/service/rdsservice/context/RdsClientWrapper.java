package com.example.sqlexamine.service.rdsservice.context;

import com.aliyun.rds20140815.Client;
import com.aliyun.tea.TeaModel;
import com.aliyun.teaopenapi.models.Config;
import com.example.sqlexamine.entity.AccessInfoEntity;
import lombok.Data;

/**
 * @Author chenl
 * @Date 2022/4/19 1:49 下午
 */
@Data
public class RdsClientWrapper {
    public static final String endPoint = "rds.aliyuncs.com";
    public static final String regionId = "cn-hangzhou";

    private Client rdsClient;
    private int readTimeout;
    private int connectTimeout;

    private RdsClientWrapper(){
        return;
    }

    public static Client rdsClientWrapper(RdsConfigContext param) throws Exception {
        Config config = new Config();
        config.setEndpoint(endPoint);
        config.setAccessKeyId(param.getAccessKeyID());
        config.setAccessKeySecret(param.getAccessKeySecret());
        Client client = new Client(config);
        return client;
    }

    public static Client rdsClientWrapper(RdsConfigContext param,int timeout) throws Exception {
        Config config = new Config();
        config.setEndpoint(endPoint);
        config.setAccessKeyId(param.getAccessKeyID());
        config.setAccessKeySecret(param.getAccessKeySecret());
        Client client = new Client(config);
        client._connectTimeout = timeout;
        client._readTimeout = timeout;
        return client;
    }
}
