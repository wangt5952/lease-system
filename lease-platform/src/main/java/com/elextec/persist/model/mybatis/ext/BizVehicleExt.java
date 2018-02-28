package com.elextec.persist.model.mybatis.ext;

import com.elextec.persist.model.mybatis.BizVehicle;

/**
 * 车辆扩展类.
 * Created by wangtao on 2018/1/16.
 */
public class BizVehicleExt extends BizVehicle {

    //制商名称
    private String mfrsName;

    public String getMfrsName() {
        return mfrsName;
    }

    public void setMfrsName(String mfrsName) {
        this.mfrsName = mfrsName;
    }
}
