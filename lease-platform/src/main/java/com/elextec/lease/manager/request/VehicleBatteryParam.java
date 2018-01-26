package com.elextec.lease.manager.request;

import com.elextec.persist.field.enums.RecordStatus;
import com.elextec.persist.model.mybatis.BizBattery;
import com.elextec.persist.model.mybatis.BizVehicle;

import java.util.Date;

/**
 * 批量添加车辆参数类（车辆与电池配对）
 * Created by js_gg on 2018/1/26.
 */
public class VehicleBatteryParam {

    private BizVehicle bizVehicleInfo;

    private String flag;

    private BizBattery batteryInfo;

    public BizVehicle getBizVehicleInfo() {
        return bizVehicleInfo;
    }

    public void setBizVehicleInfo(BizVehicle bizVehicleInfo) {
        this.bizVehicleInfo = bizVehicleInfo;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public BizBattery getBatteryInfo() {
        return batteryInfo;
    }

    public void setBatteryInfo(BizBattery batteryInfo) {
        this.batteryInfo = batteryInfo;
    }
}
