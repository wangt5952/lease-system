package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.request.RefRoleResourceParam;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.request.SysRoleParam;
import com.elextec.lease.manager.service.SysRoleService;
import com.elextec.persist.dao.mybatis.SysRefRoleResourcesMapperExt;
import com.elextec.persist.dao.mybatis.SysRefUserRoleMapperExt;
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

    @Autowired
    private SysRefUserRoleMapperExt sysRefUserRoleMapperExt;

    @Autowired
    private SysRefRoleResourcesMapperExt sysRefRoleResourcesMapperExt;

    @Override
    public PageResponse<SysRole> list(boolean needPaging, PageRequest pr) {
        // 查询总记录数
        int roleTotal = 0;
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
            roleTotal = pr.getTotal();
        } else {
            SysRoleExample sysRoleCountExample = new SysRoleExample();
            sysRoleCountExample.setDistinct(true);
            roleTotal = sysRoleMapperExt.countByExample(sysRoleCountExample);
        }
        // 分页查询
        SysRoleExample sysRolesExample = new SysRoleExample();
        sysRolesExample.setDistinct(true);
        if (needPaging) {
            sysRolesExample.setPageBegin(pr.getPageBegin());
            sysRolesExample.setPageSize(pr.getPageSize());
        }
        List<SysRole> roleLs = sysRoleMapperExt.selectByExample(sysRolesExample);
        // 组织并返回结果
        PageResponse<SysRole> presp = new PageResponse<SysRole>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(roleTotal);
        if (null == roleLs) {
            presp.setRows(new ArrayList<SysRole>());
        } else {
            presp.setRows(roleLs);
        }
        return presp;
    }

    @Override
    public PageResponse<SysRole> listByParam(boolean needPaging, SysRoleParam pr) {
        // 查询总记录数
        int roleTotal = 0;
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
            roleTotal = pr.getTotal();
        } else {
            SysRoleExample sysRoleCountExample = new SysRoleExample();
            sysRoleCountExample.setDistinct(true);
            if (WzStringUtil.isNotBlank(pr.getKeyStr())) {
                SysRoleExample.Criteria sysRoleCountCri = sysRoleCountExample.createCriteria();
                sysRoleCountCri.andRoleNameLike("%" + pr.getKeyStr() + "%");
            }
            roleTotal = sysRoleMapperExt.countByExample(sysRoleCountExample);
        }
        // 分页查询
        SysRoleExample sysRolesExample = new SysRoleExample();
        sysRolesExample.setDistinct(true);
        if (WzStringUtil.isNotBlank(pr.getKeyStr())) {
            SysRoleExample.Criteria sysRoleSelCri = sysRolesExample.createCriteria();
            sysRoleSelCri.andRoleNameLike("%" + pr.getKeyStr() + "%");
        }
        if (needPaging) {
            sysRolesExample.setPageBegin(pr.getPageBegin());
            sysRolesExample.setPageSize(pr.getPageSize());
        }
        List<SysRole> roleLs = sysRoleMapperExt.selectByExample(sysRolesExample);
        // 组织并返回结果
        PageResponse<SysRole> presp = new PageResponse<SysRole>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(roleTotal);
        if (null == roleLs) {
            presp.setRows(new ArrayList<SysRole>());
        } else {
            presp.setRows(roleLs);
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
    public void insertSysRole(SysRole roleInfo) {
        // 角色名称重复提示错误
        SysRoleExample lnExample = new SysRoleExample();
        SysRoleExample.Criteria lnCriteria = lnExample.createCriteria();
        lnCriteria.andRoleNameEqualTo(roleInfo.getRoleName());
        int lnCnt = sysRoleMapperExt.countByExample(lnExample);
        if (0 < lnCnt) {
            throw new BizException(RunningResult.MULTIPLE_RECORD.code(), "角色名称(" + roleInfo.getRoleName() + ")已存在");
        }
        // 保存用户信息
        try {
            roleInfo.setId(WzUniqueValUtil.makeUUID());
            roleInfo.setCreateTime(new Date());
            sysRoleMapperExt.insertSelective(roleInfo);
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "记录插入时发生错误", ex);
        }
    }

    @Override
    @Transactional
    public void updateSysRole(SysRole res) {
        sysRoleMapperExt.updateByPrimaryKeySelective(res);
    }

    @Override
    @Transactional
    public void deleteSysRole(List<String> ids) {
        int i = 0;
        try {
            for (; i < ids.size(); i++) {
                //判断不允许删除的角色
                if (ids.get(i).equals("1dc5e062e6964b1c857098e30d89b945")
                        || ids.get(i).equals("5e321a9f578a4c52ac036836d4e90339")
                        || ids.get(i).equals("d08e7dfd41a64b469742343b742e3df6")) {
                    throw new BizException(RunningResult.NO_FUNCTION_PERMISSION.code(),"该角色不可删除");
                }
                SysRefUserRoleExample example = new SysRefUserRoleExample();
                SysRefUserRoleExample.Criteria criteria = example.createCriteria();
                SysRefRoleResourcesExample delExample = new SysRefRoleResourcesExample();
                SysRefRoleResourcesExample.Criteria delCriteria = delExample.createCriteria();
                criteria.andRoleIdEqualTo(ids.get(i));
                int lnCnt = sysRefUserRoleMapperExt.countByExample(example);
                if(lnCnt > 0){
                    throw new BizException(RunningResult.HAVE_BIND.code(), "第" + i + "条记录删除时发生错误,有用户绑定该角色未解除");
                }
                sysRoleMapperExt.deleteByPrimaryKey(ids.get(i));
                //删除角色对应资源关系表里所有相关数据
                delCriteria.andRoleIdEqualTo(ids.get(i));
                sysRefRoleResourcesMapperExt.deleteByExample(delExample);
            }
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
        }
    }

    @Override
    @Transactional
    public void refSysRoleAndResource(RefRoleResourceParam params){
        int i = 0;
        String roleId = params.getRoleId();
        if ("true".equals(params.getDeleteAllFlg().toLowerCase())) {
            sysRoleMapperExt.deleteRoleAndResources(roleId);
        } else {
            String[] resIds = params.getResourceIds().split(",");
            SysRefRoleResourcesKey sysRefRoleResourcesKey = new SysRefRoleResourcesKey();
            if (0 < resIds.length) {
                try {
                    //删除角色原来的Resources
                    sysRoleMapperExt.deleteRoleAndResources(roleId);
                    for (; i < resIds.length; i++) {
                        sysRefRoleResourcesKey.setRoleId(roleId);
                        sysRefRoleResourcesKey.setResId(resIds[i]);
                        sysRoleMapperExt.refRoleAndResources(sysRefRoleResourcesKey);
                    }
                } catch (Exception ex) {
                    throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
                }
            }
        }

    }

    @Override
    public SysRole getSysRoleByPrimaryKey(String id) {
        SysRole data = sysRoleMapperExt.selectByPrimaryKey(id);
        if (null == data) {
            throw new BizException(RunningResult.NO_ROLE);
        }
        return data;
    }

    @Override
    public List<SysRole> listSysRolesByUserId(String userId) {
        List<SysRole> datas = sysRoleMapperExt.selectByUserId(userId);
        if (null == datas || 0 == datas.size()) {
            throw new BizException(RunningResult.NO_ROLE);
        }
        return datas;
    }
}
