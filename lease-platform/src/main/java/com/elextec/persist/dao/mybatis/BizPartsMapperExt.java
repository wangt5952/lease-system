package com.elextec.persist.dao.mybatis;

import com.elextec.lease.manager.request.BizPartsParam;
import com.elextec.persist.model.mybatis.ext.BizPartsExt;

import java.util.List;
import java.util.Map;

public interface BizPartsMapperExt extends BizPartsMapper {

    /**
     * 根据车辆的id查询配件信息和制造商
     * @param param 车辆ID、用户ID或企业ID
     * @return
     */
    public List<BizPartsExt> getById(Map<String,Object> param);

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

    //根据配件ID查找配件信息扩展
    BizPartsExt getPartInfoByPartId(Map<String,Object> param);

}
