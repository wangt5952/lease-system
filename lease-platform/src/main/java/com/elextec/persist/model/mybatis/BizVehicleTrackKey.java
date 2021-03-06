package com.elextec.persist.model.mybatis;

import java.io.Serializable;

public class BizVehicleTrackKey implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table biz_vehicle_track
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = -439033523811655318L;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column biz_vehicle_track.device_id
     *
     * @mbggenerated
     */
    private String deviceId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column biz_vehicle_track.device_type
     *
     * @mbggenerated
     */
    private String deviceType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column biz_vehicle_track.start_time
     *
     * @mbggenerated
     */
    private Long startTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column biz_vehicle_track.device_id
     *
     * @return the value of biz_vehicle_track.device_id
     *
     * @mbggenerated
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column biz_vehicle_track.device_id
     *
     * @param deviceId the value for biz_vehicle_track.device_id
     *
     * @mbggenerated
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId == null ? null : deviceId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column biz_vehicle_track.device_type
     *
     * @return the value of biz_vehicle_track.device_type
     *
     * @mbggenerated
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column biz_vehicle_track.device_type
     *
     * @param deviceType the value for biz_vehicle_track.device_type
     *
     * @mbggenerated
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType == null ? null : deviceType.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column biz_vehicle_track.start_time
     *
     * @return the value of biz_vehicle_track.start_time
     *
     * @mbggenerated
     */
    public Long getStartTime() {
        return startTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column biz_vehicle_track.start_time
     *
     * @param startTime the value for biz_vehicle_track.start_time
     *
     * @mbggenerated
     */
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }
}