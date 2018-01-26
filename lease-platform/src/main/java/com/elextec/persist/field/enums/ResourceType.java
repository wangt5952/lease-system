package com.elextec.persist.field.enums;

/**
 * 资源类别.
 * Created by wangtao on 2018/1/16.
 */
public enum ResourceType {
    CATALOG("目录"),
    MENU("菜单"),
    PAGE("页面"),
    FUNCTION("功能");

    /** Value. */
    private final String info;

    /**
     * 构造方法.
     * @param info Value
     */
    private ResourceType(String info) {
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
