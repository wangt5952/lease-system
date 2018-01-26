package com.elextec.persist.dao.mybatis;

import com.elextec.persist.model.mybatis.BizManufacturer;

import java.util.List;
import java.util.Map;

public interface BizManufacturerMapperExt extends BizManufacturerMapper {

    /**
     * 查询制造商名字
     * @param mfrs_name
     * @return
     */
    public BizManufacturer getByName(String mfrs_name);

}
