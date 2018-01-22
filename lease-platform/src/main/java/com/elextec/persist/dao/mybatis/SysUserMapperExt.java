package com.elextec.persist.dao.mybatis;

import com.elextec.persist.model.mybatis.SysUserExample;
import com.elextec.persist.model.mybatis.ext.SysUserExt;

import java.util.List;

public interface SysUserMapperExt extends SysUserMapper {
    /**
     * 查询用户扩展列表.
     * @param example 查询条件
     * @return 用户扩展信息列表
     */
    List<SysUserExt> login(SysUserExample example);
}
