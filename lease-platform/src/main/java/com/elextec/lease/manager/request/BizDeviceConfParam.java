package com.elextec.lease.manager.request;

import com.elextec.framework.plugins.paging.PageRequest;

/**
 * 设备控制参数查询条件.
 * Created by wangtao on 2018/2/5.
 */
public class BizDeviceConfParam extends PageRequest {
    /** 关键字，包括device_id. */
    private String keyStr;

    /** 设备类别. */
    private String deviceType;

    /*
     * Getter 和 Setter 方法.
     */
    public String getKeyStr() {
        return keyStr;
    }

    public void setKeyStr(String keyStr) {
        this.keyStr = keyStr;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
