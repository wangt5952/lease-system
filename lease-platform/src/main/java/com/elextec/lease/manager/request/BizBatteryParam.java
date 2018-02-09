package com.elextec.lease.manager.request;

import com.elextec.framework.plugins.paging.PageRequest;

/**
 * 电池查询参数类.
 * Created by js_gg on 2018/2/5.
 */
public class BizBatteryParam extends PageRequest {
    /** 关键字，包括code、name、brand、pn、param、mfrsid、mfrsname. */
    private String keyStr;
    /** 电池状态. */
    private String batteryStatus;

    /**用户ID*/
    private String userId;

    /**企业ID*/
    private String orgId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /*
     * Getter 和 Setter 方法.
     */
    public String getKeyStr() {
        return keyStr;
    }

    public void setKeyStr(String keyStr) {
        this.keyStr = keyStr;
    }

    public String getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(String batteryStatus) {
        this.batteryStatus = batteryStatus;
    }
}
