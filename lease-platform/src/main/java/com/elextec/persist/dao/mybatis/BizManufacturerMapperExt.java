package com.elextec.persist.dao.mybatis;

import com.elextec.lease.manager.request.BizMfrsParam;
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

    /**
     * 查询制造商扩展信息列表.
     * @param mfrsParam 查询条件
     * @return 配件扩展信息列表
     */
    public List<BizManufacturer> selectByParam(BizMfrsParam mfrsParam);

    /**
     * 查询制造商扩展信息记录数.
     * @param mfrsParam 查询条件
     * @return 配件扩展信息记录数
     */
    public int countByParam(BizMfrsParam mfrsParam);

}
