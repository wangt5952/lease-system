package com.elextec.persist.dao.mybatis;

import com.elextec.lease.manager.request.BizDeviceUsageParam;
import com.elextec.persist.model.mybatis.BizDeviceUsage;

import java.util.List;

public interface BizDeviceUsageMapperExt extends BizDeviceUsageMapper {

    /**
     * 查询记录数
     * @param bizDeviceUsageParam
     * @return
     */
    int countByParam(BizDeviceUsageParam bizDeviceUsageParam);

    /**
     * 查询分页
     * @param bizDeviceUsageParam
     * @return
     */
    List<BizDeviceUsage> selectByParam(BizDeviceUsageParam bizDeviceUsageParam);

}