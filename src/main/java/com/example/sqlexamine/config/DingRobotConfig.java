package com.example.sqlexamine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author chenl
 * @Date 2022/4/26 3:05 下午
 * dingding 机器人配置类
 */
@ConfigurationProperties("examine.ding.robot")
@Configuration
@Data
public class DingRobotConfig {
    private String sign ;
    private String accessToken;
}
