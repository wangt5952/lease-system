package com.elextec.lease.manager.service;

import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.lease.manager.request.BizDeviceTrackParam;
import com.elextec.persist.model.mybatis.BizDeviceTrack;
import com.elextec.persist.model.mybatis.BizDeviceTrackExample;
import com.elextec.persist.model.mybatis.BizDeviceTrackKey;

import java.util.List;
import java.util.Map;

public interface BizDeviceTrackService {

    /**
     * 设备轨迹表增加
     * @param record 设备轨迹对象
     */
    void add(BizDeviceTrack record);

    /**
     * 设备轨迹删除
     * @param keyList
     */
    void delete(List<BizDeviceTrackKey> keyList);

    /**
     * 设备轨迹更新
     * @param record
     * @return
     */
    void update(BizDeviceTrack record);

    /**
     * 设备轨迹查询
     * @param needPaging 分页参数
     * @param pr 查询条件
     * @return 轨迹
     */
    PageResponse<Map<String,Object>> list(boolean needPaging, BizDeviceTrackParam pr);

    /**
     * 根据ID查询设备轨迹
     * @param map
     * @return
     */
    Map<String,Object> getByPk(Map<String,Object> map);

    /**
     * 获取不重复设备id
     * @return
     */
    List<BizDeviceTrack> selectDistinctDeviceId();

    /**
     * 根据设备id获取该设备最后一次位置
     * @param deviceId
     * @return
     */
    List<BizDeviceTrack> getLocationByDeviceId(String deviceId);
}
