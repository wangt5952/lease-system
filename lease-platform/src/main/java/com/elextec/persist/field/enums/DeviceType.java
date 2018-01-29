package com.elextec.persist.field.enums;

/**
 * 设备类别.
 * Created by wangtao on 2018/1/29.
 */
public enum DeviceType {
    VEHICLE("车辆"),
    BATTERY("电池"),
    PARTS("配件");

    /** Value. */
    private final String info;

    /**
     * 构造方法.
     * @param info Value
     */
    private DeviceType(String info) {
        this.info = info;
    }

    /*
     * Getter 及 Setter方法.
     */
    public String getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
