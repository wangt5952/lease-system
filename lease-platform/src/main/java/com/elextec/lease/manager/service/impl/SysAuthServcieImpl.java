package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.utils.WzEncryptUtil;
import com.elextec.lease.manager.service.SysAuthService;
import com.elextec.persist.dao.mybatis.SysUserMapperExt;
import com.elextec.persist.model.mybatis.SysUserExample;
import com.elextec.persist.model.mybatis.ext.SysUserExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public SysUserExt login(String loginName, String authStr, long loginTime) {
        // 查询用户
        SysUserExample sysUserExample = new SysUserExample();
        SysUserExample.Criteria loginNameCri = sysUserExample.createCriteria();
        loginNameCri.andLoginNameEqualTo(loginName);
        SysUserExample.Criteria mobileCri = sysUserExample.or();
        mobileCri.andUserMobileEqualTo(loginName);
        List<SysUserExt> sysUserLs = sysUserMapperExt.login(sysUserExample);
        if (null == sysUserLs || 0 == sysUserLs.size()) {
            throw new BizException(RunningResult.NO_USER);
        } else {
            SysUserExt sue = sysUserLs.get(0);
            // 验证用户
            // 验证用户名和密码是否正确
            if (verifyUser(loginName, sue.getPassword(), authStr, loginTime)) {
                return sue;
            } else {
                throw new BizException(RunningResult.NAME_OR_PASSWORD_WRONG);
            }
        }
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
