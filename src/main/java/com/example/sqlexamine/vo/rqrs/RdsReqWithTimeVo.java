package com.example.sqlexamine.vo.rqrs;

import com.example.sqlexamine.exception.BizException;
import lombok.Data;
import lombok.ToString;

/**
 * @Author chenl
 * @Date 2022/4/22 10:24 上午
 */
@Data
@ToString
public class RdsReqWithTimeVo extends RdsReqVo implements ApiRQContext {
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 是否使用 异步
     */
    private Boolean isAsync = false;

    @Override
    public void check() throws BizException {

    }

    ;
}
