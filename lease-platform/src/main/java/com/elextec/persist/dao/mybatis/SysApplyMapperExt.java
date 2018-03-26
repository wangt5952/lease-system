package com.elextec.persist.dao.mybatis;

import com.elextec.lease.manager.request.SysApplyParam;
import com.elextec.persist.model.mybatis.ext.SysApplyExt;

import java.util.List;

public interface SysApplyMapperExt extends SysApplyMapper {
    /**
     * 查询申请扩展信息列表.
     * @param sysApplyParam 查询条件
     * @return 申请扩展信息列表
     */
    List<SysApplyExt> selectExtByParam(SysApplyParam sysApplyParam);

    /**
     * 查询申请扩展信息记录数.
     * @param sysApplyParam 查询条件
     * @return 申请扩展信息记录数
     */
    int countExtByParam(SysApplyParam sysApplyParam);
}