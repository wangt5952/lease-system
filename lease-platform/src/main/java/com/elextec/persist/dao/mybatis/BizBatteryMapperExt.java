package com.elextec.persist.dao.mybatis;

import com.elextec.persist.model.mybatis.BizBattery;
import com.elextec.persist.model.mybatis.ext.BizBatteryExt;

import java.util.List;

public interface BizBatteryMapperExt extends BizBatteryMapper {
    //根据车辆ID获取电池信息
    public List<BizBatteryExt> getBatteryInfoByVehicleId(String id);
}
