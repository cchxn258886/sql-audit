package com.example.sqlexamine.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

/**
 * @Author chenl
 * @Date 2022/3/29 4:14 下午
 */
@Configuration
@EnableTransactionManagement
@MapperScan("com.example.sqlexamine.entity.dao")
public class MybatisConfig {
    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor paginationInterceptor = new MybatisPlusInterceptor();
//        //设置请求的页面大于最大页后操作。true跳回到首页。false 继续亲求
//        paginationInterceptor.setOverflow(true);
//        //设置最大单叶限制数量
//        paginationInterceptor.setLimit(500);
//        return paginationInterceptor;
        paginationInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        Properties properties = new Properties();
        properties.put("LIMIT", 500);
        properties.put("OVERFLOW", true);
        paginationInterceptor.setProperties(properties);
        return paginationInterceptor;
    }
}
