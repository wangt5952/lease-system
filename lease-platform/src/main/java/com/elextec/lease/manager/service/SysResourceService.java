package com.elextec.lease.manager.service;

import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.lease.manager.request.SysResParam;
import com.elextec.lease.model.SysResourcesIcon;
import com.elextec.persist.model.mybatis.SysResources;

import java.util.List;

/**
 * 接口 资源控制Service.
 * Created by wangtao on 2018/1/16.
 */
public interface SysResourceService {
    /**
     * 获得资源列表.
     * @param needPaging 是否需要分页
     * @param pr 分页参数
     * @return 资源列表
     * @throws BizException 查询业务异常
     */
    public PageResponse<SysResources> list(boolean needPaging, PageRequest pr);

    /**
     * 获得资源列表.
     * @param needPaging 是否需要分页
     * @param pr 带条件的分页参数
     * @return 资源列表
     */
    public PageResponse<SysResources> listByParam(boolean needPaging, SysResParam pr);

    /**
     * 批量插入资源.
     * @param resourceInfos 资源信息
     * @throws BizException 插入时异常，异常时全部数据回滚，日志记录出错记录号
     */
    public void insertSysResources(List<SysResources> resourceInfos);

    /**
     * 插入资源.
     * @param resourceInfo 资源信息
     */
    public void insertSysResource(SysResources resourceInfo);

    /**
     * 修改资源信息.
     * @param res 新的资源信息
     */
    public void updateSysResources(SysResources res);

    /**
     * 批量删除资源.
     * @param ids 待删除的资源ID列表
     */
    public void deleteSysResources(List<String> ids);

    /**
     * 根据ID查询资源信息
     * @param id 资源ID
     * @return 资源信息
     */
    public SysResources getSysResourceByPrimaryKey(String id);

    /**
     * 根据角色ID查询资源列表.
     * @param roleId 角色ID
     * @return 资源列表
     */
    public List<SysResources> listSysResourcesByRoleId(String roleId);

    /**
     * 查询资源Icon列表.
     * @return 资源Icon列表
     */
    public List<SysResourcesIcon> listSysResourceIcons();
}
