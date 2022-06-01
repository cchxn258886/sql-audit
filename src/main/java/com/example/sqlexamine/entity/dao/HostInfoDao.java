package com.example.sqlexamine.entity.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sqlexamine.entity.HostInfoEntity;
import com.example.sqlexamine.entity.dto.HostInfoDto;
import org.apache.ibatis.annotations.Mapper;

/**
 * 主机信息表
 * 
 * @author chenl
 * @email 244766516@qq.com
 * @date 2022-03-29 16:43:30
 */
@Mapper
public interface HostInfoDao extends BaseMapper<HostInfoEntity> {
    HostInfoDto getById(String uuid);
}
