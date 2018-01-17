package com.elextec.lease.manager.service;

import com.elextec.framework.exceptions.BizException;
import com.elextec.persist.model.mybatis.ext.SysUserExt;

/**
 * 接口 权限控制Service.
 * Created by wangtao on 2018/1/16.
 */
public interface SysAuthService {
    public SysUserExt login(String loginName, String password) throws BizException;
}
