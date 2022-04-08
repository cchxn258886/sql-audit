package com.example.sqlexamine.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author chenl
 * @Date 2022/3/29 4:14 下午
 */
@Configuration
@EnableTransactionManagement
@MapperScan("com.example.sqlexamine.entity.dao")
public class MybatisConfig {
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        //设置请求的页面大于最大页后操作。true跳回到首页。false 继续亲求
        paginationInterceptor.setOverflow(true);
        //设置最大单叶限制数量
        paginationInterceptor.setLimit(500);
        return paginationInterceptor;
    }
}
