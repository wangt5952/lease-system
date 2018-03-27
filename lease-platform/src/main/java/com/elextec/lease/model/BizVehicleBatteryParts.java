package com.elextec.lease.model;

import com.elextec.persist.model.mybatis.ext.BizBatteryExt;
import com.elextec.persist.model.mybatis.ext.BizPartsExt;
import com.elextec.persist.model.mybatis.ext.BizVehicleExt;

import java.util.List;

/**
 * 用户车辆信息返回对象
 * Created by js_gg on 2018/2/2.
 */
public class BizVehicleBatteryParts extends BizVehicleExt {
    //车辆下所有电池信息
    public List<BizBatteryExt> bizBatteries;

    //车辆下所有配件信息
    public List<BizPartsExt> bizPartss;

    //配件数量
    public Integer partCount;

    public Integer getPartCount() {
        return partCount;
    }

    public void setPartCount(Integer partCount) {
        this.partCount = partCount;
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

}
