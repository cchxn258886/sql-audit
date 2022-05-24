package com.example.sqlexamine.entity.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sqlexamine.entity.AccessInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author chenl
 * @Date 2022/4/19 9:59 上午
 * 数据库 AeecssInfo
 */
@Mapper
public interface AccessInfoDao extends BaseMapper<AccessInfoEntity> {
}
