package com.elextec.persist.dao.mybatis;

import com.elextec.persist.model.mybatis.SysRefRoleResourcesKey;

public interface SysRoleMapperExt extends SysRoleMapper {

    void refRoleAndResources(SysRefRoleResourcesKey sysRefRoleResourcesKey);

    void deleteRoleAndResources(String userId);
}
