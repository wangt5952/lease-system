package com.elextec.lease.manager.controller;

import com.elextec.framework.base.BaseController;
import com.elextec.framework.base.MessageResponse;
import com.elextec.framework.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.redis.RedisClient;
import com.elextec.framework.utils.WzStringUtils;
import com.elextec.lease.manager.persist.model.mybatis.ext.SysUserExt;
import com.elextec.lease.manager.service.SysAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 权限控制Controller.
 * Created by wangtao on 2018/1/16.
 */
@RestController
@RequestMapping(path = "/manager/auth")
public class SysAuthController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(SysAuthController.class);

    @Autowired
    private SysAuthService sysAuthService;

    @Autowired
    private RedisClient redisClient;

    @RequestMapping(path = {"/login"})
    public MessageResponse login(String loginName, String password) {
        if (WzStringUtils.isBlank(loginName) || WzStringUtils.isBlank(password)) {
            MessageResponse mr = new MessageResponse(RunningResult.UNAUTHORIZED);
            return mr;
        } else {
            try {
                SysUserExt userExt = sysAuthService.login(loginName, password);
                redisClient.valueOperations().set("LOGIN_USER", userExt, 5, TimeUnit.MINUTES);
                MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, userExt);
                return mr;
            } catch (BizException e) {
                logger.error(e.getMessage(), e);
                MessageResponse mr = new MessageResponse(e.getInfoCode(), e.getMessage());
                return mr;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                MessageResponse mr = new MessageResponse(RunningResult.SERVER_ERROR.code(), e.getMessage());
                return mr;
            }
        }
    }
}
