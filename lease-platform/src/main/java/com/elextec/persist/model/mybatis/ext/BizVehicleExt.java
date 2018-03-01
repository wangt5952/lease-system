package com.elextec.persist.model.mybatis.ext;

import com.elextec.persist.model.mybatis.BizVehicle;

/**
 * 车辆扩展类.
 * Created by wangtao on 2018/1/16.
 */
public class BizVehicleExt extends BizVehicle {

    //制商名称
    private String mfrsName;

    //电池ID
    private String batteryId;

    public String getBatteryId() {
        return batteryId;
    }

    public void setBatteryId(String batteryId) {
        this.batteryId = batteryId;
    }

    public String getMfrsName() {
        return mfrsName;
    }

    public void setMfrsName(String mfrsName) {
        this.mfrsName = mfrsName;
    }
}
