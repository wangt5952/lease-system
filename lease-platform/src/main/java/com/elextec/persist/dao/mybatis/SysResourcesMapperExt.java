package com.elextec.persist.dao.mybatis;

import com.elextec.lease.manager.request.SysResParam;
import com.elextec.persist.model.mybatis.SysResources;
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

    /**
     * 查询角色关联的所有资源.
     * @param roleId 角色ID
     */
    List<SysResources> selectByRoleId(@Param("roleId") String roleId);

    /**
     * 查询资源信息列表.
     * @param sysResParam 查询条件
     * @return 资源信息列表
     */
    List<SysResources> selectByParam(SysResParam sysResParam);

    /**
     * 查询资源信息记录数.
     * @param sysResParam 查询条件
     * @return 资源信息记录数
     */
    int countByParam(SysResParam sysResParam);
}
