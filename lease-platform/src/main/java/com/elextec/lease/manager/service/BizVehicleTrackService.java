package com.elextec.lease.manager.service;

import com.elextec.persist.model.mybatis.BizVehicleTrack;

import java.util.List;

/**
 * 接口 轨迹信息管理Service.
 * Created by wangtao on 2018/1/16.
 */
public interface BizVehicleTrackService {

    /**
     * 插入轨迹信息.
     * @param trackInfo 车辆轨迹信息
     */
    public void insertVehicleTrack(BizVehicleTrack trackInfo);

    /**
     * 根据时间区间查询车辆轨迹信息.
     * @param deviceId 设备Id
     * @param deviceType 设备类别
     * @param startTime 开始时间
     * @param endTime 结束时间
     * */
    public List<BizVehicleTrack> getVehicleTracksByTime(String deviceId, String deviceType, long startTime, long endTime);

    /**
     * 根据车辆ID和时间区间查询车辆轨迹信息.
     * @param vehicleId 车辆Id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * */
    public List<BizVehicleTrack> getVehicleTracksByVehicleIdAndTime(String vehicleId, long startTime, long endTime);

    /**
     * 上传数据接口.
     * @param info 监听队列值
     */
    public void sensorData(String info);
}
