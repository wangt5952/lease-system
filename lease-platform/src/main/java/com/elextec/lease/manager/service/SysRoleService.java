package com.elextec.lease.manager.service;

import com.elextec.framework.common.request.RefRoleResourceParam;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.persist.model.mybatis.SysRole;

import java.util.List;

/**
 * 接口 角色控制Service.
 * Created by wangtao on 2018/1/16.
 */
public interface SysRoleService {
    /**
     * 获得角色列表.
     * @param needPaging 是否需要分页
     * @param pr 分页参数
     * @return 角色列表
     * @throws BizException 查询业务异常
     */
    public PageResponse<SysRole> list(boolean needPaging, PageRequest pr);

    /**
     * 批量插入角色.
     * @param roleInfos 角色信息
     * @throws BizException 插入时异常，异常时全部数据回滚，日志记录出错记录号
     */
    public void insertSysRoles(List<SysRole> roleInfos);

    /**
     * 插入角色.
     * @param roleInfo 角色信息
     */
    public void insertSysRole(SysRole roleInfo);

    /**
     * 修改角色信息.
     * @param roleInfo 新的角色信息
     */
    public void updateSysRole(SysRole roleInfo);

    /**
     * 批量删除角色.
     * @param ids 待删除的角色ID列表
     */
    public void deleteSysRole(List<String> ids);

    /**
     * 给角色分配资源
     * @param params 角色对应资源map
     * */
    public void refSysRoleAndResource(RefRoleResourceParam params);

    /**
     * 根据ID查询角色信息.
     * @param id 角色ID
     * */
    public SysRole getSysRoleByPrimaryKey(String id);
}
