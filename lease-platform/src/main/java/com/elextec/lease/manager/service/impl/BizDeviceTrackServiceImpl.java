package com.elextec.lease.manager.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzDateUtil;
import com.elextec.framework.utils.WzGPSUtil;
import com.elextec.lease.manager.request.BizDeviceTrackParam;
import com.elextec.lease.manager.service.BizDeviceTrackService;
import com.elextec.persist.dao.mybatis.BizBatteryMapperExt;
import com.elextec.persist.dao.mybatis.BizDeviceTrackMapperExt;
import com.elextec.persist.dao.mybatis.BizVehicleMapperExt;
import com.elextec.persist.dao.mybatis.SysUserMapperExt;
import com.elextec.persist.model.mybatis.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BizDeviceTrackServiceImpl implements BizDeviceTrackService {

    @Autowired
    private BizDeviceTrackMapperExt bizDeviceTrackMapperExt;

    @Override
    public void add(BizDeviceTrack bizDeviceTrack) {
        //将百度坐标转成gps坐标
        double[] gpsLocation = WzGPSUtil.bd2wgs(bizDeviceTrack.getLat(),bizDeviceTrack.getLon());
        BizDeviceTrack track = new BizDeviceTrack();
        track.setDeviceId(bizDeviceTrack.getDeviceId());//设备id
        track.setLat(gpsLocation[0]);//gps纬度
        track.setLon(gpsLocation[1]);//gps经度
        track.setLocTime(new Date().getTime());//当前时间戳
        bizDeviceTrackMapperExt.insert(track);
    }

    @Override
    public void delete(List<BizDeviceTrackKey> keyList) {
        int i = 0;
        try {
            for (;i < keyList.size();i++) {
                bizDeviceTrackMapperExt.del(keyList.get(i).getDeviceId());
            }
        } catch (Exception e) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", e);
        }
    }

    @Override
    public void update(BizDeviceTrack bizDeviceTrack) {
        BizDeviceTrackExample bizDeviceTrackExample = new BizDeviceTrackExample();
        BizDeviceTrackExample.Criteria criteria = bizDeviceTrackExample.createCriteria();
        criteria.andDeviceIdEqualTo(bizDeviceTrack.getDeviceId());
        if (bizDeviceTrackMapperExt.countByExample(bizDeviceTrackExample) == 0) {
            throw new BizException(RunningResult.NO_RESOURCE.code(),"修改设备不存在");
        }
        bizDeviceTrackMapperExt.updateByPrimaryKey(bizDeviceTrack);
    }

    @Override
    public PageResponse<Map<String,Object>> list(boolean needPaging, BizDeviceTrackParam pr) {
        int devTotal = 0;// 查询总记录数
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
            devTotal = pr.getTotal();
        } else {
            devTotal = bizDeviceTrackMapperExt.selectDistinctDeviceId().size();
        }
        List<Map<String,Object>> mapList = new ArrayList<Map<String, Object>>();//存放消息结果
        List<BizDeviceTrack> deviceIds = this.selectDistinctDeviceId();//不重复的设备id
        for (int i = 0; i < deviceIds.size(); i++) {
            String location = null;//定位
            String deviceId = deviceIds.get(i).getDeviceId();
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("deviceId",deviceId);
            BizDeviceTrackExample bizDeviceTrackExample = new BizDeviceTrackExample();
            BizDeviceTrackExample.Criteria criteria = bizDeviceTrackExample.createCriteria();
            criteria.andDeviceIdEqualTo(deviceId);
            List<BizDeviceTrack> list = bizDeviceTrackMapperExt.selectByExample(bizDeviceTrackExample);
            for (int j = 0; j < list.size(); j++) {
                if (j == 0) {
                    location = list.get(j).getLon().toString() +","+list.get(j).getLat().toString() + ";";
                } else {
                    location += list.get(j).getLon().toString() +","+list.get(j).getLat().toString() + ";";
                }
            }
            map.put("location",location);
            mapList.add(map);
        }
        // 组织并返回结果
        PageResponse<Map<String,Object>> presp = new PageResponse<Map<String,Object>>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(devTotal);
        if (null == mapList) {
            presp.setRows(new ArrayList<Map<String,Object>>());
        } else {
            presp.setRows(mapList);
        }
        return presp;
    }

    @Override
    public Map<String,Object> getByPk(Map<String,Object> map) {
        //返回结果
        Map<String,Object> maps = new HashMap<String,Object>();
        //查询设备是否存在
        BizDeviceTrackExample bizDeviceTrackExample = new BizDeviceTrackExample();
        BizDeviceTrackExample.Criteria criteria = bizDeviceTrackExample.createCriteria();
        criteria.andDeviceIdEqualTo(map.get("deviceId").toString());
        if (bizDeviceTrackMapperExt.countByExample(bizDeviceTrackExample) == 0) {
            throw new BizException(RunningResult.NO_RESOURCE.code(),"该设备不存在");
        }
        //根据时间区间查找轨迹链
        List<BizDeviceTrack> list = bizDeviceTrackMapperExt.deviceLocation(map);
        if (list.size() != 0) {
            List<JSONObject> doubles = new ArrayList<JSONObject>();
            for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                double[] locations = WzGPSUtil.wgs2bd(list.get(i).getLat().doubleValue(), list.get(i).getLon().doubleValue());
                jsonObject.put("LAT", locations[0]);
                jsonObject.put("LON", locations[1]);
                doubles.add(jsonObject);
            }
            maps.put("deviceId", list.get(0).getDeviceId().toString());
            maps.put("location", doubles);
        } else {
            throw new BizException(RunningResult.PARAM_ANALYZE_ERROR.code(),"该时间内无设备轨迹");
        }
        return maps;
    }

    @Override
    public List<BizDeviceTrack> selectDistinctDeviceId() {
        return bizDeviceTrackMapperExt.selectDistinctDeviceId();
    }

    @Override
    public List<BizDeviceTrack> getLocationByDeviceId(String deviceId) {
        return bizDeviceTrackMapperExt.getLocationByDeviceId(deviceId);
    }
}
