package com.elextec.persist.dao.mybatis;

import com.elextec.persist.model.mybatis.BizDeviceTrack;

import java.util.List;
import java.util.Map;

public interface BizDeviceTrackMapperExt extends BizDeviceTrackMapper {

    /**
     * 删除设备轨迹点
     * @param deviceId
     */
    void del(String deviceId);

    /**
     * 根据设备查询当前轨迹
     * @param map
     * @return
     */
    List<BizDeviceTrack> deviceLocation(Map<String,Object> map);

    /**
     * 获取所有不重复的设备
     * @return
     */
    List<BizDeviceTrack> selectDistinctDeviceId();

    /**
     * 获取当前设备的最后位置
     * @param deviceId
     * @return
     */
    List<BizDeviceTrack> getLocationByDeviceId(String deviceId);

    /**
     * 获取总时长
     * @param map
     * @return
     */
    List<BizDeviceTrack> getTotalTime(Map<String,Object> map);

}