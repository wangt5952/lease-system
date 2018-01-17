package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
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
    private final Logger logger = LoggerFactory.getLogger(SysAuthServcieImpl.class);

    @Autowired
    private SysUserMapperExt sysUserMapperExt;

    @Override
    public SysUserExt login(String loginName, String password) throws BizException {
        SysUserExample sysUserExample = new SysUserExample();
        SysUserExample.Criteria sysUserExampleCri = sysUserExample.createCriteria();
        sysUserExampleCri.andLoginNameEqualTo(loginName);
        List<SysUserExt> sysUserLs = sysUserMapperExt.selectExtByExample(sysUserExample);
        if (null == sysUserLs || 0 == sysUserLs.size()) {
            throw new BizException(RunningResult.UNAUTHORIZED);
        } else {
            return sysUserLs.get(0);
        }
    }
}
