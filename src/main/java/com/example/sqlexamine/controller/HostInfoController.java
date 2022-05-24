package com.example.sqlexamine.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.example.sqlexamine.entity.HostInfoEntity;
import com.example.sqlexamine.service.HostInfoService;
import com.example.sqlexamine.utils.PageUtils;
import com.example.sqlexamine.utils.Resp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;





/**
 * 主机信息表
 *
 * @author chenl
 * @email 244766516@qq.com
 * @date 2022-03-29 16:43:30
 */
@RestController
@RequestMapping("/api/hostinfo")
public class HostInfoController {
    @Autowired
    private HostInfoService hostInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Resp list(@RequestParam Map<String, Object> params){
        PageUtils page = hostInfoService.queryPage(params);
        return Resp.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{uuid}")
    public Resp info(@PathVariable("uuid") String uuid){
		HostInfoEntity hostInfo = hostInfoService.getById(uuid);

        return Resp.ok().put("hostInfo", hostInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Resp save(@RequestBody HostInfoEntity hostInfo){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        hostInfo.setUuid(uuid);
        hostInfo.setCreateTime(new Date());
        hostInfo.setUpdateTime(new Date());
		hostInfoService.saveWithCloudInfo(hostInfo);
        return Resp.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Resp update(@RequestBody HostInfoEntity hostInfo){
		hostInfoService.updateById(hostInfo);

        return Resp.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Resp delete(@RequestBody String[] uuids){
		hostInfoService.removeByIds(Arrays.asList(uuids));

        return Resp.ok();
    }

}
