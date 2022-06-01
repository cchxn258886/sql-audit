package com.example.sqlexamine.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author chenl
 * @Date 2022/4/24 11:03 上午
 */
@Data
@TableName("slow_log_temp_text")
public class SlowLogTempTextEntity {
    @TableId(type = IdType.INPUT)
    private String id;
    private String sqlTextHash;
    private String sqlText;
    /** 添加 createTime 字段。阿里云的变更导致这张表筛选条件出现问题 */
    private Date createTime;
}
