package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.manager.service.BizVehicleTrackService;
import com.elextec.persist.dao.mybatis.BizVehicleTrackMapperExt;
import com.elextec.persist.model.mybatis.BizVehicleTrack;
import com.elextec.persist.model.mybatis.BizVehicleTrackExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BizVehicleTrackServiceImpl implements BizVehicleTrackService {

    @Autowired
    private BizVehicleTrackMapperExt bizVehicleTrackMapperExt;

    @Override
    public void insertVehicleTrack(BizVehicleTrack trackInfo) {
        if (null == trackInfo
                || WzStringUtil.isBlank(trackInfo.getDeviceId())
                || WzStringUtil.isBlank(trackInfo.getDeviceType())
                || WzStringUtil.isBlank(trackInfo.getLocations())
                || null == trackInfo.getStartTime()){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(),"参数验证失败");
        } else {
            BizVehicleTrackExample bvtCntExample = new BizVehicleTrackExample();
            BizVehicleTrackExample.Criteria bvtCntExampleCri = bvtCntExample.createCriteria();
            bvtCntExampleCri.andDeviceIdEqualTo(trackInfo.getDeviceId());
            bvtCntExampleCri.andDeviceTypeEqualTo(trackInfo.getDeviceType());
            bvtCntExampleCri.andEndTimeIsNotNull();
            bvtCntExampleCri.andEndTimeGreaterThanOrEqualTo(trackInfo.getStartTime());
            try {
                int bvtCnt = bizVehicleTrackMapperExt.countByExample(bvtCntExample);
                if (0 < bvtCnt) {
                    throw new BizException(RunningResult.MULTIPLE_RECORD.code(), "已存在重复时间段轨迹，请确认");
                }
                bizVehicleTrackMapperExt.insertSelective(trackInfo);
            } catch (Exception ex) {
                throw new BizException(RunningResult.DB_ERROR.code(), "记录插入时发生错误", ex);
            }
        }
    }

    @Override
    public List<BizVehicleTrack> getVehicleTracksByTime(String deviceId, String deviceType, long startTime, long endTime) {
        BizVehicleTrackExample bizVehicleTrackExample = new BizVehicleTrackExample();
        BizVehicleTrackExample.Criteria selectTrack = bizVehicleTrackExample.createCriteria();
        selectTrack.andDeviceIdEqualTo(deviceId);
        selectTrack.andDeviceTypeEqualTo(deviceType);
        selectTrack.andStartTimeBetween(startTime,endTime);
        List<BizVehicleTrack> list = bizVehicleTrackMapperExt.selectByExample(bizVehicleTrackExample);
        return list;
    }
}
