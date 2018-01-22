package com.elextec.persist.field.enums;

/**
 * 记录信息状态.
 * 包括用户、企业、车辆、电池、配件等
 * Created by wangtao on 2018/1/16.
 */
public enum RecordStatus {
    NORMAL("正常"),
    FREEZE("冻结/维保"),
    INVALID("作废");

    /** Value. */
    private final String info;

    /**
     * 构造方法.
     * @param info Value
     */
    private RecordStatus(String info) {
        this.info = info;
    }

    /*
     * Getter 及 Setter方法.
     */
    public String getInfo() {
        return info;
    }
}
