package com.elextec.lease.manager.service;

import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.lease.manager.request.BizDeviceUsageParam;
import com.elextec.persist.model.mybatis.BizDeviceUsage;

public interface BizDeviceUsageService {

    /**
     * 设备统计接口
     * @param needPaging 分页参数
     * @param pr 查询条件
     * @return
     */
    PageResponse<BizDeviceUsage> list(boolean needPaging, BizDeviceUsageParam pr);

}
