package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.utils.WzEncryptUtil;
import com.elextec.lease.manager.service.SysAuthService;
import com.elextec.persist.dao.mybatis.SysResourcesMapperExt;
import com.elextec.persist.dao.mybatis.SysUserMapperExt;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.field.enums.RealNameAuthFlag;
import com.elextec.persist.model.mybatis.SysResources;
import com.elextec.persist.model.mybatis.SysUserExample;
import com.elextec.persist.model.mybatis.ext.SysUserExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限控制Service实现类.
 * Created by wangtao on 2018/1/16.
 */
@Service
public class SysAuthServcieImpl implements SysAuthService {
    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(SysAuthServcieImpl.class);

    @Autowired
    private SysUserMapperExt sysUserMapperExt;

    @Autowired
    private SysResourcesMapperExt sysResourcesMapperExt;

    @Override
    public Map<String, Object> login(String loginName, String authStr, long loginTime) {
        // 返回结果定义
        Map<String, Object> loginData = new HashMap<String, Object>();

        // 查询用户
        SysUserExample sysUserExample = new SysUserExample();
        SysUserExample.Criteria loginNameCri = sysUserExample.createCriteria();
        loginNameCri.andLoginNameEqualTo(loginName);
        SysUserExample.Criteria mobileCri = sysUserExample.or();
        mobileCri.andUserMobileEqualTo(loginName);
        List<SysUserExt> sysUserLs = sysUserMapperExt.selectExtByExample(sysUserExample);

        // 处理用户信息
        if (null == sysUserLs || 0 == sysUserLs.size()) {
            throw new BizException(RunningResult.NO_USER);
        }
        SysUserExt sue = sysUserLs.get(0);
        // 验证用户名和密码是否正确
        if (verifyUser(loginName, sue.getPassword(), authStr, loginTime)) {
            loginData.put(WzConstants.KEY_USER_INFO, sue);
        } else {
            throw new BizException(RunningResult.NAME_OR_PASSWORD_WRONG);
        }
        // 处理资源信息（通过用户ID获取）
        List<SysResources> sysResLs = sysResourcesMapperExt.selectByUserId(sue.getId());
        if (null == sysResLs || 0 == sysResLs.size()) {
            throw new BizException(RunningResult.NO_PERMISSION);
        }
        loginData.put(WzConstants.KEY_RES_INFO, sysResLs);
        return loginData;
    }

    @Override
    public Map<String, Object> mobileLogin(String loginName, String authStr, long loginTime) {
        // 返回结果定义
        Map<String, Object> loginData = new HashMap<String, Object>();

        // 查询用户
        SysUserExample sysUserExample = new SysUserExample();
        SysUserExample.Criteria loginNameCri = sysUserExample.createCriteria();
        loginNameCri.andLoginNameEqualTo(loginName);
        //手机登录必须是个人客户
        loginNameCri.andUserTypeEqualTo(OrgAndUserType.INDIVIDUAL);
        SysUserExample.Criteria mobileCri = sysUserExample.or();
        mobileCri.andUserMobileEqualTo(loginName);
        //手机登录必须是个人客户
        mobileCri.andUserTypeEqualTo(OrgAndUserType.INDIVIDUAL);
        List<SysUserExt> sysUserLs = sysUserMapperExt.selectExtByExample(sysUserExample);

        // 处理用户信息
        if (null == sysUserLs || 0 == sysUserLs.size()) {
            throw new BizException(RunningResult.NO_USER);
        }
        SysUserExt sue = sysUserLs.get(0);
        // 验证用户名和密码是否正确
        if (verifyUser(loginName, sue.getPassword(), authStr, loginTime)) {
            loginData.put(WzConstants.KEY_USER_INFO, sue);
        } else {
            throw new BizException(RunningResult.NAME_OR_PASSWORD_WRONG);
        }
        return loginData;
    }

    @Override
    public boolean verifyUser(String loginName, String password, String authStr, long authTime) {
        // 如果登录时间和当前时间相差超过2分钟，则报错“认证超时”
        if ((System.currentTimeMillis() - authTime) > 120000) {
            throw new BizException(RunningResult.AUTH_OVER_TIME);
        }
        String chkStr = loginName + password + authTime;
        if (authStr.equals(WzEncryptUtil.getMD5(chkStr, true))) {
            return true;
        } else {
            throw new BizException(RunningResult.NAME_OR_PASSWORD_WRONG);
        }
    }
}
