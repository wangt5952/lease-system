package com.elextec.persist.model.mybatis.ext;

import com.elextec.persist.model.mybatis.BizBattery;

/**
 * 电池扩展类
 * Created by js_gg on 2018/2/2.
 */
public class BizBatteryExt extends BizBattery {

    //制商名称
    private String mfrsName;

    //车辆ID
    private String vehicleId;

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getMfrsName() {
        return mfrsName;
    }

    public void setMfrsName(String mfrsName) {
        this.mfrsName = mfrsName;
    }
}
