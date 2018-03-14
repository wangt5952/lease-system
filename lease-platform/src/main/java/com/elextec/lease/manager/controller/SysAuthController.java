package com.elextec.lease.manager.controller;

import com.alibaba.fastjson.JSON;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.common.request.LoginParam;
import com.elextec.framework.common.request.ModifyPasswordParam;
import com.elextec.framework.common.request.ResetPasswordParam;
import com.elextec.framework.common.request.SmsParam;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.sms.SmsClient;
import com.elextec.framework.utils.WzCaptchaUtil;
import com.elextec.framework.utils.WzCheckCodeUtil;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.service.SysAuthService;
import com.elextec.lease.manager.service.SysUserService;
import com.elextec.persist.model.mybatis.SysUser;
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

    @Value("${localsetting.login-overtime-sec}")
    private String loginOvertime;

    @Autowired
    private SysAuthService sysAuthService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SmsClient smsClient;

    /**
     * 登录.
     * @param loginNameAndPassword 登录参数JSON
     * <pre>
     *     {
     *         loginName:登录用户名,
     *         loginAuthStr:验证字符串 MD5(用户名+MD5(密码).upper()+loginTime).upper(),
     *         loginTime:登录时间,
     *         needCaptcha:是否需要验证码，仅true有效,
     *         capthaToken:验证码Token,
     *         captcha:验证码内容
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
     *             },
     *             key_res_info:[
     *                 {
     *                     id:资源ID,
     *                     res_code:资源Code,
     *                     res_name:资源名称,
     *                     res_type:资源类型,
     *                     res_url:请求地址或页面名,
     *                     res_sort:分组排序（通过分组排序将菜单组排序后，菜单组内通过级别进行排序）,
     *                     show_flag:是否显示,
     *                     parent:父级ID（最上级为null）,
     *                     level:级别,
     *                     create_user:创建人,
     *                     create_time:创建时间,
     *                     update_user:更新人,
     *                     update_time:更新时间
     *                 },
     *                 ... ...
     *             ]
     *         }
     *     }
     * </pre>
     */
    @RequestMapping(path = {"/login"})
    public MessageResponse login(@RequestBody String loginNameAndPassword) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(loginNameAndPassword)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            LoginParam loginParam = null;
            try {
                String paramStr = URLDecoder.decode(loginNameAndPassword, "utf-8");
                loginParam = JSON.parseObject(paramStr, LoginParam.class);
                if (null == loginParam || WzStringUtil.isBlank(loginParam.getLoginName())
                        || WzStringUtil.isBlank(loginParam.getLoginAuthStr())
                        || null == loginParam.getLoginTime()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                // 验证码判断
                if (WzStringUtil.isNotBlank(loginParam.getNeedCaptcha())
                        && "true".equals(loginParam.getNeedCaptcha().toLowerCase())) {
                    // 未获得图形验证码Token
                    if (WzStringUtil.isBlank(loginParam.getCaptchaToken())
                            || WzStringUtil.isBlank(loginParam.getCaptcha())) {
                        throw new BizException(RunningResult.NO_PARAM.code(), "验证码解析失败");
                        // 开始验证图形验证码
                    } else {
                        // 获得预存图形验证码
                        String cc = (String) redisClient.valueOperations().get(WzConstants.GK_CAPTCHA + loginParam.getCaptchaToken());
                        // 图形验证码过期报错
                        if (WzStringUtil.isBlank(cc)) {
                            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "验证码已过期");
                        }
                        // 验证码不一致报错
                        if (!cc.equalsIgnoreCase(loginParam.getCaptcha())) {
                            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "验证码验证失败");
                        }
                    }
                    // 只要进行验证后，不管成功失败均将之前验证码作废
                    redisClient.valueOperations().getOperations().delete(WzConstants.GK_CAPTCHA + loginParam.getCaptchaToken());
                }
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }

            // 验证用户并返回用户信息
            Map<String, Object> loginVo = sysAuthService.login(loginParam.getLoginName(), loginParam.getLoginAuthStr(), loginParam.getLoginTime());
            // 组织登录返回信息
            Map<String, Object> loginInfo = new HashMap<String, Object>();
            // 登录Token
            // 生成登录token
            String loginToken = WzUniqueValUtil.makeUUID();
            loginInfo.put(WzConstants.KEY_LOGIN_TOKEN, loginToken);
            // 设置登录信息
            loginInfo.put(WzConstants.KEY_USER_INFO, loginVo.get(WzConstants.KEY_USER_INFO));
            loginInfo.put(WzConstants.KEY_RES_INFO, loginVo.get(WzConstants.KEY_RES_INFO));
            // 设置超时时间
            Integer overtime = 300;
            if (WzStringUtil.isNumeric(loginOvertime)) {
                overtime = Integer.parseInt(loginOvertime);
            }
            // 登录成功，保存到Redis中
            redisClient.valueOperations().set(WzConstants.GK_LOGIN_INFO + loginToken, loginInfo, overtime, TimeUnit.SECONDS);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, loginInfo);
            return mr;
        }
    }

    /**
     * 退出.
     * @param request 请求
     * @param response 响应
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
        redisClient.valueOperations().getOperations().delete(WzConstants.GK_LOGIN_INFO + token);
        // 组织返回结果并返回
        MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
        return null;
    }

    /**
     * 修改密码.
     * @param request 请求
     * @param oldAndNewPassword 旧密码及新密码
     * <pre>
     *     {
     *          oldAuthStr:同登录，旧密码验证字符串 MD5(loginName(登录用户名)+MD5(登录密码).upper()+authTime).upper(),
     *          authTime:验证时间,
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
    public MessageResponse modifyPassword(@RequestBody String oldAndNewPassword, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(oldAndNewPassword)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            ModifyPasswordParam modifyPasswordParam = null;
            try {
                String paramStr = URLDecoder.decode(oldAndNewPassword, "utf-8");
                modifyPasswordParam = JSON.parseObject(paramStr, ModifyPasswordParam.class);
                if (null == modifyPasswordParam
                        || WzStringUtil.isBlank(modifyPasswordParam.getNewPassword())
                        || WzStringUtil.isBlank(modifyPasswordParam.getOldAuthStr())
                        || null == modifyPasswordParam.getAuthTime()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            SysUserExt sue = getLoginUserInfo(request);
            // 验证用户并返回用户信息
            if (sysAuthService.verifyUser(sue.getLoginName(), sue.getPassword(), modifyPasswordParam.getOldAuthStr(), modifyPasswordParam.getAuthTime())) {
                SysUser updateVo = new SysUser();
                updateVo.setId(sue.getId());
                updateVo.setPassword(modifyPasswordParam.getNewPassword());
                updateVo.setUpdateUser(sue.getId());
                sysUserService.updateSysUser(updateVo);
            }
        }
        // 组织返回结果并返回
        MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
        return mr;
    }

    /**
     * 获得图片验证码.
     * @return 图片验证码Token
     * <pre>
     *     {
     *          code:处理Code,
     *          message:处理消息,
     *          respData:{
     *              key_captcha_token:图片验证码Token,
     *              key_captcha_base64:图形验证码图像Base64码
     *          }
     *     }
     * </pre>
     */
    @RequestMapping(path = {"/getcaptcha"})
    public MessageResponse getCaptcha() {
        String code = WzCheckCodeUtil.makeEDCode(4);
        String token = WzUniqueValUtil.makeUUID();
        // 生成验证码图片Base64码
        String codeBase64 = WzCaptchaUtil.madeAndGetCapthaBase64(code, 0, 0);
        // 图片验证码2分钟有效
        redisClient.valueOperations().set(WzConstants.GK_CAPTCHA + token, code, 120, TimeUnit.SECONDS);
        Map<String, String> tokenMap = new HashMap<String, String>();
        tokenMap.put(WzConstants.KEY_CAPTCHA_TOKEN, token);
        tokenMap.put(WzConstants.KEY_CAPTCHA_BASE64, codeBase64);
        return new MessageResponse(RunningResult.SUCCESS, tokenMap);
    }

    /**
     * 发送短信验证码.
     * @param mobileAndVC 发送短信验证码需要参数
     * <pre>
     *     {
     *         mobile:手机号码,
     *         captcha:图形验证码,
     *         captchaToken:图形验证码Token,
     *         needCaptchaToken:是否需要验证图形验证码（仅 true 时需要验证）
     *     }
     * </pre>
     * @return 发送结果
     * <pre>
     *     {
     *          code:处理Code,
     *          message:处理消息,
     *          respData:{
     *              key_sms_vcode_token:短信验证码Token
     *          }
     *     }
     * </pre>
     */
    @RequestMapping(path = {"/sendsms"})
    public MessageResponse sendSms(@RequestBody String mobileAndVC) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(mobileAndVC)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            SmsParam smsParam = null;
            try {
                String paramStr = URLDecoder.decode(mobileAndVC, "utf-8");
                smsParam = JSON.parseObject(paramStr, SmsParam.class);
                if (null == smsParam || WzStringUtil.isBlank(smsParam.getMobile())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                // 需要验证图形验证码
                if (WzStringUtil.isNotBlank(smsParam.getNeedCaptchaToken())
                        && "true".equals(smsParam.getNeedCaptchaToken().toLowerCase())) {
                    // 未获得图形验证码Token
                    if (WzStringUtil.isBlank(smsParam.getCaptchaToken())
                            || WzStringUtil.isBlank(smsParam.getCaptcha())) {
                        throw new BizException(RunningResult.NO_PARAM.code(), "验证码解析失败");
                        // 开始验证图形验证码
                    } else {
                        // 获得预存图形验证码
                        String cc = (String) redisClient.valueOperations().get(WzConstants.GK_CAPTCHA + smsParam.getCaptchaToken());
                        // 图形验证码过期报错
                        if (WzStringUtil.isBlank(cc)) {
                            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "验证码已过期");
                        }
                        // 验证码不一致报错
                        if (!cc.equalsIgnoreCase(smsParam.getCaptcha())) {
                            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "验证码验证失败");
                        }
                    }
                }
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 只要进行验证后，不管成功失败均将之前验证码作废
            redisClient.valueOperations().getOperations().delete(WzConstants.GK_CAPTCHA + smsParam.getCaptchaToken());

            // 生成短信验证码
            String smsVCode = WzCheckCodeUtil.makeDCode(6);
            String smsVCodeToken = WzUniqueValUtil.makeUUID();
            // 短信验证码2分钟有效
            redisClient.valueOperations().set(WzConstants.GK_SMS_VCODE + smsVCodeToken, smsVCode, 120, TimeUnit.SECONDS);
            redisClient.valueOperations().set(WzConstants.GK_SMS_VCODE_MOBILE + smsVCodeToken, smsParam.getMobile());
            Map<String, String> smsTokenMap = new HashMap<String, String>();
            smsTokenMap.put(WzConstants.KEY_SMS_VCODE_TOKEN, smsVCodeToken);
            smsClient.sendSmsByTemplate1(smsParam.getMobile(), smsVCode);
            return new MessageResponse(RunningResult.SUCCESS, smsTokenMap);
        }
    }

    /**
     * 重置密码.
     * @param request 请求
     * @param smsTokenAndNewPassword 重置密码参数JSON
     * <pre>
     *     {
     *         smsToken:重置密码用短信验证码Token,
     *         smsVCode:短信验证码,
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
    public MessageResponse resetPassword(@RequestBody String smsTokenAndNewPassword, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(smsTokenAndNewPassword)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            ResetPasswordParam resetParam = null;
            try {
                String paramStr = URLDecoder.decode(smsTokenAndNewPassword, "utf-8");
                resetParam = JSON.parseObject(paramStr, ResetPasswordParam.class);
                if (null == resetParam
                        || WzStringUtil.isBlank(resetParam.getSmsVCode())
                        || WzStringUtil.isBlank(resetParam.getSmsToken())
                        || WzStringUtil.isBlank(resetParam.getNewPassword())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                // 验证短信验证码
                // 获得预存短信验证码
                String vCode = (String) redisClient.valueOperations().get(WzConstants.GK_SMS_VCODE + resetParam.getSmsToken());
                // 短信验证码过期报错
                if (WzStringUtil.isBlank(vCode)) {
                    throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "验证码已过期");
                }
                // 验证码不一致报错
                if (!vCode.equalsIgnoreCase(resetParam.getSmsVCode())) {
                    throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "验证码验证失败");
                }
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 只要进行验证后，不管成功失败均将之前验证码作废
            redisClient.valueOperations().getOperations().delete(WzConstants.GK_SMS_VCODE + resetParam.getSmsToken());

            // 获得登录用户信息
            String userMobile = (String) redisClient.valueOperations().get(WzConstants.GK_SMS_VCODE_MOBILE + resetParam.getSmsToken());
            SysUser user = sysUserService.getByMobile(userMobile);
            SysUser updateVo = new SysUser();
            updateVo.setId(user.getId());
            updateVo.setPassword(resetParam.getNewPassword());
            updateVo.setUpdateUser(user.getId());
            // 更新密码
            sysUserService.updateSysUser(updateVo);
            // 缓存中的手机号码删除掉
            redisClient.valueOperations().getOperations().delete(WzConstants.GK_SMS_VCODE_MOBILE + resetParam.getSmsToken());
            return new MessageResponse(RunningResult.SUCCESS);
        }
    }
}
