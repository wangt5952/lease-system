package com.elextec.framework;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.redis.RedisClient;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.persist.model.mybatis.ext.SysUserExt;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 基础Controller.
 * Created by wangtao on 2018/1/16.
 */
public class BaseController {

    @Autowired
    protected RedisClient redisClient;

    /**
     * 获得PC端登录用户信息.
     * @param request HttpServletRequest
     * @return 登录用户信息对象
     */
    protected SysUserExt getLoginUserInfo(HttpServletRequest request) {
        String userToken = request.getHeader(WzConstants.HEADER_LOGIN_TOKEN);
        if (WzStringUtil.isBlank(userToken)) {
            throw new BizException(RunningResult.AUTH_OVER_TIME);
        }
        Map<String, Object> userInfo = (Map<String, Object>) redisClient.valueOperations().get(WzConstants.GK_LOGIN_INFO + userToken);
        SysUserExt sue = (SysUserExt) userInfo.get(WzConstants.KEY_USER_INFO);
        return sue;
    }

    /**
     * 修改用户后更新PC端Session中的登录用户信息.
     * @param request HttpServletRequest
     * @param newUserExt 新用户登录信息
     * @param ot 超时时间（秒）
     */
    protected void resetLoginUserInfo(HttpServletRequest request, SysUserExt newUserExt, int ot) {
        String userToken = request.getHeader(WzConstants.HEADER_LOGIN_TOKEN);
        if (WzStringUtil.isBlank(userToken)) {
            throw new BizException(RunningResult.AUTH_OVER_TIME);
        }
        Map<String, Object> userInfo = (Map<String, Object>) redisClient.valueOperations().get(WzConstants.GK_LOGIN_INFO + userToken);
        userInfo.remove(WzConstants.KEY_USER_INFO);
        userInfo.put(WzConstants.KEY_USER_INFO, newUserExt);
        redisClient.valueOperations().set(WzConstants.GK_LOGIN_INFO + userToken, userInfo, ot, TimeUnit.SECONDS);
    }
}
