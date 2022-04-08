package com.example.sqlexamine.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 主机信息表
 * 
 * @author chenl
 * @email 244766516@qq.com
 * @date 2022-03-29 16:43:30
 */
@Data
@TableName("host_info")
public class HostInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(type = IdType.INPUT)
	private String uuid;
	/**
	 * 远程ip地址或者域名
	 */
	private Integer ip;
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
