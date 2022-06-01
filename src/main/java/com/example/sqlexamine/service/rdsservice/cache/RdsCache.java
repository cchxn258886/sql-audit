package com.example.sqlexamine.service.rdsservice.cache;

import com.aliyun.rds20140815.Client;
import com.aliyun.rds20140815.models.DescribeDBInstancesRequest;
import com.aliyun.rds20140815.models.DescribeDBInstancesResponse;
import com.aliyun.rds20140815.models.DescribeDBInstancesResponseBody;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.sqlexamine.entity.AccessInfoEntity;
import com.example.sqlexamine.entity.dao.AccessInfoDao;
import com.example.sqlexamine.exception.BizException;
import com.example.sqlexamine.service.rdsservice.context.RdsClientWrapper;
import com.example.sqlexamine.service.rdsservice.context.RdsConfigContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author chenl
 * @Date 2022/4/18 5:30 下午
 */
@Component
public class RdsCache {
    @Autowired
    AccessInfoDao accessInfoDao;
    /**
     * key:环境名  value:这个id对应的配置
     */
    private static final Map<String, RdsConfigContext> rdsConfigContextMap = new ConcurrentHashMap<>();
    /**
     * key:环境名 value:对应的rds instanceId 这里需要调用rds获取到信息
     */
    private static final Map<String, List<String>> rdsInstanceConfigContextMap = new ConcurrentHashMap<>();

    public RdsConfigContext getRdsConfigContext(String envName) {
        RdsConfigContext rdsConfigContext = rdsConfigContextMap.get(envName);
        if (Objects.isNull(rdsConfigContext)){
            initCacheFromDB();
        }
        return rdsConfigContextMap.get(envName);
    }

    /**
     * 数据库通过desc 获取entity
     */
    private AccessInfoEntity getAccessInfoEntity(String envName) {
        QueryWrapper<AccessInfoEntity> accessInfoEntityQueryWrapper = new QueryWrapper<>();
        accessInfoEntityQueryWrapper.eq("description", envName);
        AccessInfoEntity accessInfoEntity = accessInfoDao.selectOne(accessInfoEntityQueryWrapper);
        if (Objects.isNull(accessInfoEntity)){
            throw new BizException("环境不存在,需要导入数据");
        }
        return accessInfoEntity;
    }

    /**
     * 从缓存中获取instanceList
     *
     * @Param envName 职培
     */
    public List<String> getInstanceList(String envName) {
//        RdsConfigContext rdsConfigContext = getRdsConfigContext(envName);
        AccessInfoEntity accessInfoEntity = getAccessInfoEntity(envName);
        if (Objects.isNull(accessInfoEntity)) {
            throw new BizException("环境不存在,需要导入数据");
        }
        List<String> list = rdsInstanceConfigContextMap.get(envName);
        if (Objects.isNull(list) || list.size() == 0){
            getConfigFromAliyun();
        }
        return  rdsInstanceConfigContextMap.get(envName);
    }


    /**
     * 从数据库缓存数据到concurrentHashMap
     */
    private synchronized void initCacheFromDB() {
        QueryWrapper<AccessInfoEntity> accessInfoEntityQueryWrapper = new QueryWrapper<>();
        List<AccessInfoEntity> accessInfoEntities = accessInfoDao.selectList(accessInfoEntityQueryWrapper);
        for (AccessInfoEntity accessInfoEntity : accessInfoEntities) {
            RdsConfigContext rdsConfigContext = new RdsConfigContext();
            rdsConfigContext.setAccessKeyID(accessInfoEntity.getAccessId());
            rdsConfigContext.setAccessKeySecret(accessInfoEntity.getAccessKeySecret());
            rdsConfigContextMap.put(accessInfoEntity.getDescription(), rdsConfigContext);
        }
        //getConfigFromAliyun(accessInfoEntities);
    }

    /**
     * 从 rds 获取到对应到数据存到concurrentHashMap
     */
    private synchronized void getConfigFromAliyun() {
        QueryWrapper<AccessInfoEntity> accessInfoEntityQueryWrapper = new QueryWrapper<>();
        List<AccessInfoEntity> accessInfoEntities = accessInfoDao.selectList(accessInfoEntityQueryWrapper);
        for (AccessInfoEntity accessInfoEntity : accessInfoEntities) {
            RdsConfigContext rdsConfigContext = new RdsConfigContext();
            rdsConfigContext.setAccessKeyID(accessInfoEntity.getAccessId());
            rdsConfigContext.setAccessKeySecret(accessInfoEntity.getAccessKeySecret());
            try {
                Client client = RdsClientWrapper.rdsClientWrapper(rdsConfigContext);
                List<String> instanceIdFromAliyun = getInstanceIdFromAliyun(client);
                rdsInstanceConfigContextMap.put(accessInfoEntity.getDescription(), instanceIdFromAliyun);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取rds instanceId 组成list返回
     */
    private List<String> getInstanceIdFromAliyun(Client client) throws Exception {
        DescribeDBInstancesRequest describeDBInstancesRequest = new DescribeDBInstancesRequest();
        describeDBInstancesRequest.setRegionId(RdsClientWrapper.regionId);
        DescribeDBInstancesResponse response = client.describeDBInstances(describeDBInstancesRequest);
        List<DescribeDBInstancesResponseBody.DescribeDBInstancesResponseBodyItemsDBInstance> dbInstanceList = response.getBody().getItems().getDBInstance();
        List<String> dbInstanceIdList = dbInstanceList.stream().filter((item) -> {
            return  item.getDBInstanceDescription().contains("生产") && "Running".equals(item.getDBInstanceStatus()) ||
                    "Running".equals(item.getDBInstanceStatus()) && item.getDBInstanceDescription().contains("prod");
        }).map((item) -> {
            return item.getDBInstanceId();
        }).collect(Collectors.toList());

//        Map<String, String> collect = dbInstanceList.stream().filter((item) -> {
//            return item.getDBInstanceDescription().contains("生产") && "Running".equals(item.getDBInstanceStatus());
//        }).collect(Collectors.toMap(
//                (item) -> {
//                    return item.getDBInstanceDescription();
//                },
//                (item) -> {
//                    return item.getDBInstanceId();
//                }
//        ));
//        System.out.println("collect"+collect.toString());
        return dbInstanceIdList;
    }

}
