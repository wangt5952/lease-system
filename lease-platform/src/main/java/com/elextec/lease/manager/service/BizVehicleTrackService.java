package com.elextec.lease.manager.service;

import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.lease.manager.request.BizVehicleParam;
import com.elextec.lease.manager.request.VehicleBatteryParam;
import com.elextec.lease.model.BizVehicleBatteryParts;
import com.elextec.persist.model.mybatis.BizVehicle;
import com.elextec.persist.model.mybatis.BizVehicleTrack;
import com.elextec.persist.model.mybatis.ext.BizVehicleExt;

import java.util.List;
import java.util.Map;

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
     * 根据时间区间查询车辆轨迹信息
     * @param deviceId 设备id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * */
    public List<BizVehicleTrack> getVehicleTracksByTime(String deviceId,long startTime,long endTime);

}
