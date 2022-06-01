package com.example.sqlexamine.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author chenl
 * @Date 2022/4/19 10:00 上午
 * create table  access_info
 * (access_id varchar(50),access_key_secret varchar(50),primary key (access_id));
 */
@Data
@TableName("access_info")
public class AccessInfoEntity {

    @TableId(type = IdType.INPUT)
    String accessId;

    String accessKeySecret;
    String description;
}
