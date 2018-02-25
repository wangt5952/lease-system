package com.elextec.lease.manager.service;

import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.lease.manager.request.BizBatteryParam;
import com.elextec.persist.model.mybatis.BizBattery;
import com.elextec.persist.model.mybatis.ext.BizBatteryExt;

import java.util.List;

/**
 * 接口 电池控制Service.
 * Created by wangtao on 2018/1/16.
 */
public interface BizBatteryService {
    /**
     * 获得电池列表.
     * @param needPaging 是否需要分页
     * @param pr 分页参数
     * @return 电池列表
     */
    public PageResponse<BizBattery> list(boolean needPaging, PageRequest pr);

    /**
     * 获得电池列表（附带扩展信息）.
     * @param needPaging 是否需要分页
     * @param pr 带条件的分页参数
     * @return 电池列表
     */
    public PageResponse<BizBatteryExt> listExtByParam(boolean needPaging, BizBatteryParam pr);

    /**
     * 批量插入电池.
     * @param batteryInfos 电池信息
     */
    public void insertBatterys(List<BizBattery> batteryInfos);

    /**
     * 插入电池.
     * @param batteryInfo 用户信息
     */
    public void insertBattery(BizBattery batteryInfo);

    /**
     * 修改电池信息.
     * @param batteryInfo 新的电池信息
     */
    public void updateBattery(BizBattery batteryInfo);

    /**
     * 批量删除电池.
     * @param ids 待删除的电池ID列表
     */
    public void deleteBattery(List<String> ids);


    /**
     * 根据ID查询电池信息
     * @param id 电池ID
     */
    public BizBattery getByPrimaryKey(String id);
}
