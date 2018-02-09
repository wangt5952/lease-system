package com.elextec.lease.manager.request;

import com.elextec.framework.plugins.paging.PageRequest;

/**
 * 车辆查询参数类.
 * Created by js_gg on 2018/2/5.
 */
public class BizVehicleParam extends PageRequest {
    /** 关键字，包括code、brand、pn、madein、mfrsid、mfrsname. */
    private String keyStr;
    /** 车辆状态. */
    private String vehicleStatus;

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

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }
}
