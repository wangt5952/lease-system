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

    protected SysUserExt getLoginUserInfo(HttpServletRequest request) {
        String userToken = request.getHeader(WzConstants.HEADER_LOGIN_TOKEN);
        Map<String, Object> userInfo = (Map<String, Object>) redisClient.valueOperations().get(WzConstants.GK_PC_LOGIN_INFO + userToken);
        SysUserExt sue = (SysUserExt) userInfo.get(WzConstants.KEY_USER_INFO);
        return null;
    }
}
