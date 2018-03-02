package com.elextec.framework.filter;

import com.alibaba.fastjson.JSONObject;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.plugins.redis.RedisClient;
import com.elextec.framework.utils.WzHttpUtil;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.persist.field.enums.ResourceType;
import com.elextec.persist.model.mybatis.SysResources;
import com.elextec.persist.model.mybatis.ext.SysUserExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 请求Filter.
 * @author wangtao
 */
@Order(Integer.MAX_VALUE)
@WebFilter(filterName = "VisitFilter", urlPatterns = "/*")
public class VisitFilter implements Filter {

    /** 日志. */
    private static final Logger logger = LoggerFactory.getLogger(VisitFilter.class);

    @Value("${localsetting.platform-type}")
    private String platformType;
    @Value("${localsetting.login-overtime-sec}")
    private String loginOverTimeSec;
    @Value("${localsetting.nofilters}")
    private String nofilters;
    @Value("${localsetting.white-flag}")
    private String whiteFlag;
    @Value("${localsetting.white-url}")
    private String whiteUrl;
    @Value("${localsetting.black-flag}")
    private String blackFlag;
    @Value("${localsetting.black-url}")
    private String blackUrl;

    @Autowired
    private RedisClient redisClient;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("过滤器初始化");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {


        // 请求
        HttpServletRequest req = (HttpServletRequest) request;
        // 响应
        HttpServletResponse resp = (HttpServletResponse) response;
        // 请求URL
        String url = req.getRequestURI();
        // 请求方法
        String method = req.getMethod();
        // 请求IP
        String ipStr = WzHttpUtil.getClientIP(req);

        logger.info("请求:" + url + "-" + method);

        if (WzStringUtil.isBlank(ipStr)) {
            blackFlag = "false";
            whiteFlag = "false";
        }
        // 黑名单验证
        if (WzStringUtil.isNotBlank(blackFlag) && "true".equals(blackFlag.toLowerCase())) {
            if (WzStringUtil.isNotBlank(blackUrl)) {
                String[] blackIps = blackUrl.split(",");
                for (int i = 0; i < blackIps.length; i++) {
                    if (ipStr.equalsIgnoreCase(blackIps[i])) {
                        MessageResponse errInfo = new MessageResponse(RunningResult.FORBIDDEN);
                        resp.getWriter().write(JSONObject.toJSONString(errInfo));
                        return;
                    }
                }
            }
        }
        // 白名单验证
        if (WzStringUtil.isNotBlank(whiteFlag) && "true".equals(whiteFlag.toLowerCase())) {
            boolean isOk = false;
            if (WzStringUtil.isBlank(whiteUrl)) {
                MessageResponse errInfo = new MessageResponse(RunningResult.FORBIDDEN);
                resp.getWriter().write(JSONObject.toJSONString(errInfo));
                return;
            } else {
                String[] whiteIps = whiteUrl.split(",");
                for (int i = 0; i < whiteIps.length; i++) {
                    if (ipStr.equalsIgnoreCase(whiteIps[i])) {
                        isOk = true;
                        break;
                    }
                }
                if (!isOk) {
                    MessageResponse errInfo = new MessageResponse(RunningResult.FORBIDDEN);
                    resp.getWriter().write(JSONObject.toJSONString(errInfo));
                    return;
                }
            }
        }
        // 无需验证请求过滤
        if (WzStringUtil.isNotBlank(nofilters)) {
            String[] noFilterUrls = nofilters.split(",");
            for (int i = 0; i < noFilterUrls.length; i++) {
                if (-1 < url.indexOf(noFilterUrls[i])) {
                    chain.doFilter(request, response);
                    return;
                }
            }
        }
        // 获得用户登录信息
        String uToken = req.getHeader(WzConstants.HEADER_LOGIN_TOKEN);
        // 无Token则报未登录
        if (WzStringUtil.isBlank(uToken)) {
            MessageResponse errInfo = new MessageResponse(RunningResult.AUTH_OVER_TIME.code(), "尚未登录，请登录");
            resp.getWriter().write(JSONObject.toJSONString(errInfo));
            return;
        }
        // 未获得登录缓存信息则报登录超时
        Map<String, Object> uInfo = (Map<String, Object>) redisClient.valueOperations().get(WzConstants.GK_LOGIN_INFO + uToken);
        if (null == uInfo) {
            MessageResponse errInfo = new MessageResponse(RunningResult.AUTH_OVER_TIME);
            resp.getWriter().write(JSONObject.toJSONString(errInfo));
            return;
        }
        // 未获得登录用户信息则报登录超时
        SysUserExt uVo = (SysUserExt) uInfo.get(WzConstants.KEY_USER_INFO);
        if (null == uVo) {
            MessageResponse errInfo = new MessageResponse(RunningResult.AUTH_OVER_TIME);
            resp.getWriter().write(JSONObject.toJSONString(errInfo));
            return;
        }
        // 平台可访问验证
        if (WzStringUtil.isBlank(platformType)) {
            platformType = "mobile";
        }
        if ("all".equals(platformType.toLowerCase())) {
            // 所有接口均可访问
        } else if ("mobile".equals(platformType.toLowerCase())) {
            // 请求中必有如下路径
            if (0 > url.indexOf("/mobile/")) {
                MessageResponse errInfo = new MessageResponse(RunningResult.NO_PERMISSION);
                resp.getWriter().write(JSONObject.toJSONString(errInfo));
                return;
            }
        } else if ("manager".equals(platformType.toLowerCase())) {
            // 请求中必有如下路径
            if (0 > url.indexOf("/manager/")) {
                MessageResponse errInfo = new MessageResponse(RunningResult.NO_PERMISSION);
                resp.getWriter().write(JSONObject.toJSONString(errInfo));
                return;
            }
        } else if ("deviceapi".equals(platformType.toLowerCase())) {
            // 请求中必有如下路径
            if (0 > url.indexOf("/device/")) {
                MessageResponse errInfo = new MessageResponse(RunningResult.NO_PERMISSION);
                resp.getWriter().write(JSONObject.toJSONString(errInfo));
                return;
            }
        } else {
            MessageResponse errInfo = new MessageResponse(RunningResult.FORBIDDEN);
            resp.getWriter().write(JSONObject.toJSONString(errInfo));
            return;
        }
        // 验证用户权限
        List<SysResources> urVoLs = (List<SysResources>) uInfo.get(WzConstants.KEY_RES_INFO);
        if ("manager".equals(platformType.toLowerCase())) {
            boolean isCanUsed = false;
            boolean hasFunction = false;
            if (null == urVoLs || 0 == urVoLs.size()) {
                isCanUsed = false;
            } else {
                for (SysResources sr : urVoLs) {
                    if (ResourceType.FUNCTION.equals(sr.getResType())) {
                        if (WzStringUtil.isNotBlank(sr.getResUrl())
                                && -1 < url.indexOf(sr.getResUrl())) {
                            isCanUsed = true;
                            break;
                        }
                        hasFunction = true;
                    }
                }
            }
            if (hasFunction && !isCanUsed) {
                MessageResponse errInfo = new MessageResponse(RunningResult.NO_PERMISSION.code(), "您无权使用该功能");
                resp.getWriter().write(JSONObject.toJSONString(errInfo));
                return;
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("过滤器销毁");
    }

}
