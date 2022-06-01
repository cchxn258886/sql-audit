package com.example.sqlexamine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.sqlexamine.entity.dto.SlowLogInfoDto;
import com.example.sqlexamine.entity.po.SlowLogTemplateEntity;

import java.util.Date;
import java.util.List;

/**
 * @Author chenl
 * @Date 2022/4/28 1:53 下午
 */
public interface SlowLogTempService extends IService<SlowLogTemplateEntity> {
    List<SlowLogInfoDto> getSlowLogInfo(Date startTime, Date endTime);

}
