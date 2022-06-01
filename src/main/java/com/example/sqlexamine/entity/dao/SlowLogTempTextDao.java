package com.example.sqlexamine.entity.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sqlexamine.entity.dto.SlowLogInfoDto;
import com.example.sqlexamine.entity.po.SlowLogTempTextEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author chenl
 * @Date 2022/4/24 11:06 上午
 */
@Mapper
public interface SlowLogTempTextDao extends BaseMapper<SlowLogTempTextEntity> {
    void insertList(@Param("list") ArrayList<SlowLogTempTextEntity> slowLogTempTextEntities);
    List<SlowLogInfoDto> getSlowLogInfo(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
