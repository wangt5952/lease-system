package com.elextec.framework.interceptors;

import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.utils.WzDateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Controller访问拦截器.
 * Created by wangtao on 2018/3/6.
 */
public class ControllerInterceptor implements HandlerInterceptor {
    /** 日志. */
    private static final Logger logger = LoggerFactory.getLogger(ControllerInterceptor.class);

    /*
     * 在请求处理之前进行调用（Controller方法调用之前）.
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if (!o.getClass().isAssignableFrom(HandlerMethod.class)) {
            return true;
        }
        final HandlerMethod hm = (HandlerMethod) o;
        StringBuffer logStrBuf = new StringBuffer("===");
        logStrBuf.append(WzDateUtil.dt2Str(new Date(), WzDateUtil.DT_FMT_5)).append(" ");
        logStrBuf.append(hm.getBean().getClass().getName()).append(".");
        logStrBuf.append(hm.getMethod().getName()).append("()访问开始===").append(WzConstants.KEY_LINE_SEPARATOR);
        logger.info(logStrBuf.toString());
        return true;
    }

    /*
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）.
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if (!o.getClass().isAssignableFrom(HandlerMethod.class)) {
            return;
        }
        final HandlerMethod hm = (HandlerMethod) o;
        StringBuffer logStrBuf = new StringBuffer("===");
        logStrBuf.append(WzDateUtil.dt2Str(new Date(), WzDateUtil.DT_FMT_5)).append(" ");
        logStrBuf.append(hm.getBean().getClass().getName()).append(".");
        logStrBuf.append(hm.getMethod().getName()).append("()访问结束===");
    }

    /*
     * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）.
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}
