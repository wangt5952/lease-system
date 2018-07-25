package com.elextec.persist.model.mybatis.ext;

import com.elextec.persist.model.mybatis.BizDeviceConf;

public class BizDeviceConfExt extends BizDeviceConf {

    private String electric;//电量

    private String location;//定位

    public String getElectric() {
        return electric;
    }

    public void setElectric(String electric) {
        this.electric = electric;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
