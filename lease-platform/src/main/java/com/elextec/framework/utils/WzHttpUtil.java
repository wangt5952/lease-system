package com.elextec.framework.utils;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

/**
 * 网络请求工具类.
 * Created by wangtao on 2018/1/16.
 */
public class WzHttpUtil {
    /** 日志. */
    private static final Logger logger = LoggerFactory.getLogger(WzHttpUtil.class);

    /**
     * 获得请求端的IP.
     * @param request HttpServletRequest
     * @return 请求端IP
     */
    public static String getClientIP(HttpServletRequest request) {
        String ipStr = request.getHeader("X-Real-IP");
        if (WzStringUtil.isBlank(ipStr) || "unknown".equalsIgnoreCase(ipStr)) {
            ipStr = request.getHeader("X-Forwarded-For");
        }
        if (WzStringUtil.isBlank(ipStr) || "unknown".equalsIgnoreCase(ipStr)) {
            ipStr = request.getHeader("Proxy-Client-IP");
        }
        if (WzStringUtil.isBlank(ipStr) || "unknown".equalsIgnoreCase(ipStr)) {
            ipStr = request.getHeader("WL-Proxy-Client-IP");
        }
        if (WzStringUtil.isBlank(ipStr) || "unknown".equalsIgnoreCase(ipStr)) {
            ipStr = request.getHeader("HTTP_CLIENT_IP");
        }
        if (WzStringUtil.isBlank(ipStr) || "unknown".equalsIgnoreCase(ipStr)) {
            ipStr = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (WzStringUtil.isBlank(ipStr) || "unknown".equalsIgnoreCase(ipStr)) {
            ipStr = request.getRemoteAddr();
        }
        // 多级反向代理
        if (WzStringUtil.isNotBlank(ipStr) && WzStringUtil.isNotBlank(ipStr.trim())) {
            StringTokenizer st = new StringTokenizer(ipStr, ",");
            if (st.countTokens() > 1) {
                return st.nextToken();
            }
        }
        return ipStr;
    }
}
