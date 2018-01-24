package com.elextec.lease.manager.service;

import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.persist.model.mybatis.BizVehicle;
import com.elextec.persist.model.mybatis.SysResources;
import com.elextec.persist.model.mybatis.ext.BizVehicleExt;

import java.util.List;

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
    public PageResponse<SysResources> list(boolean needPaging, PageRequest pr);

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
    public void insertVehicles(List<BizVehicle> vehicleInfos);

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
}
