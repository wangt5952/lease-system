package com.elextec.persist.dao.mybatis;

import com.elextec.lease.manager.request.BizVehicleParam;
import com.elextec.lease.model.BizVehicleBatteryParts;
import com.elextec.persist.model.mybatis.ext.BizVehicleExt;

import java.util.List;
import java.util.Map;

public interface BizVehicleMapperExt extends BizVehicleMapper {
    /**
     * 根据ID查询车辆信息.
     * @param param 车辆ID,是否查询在用电池
     * @return 车辆信息
     */
    List<Map<String, Object>> getVehicleInfoById(Map<String, Object> param);

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

    /**
     * 根据电池编码查询车辆扩展信息列表.
     * @param batteryCodes 查询条件（电池编码列表）
     * @return 车辆扩展信息列表
     */
    List<Map<String, Object>> selectExtByBatteryCodes(List<String> batteryCodes);

//    /**
//     * 查询车辆是否已被绑定
//     * @param id 车辆ID
//     * */
//    int isBindOrUnBind(String id);
//
//    /**
//     * 用户与车辆解绑
//     * @param param 车辆ID与用户ID
//     * */
//    int vehicleUnBind(Map<String,Object> param);
//
//    /**
//     * 用户与车辆绑定
//     * @param param 车辆ID与用户ID
//     * */
//    int vehicleBind(Map<String,Object> param);
}
