package com.elextec.persist.field.enums;

/**
 * 申请类型和状态
 * Created by wangtao on 2018/1/29.
 */
public enum ApplyTypeAndStatus {
    VEHICLEAPPLY("车辆申请"),
    TOBEAUDITED("待审核"),
    AGREE("同意"),
    REJECT("驳回");


    /** Value. */
    private final String info;

    /**
     * 构造方法.
     * @param info Value
     */
    private ApplyTypeAndStatus(String info) {
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
