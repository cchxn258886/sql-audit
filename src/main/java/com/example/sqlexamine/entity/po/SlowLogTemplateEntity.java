package com.example.sqlexamine.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author chenl
 * @Date 2022/4/21 5:36 下午
 */
@Data
@TableName("slow_log_template")
@AllArgsConstructor
@NoArgsConstructor
public class SlowLogTemplateEntity {
    @TableId(type = IdType.INPUT)
    private String uuid;
    /**
     * 创建的时间
     */
    private Date createTime;
    /**
     * sql 模版 such:select * from tablename where id in ($1,$2);
     */
    private String sqlTextHash;
    /**
     * 最大执行时间
     */
    private Long maxExecTime;
    /**
     * 数据库名
     */
    private String dbName;
    /**
     * 实例 id
     */
    private String instanceId;
}
