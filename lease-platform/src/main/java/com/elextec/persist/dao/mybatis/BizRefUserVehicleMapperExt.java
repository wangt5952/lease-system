package com.elextec.persist.dao.mybatis;

import com.elextec.persist.model.mybatis.BizVehicle;

import java.util.List;

public interface BizRefUserVehicleMapperExt extends BizRefUserVehicleMapper {

    public List<BizVehicle> getVehicleByUserId(String sysUserId);

}
