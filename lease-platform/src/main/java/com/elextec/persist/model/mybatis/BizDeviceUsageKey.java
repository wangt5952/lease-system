package com.elextec.persist.model.mybatis;

import java.io.Serializable;
import java.util.Date;

public class BizDeviceUsageKey implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table biz_device_usage
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = -1212875747113267414L;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column biz_device_usage.device_id
     *
     * @mbggenerated
     */
    private String deviceId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column biz_device_usage.rec_time
     *
     * @mbggenerated
     */
    private Date recTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column biz_device_usage.device_id
     *
     * @return the value of biz_device_usage.device_id
     *
     * @mbggenerated
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column biz_device_usage.device_id
     *
     * @param deviceId the value for biz_device_usage.device_id
     *
     * @mbggenerated
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId == null ? null : deviceId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column biz_device_usage.rec_time
     *
     * @return the value of biz_device_usage.rec_time
     *
     * @mbggenerated
     */
    public Date getRecTime() {
        return recTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column biz_device_usage.rec_time
     *
     * @param recTime the value for biz_device_usage.rec_time
     *
     * @mbggenerated
     */
    public void setRecTime(Date recTime) {
        this.recTime = recTime;
    }
}