package com.elextec.lease.manager.service;

import com.elextec.framework.common.request.RefUserRolesParam;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.lease.manager.request.SysUserParam;
import com.elextec.lease.model.BizVehicleBatteryParts;
import com.elextec.persist.model.mybatis.SysUser;
import com.elextec.persist.model.mybatis.SysUserExample;
import com.elextec.persist.model.mybatis.ext.SysUserExt;

import java.util.List;
import java.util.Map;

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
     */
    public PageResponse<SysUserExt> list(boolean needPaging, SysUserParam pr);

    /**
     * 获得用户列表（附带扩展信息）.
     * @param needPaging 是否需要分页
     * @param pr 带条件的分页参数
     * @return 用户列表
     */
    public PageResponse<SysUserExt> listExtByParam(boolean needPaging, SysUserParam pr);

    /**
     * 批量插入用户.
     * @param userInfos 用户信息
     */
    public void insertSysUsers(List<SysUser> userInfos);

    /**
     * 插入用户.
     * @param userInfo 用户信息
     */
    public void insertSysUser(SysUser userInfo);

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
     * 给用户分配角色.
     * @param params 用户对应角色map
     */
    public void refSysUserAndRoles(RefUserRolesParam params);

    /**
     * 根据ID查询用户信息.
     * @param id 用户ID
     */
    public SysUser getSysUserByPrimaryKey(String id);

    /**
     * 根据ID查询用户扩展信息.
     * @param example 查询条件
     * @return 用户扩展信息
     */
    public SysUserExt getExtById(SysUserExample example);

    /**
     * 根据用户ID查询车辆、电池以及配件信息
     * */
    public List<BizVehicleBatteryParts> getVehiclePartsById(String userId);


    /**
     * 用户与车辆解绑.
     * @param userId 用户ID
     * @param vehicleId 车辆ID
     * @param orgId 操作用户的企业ID
     */
    public void unBind(String userId,String vehicleId,String orgId);

    /**
     * 用户与车辆绑定.
     * @param userId 用户ID
     * @param vehicleId 车辆ID
     * @param orgId 操作用户的企业ID
     */
    public void bind(String userId,String vehicleId,String orgId);
}
