package com.elextec.persist.dao.mybatis;

import com.elextec.lease.manager.request.BizBatteryParam;
import com.elextec.persist.model.mybatis.BizBattery;
import com.elextec.persist.model.mybatis.BizBatteryExample;
import com.elextec.persist.model.mybatis.BizVehicle;
import com.elextec.persist.model.mybatis.ext.BizBatteryExt;

import java.util.List;
import java.util.Map;

public interface BizBatteryMapperExt extends BizBatteryMapper {
    /**
     * 查询电池扩展信息列表.
     * @param batteryParam 查询条件
     * @return 电池扩展信息列表
     */
    List<BizBatteryExt> selectExtByParam(BizBatteryParam batteryParam);

    /**
     * 查询电池扩展信息记录数.
     * @param batteryParam 查询条件
     * @return 电池扩展信息记录数
     */
    int countExtByParam(BizBatteryParam batteryParam);

    /**
     * 根据车辆ID获取电池信息.
     */
    public List<BizBatteryExt> getBatteryInfoByVehicleId(Map<String,Object> param);

    /*
     * 根据电池ID查找电池信息扩展.
     */
    BizBatteryExt getBatteryInfoByBatteryId(Map<String,Object> param);

    /**
     * 根据设备ID获取车辆ID.
     * @param deviceCode 设备ID（电池编号）
     * @return 车辆ID
     */
    BizBatteryExt getVehicleIdByDeviceId(String deviceCode);

    /**
     * 根据设备id获取电池信息
     * @param deviceId 设备id
     * @return 关联的电池信息
     */
    BizBattery getBizBatteryInfoByDevice(String deviceId);
}
