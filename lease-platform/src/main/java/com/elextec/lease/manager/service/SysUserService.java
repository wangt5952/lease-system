package com.elextec.lease.manager.service;

import com.elextec.framework.common.request.RefUserRolesParam;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.persist.model.mybatis.SysResources;
import com.elextec.persist.model.mybatis.SysUser;

import java.util.List;

/**
 * 接口 用户控制Service.
 * Created by wangtao on 2018/1/16.
 */
public interface SysUserService {
    /**
     * 获得用户列表.
     * @param needPaging 是否需要分页
     * @param pr 分页参数
     * @return 用户列表
     * @throws BizException 查询业务异常
     */
    public PageResponse<SysUser> list(boolean needPaging, PageRequest pr);

    /**
     * 批量插入用户.
     * @param userInfos 用户信息
     * @throws BizException 插入时异常，异常时全部数据回滚，日志记录出错记录号
     */
    public void insertSysUsers(List<SysUser> userInfos);

    /**
     * 修改用户信息.
     * @param userInfo 新的用户信息
     */
    public void updateSysUser(SysUser userInfo);

    /**
     * 批量删除用户.
     * @param ids 待删除的用户ID列表
     */
    public void deleteSysUser(List<String> ids);

    /**
     * 给用户分配角色
     * @param params 用户对应角色map
     * */
    public void refUserAndRoles(RefUserRolesParam params);

    /**
     * 根据ID查询用户信息
     * @param id 用户ID
     * */
    public SysUser getByPrimaryKey(String id);
}
