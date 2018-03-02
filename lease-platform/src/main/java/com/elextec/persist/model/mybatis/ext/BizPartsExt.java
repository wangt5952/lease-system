package com.elextec.persist.model.mybatis.ext;

import com.elextec.persist.model.mybatis.BizParts;

import java.util.Date;

/**
 * 配件扩展类
 * Created by js_gg on 2018/2/2.
 */
public class BizPartsExt extends BizParts {

    //制商名称
    private String mfrsName;

    //车辆id
    private String vehicleId;

    //绑定时间
    private Date bindTime;

    //解绑时间
    private Date unbindTime;

    public Date getBindTime() {
        return bindTime;
    }

    public void setBindTime(Date bindTime) {
        this.bindTime = bindTime;
    }

    public Date getUnbindTime() {
        return unbindTime;
    }

    public void setUnbindTime(Date unbindTime) {
        this.unbindTime = unbindTime;
    }

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
