package com.example.sqlexamine.service.rdsservice.context;

import lombok.Data;

/**
 * @Author chenl
 * @Date 2022/4/18 5:33 下午
 *     private static String AccessKeyID = "LTAI5tHDVAoessXQ5GVNjXZg";
 *     private static String AccessKeySecret = "UMTPwvJNHG51NRpRiddKSVdNwTf5aD";
 *     private static String regionId = "cn-hangzhou";
 *     private static String endpoint = "rm-bp1b476e1uxk2uo8goo.mysql.rds.aliyuncs.com";//测试数据库
 *     private static String endPoint2 = "rds.aliyuncs.com";
 */
@Data
public class RdsConfigContext {
//    private static final String regionId = "cn-hangzhou";
    private String AccessKeyID;
    private String AccessKeySecret;
}
