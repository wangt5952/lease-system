package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzFileUtil;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.request.SysResParam;
import com.elextec.lease.manager.service.SysResourceService;
import com.elextec.lease.model.SysResourcesIcon;
import com.elextec.persist.dao.mybatis.SysResourcesMapperExt;
import com.elextec.persist.model.mybatis.SysResources;
import com.elextec.persist.model.mybatis.SysResourcesExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
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

    @Value("${localsetting.upload-res-icon-root}")
    private String uploadResIconRoot;

    @Value("${localsetting.download-res-icon-prefix}")
    private String downloadResIconPrefix;

    @Autowired
    private SysResourcesMapperExt sysResourcesMapperExt;

    @Override
    public PageResponse<SysResources> list(boolean needPaging, PageRequest pr) {
        // 查询总记录数
        int resTotal = 0;
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
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
    public PageResponse<SysResources> listByParam(boolean needPaging, SysResParam pr) {
        // 查询总记录数
        int resTotal = 0;
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
            resTotal = pr.getTotal();
        } else {
            resTotal = sysResourcesMapperExt.countByParam(pr);
        }
        // 分页查询
        if (needPaging) {
            pr.setPageBegin();
        }
        List<SysResources> resLs = sysResourcesMapperExt.selectByParam(pr);
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
    public void insertSysResource(SysResources resourceInfo) {
        // 资源code重复提示错误
        SysResourcesExample lnExample = new SysResourcesExample();
        SysResourcesExample.Criteria lnCriteria = lnExample.createCriteria();
        lnCriteria.andResCodeEqualTo(resourceInfo.getResCode());
        int lnCnt = sysResourcesMapperExt.countByExample(lnExample);
        if (0 < lnCnt) {
            throw new BizException(RunningResult.MULTIPLE_RECORD.code(), "资源code(" + resourceInfo.getResCode() + ")已存在");
        }
        try {
            resourceInfo.setId(WzUniqueValUtil.makeUUID());
            resourceInfo.setCreateTime(new Date());
            sysResourcesMapperExt.insertSelective(resourceInfo);
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "记录插入时发生错误", ex);
        }
    }

    @Override
    @Transactional
    public void updateSysResources(SysResources res) {
        sysResourcesMapperExt.updateByPrimaryKeySelective(res);
    }

    @Override
    @Transactional
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
    public SysResources getSysResourceByPrimaryKey(String id) {
        SysResources data = sysResourcesMapperExt.selectByPrimaryKey(id);
        if (null == data) {
            throw new BizException(RunningResult.NO_RESOURCE);
        }
        return data;
    }

    @Override
    public List<SysResources> listSysResourcesByRoleId(String roleId) {
        List<SysResources> datas = sysResourcesMapperExt.selectByRoleId(roleId);
        if (null == datas || 0 == datas.size()) {
            throw new BizException(RunningResult.NO_RESOURCE);
        }
        return datas;
    }

    @Override
    public List<SysResourcesIcon> listSysResourceIcons() {
        if (WzStringUtil.isBlank(uploadResIconRoot)) {
            throw new BizException(RunningResult.NO_DIRECTORY);
        }
        if (WzStringUtil.isBlank(downloadResIconPrefix)) {
            throw new BizException(RunningResult.NOT_FOUND);
        }
        File iconDir = new File(uploadResIconRoot);
        if (iconDir.exists() && iconDir.isDirectory()) {
            File[] iconFiles = iconDir.listFiles();
            List<SysResourcesIcon> iconLs = new ArrayList<SysResourcesIcon>();
            SysResourcesIcon iconVo = null;
            for (int i = 0; i < iconFiles.length; i++) {
                iconVo = new SysResourcesIcon();
                iconVo.setIconName(iconFiles[i].getName());
                iconVo.setIconUrl(WzFileUtil.makeRequestUrl(uploadResIconRoot, "", iconFiles[i].getName()));
                iconLs.add(iconVo);
            }
            if (0 == iconLs.size()) {
                throw new BizException(RunningResult.NOT_FOUND.code(), "未获得ICON文件");
            }
            return iconLs;
        } else {
            throw new BizException(RunningResult.NO_DIRECTORY);
        }
    }
}
