package com.elextec.lease.manager.service;

import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.lease.manager.request.BizVehicleParam;
import com.elextec.lease.manager.request.VehicleBatteryParam;
import com.elextec.lease.model.BizVehicleBatteryParts;
import com.elextec.persist.model.mybatis.BizVehicle;
import com.elextec.persist.model.mybatis.ext.BizPartsExt;
import com.elextec.persist.model.mybatis.ext.BizVehicleExt;

import java.util.List;
import java.util.Map;

/**
 * 接口 车辆管理Service.
 * Created by wangtao on 2018/1/16.
 */
public interface BizVehicleService {
    /**
     * 获得车辆列表.
     * @param needPaging 是否需要分页
     * @param pr 分页参数
     * @return 车辆列表
     * @throws BizException 查询业务异常
     */
    public PageResponse<BizVehicle> list(boolean needPaging, PageRequest pr);

    /**
     * 获得车辆列表（附带扩展信息）.
     * @param needPaging 是否需要分页
     * @param pr 带条件的分页参数
     * @return 车辆列表
     */
    public PageResponse<BizVehicleExt> listExtByParam(boolean needPaging, BizVehicleParam pr);

    /**
     * 根据中心点经纬度及半径获得相关车辆信息.
     * @param lng 经度
     * @param lat 纬度
     * @param radius 半径(米)
     * @return 范围内的车辆列表
     */
    public List<BizVehicleExt> listByLocation(long lng, long lat, int radius);

    /**
     * 批量插入车辆.
     * @param vehicleInfos 车辆信息列表
     * @throws BizException 插入时异常，异常时全部数据回滚，日志记录出错记录号
     */
    public void insertVehicles(List<VehicleBatteryParam> vehicleInfos);

    /**
     * 插入车辆.
     * @param vehicleInfo 用户信息
     */
    public void insertVehicle(VehicleBatteryParam vehicleInfo);

    /**
     * 修改车辆信息.
     * @param vehicle 新的车辆信息
     */
    public void updateVehicle(BizVehicle vehicle);

    /**
     * 批量删除车辆.
     * @param ids 待删除的车辆ID列表
     */
    public void deleteVehicles(List<String> ids);

    /**
     * 根据ID查询车辆信息
     * @param id 车辆ID
     * @param isUsed 是否查询在用电池，true：查在用电池；false：查在用及解绑的电池
     * */
    public List<Map<String,Object>> getByPrimaryKey(String id, Boolean isUsed);

    /**
     * 根据用户ID查询车辆信息
     * @param id 用户ID
     * */
    public List<BizVehicleBatteryParts> getByUserId(String id);

    /**
     * 车辆与电池解绑.
     * @param vehicleId 车辆ID
     * @param batteryId 电池ID
     */
    public void unBind(String vehicleId,String batteryId);

    /**
     * 车辆与电池绑定.
     * @param vehicleId 车辆ID
     * @param batteryId 电池ID
     */
    public void bind(String vehicleId,String batteryId);

    /**
     * 根据电池编码查询车辆信息.
     * @param batteryCodes 电池编码列表
     * @return 车辆信息列表
     */
    public List<Map<String, Object>> listByBatteryCode(List<String> batteryCodes);

    /**
     * 根据车辆ID查询车辆基本信息和电池信息.
     * @param id 车辆ID
     * @param isUsed 是否查询在用电池，true：查在用电池；false：查在用及解绑的电池
     * @return 车辆扩展信息列表
     */
    public BizVehicleBatteryParts queryBatteryInfoByVehicleId(String id, Boolean isUsed);

    /**
     * 根据车辆ID查询配件信息
     * @param id 车辆ID
     * */
    public List<BizPartsExt> getBizPartsByVehicle(String id);

}
