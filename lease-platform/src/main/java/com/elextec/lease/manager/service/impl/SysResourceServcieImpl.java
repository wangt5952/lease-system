package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.service.SysResourceService;
import com.elextec.persist.dao.mybatis.SysResourcesMapperExt;
import com.elextec.persist.model.mybatis.SysResources;
import com.elextec.persist.model.mybatis.SysResourcesExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 资源管理Service实现类.
 * Created by wangtao on 2018/1/16.
 */
@Service
public class SysResourceServcieImpl implements SysResourceService {
    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(SysResourceServcieImpl.class);

    @Autowired
    private SysResourcesMapperExt sysResourcesMapperExt;

    @Override
    public PageResponse<SysResources> list(boolean needPaging, PageRequest pr) {
        // 查询总记录数
        int resTotal = 0;
        if (0 < pr.getTotal()) {
            resTotal = pr.getTotal();
        } else {
            SysResourcesExample sysResourcesCountExample = new SysResourcesExample();
            sysResourcesCountExample.setDistinct(true);
            resTotal = sysResourcesMapperExt.countByExample(sysResourcesCountExample);
        }
        // 分页查询
        SysResourcesExample sysResourcesExample = new SysResourcesExample();
        sysResourcesExample.setDistinct(true);
        if (needPaging) {
            sysResourcesExample.setPageBegin(pr.getPageBegin());
            sysResourcesExample.setPageSize(pr.getPageSize());
        }
        List<SysResources> resLs = sysResourcesMapperExt.selectByExample(sysResourcesExample);
        // 组织并返回结果
        PageResponse<SysResources> presp = new PageResponse<SysResources>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(resTotal);
        if (null == resLs) {
            presp.setRows(new ArrayList<SysResources>());
        } else {
            presp.setRows(resLs);
        }
        return presp;
    }

    @Override
    @Transactional
    public void insertSysResources(List<SysResources> resourceInfos) {
        int i = 0;
        SysResources insertVo = null;
        try {
            for (; i < resourceInfos.size(); i++) {
                insertVo = resourceInfos.get(i);
                insertVo.setId(WzUniqueValUtil.makeUUID());
                insertVo.setCreateTime(new Date());
                sysResourcesMapperExt.insertSelective(insertVo);
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录插入时发生错误", ex);
        }
    }

    @Override
    public void updateSysResources(SysResources res) {
        sysResourcesMapperExt.updateByPrimaryKeySelective(res);
    }

    @Override
    public void deleteSysResources(List<String> ids) {
        int i = 0;
        try {
            for (; i < ids.size(); i++) {
                sysResourcesMapperExt.deleteByPrimaryKey(ids.get(i));
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
        }
    }

    @Override
    public SysResources getByPrimaryKey(String id) {
        return sysResourcesMapperExt.selectByPrimaryKey(id);
    }
}
