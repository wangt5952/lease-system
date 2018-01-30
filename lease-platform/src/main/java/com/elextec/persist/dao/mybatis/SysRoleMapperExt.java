package com.elextec.persist.dao.mybatis;

import com.elextec.persist.model.mybatis.SysRefRoleResourcesKey;
import com.elextec.persist.model.mybatis.SysRole;

import java.util.List;

public interface SysRoleMapperExt extends SysRoleMapper {

    /**
     * 为角色分配资源.
     * @param sysRefRoleResourcesKey 角色ID及资源ID列表
     */
    void refRoleAndResources(SysRefRoleResourcesKey sysRefRoleResourcesKey);

    /**
     * 删除角色关联的资源.
     * @param roleId 角色ID
     */
    void deleteRoleAndResources(String roleId);

    /**
     * 查询用户的所有角色.
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> selectByUserId(String userId);
}
