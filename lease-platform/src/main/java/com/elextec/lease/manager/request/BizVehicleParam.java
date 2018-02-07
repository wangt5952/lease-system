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
