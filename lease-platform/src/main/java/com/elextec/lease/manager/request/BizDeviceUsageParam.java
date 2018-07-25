package com.elextec.lease.manager.request;

import com.elextec.framework.plugins.paging.PageRequest;

/**
 * 设备统计接口查询参数类
 * create by yangkun on 2018/07/24
 */
public class BizDeviceUsageParam extends PageRequest {

    //企业id
    private String orgId;

    //设备id
    private String deviceId;

    //起始时间
    private long startTime;

    //结束时间
    private long endTime;

    @Override
    public String getOrgId() {
        return orgId;
    }

    @Override
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
