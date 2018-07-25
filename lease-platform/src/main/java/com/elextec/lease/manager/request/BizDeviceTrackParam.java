package com.elextec.lease.manager.request;

import com.elextec.framework.plugins.paging.PageRequest;

/**
 * 设备轨迹查询参数表
 * create by yangkun on 2018/07/17
 */
public class BizDeviceTrackParam extends PageRequest {

    /**设备id.*/
    private String keyStr;

    public String getKeyStr() {
        return keyStr;
    }

    public void setKeyStr(String keyStr) {
        this.keyStr = keyStr;
    }
}
