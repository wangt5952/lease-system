package com.elextec.persist.dao.mybatis;

import java.util.Map;

public interface BizVehicleMapperExt extends BizVehicleMapper {
    Map<String,Object> getVehicleInfoById(String id);
}
