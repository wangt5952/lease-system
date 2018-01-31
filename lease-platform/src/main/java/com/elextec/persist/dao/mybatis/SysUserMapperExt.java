package com.elextec.persist.dao.mybatis;

import com.elextec.persist.model.mybatis.SysRefUserRoleKey;
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
}
