package com.elextec.persist.dao.mybatis;

import com.elextec.persist.model.mybatis.BizParts;
import com.elextec.persist.model.mybatis.ext.BizPartsExt;

import java.util.List;

public interface BizPartsMapperExt extends BizPartsMapper {

    /**
     * 根据车辆的id查询配件信息和制造商
     * @param id
     * @return
     */
    public List<BizPartsExt> getById(String id);

}
