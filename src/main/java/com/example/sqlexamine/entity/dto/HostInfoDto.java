package com.example.sqlexamine.entity.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author chenl
 * @Date 2022/3/30 5:38 下午
 */
@Data
public class HostInfoDto {
    private String uuid;
    /**
     * 远程ip地址或者域名
     */
    private String  ip;
    /**
     * 用户名

     */

    private String username;
    /**
     * 密码

     */
    private String hostPassword;
    /**
     * 描述
     */
    private String hostDesc;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 云主机名
     * */
    private String cloudHostInfo;
}
