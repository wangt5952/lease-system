package com.elextec.persist.dao.mybatis;

import com.elextec.lease.manager.request.SysUserParam;
import com.elextec.persist.model.mybatis.BizOrganization;
import com.elextec.persist.model.mybatis.SysRefUserRoleKey;
import com.elextec.persist.model.mybatis.SysUser;
import com.elextec.persist.model.mybatis.SysUserExample;
import com.elextec.persist.model.mybatis.ext.SysUserExt;

import java.util.List;

public interface SysUserMapperExt extends SysUserMapper {
    /**
     * 查询用户扩展列表.
     * @param example 查询条件
     * @return 用户扩展信息列表
     */
    List<SysUserExt> selectExtByExample(SysUserExample example);

    /**
     * 为用户分配角色
     * @param sysRefUserRoleKey 用户和角色对
     */
    void refUserAndRoles(SysRefUserRoleKey sysRefUserRoleKey);

    /**
     * 删除用户所有角色.
     * @param userId 用户ID
     */
    void deleteUserAndRoles(String userId);

    /**
     * 查询用户扩展信息列表.
     * @param sysUserParam 查询条件
     * @return 用户扩展信息列表
     */
    List<SysUserExt> selectExtByParam(SysUserParam sysUserParam);

    /**
     * 查询用户扩展信息记录数.
     * @param sysUserParam 查询条件
     * @return 用户扩展信息记录数
     */
    int countExtByParam(SysUserParam sysUserParam);

    /**
     * 根据车辆id查询用户信息
     * @param vehicleId 车辆id
     * @return 用户信息
     */
    SysUser getUserByVehicle(String vehicleId);

}
