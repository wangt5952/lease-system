package com.elextec.lease.manager.service.impl;

import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.lease.manager.request.BizDeviceUsageParam;
import com.elextec.lease.manager.service.BizDeviceUsageService;
import com.elextec.persist.dao.mybatis.BizDeviceUsageMapperExt;
import com.elextec.persist.model.mybatis.BizDeviceUsage;
import com.elextec.persist.model.mybatis.BizDeviceUsageExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BizDeviceUsageServiceImpl implements BizDeviceUsageService {

    @Autowired
    private BizDeviceUsageMapperExt bizDeviceUsageMapperExt;

    @Override
    public PageResponse<BizDeviceUsage> list(boolean needPaging, BizDeviceUsageParam pr) {
        int usageTotal = 0;
        if (pr.getTotal() != null && pr.getTotal() > 0) {
            usageTotal = pr.getTotal();
        } else {
            usageTotal = bizDeviceUsageMapperExt.countByParam(pr);
        }
        //分页查询
        if (needPaging) {
            pr.setPageBegin();
            pr.setNeedPaging("true");
        }else{
            pr.setNeedPaging("false");
        }
        List<BizDeviceUsage> bizDeviceUsageList = bizDeviceUsageMapperExt.selectByParam(pr);
        // 组织并返回结果
        PageResponse<BizDeviceUsage> presp = new PageResponse<BizDeviceUsage>();
        if(needPaging){
            presp.setCurrPage(pr.getCurrPage());
            presp.setPageSize(pr.getPageSize());
        }
        presp.setTotal(usageTotal);
        if (null == bizDeviceUsageList) {
            presp.setRows(new ArrayList<BizDeviceUsage>());
        } else {
            presp.setRows(bizDeviceUsageList);
        }
        return presp;
    }
}
