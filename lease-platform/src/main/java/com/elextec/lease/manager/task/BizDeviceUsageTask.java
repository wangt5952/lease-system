package com.elextec.lease.manager.task;

import com.elextec.framework.utils.WzDateUtil;
import com.elextec.framework.utils.WzGPSUtil;
import com.elextec.persist.dao.mybatis.BizDeviceConfMapperExt;
import com.elextec.persist.dao.mybatis.BizDeviceTrackMapperExt;
import com.elextec.persist.dao.mybatis.BizDeviceUsageMapperExt;
import com.elextec.persist.model.mybatis.BizDeviceTrack;
import com.elextec.persist.model.mybatis.BizDeviceUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 设备定时统计类
 * create by yangkun on 2018/07/19
 * 
 */
@Component
public class BizDeviceUsageTask {

    @Autowired
    private BizDeviceConfMapperExt bizDeviceConfMapperExt;

    @Autowired
    private BizDeviceTrackMapperExt bizDeviceTrackMapperExt;

    @Autowired
    private BizDeviceUsageMapperExt bizDeviceUsageMapperExt;

    /**
     * 每天12点05分启动添加
     */
//    @Scheduled(cron = "0 5 12 * * ?\n")
//    @Scheduled(cron = "*/5 * * * * ?")//五秒一次
//    public void add(){
//        //获取所有设备号
//        List<String> deviceIds = bizDeviceConfMapperExt.getDistinctDeviceId();
//        System.out.println(deviceIds);
//        for (int i = 0; i < deviceIds.size(); i++) {
//            BizDeviceUsage bizDeviceUsage = new BizDeviceUsage();
//            //当前设备id赋值
//            bizDeviceUsage.setDeviceId(deviceIds.get(i));
//            //当前设备记录入库时间（当前日期的前一天0点0分0秒）赋值
//            bizDeviceUsage.setRecTime(WzDateUtil.dayStart(WzDateUtil.afterNDays(new Date(),-1)));
//            //当前设备id
//            String deviceId = deviceIds.get(i);
//            //当前时间的前一天起始时间（0点0分0秒）时间戳毫秒
//            long startTime = WzDateUtil.dayStart(WzDateUtil.afterNDays(new Date(),-1)).getTime();
//            //当前时间的前一天结束时间（23点59分59秒）时间戳毫秒
//            long endTime = WzDateUtil.dayEnd(WzDateUtil.afterNDays(new Date(),-1)).getTime();
//            //计算当前设备前一天的使用总时间
//            int totalTimes = this.getTotalTime(deviceId, startTime, endTime);
//            //计算当前设备前一天的使用总距离
//            long totalDisances = this.getTotalDistance(deviceId, startTime, endTime);
//            bizDeviceUsage.setUseDistance(totalTimes);//当前设备当天使用总时间（毫秒）
//            bizDeviceUsage.setUseDuration(totalDisances);//当前设备当天使用总距离（米）
//            bizDeviceUsageMapperExt.insert(bizDeviceUsage);
//        }
//    }

    /**
     * 计算车辆使用时长
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    private int getTotalTime(String deviceId, long startTime, long endTime){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("deviceId",deviceId);
        map.put("startTime",startTime);
        map.put("endTime",endTime);
        List<BizDeviceTrack> bizDeviceTrackList = bizDeviceTrackMapperExt.getTotalTime(map);
        int totalTime = 0;//总时长
        for (int i = 0; i < bizDeviceTrackList.size(); i++) {
            if ((i+1) == bizDeviceTrackList.size()) {
                //最后一轮,计算总时间
                totalTime += bizDeviceTrackList.get(i).getLocTime() - bizDeviceTrackList.get(i + 1).getLocTime();
                break;
            } else {
                totalTime += bizDeviceTrackList.get(i).getLocTime() - bizDeviceTrackList.get(i + 1).getLocTime();
            }

        }
        return totalTime;
    }

    /**
     * 计算车辆使用距离
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    private long getTotalDistance(String deviceId, long startTime, long endTime) {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("deviceId",deviceId);
        map.put("startTime",startTime);
        map.put("endTime",endTime);
        List<BizDeviceTrack> bizDeviceTrackList = bizDeviceTrackMapperExt.getTotalTime(map);
        long totalDistance = 0;
        for (int i = 0; i < bizDeviceTrackList.size(); i++) {
            //最后一轮计算
            if ((i+1) == bizDeviceTrackList.size()) {
                //第一经度
                double lon1 = bizDeviceTrackList.get(i).getLon();
                //第一纬度
                double lat1 = bizDeviceTrackList.get(i).getLat();
                //第二经度
                double lon2 = bizDeviceTrackList.get(i + 1).getLon();
                //第二纬度
                double lat2 = bizDeviceTrackList.get(i + 1).getLat();
                //计算两点之间的距离
                totalDistance += WzGPSUtil.calcDistanceByM(lon1, lat1, lon2, lat2);
                break;
            } else {
                //第一经度
                double lon1 = bizDeviceTrackList.get(i).getLon();
                //第一纬度
                double lat1 = bizDeviceTrackList.get(i).getLat();
                //第二经度
                double lon2 = bizDeviceTrackList.get(i + 1).getLon();
                //第二纬度
                double lat2 = bizDeviceTrackList.get(i + 1).getLat();
                //计算两点之间的距离
                totalDistance += WzGPSUtil.calcDistanceByM(lon1, lat1, lon2, lat2);
            }
        }
        return totalDistance;
    }

}
