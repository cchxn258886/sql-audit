package com.example.sqlexamine.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.sqlexamine.entity.HostInfoEntity;
import com.example.sqlexamine.entity.dao.HostInfoDao;
import com.example.sqlexamine.service.HostInfoService;
import com.example.sqlexamine.utils.PageUtils;
import com.example.sqlexamine.utils.Query;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("hostInfoService")
public class HostInfoServiceImpl extends ServiceImpl<HostInfoDao, HostInfoEntity> implements HostInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<HostInfoEntity> page = this.page(
                new Query<HostInfoEntity>().getPage(params),
                new QueryWrapper<HostInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveWithCloudInfo(HostInfoEntity hostInfo) {
        //如果getCloudHostInfo 这个不为空就是代表的是插入的是云服务器信息
        if (!StringUtils.isEmpty(hostInfo.getCloudHostInfo())) {
            addCloudHost(hostInfo);
        } else {
            addCommonHost(hostInfo);
        }
    }

    private void addCloudHost(HostInfoEntity hostInfo) {
        HostInfoEntity hostInfoEntity = new HostInfoEntity();
        BeanUtils.copyProperties(hostInfo, hostInfoEntity);
        hostInfoEntity.setIp(0);
        this.baseMapper.insert(hostInfoEntity);
    }

    private void addCommonHost(HostInfoEntity hostInfo) {
        HostInfoEntity hostInfoEntity = new HostInfoEntity();
        BeanUtils.copyProperties(hostInfo, hostInfoEntity);
        hostInfoEntity.setCloudHostInfo("");
        this.baseMapper.insert(hostInfoEntity);
    }
}