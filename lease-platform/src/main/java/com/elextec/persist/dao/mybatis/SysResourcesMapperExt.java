package com.elextec.persist.dao.mybatis;

import com.elextec.persist.model.mybatis.SysRefUserRoleKey;
import com.elextec.persist.model.mybatis.SysResources;
import com.elextec.persist.model.mybatis.ext.SysUserExt;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 接口 资源相关扩展接口.
 */
public interface SysResourcesMapperExt extends SysResourcesMapper {
    /**
     * 查询用户的所有资源.
     * @param userId 用户ID
     */
    List<SysResources> selectByUserId(@Param("userId") String userId);
}
