package com.example.sqlexamine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author chenl
 * @Date 2022/3/28 10:24 上午
 */
@Configuration
@ConfigurationProperties("examine.datetime.judge")
@Data
public class SqlExamineConfig {
    private boolean enable = false;
}
