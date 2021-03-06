package com.elextec.lease.manager.service;

import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.lease.manager.request.BizVehicleParam;
import com.elextec.lease.manager.request.VehicleBatteryParam;
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
     * @param param 车辆ID
     * @param isUsed 是否查询在用电池，true：查在用电池；false：查在用及解绑的电池
     * */
    public List<Map<String,Object>> getByPrimaryKey(Map<String,Object> param, Boolean isUsed);

    /**
     * 根据用户ID查询车辆信息
     * @param id 用户ID
     * */
//    public List<BizVehicleBatteryParts> getByUserId(String id);
    public List<BizVehicleExt> getByUserId(String id);

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
     * @param param 电池编码列表
     * @return 车辆信息列表
     */
    public List<Map<String, Object>> listByBatteryCode(Map<String,Object> param);

    /**
     * 根据车辆ID查询车辆基本信息和电池信息.
     * @param paramMap 车辆ID,用户ID或企业ID
     * @param isUsed 是否查询在用电池，true：查在用电池；false：查在用及解绑的电池
     * @return 车辆扩展信息列表
     */
//    public BizVehicleBatteryParts queryBatteryInfoByVehicleId(Map<String,Object> paramMap, Boolean isUsed);
    public BizVehicleExt queryBatteryInfoByVehicleId(Map<String,Object> paramMap, Boolean isUsed);

    /**
     * 根据车辆ID查询配件信息
     * @param param 车辆ID、用户ID或企业ID
     * */
    public List<BizPartsExt> getBizPartsByVehicle(Map<String,Object> param);

    /**
     * 根据车辆ID查询配件信息
     * @param orgId 企业ID
     * */
    public int getOrgBindVehicle(String orgId);

    /**
     * 查询当前登录用户下的所有车辆
     * @param sysUserId 当前登录用户的id
     * @return 所有车辆
     */
    public List<BizVehicle> getVehicleByUserId(String sysUserId,String orgId);

    /**
     * 获得车辆列表（附带扩展信息）.
     * @param needPaging 是否需要分页
     * @param pr 带条件的分页参数
     * @return 车辆列表
     */
    public PageResponse<BizVehicleExt> selectExtUnbindExtByParams(boolean needPaging, BizVehicleParam pr);

    /**
     * 查询该企业下有多少车
     * @param pagingParam 车辆查询参数类.
     * @return 车数
     */
    public int orgCountVehicle(BizVehicleParam pagingParam);

    /**
     * 车辆回收
     * @param orgId 企业Id
     * @param loginOrgId 登录用户Id
     */
    public void vehicleRecovery(String orgId,String loginOrgId);

}
