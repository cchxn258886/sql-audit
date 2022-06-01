package com.example.sqlexamine.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.sqlexamine.entity.HostInfoEntity;
import com.example.sqlexamine.utils.PageUtils;

import java.util.Map;

/**
 * 主机信息表
 *
 * @author chenl
 * @email 244766516@qq.com
 * @date 2022-03-29 16:43:30
 */
public interface HostInfoService extends IService<HostInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveWithCloudInfo(HostInfoEntity hostInfo);
}

