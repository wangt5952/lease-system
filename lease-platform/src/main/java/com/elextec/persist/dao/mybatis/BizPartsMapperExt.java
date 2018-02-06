package com.elextec.persist.dao.mybatis;

import com.elextec.lease.manager.request.BizPartsParam;
import com.elextec.persist.model.mybatis.ext.BizPartsExt;

import java.util.List;

public interface BizPartsMapperExt extends BizPartsMapper {

    /**
     * 根据车辆的id查询配件信息和制造商
     * @param id
     * @return
     */
    public List<BizPartsExt> getById(String id);

    /**
     * 查询配件扩展信息列表
     * @param partsParam 查询条件
     * @return 配件扩展信息列表
     */
    public List<BizPartsExt> selectExtByParam(BizPartsParam partsParam);

    /**
     * 查询配件扩展信息记录数
     * @param partsParam 查询条件
     * @return 配件扩展信息记录数
     */
    public int countExtByParam(BizPartsParam partsParam);

}
