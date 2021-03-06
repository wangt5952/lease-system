package com.elextec.persist.dao.mybatis;

import com.elextec.lease.manager.request.BizVehicleParam;
import com.elextec.persist.model.mybatis.BizVehicle;
import com.elextec.persist.model.mybatis.SysUser;
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
//    List<BizVehicleBatteryParts> getVehicleInfoByUserId(String id);
    List<BizVehicleExt> getVehicleInfoByUserId(String id);

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
     * @param paramMap 查询条件（电池编码列表）
     * @return 车辆扩展信息列表
     */
    List<Map<String, Object>> selectExtByBatteryCodes(Map<String,Object> paramMap);


    /**
     * 根据车辆ID查找车辆基本信息
     * @param paramMap 查询条件（车辆ID、用户ID或企业ID）
     * @return 车辆基本信息列表
     * */
//    BizVehicleBatteryParts getVehicleInfoByVehicleId(Map<String,Object> paramMap);
    BizVehicleExt getVehicleInfoByVehicleId(Map<String,Object> paramMap);

    /**
     * 查询未绑定车辆 分页
     * @param bizVehicleParam 分页参数
     * @return 车辆列表
     */
    public List<BizVehicleExt> selectExtUnbindExtByParams(BizVehicleParam bizVehicleParam);

    /**
     * 未绑定车辆数量
     * @param bizVehicleParam
     * @return 个数
     */
    public int countExtUnbindExtByParam(BizVehicleParam bizVehicleParam);

    /**
     * 根据电池id查询绑定的车辆信息
     * @param batteryId 电池id
     * @return 车辆信息
     */
    BizVehicle getBizVehicleInfoByBattery(String batteryId);

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
