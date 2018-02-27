package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
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
        if (trackInfo != null || !trackInfo.equals("")){
            bizVehicleTrackMapperExt.insert(trackInfo);
        } else {
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(),"参数验证失败");
        }
    }

    @Override
    public List<BizVehicleTrack> getVehicleTracksByTime(String deviceId, long startTime, long endTime) {
        BizVehicleTrackExample bizVehicleTrackExample = new BizVehicleTrackExample();
        BizVehicleTrackExample.Criteria selectTrack = bizVehicleTrackExample.createCriteria();
        selectTrack.andDeviceIdEqualTo(deviceId);
        selectTrack.andStartTimeBetween(startTime,endTime);
        List<BizVehicleTrack> list = bizVehicleTrackMapperExt.selectByExample(bizVehicleTrackExample);
        return list;
    }

}
