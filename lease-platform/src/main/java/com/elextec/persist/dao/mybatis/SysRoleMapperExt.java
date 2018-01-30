package com.elextec.persist.dao.mybatis;

import com.elextec.persist.model.mybatis.SysRefRoleResourcesKey;
import com.elextec.persist.model.mybatis.SysRole;

import java.util.List;

public interface SysRoleMapperExt extends SysRoleMapper {

    void refRoleAndResources(SysRefRoleResourcesKey sysRefRoleResourcesKey);

    void deleteRoleAndResources(String userId);

    List<SysRole> selectByUserId(String userId);
}
