package com.elextec.persist.dao.mybatis;

import com.elextec.lease.model.BizVehicleBatteryParts;

import java.util.List;
import java.util.Map;

public interface BizVehicleMapperExt extends BizVehicleMapper {
    Map<String,Object> getVehicleInfoById(String id);

    List<BizVehicleBatteryParts> getVehicleInfoByUserId(String id);
}
