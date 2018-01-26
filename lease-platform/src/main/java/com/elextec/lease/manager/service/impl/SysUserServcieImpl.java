package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.request.RefUserRolesParam;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.service.SysResourceService;
import com.elextec.lease.manager.service.SysUserService;
import com.elextec.persist.dao.mybatis.SysResourcesMapperExt;
import com.elextec.persist.dao.mybatis.SysUserMapperExt;
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
public class SysUserServcieImpl implements SysUserService {
    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(SysUserServcieImpl.class);

    @Autowired
    private SysUserMapperExt sysUserMapperExt;

    @Override
    public PageResponse<SysUser> list(boolean needPaging, PageRequest pr) {
        // 查询总记录数
        int resTotal = 0;
        if (0 < pr.getTotal()) {
            resTotal = pr.getTotal();
        } else {
            SysUserExample sysUserCountExample = new SysUserExample();
            sysUserCountExample.setDistinct(true);
            resTotal = sysUserMapperExt.countByExample(sysUserCountExample);
        }
        // 分页查询
        SysUserExample sysUsersExample = new SysUserExample();
        sysUsersExample.setDistinct(true);
        if (needPaging) {
            sysUsersExample.setPageBegin(pr.getPageBegin());
            sysUsersExample.setPageSize(pr.getPageSize());
        }
        List<SysUser> resLs = sysUserMapperExt.selectByExample(sysUsersExample);
        // 组织并返回结果
        PageResponse<SysUser> presp = new PageResponse<SysUser>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(resTotal);
        if (null == resLs) {
            presp.setRows(new ArrayList<SysUser>());
        } else {
            presp.setRows(resLs);
        }
        return presp;
    }

    @Override
    @Transactional
    public void insertSysUsers(List<SysUser> usersInfos) {
        int i = 0;
        SysUser insertVo = null;
        try {
            for (; i < usersInfos.size(); i++) {
                insertVo = usersInfos.get(i);
                insertVo.setId(WzUniqueValUtil.makeUUID());
                insertVo.setCreateTime(new Date());
                sysUserMapperExt.insertSelective(insertVo);
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录插入时发生错误", ex);
        }
    }

    @Override
    public void updateSysUser(SysUser res) {
        sysUserMapperExt.updateByPrimaryKeySelective(res);
    }

    @Override
    public void deleteSysUser(List<String> ids) {
        int i = 0;
        try {
            for (; i < ids.size(); i++) {
                sysUserMapperExt.deleteByPrimaryKey(ids.get(i));
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
        }
    }

    @Override
    @Transactional
    public void refUserAndRoles(RefUserRolesParam params){
        int i = 0;
        String userId = params.getUserId();
        String[] rolesIds = params.getRoleIds().split(",");
        SysRefUserRoleKey sysRefUserRoleKey = new SysRefUserRoleKey();
        if(rolesIds.length > 0){
            try{
                //删除用户原来的ROLE
                sysUserMapperExt.deleteUserAndRoles(userId);
                for(; i<rolesIds.length; i++){
                    sysRefUserRoleKey.setUserId(userId);
                    sysRefUserRoleKey.setRoleId(rolesIds[i]);
                    sysUserMapperExt.refUserAndRoles(sysRefUserRoleKey);
                }
            }catch(Exception ex){
                throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
            }
        }

    }

    @Override
    public SysUser getByPrimaryKey(String id) {
        return sysUserMapperExt.selectByPrimaryKey(id);
    }
}