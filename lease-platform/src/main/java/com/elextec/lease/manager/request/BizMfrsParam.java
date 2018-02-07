package com.elextec.lease.manager.request;

import com.elextec.framework.plugins.paging.PageRequest;

/**
 * 制造商查询参数类
 * Create By Yangkun on 2018/2/6
 */
public class BizMfrsParam extends PageRequest {

    /* 查询关键字，包括name、introduce、address. */
    private String keyStr;

    /* 制造商类型. */
    private String mfrsType;

    /* 制造商状态. */
    private String mfrsStatus;

    /*
     * Getter 和 Setter 方法.
     */
    public String getKeyStr() {
        return keyStr;
    }

    public void setKeyStr(String keyStr) {
        this.keyStr = keyStr;
    }

    public String getMfrsType() {
        return mfrsType;
    }

    public void setMfrsType(String mfrsType) {
        this.mfrsType = mfrsType;
    }

    public String getMfrsStatus() {
        return mfrsStatus;
    }

    public void setMfrsStatus(String mfrsStatus) {
        this.mfrsStatus = mfrsStatus;
    }
}
