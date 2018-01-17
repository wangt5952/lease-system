package com.elextec.lease.manager.persist.enums;

/**
 * 显示标志.
 * Created by wangtao on 2018/1/16.
 */
public enum ShowFlag {
    SHOW("显示"),
    HIDDEN("隐藏");

    /** Value. */
    private final String info;

    /**
     * 构造方法.
     * @param info Value
     */
    private ShowFlag(String info) {
        this.info = info;
    }

    /*
     * Getter 及 Setter方法.
     */
    public String getInfo() {
        return info;
    }
}
