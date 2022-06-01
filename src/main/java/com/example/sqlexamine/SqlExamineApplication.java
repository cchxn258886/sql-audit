package com.example.sqlexamine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class SqlExamineApplication {

    public static void main(String[] args) {
        SpringApplication.run(SqlExamineApplication.class, args);
    }

}
