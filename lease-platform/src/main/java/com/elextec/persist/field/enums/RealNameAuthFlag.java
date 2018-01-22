package com.elextec.persist.field.enums;

/**
 * 实名认证标志.
 * Created by wangtao on 2018/1/16.
 */
public enum RealNameAuthFlag {
    AUTHORIZED("已实名认证"),
    UNAUTHORIZED("未实名认证");

    /** Value. */
    private final String info;

    /**
     * 构造方法.
     * @param info Value
     */
    private RealNameAuthFlag(String info) {
        this.info = info;
    }

    /*
     * Getter 及 Setter方法.
     */
    public String getInfo() {
        return info;
    }
}
