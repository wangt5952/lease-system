package com.elextec.lease.manager.controller;

import com.alibaba.fastjson.JSON;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.common.request.LoginParam;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.redis.RedisClient;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.service.SysAuthService;
import com.elextec.persist.model.mybatis.ext.SysUserExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 权限控制Controller.
 * Created by wangtao on 2018/1/16.
 */
@RestController
@RequestMapping(path = "/manager/auth")
public class SysAuthController extends BaseController {

    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(SysAuthController.class);

    @Value("localsetting.login-overtime-sec")
    private String loginOvertime;

    @Autowired
    private SysAuthService sysAuthService;

    @Autowired
    private RedisClient redisClient;

    /**
     * 登录.
     * @param loginNameAndPassword 登录参数JSON
     * <pre>
     *     {
     *         loginName:登录用户名,
     *         loginAuthStr:验证字符串 MD5(用户名+MD5(密码).upper()+loginTime).upper(),
     *         loginTime:登录时间
     *     }
     * </pre>
     * @return 登录结果及登录账户信息
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:{
     *             key_login_token:登录Token,
     *             key_user_info:{
     *                 id:ID,
     *                 loginName:登录用户名,
     *                 userMobile:手机号码,
     *                 userType:用户类型（PLATFORM-平台、ENTERPRISE-企业、INDIVIDUAL-个人）,
     *                 userIcon:Icon路径,
     *                 nickName:昵称,
     *                 userName:名称,
     *                 userRealNameAuthFlag:是否已实名认证,
     *                 userPid:身份证号,
     *                 userIcFront:身份证正面照路径,
     *                 userIcBack:身份证背面照路径,
     *                 userIcGroup:本人于身份证合照路径,
     *                 orgId:所属企业ID,
     *                 orgCode:企业Code,
     *                 orgName:企业名,
     *                 userStatus:用户状态（NORMAL-正常、FREEZE-冻结/维保、INVALID-作废）,
     *                 createUser:创建人,
     *                 createTime:创建时间,
     *                 updateUser:更新人,
     *                 updateTime:更新时间
     *             }
     *         }
     *     }
     * </pre>
     */
    @RequestMapping(path = {"/login"})
    public MessageResponse login(@RequestBody String loginNameAndPassword) {
        // 无参数则报“非法请求”
        if (WzStringUtil.isBlank(loginNameAndPassword)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误
            LoginParam loginParam = null;
            try {
                String paramStr = URLDecoder.decode(loginNameAndPassword, "utf-8");
                loginParam = JSON.parseObject(paramStr, LoginParam.class);
                if (null == loginParam || WzStringUtil.isBlank(loginParam.getLoginName())
                        || WzStringUtil.isBlank(loginParam.getLoginAuthStr())
                        || null == loginParam.getLoginTime()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }

            // 验证用户并返回用户信息
            SysUserExt userExt = sysAuthService.login(loginParam.getLoginName(), loginParam.getLoginAuthStr(), loginParam.getLoginTime());
            // 组织登录返回信息
            Map<String, Object> loginInfo = new HashMap<String, Object>();
            // 登录Token
            // 生成登录token
            String loginToken = WzUniqueValUtil.makeUUID();
            loginInfo.put(WzConstants.KEY_LOGIN_TOKEN, loginToken);
            // 设置登录用户信息
            loginInfo.put(WzConstants.KEY_USER_INFO, userExt);
            // 设置超时时间
            Integer overtime = 900;
            if (WzStringUtil.isNumeric(loginOvertime)) {
                overtime = Integer.parseInt(loginOvertime);
            }
            // 登录成功，保存到Redis中
            redisClient.valueOperations().set(WzConstants.GK_PC_LOGIN_INFO + ":" + loginToken, userExt, overtime, TimeUnit.SECONDS);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, loginInfo);
            return mr;
        }
    }

    /**
     * 退出.
     * @return 退出结果
     * <pre>
     *     {
     *          code:处理Code,
     *          message:处理消息,
     *          respData:""
     *     }
     * </pre>
     */
    @RequestMapping(path = {"/logout"})
    public MessageResponse logout(HttpServletRequest request, HttpServletResponse response) {
        String token = WzStringUtil.defaultIfEmpty(request.getHeader(WzConstants.HEADER_LOGIN_TOKEN), "");
        // 清除登录信息
        redisClient.valueOperations().getOperations().delete(WzConstants.GK_PC_LOGIN_INFO + ":" + token);
        // 组织返回结果并返回
        MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
        return null;
    }

    /**
     * 修改密码.
     * @param oldAndNewPassword 旧密码及新密码
     * <pre>
     *     {
     *          userId:用户ID,
     *          oldPassword:旧密码,
     *          newPassword:新密码
     *     }
     * </pre>
     * @return 密码修改结果
     * <pre>
     *     {
     *          code:处理Code,
     *          message:处理消息,
     *          respData:""
     *     }
     * </pre>
     */
    @RequestMapping(path = {"/modifypassword"})
    public MessageResponse modifyPassword(@RequestBody String oldAndNewPassword) {
        return null;
    }

    /**
     * 发送短信验证码.
     * @param mobileAndVC 手机号码及验证码
     * <pre>
     *     {
     *          mobile:手机号码,
     *          vc:图片验证码（用于发送短信用，根据短信平台可选）
     *     }
     * </pre>
     * @return 发送结果
     * <pre>
     *     {
     *          code:处理Code,
     *          message:处理消息,
     *          respData:{
     *              resetToken:重置Token，重置密码时需要将此Token传回进行验证
     *          }
     *     }
     * </pre>
     */
    @RequestMapping(path = {"/sendsms"})
    public MessageResponse sendSms(@RequestBody String mobileAndVC) {
        return null;
    }

    /**
     * 重置密码.
     * @param resetTokenAndNewPassword 重置密码参数JSON
     * <pre>
     *     {
     *         userId:用户ID,
     *         resetToken:重置密码Token,
     *         newPassword:新密码
     *     }
     * </pre>
     * @return 密码重置结果
     * <pre>
     *     {
     *          code:处理Code,
     *          message:处理消息,
     *          respData:""
     *     }
     * </pre>
     */
    @RequestMapping(path = {"/resetpassword"})
    public MessageResponse resetPassword(@RequestBody String resetTokenAndNewPassword) {
        return null;
    }
}
