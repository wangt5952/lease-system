package com.elextec.persist.field.enums;

/**
 * 绑定状态.
 * Created by wangtao on 2018/1/29.
 */
public enum BindStatus {
    BIND("已绑定"),
    UNBIND("未绑定");

    /** Value. */
    private final String info;

    /**
     * 构造方法.
     * @param info Value
     */
    private BindStatus(String info) {
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
