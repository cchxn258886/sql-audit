package com.example.sqlexamine.entity.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sqlexamine.entity.po.SlowLogTemplateEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

/**
 * @Author chenl
 * @Date 2022/4/21 5:54 下午
 */
@Mapper
public interface SlowLogTemplateDao extends BaseMapper<SlowLogTemplateEntity> {
    void insertList(@Param("list") ArrayList<SlowLogTemplateEntity> slowLogTemplateEntities);
}
