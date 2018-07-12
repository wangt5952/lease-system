package com.elextec.persist.model.mybatis;

import com.elextec.persist.field.enums.DeviceType;
import java.io.Serializable;

public class BizDeviceConfKey implements Serializable {

    private static final long serialVersionUID = -39125040178059025L;

    private String deviceId;

    private DeviceType deviceType;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId == null ? null : deviceId.trim();
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }
}