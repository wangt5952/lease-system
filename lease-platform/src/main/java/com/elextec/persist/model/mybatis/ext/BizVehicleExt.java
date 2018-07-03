package com.elextec.persist.model.mybatis.ext;

import com.elextec.persist.model.mybatis.BizVehicle;

import java.util.List;

/**
 * 车辆扩展类.
 * Created by wangtao on 2018/1/16.
 */
public class BizVehicleExt extends BizVehicle {

    /** 制商名称. */
    private String mfrsName;

    /** 电池ID. */
    private String batteryId;

    /** 车辆下所有电池信息. */
    private List<BizBatteryExt> bizBatteries;

    /** 车辆下所有配件信息. */
    private List<BizPartsExt> bizPartss;

    /** 配件数量. */
    private Integer partCount;

    /** 车辆绑定企业名称. */
    private String orgName;

    /** 车辆所属用户. */
    private String loginName;

     /*
      * Getter And Setter 方法.
      */
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

    public List<BizBatteryExt> getBizBatteries() {
        return bizBatteries;
    }

    public void setBizBatteries(List<BizBatteryExt> bizBatteries) {
        this.bizBatteries = bizBatteries;
    }

    public List<BizPartsExt> getBizPartss() {
        return bizPartss;
    }

    public void setBizPartss(List<BizPartsExt> bizPartss) {
        this.bizPartss = bizPartss;
    }

    public Integer getPartCount() {
        return partCount;
    }

    public void setPartCount(Integer partCount) {
        this.partCount = partCount;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
