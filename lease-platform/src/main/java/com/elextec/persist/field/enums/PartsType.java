package com.elextec.persist.field.enums;

/**
 * 配件类别.
 * Created by wangtao on 2018/1/16.
 */
public enum PartsType {
    SEATS("车座");

    /** Value. */
    private final String info;

    /**
     * 构造方法.
     * @param info Value
     */
    private PartsType(String info) {
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
