package com.elextec.lease.manager.service;

import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.persist.model.mybatis.BizBattery;

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
     * @return 用户列表
     * @throws BizException 查询业务异常
     */
    public PageResponse<BizBattery> list(boolean needPaging, PageRequest pr);

    /**
     * 批量插入电池.
     * @param batteryInfos 电池信息
     * @throws BizException 插入时异常，异常时全部数据回滚，日志记录出错记录号
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
     * */
    public BizBattery getBatteryByPrimaryKey(String id);
}
