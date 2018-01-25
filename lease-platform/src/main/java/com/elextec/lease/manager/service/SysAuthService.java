package com.elextec.lease.manager.service;

import com.elextec.framework.exceptions.BizException;
import com.elextec.persist.model.mybatis.ext.SysUserExt;

/**
 * 接口 权限控制Service.
 * Created by wangtao on 2018/1/16.
 */
public interface SysAuthService {
    /**
     * 登录.
     * @param loginName 用户名或手机号码
     * @param authStr 验证字符串
     * @param loginTime 登录时间
     * @return 查询到的用户信息
     */
    public SysUserExt login(String loginName, String authStr, long loginTime);

    /**
     * 用户权限认证.
     * @param loginName 登录名或手机号码
     * @param password 登录密码
     * @param authStr 验证字符串
     * @param authTime 验证时间
     * @return 验证结果
     */
    public boolean verifyUser(String loginName, String password, String authStr, long authTime);
}
