package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.request.RefRoleResourceParam;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.service.SysRoleService;
import com.elextec.persist.dao.mybatis.SysRoleMapperExt;
import com.elextec.persist.model.mybatis.*;
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
public class SysRoleServcieImpl implements SysRoleService {
    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(SysRoleServcieImpl.class);

    @Autowired
    private SysRoleMapperExt sysRoleMapperExt;

    @Override
    public PageResponse<SysRole> list(boolean needPaging, PageRequest pr) {
        // 查询总记录数
        int resTotal = 0;
        if (0 < pr.getTotal()) {
            resTotal = pr.getTotal();
        } else {
            SysRoleExample sysRoleCountExample = new SysRoleExample();
            sysRoleCountExample.setDistinct(true);
            resTotal = sysRoleMapperExt.countByExample(sysRoleCountExample);
        }
        // 分页查询
        SysRoleExample sysRolesExample = new SysRoleExample();
        sysRolesExample.setDistinct(true);
        if (needPaging) {
            sysRolesExample.setPageBegin(pr.getPageBegin());
            sysRolesExample.setPageSize(pr.getPageSize());
        }
        List<SysRole> resLs = sysRoleMapperExt.selectByExample(sysRolesExample);
        // 组织并返回结果
        PageResponse<SysRole> presp = new PageResponse<SysRole>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(resTotal);
        if (null == resLs) {
            presp.setRows(new ArrayList<SysRole>());
        } else {
            presp.setRows(resLs);
        }
        return presp;
    }

    @Override
    @Transactional
    public void insertSysRoles(List<SysRole> rolesInfos) {
        int i = 0;
        SysRole insertVo = null;
        try {
            for (; i < rolesInfos.size(); i++) {
                insertVo = rolesInfos.get(i);
                insertVo.setId(WzUniqueValUtil.makeUUID());
                insertVo.setCreateTime(new Date());
                sysRoleMapperExt.insertSelective(insertVo);
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录插入时发生错误", ex);
        }
    }

    @Override
    public void updateSysRole(SysRole res) {
        sysRoleMapperExt.updateByPrimaryKeySelective(res);
    }

    @Override
    public void deleteSysRole(List<String> ids) {
        int i = 0;
        try {
            for (; i < ids.size(); i++) {
                sysRoleMapperExt.deleteByPrimaryKey(ids.get(i));
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
        }
    }

    @Override
    public void refRoleAndResource(RefRoleResourceParam params){
        int i = 0;
        String roleId = params.getRoleId();
        String[] resourceIds = params.getResources().split(",");
        SysRefRoleResourcesKey sysRefRoleResourcesKey = null;
        if(resourceIds.length > 0){
            try{
                //删除角色原来的Resources
                sysRoleMapperExt.deleteRoleAndResources(roleId);
                for(; i<resourceIds.length; i++){
                    sysRefRoleResourcesKey.setRoleId(roleId);
                    sysRefRoleResourcesKey.setResId(resourceIds[i]);
                    sysRoleMapperExt.refRoleAndResources(sysRefRoleResourcesKey);
                }
            }catch(Exception ex){
                throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
            }
        }

    }
}
