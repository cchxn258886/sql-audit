package com.example.sqlexamine.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.sqlexamine.entity.dao.SlowLogTempTextDao;
import com.example.sqlexamine.entity.dao.SlowLogTemplateDao;
import com.example.sqlexamine.entity.dto.SlowLogInfoDto;
import com.example.sqlexamine.entity.po.SlowLogTemplateEntity;
import com.example.sqlexamine.exception.BizException;
import com.example.sqlexamine.service.SlowLogTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author chenl
 * @Date 2022/4/28 1:55 下午
 */
@Service
public class SlowLogTempServiceImpl extends ServiceImpl<SlowLogTemplateDao, SlowLogTemplateEntity> implements SlowLogTempService {
    @Autowired
    SlowLogTempTextDao slowLogTempTextDao;
    @Override
    public List<SlowLogInfoDto> getSlowLogInfo(Date startTime, Date endTime) {
        if (Objects.isNull(startTime) || Objects.isNull(endTime)){
            throw new BizException("开始时间或者结束时间为空");
        }
        return slowLogTempTextDao.getSlowLogInfo(startTime,endTime);

    }
}
