package com.elextec.lease.manager.service;

import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.lease.manager.request.BizBatteryParam;
import com.elextec.lease.manager.request.BizDeviceConfParam;
import com.elextec.persist.model.mybatis.BizDeviceConf;
import com.elextec.persist.model.mybatis.BizDeviceConfKey;

import java.util.List;
import java.util.Map;

/**
 * 接口 设备参数设置管理Service.
 * Created by wangtao on 2018/1/16.
 */
public interface BizDeviceConfService {
    /**
     * 获得设备参数列表.
     * @param needPaging 是否需要分页
     * @param pr 分页参数
     * @return 设备参数列表
     */
    public PageResponse<BizDeviceConf> list(boolean needPaging, PageRequest pr);

    /**
     * 获得设备参数列表.
     * @param needPaging 是否需要分页
     * @param pr 带条件的分页参数
     * @return 设备参数列表
     */
    public PageResponse<BizDeviceConf> listByParam(boolean needPaging, BizDeviceConfParam pr);

    /**
     * 获得设备参数列表（增加电量和定位信息）
     * @param needPaging 是否需要分页
     * @param pr 带条件的分页参数
     * @return 设备参数列表
     */
    public PageResponse<Map<String,Object>> lists(boolean needPaging, BizDeviceConfParam pr);

    /**
     * 批量插入设备参数.
     * @param deviceConfs 设备参数
     */
    public void insertBizDeviceConfs(List<BizDeviceConf> deviceConfs);

    /**
     * 插入设备参数.
     * @param deviceConf 设备参数
     */
    public void insertBizDeviceConf(BizDeviceConf deviceConf);

    /**
     * 修改设备参数.
     * @param deviceConf 新的设备参数
     */
    public void updateBizDeviceConf(BizDeviceConf deviceConf);

    /**
     * 批量删除设备参数.
     * @param deviceConfKeyss 待删除的设备参数列表
     */
    public void deleteBizDeviceConfs(List<BizDeviceConfKey> deviceConfKeyss);

    /**
     * 根据设备ID及类别查询设备参数信息.
     * @param key 查询主键
     * <pre>
     *     {
     *         deviceId:设备ID,
     *         deviceType:设备类别
     *     }
     * </pre>
     * @return 设备参数信息
     */
    public BizDeviceConf getBizDeviceConfByPrimaryKey(BizDeviceConfKey key);

    /**
     * 根据设备id查询当前电量
     * @param deviceId 设备id
     * @return 当前设备电量
     */
    public Map<String,Object> getElectricByDevice(String deviceId);

    /**
     * 根据设备id查询当前定位
     * @param bizDeviceConfKey 设备参数
     * @return 定位
     */
    public Map<String,Object> getLocationByDevice(BizDeviceConfKey bizDeviceConfKey);

    /**
     * 根据设备id查询关联信息
     * @param deviceId 设备id
     * @return 关联信息
     */
    public Map<String,Object> getRelationInformationByDevice(String deviceId);

}
