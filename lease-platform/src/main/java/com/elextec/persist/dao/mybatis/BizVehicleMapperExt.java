package com.elextec.persist.dao.mybatis;

import com.elextec.lease.manager.request.BizVehicleParam;
import com.elextec.lease.model.BizVehicleBatteryParts;
import com.elextec.persist.model.mybatis.ext.BizVehicleExt;

import java.util.List;
import java.util.Map;

public interface BizVehicleMapperExt extends BizVehicleMapper {
    /**
     * 根据ID查询车辆信息.
     * @param id 车辆ID
     * @return 车辆信息
     */
    Map<String, Object> getVehicleInfoById(String id);

    /**
     * 根据用户ID查询车辆信息.
     * @param id 用户ID
     * @return 车辆信息列表
     */
    List<BizVehicleBatteryParts> getVehicleInfoByUserId(String id);

    /**
     * 查询车辆扩展信息列表.
     * @param bizVehicleParam 查询条件
     * @return 车辆扩展信息列表
     */
    List<BizVehicleExt> selectExtByParam(BizVehicleParam bizVehicleParam);

    /**
     * 查询车辆扩展信息记录数.
     * @param bizVehicleParam 查询条件
     * @return 车辆扩展信息记录数
     */
    int countExtByParam(BizVehicleParam bizVehicleParam);
}
