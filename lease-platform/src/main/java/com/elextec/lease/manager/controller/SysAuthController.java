package com.elextec.lease.manager.controller;

import com.alibaba.fastjson.JSON;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.common.request.LoginParam;
import com.elextec.framework.common.response.MessageResponse;
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
    private Integer loginOvertime;

    @Autowired
    private SysAuthService sysAuthService;

    @Autowired
    private RedisClient redisClient;

    /**
     * 登录.
     * @param loginNameAndPassword 登录参数JSON
     * @return
     */
    @RequestMapping(path = {"/login"})
    public MessageResponse login(@RequestBody String loginNameAndPassword) {
        // 无参数则报“非法请求”
        if (WzStringUtil.isBlank(loginNameAndPassword)) {
            MessageResponse mr = new MessageResponse(RunningResult.UNAUTHORIZED);
            return mr;
        } else {
            // 参数解析错误则报“非法请求”
            LoginParam loginParam = JSON.parseObject(loginNameAndPassword, LoginParam.class);
            if (null == loginParam || WzStringUtil.isBlank(loginParam.getLoginName())
                    || WzStringUtil.isBlank(loginParam.getLoginAuthStr())
                    || null == loginParam.getLoginTime()) {
                return new MessageResponse(RunningResult.UNAUTHORIZED);
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
            Integer overtime = (null == loginOvertime) ? 900 : loginOvertime;
            // 登录成功，保存到Redis中
            redisClient.valueOperations().set(WzConstants.GK_LOGIN_INFO + ":" + loginToken, userExt, overtime, TimeUnit.SECONDS);
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
    public MessageResponse logout() {
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
