package com.elextec.persist.field.enums;

/**
 * 组织类别.
 * Created by wangtao on 2018/1/16.
 */
public enum OrgAndUserType {
    PLATFORM("平台"),
    ENTERPRISE("企业"),
    INDIVIDUAL("个人");

    /** Value. */
    private final String info;

    /**
     * 构造方法.
     * @param info Value
     */
    private OrgAndUserType(String info) {
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
