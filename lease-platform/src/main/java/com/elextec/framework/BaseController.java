package com.elextec.framework;

import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.plugins.redis.RedisClient;
import com.elextec.persist.model.mybatis.ext.SysUserExt;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
    protected SysUserExt getPcLoginUserInfo(HttpServletRequest request) {
        String userToken = request.getHeader(WzConstants.HEADER_LOGIN_TOKEN);
        Map<String, Object> userInfo = (Map<String, Object>) redisClient.valueOperations().get(WzConstants.GK_PC_LOGIN_INFO + userToken);
        SysUserExt sue = (SysUserExt) userInfo.get(WzConstants.KEY_USER_INFO);
        return sue;
    }

    /**
     * 修改用户后更新PC端Session中的登录用户信息.
     * @param request HttpServletRequest
     * @param newUserExt 新用户登录信息
     */
    protected void resetPcLoginUserInfo(HttpServletRequest request, SysUserExt newUserExt) {
        String userToken = request.getHeader(WzConstants.HEADER_LOGIN_TOKEN);
        Map<String, Object> userInfo = (Map<String, Object>) redisClient.valueOperations().get(WzConstants.GK_PC_LOGIN_INFO + userToken);
        userInfo.remove(WzConstants.KEY_USER_INFO);
        userInfo.put(WzConstants.KEY_USER_INFO, newUserExt);
    }

    /**
     * 获得移动端登录用户信息.
     * @param request HttpServletRequest
     * @return 登录用户信息对象
     */
    protected SysUserExt getMobileLoginUserInfo(HttpServletRequest request) {
        String userToken = request.getHeader(WzConstants.HEADER_LOGIN_TOKEN);
        Map<String, Object> userInfo = (Map<String, Object>) redisClient.valueOperations().get(WzConstants.GK_MOBILE_LOGIN_INFO + userToken);
        SysUserExt sue = (SysUserExt) userInfo.get(WzConstants.KEY_USER_INFO);
        return sue;
    }

    /**
     * 修改用户后更新移动端Session中的登录用户信息.
     * @param request HttpServletRequest
     * @param newUserExt 新用户登录信息
     */
    protected void resetMobileLoginUserInfo(HttpServletRequest request, SysUserExt newUserExt) {
        String userToken = request.getHeader(WzConstants.HEADER_LOGIN_TOKEN);
        Map<String, Object> userInfo = (Map<String, Object>) redisClient.valueOperations().get(WzConstants.GK_MOBILE_LOGIN_INFO + userToken);
        userInfo.remove(WzConstants.KEY_USER_INFO);
        userInfo.put(WzConstants.KEY_USER_INFO, newUserExt);
    }
}
