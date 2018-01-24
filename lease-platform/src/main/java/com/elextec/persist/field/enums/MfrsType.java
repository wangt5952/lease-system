package com.elextec.persist.field.enums;

/**
 * 制造商类别.
 * Created by wangtao on 2018/1/16.
 */
public enum MfrsType {
    VEHICLE("车辆"),
    BATTERY("电池"),
    PARTS("配件");

    /** Value. */
    private final String info;

    /**
     * 构造方法.
     * @param info Value
     */
    private MfrsType(String info) {
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
