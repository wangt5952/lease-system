package com.elextec.lease.mobile.controller;

import com.alibaba.fastjson.JSON;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.common.request.*;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.sms.SmsClient;
import com.elextec.framework.utils.*;
import com.elextec.lease.manager.service.BizOrganizationService;
import com.elextec.lease.manager.service.SysAuthService;
import com.elextec.lease.manager.service.SysUserService;
import com.elextec.lease.model.BizVehicleBatteryParts;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.field.enums.RealNameAuthFlag;
import com.elextec.persist.field.enums.RecordStatus;
import com.elextec.persist.model.mybatis.SysRefUserRoleKey;
import com.elextec.persist.model.mybatis.SysUser;
import com.elextec.persist.model.mybatis.SysUserExample;
import com.elextec.persist.model.mybatis.ext.SysUserExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 移动端用户访问接口.
 * Created by wangtao on 2018/1/30.
 */
@RestController
@RequestMapping(path = "/mobile/v1/auth")
public class SysAuthApi extends BaseController {
    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(SysAuthApi.class);

    @Value("${localsetting.login-overtime-sec-mobile}")
    private String loginOvertime;

    @Value("${localsetting.upload-user-icon-root}")
    private String uploadUserIconRoot;

    @Value("${localsetting.download-user-icon-prefix}")
    private String downloadUserIconPrefix;

    @Value("${localsetting.upload-user-realname-root}")
    private String uploadUserRealnameRoot;

    @Value("${localsetting.download-user-realname-prefix}")
    private String downloadUserRealnamePrefix;

    @Autowired
    private SysAuthService sysAuthService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private BizOrganizationService bizOrganizationService;

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
     *         needCaptcha:是否需要验证码
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
     *             ],
     *             key_vehicle_info:[
     *                 {
     *                     id:ID,
     *                     vehicleCode:车辆编号,
     *                     vehiclePn:车辆型号,
     *                     vehicleBrand:车辆品牌,
     *                     vehicleMadeIn:车辆产地,
     *                     mfrsName:生产商名称,
     *                     vehicleStatus:车辆状态（正常、冻结、报废）,
     *                     createUser:创建人,
     *                     createTime:创建时间,
     *                     updateUser:更新人,
     *                     updateTime:更新时间,
     *                     bizBatteries:[电池信息
     *                         {
     *                              id:ID,
     *                              batteryCode:电池编号,
     *                              batteryName:电池货名,
     *                              batteryBrand:电池品牌,
     *                              batteryPn:电池型号,
     *                              batteryParameters:电池参数,
     *                              mfrsName:生产商名称,
     *                              batteryStatus:电池状态（正常、冻结、作废）,
     *                              createUser:创建人,
     *                              createTime:创建时间,
     *                              updateUser:更新人,
     *                              updateTime:更新时间
     *                         },
     *                         ........
     *                     ],
     *                     bizPartss:[配件信息
     *                         {
     *                             id:ID,
     *                             partsCode:配件编码,
     *                             partsName:配件货名,
     *                             partsBrand:配件品牌,
     *                             partsPn:配件型号,
     *                             partsType:配件类别（）,
     *                             partsParameters:配件参数,
     *                             mfrsName:生产商名称,
     *                             partsStatus:配件状态（正常、冻结、作废）,
     *                             createUser:创建人,
     *                             createTime:创建时间,
     *                             updateUser:更新人,
     *                             updateTime:更新时间
     *                         },
     *                         .........
     *                     ]
     *                 },
     *                 .......
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
            }catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }

            // 验证用户并返回用户信息
            Map<String, Object> loginVo = sysAuthService.mobileLogin(loginParam.getLoginName(), loginParam.getLoginAuthStr(), loginParam.getLoginTime());

            //获取用户关联车辆信息
            SysUserExt user = (SysUserExt)loginVo.get(WzConstants.KEY_USER_INFO);
            List<BizVehicleBatteryParts> vehicleBatteryPartss = sysUserService.getVehiclePartsById(user.getId());

            // 组织登录返回信息
            Map<String, Object> loginInfo = new HashMap<String, Object>();
            // 登录Token
            // 生成登录token
            String loginToken = WzUniqueValUtil.makeUUID();
            loginInfo.put(WzConstants.KEY_LOGIN_TOKEN, loginToken);
            // 设置登录信息
            loginInfo.put(WzConstants.KEY_USER_INFO, loginVo.get(WzConstants.KEY_USER_INFO));
            loginInfo.put(WzConstants.KEY_RES_INFO, loginVo.get(WzConstants.KEY_RES_INFO));
            loginInfo.put(WzConstants.KEY_VEHICLE_INFO,vehicleBatteryPartss);
            // 设置超时时间
            Integer overtime = 300;
            if (WzStringUtil.isNumeric(loginOvertime)) {
                overtime = Integer.parseInt(loginOvertime);
            }
            // 登录成功，保存到Redis中
            redisClient.valueOperations().set(WzConstants.GK_LOGIN_INFO + loginToken, loginInfo, overtime, TimeUnit.MINUTES);
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
            }catch (BizException ex) {
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

                // 短信验证码过期报错// 验证短信验证码
                // 获得预存短信验证码
                String vCode = (String) redisClient.valueOperations().get(WzConstants.GK_SMS_VCODE + resetParam.getSmsToken());
                if (WzStringUtil.isBlank(vCode)) {
                    throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "验证码已过期");
                }
                // 验证码不一致报错
                if (!vCode.equals(resetParam.getSmsVCode())) {
                    throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "验证码验证失败");
                }
            } catch (BizException ex) {
                throw ex;
            }catch (Exception ex) {
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

    /**
     * 用户注册.
     * @param smsTokenAndUserInfo 用户信息JSON和检证码TOKEN
     * <pre>
     *     {
     *         loginName:用户名(可以为空，为空是手机号码注册),
     *         userMobile:用户手机号码,
     *         password:用户密码，
     *         createUser:创建人,
     *         updateUser:更新人,
     *         smsToken:注册用短信验证码Token,
     *         smsVCode:短信验证码,
     *         orgId:企业Id
     *     }
     * </pre>
     * @return 用户注册结果
     * <pre>
     *     {
     *          code:处理Code,
     *          message:处理消息,
     *          respData:""
     *     }
     * </pre>
     */
    @RequestMapping(path = {"/register"})
    public MessageResponse register(@RequestBody String smsTokenAndUserInfo){
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(smsTokenAndUserInfo)) {
            return new MessageResponse(RunningResult.NO_PARAM);
        } else {
            RegisterParam resetParam = null;
            try{
                String paramStr = URLDecoder.decode(smsTokenAndUserInfo, "utf-8");
                resetParam = JSON.parseObject(paramStr, RegisterParam.class);
            }catch (BizException ex) {
                throw ex;
            }catch(Exception ex){
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }

            //断定必填参数是否为空
            if (null == resetParam
                    || WzStringUtil.isBlank(resetParam.getSmsVCode())
                    || WzStringUtil.isBlank(resetParam.getSmsToken())
                    || WzStringUtil.isBlank(resetParam.getUserMobile())
                    || WzStringUtil.isBlank(resetParam.getOrgId())) {
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
            }
            // 短信验证码过期报错// 验证短信验证码
            // 获得预存短信验证码
            String vCode = (String) redisClient.valueOperations().get(WzConstants.GK_SMS_VCODE + resetParam.getSmsToken());
            if (WzStringUtil.isBlank(vCode)) {
                throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "验证码已过期");
            }
            // 验证码不一致报错
            if (!vCode.equals(resetParam.getSmsVCode())) {
                throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "验证码验证失败");
            }
            // 只要进行验证后，不管成功失败均将之前验证码作废
            redisClient.valueOperations().getOperations().delete(WzConstants.GK_SMS_VCODE + resetParam.getSmsToken());
            redisClient.valueOperations().getOperations().delete(WzConstants.GK_SMS_VCODE_MOBILE + resetParam.getSmsToken());

            SysUser userTemp = new SysUser();
            userTemp.setUserMobile(resetParam.getUserMobile());
            userTemp.setCreateUser(resetParam.getCreateUser());
            userTemp.setUpdateUser(resetParam.getUpdateUser());
            userTemp.setPassword(resetParam.getPassword());
            userTemp.setOrgId(resetParam.getOrgId());
            //判断用户是手机号码注册还是用户名注册
            if(!WzStringUtil.isNotBlank(resetParam.getLoginName())){
                //用户名为空，则是手机号码注册，注入默认用户名为手机号码
                userTemp.setLoginName(resetParam.getUserMobile());
            }
            //手机注册默认是个人帐户
            userTemp.setUserType(OrgAndUserType.INDIVIDUAL);
            //默认状态为正常
            userTemp.setUserStatus(RecordStatus.NORMAL);
            //默认暂时未实名认证
            userTemp.setUserRealNameAuthFlag(RealNameAuthFlag.UNAUTHORIZED);
            sysUserService.insertSysUser(userTemp);

            //手机端注册，默认给该用户分配个人用户权限角色
            SysUserExample sysUserExample = new SysUserExample();
            SysUserExample.Criteria sysUserExampleCriteria = sysUserExample.createCriteria();
            sysUserExampleCriteria.andUserMobileEqualTo(resetParam.getUserMobile());
            SysUser sysUser = sysUserService.getExtById(sysUserExample);

            RefUserRolesParam refUserRolesParam = new RefUserRolesParam();
            refUserRolesParam.setUserId(sysUser.getId());
            refUserRolesParam.setRoleIds("1dc5e062e6964b1c857098e30d89b945");
            refUserRolesParam.setDeleteAllFlg("false");
            sysUserService.refSysUserAndRoles(refUserRolesParam);

            return new MessageResponse(RunningResult.SUCCESS);
        }
    }

    /**
     * 补全用户信息.
     * @param userInfo 用户信息JSON
     * <pre>
     *     {
     *         id:ID,
     *         loginName:用户名,
     *         nickName:用户昵称,
     *         userName:用户姓名,
     *         userPid:用户身份证码,
     *         updateUser:更新人
     *     }
     * </pre>
     * @return 补全用户信息结果
     * <pre>
     *     {
     *          code:处理Code,
     *          message:处理消息,
     *          respData:""
     *     }
     * </pre>
     */
    @RequestMapping(path = {"/updateuserinfo"})
    public MessageResponse updateUserInfo(@RequestBody String userInfo){
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(userInfo)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            SysUser resetParam = null;
            try{
                String paramStr = URLDecoder.decode(userInfo, "utf-8");
                resetParam = JSON.parseObject(paramStr, SysUser.class);
                if(null == resetParam){
                    throw new BizException(RunningResult.PARAM_VERIFY_ERROR);
                }
                if(WzStringUtil.isBlank(resetParam.getUpdateUser())){
                    throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "更新人不能为空");
                }
            }catch (BizException ex) {
                throw ex;
            }catch(Exception ex){
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
                //根据ID获取用户原本信息
                if(WzStringUtil.isNotBlank(resetParam.getId())){
                    SysUser userTemp = sysUserService.getSysUserByPrimaryKey(resetParam.getId());
                    if(userTemp == null){
                        throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "用户不存在");
                    }

                    //判断用户话原来手机号与用户名是否相同，不相同的话，用户名不可更改
                    if(!userTemp.getLoginName().equals(userTemp.getUserMobile()) &&
                            !userTemp.getLoginName().equals(resetParam.getLoginName())){
                        MessageResponse mr = new MessageResponse(RunningResult.PARAM_VERIFY_ERROR);
                        return mr;
                    }else{
                        //原用户名与原手机号码不相同，可更改用户名
                        userTemp.setLoginName(resetParam.getLoginName());
                        userTemp.setNickName(resetParam.getNickName());
                        userTemp.setUserName(resetParam.getUserName());
                        userTemp.setUpdateUser(resetParam.getUpdateUser());
                        //判断用户是否已实名认证，如果已实名认证，则不可修改身份证号码
                        if(!RealNameAuthFlag.AUTHORIZED.toString().equals(userTemp.getUserRealNameAuthFlag().toString()) &&
                                 !RealNameAuthFlag.TOAUTHORIZED.toString().equals(userTemp.getUserRealNameAuthFlag().toString())){
                             //用户不是已认证或待认证状态，可以更改用户身份证号
                             userTemp.setUserPid(resetParam.getUserPid());
                        }else{
                             //若已实名认证或待认证状态，并且上传参数与数据库保存参数不相同，则参数解析失败
                             if(!resetParam.getUserPid().equals(userTemp.getUserPid())){
                                 MessageResponse mr = new MessageResponse(RunningResult.PARAM_VERIFY_ERROR);
                                 return mr;
                             }
                             userTemp.setUserPid(resetParam.getUserPid());
                        }
                        //修改用户信息
                        sysUserService.updateSysUser(userTemp);
                        return new MessageResponse(RunningResult.SUCCESS);
                    }
                }else{
                    //ID为空的话，参数解析失败
                    MessageResponse mr = new MessageResponse(RunningResult.PARAM_VERIFY_ERROR);
                    return mr;
                }
        }
    }

    /**
     * 上传用户头像.
     * @param userIdAndIconBase64Data 用户ID和用户头像图片BASE64
     * @param request 登录用户
     * <pre>
     *     {
     *         id:ID,
     *         userIcon:用户头像BASE64,
     *         updateUser:更新人
     *     }
     * </pre>
     * @return 上传用户头像结果
     * <pre>
     *     {
     *          code:处理Code,
     *          message:处理消息,
     *          respData:移动端获取图片地址
     *     }
     * </pre>
     */
    @RequestMapping(path = {"/uplodeusericon"})
    public MessageResponse uplodeUserIcon(@RequestBody String userIdAndIconBase64Data,HttpServletRequest request){
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(userIdAndIconBase64Data)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            SysUser resetParam = null;
            String paramStr=null;
            try{
                paramStr = URLDecoder.decode(userIdAndIconBase64Data, "utf-8");
                resetParam = JSON.parseObject(paramStr, SysUser.class);
                if(null == resetParam){
                    throw new BizException(RunningResult.PARAM_VERIFY_ERROR);
                }
                if(WzStringUtil.isBlank(resetParam.getUpdateUser())){
                    throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "更新人不能为空");
                }
            }catch (BizException ex) {
                throw ex;
            }catch(Exception ex){
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
                if(WzStringUtil.isNotBlank(resetParam.getId())){
                    if(WzStringUtil.isNotBlank(resetParam.getUserIcon())){
                        SysUser userTemp = sysUserService.getSysUserByPrimaryKey(resetParam.getId());
                        if(userTemp == null){
                            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "用户不存在");
                        }

                        //去掉BASE64里的空格和回车
                        String userIconStr = resetParam.getUserIcon().replace(" ","+");
                        String imageName = WzUniqueValUtil.makeUniqueTimes();
                        WzFileUtil.save(userIconStr, uploadUserIconRoot, "", imageName, WzFileUtil.EXT_JPG);
                        String requestUrl = WzFileUtil.makeRequestUrl(downloadUserIconPrefix,"", imageName + WzFileUtil.EXT_JPG);
                        //获取旧的头像文件名
                        String oldIconName = userTemp.getUserIcon();
                        //数据库只保存文件名
                        userTemp.setUserIcon(imageName + WzFileUtil.EXT_JPG);
                        userTemp.setUpdateUser(resetParam.getUpdateUser());
                        sysUserService.updateSysUser(userTemp);
                        //删除旧的头像文件
                        if(WzStringUtil.isNotBlank(oldIconName)){
                            WzFileUtil.deleteFile(uploadUserIconRoot,oldIconName);
                        }

                        //获取登录token
                        String userToken = request.getHeader(WzConstants.HEADER_LOGIN_TOKEN);
                        //获取所有登录信息
                        Map<String,Object> userInfo = (Map<String, Object>) redisClient.valueOperations().get(WzConstants.GK_LOGIN_INFO + userToken);
                        //获取登录用户信息
                        SysUserExt sysUserExt = (SysUserExt) userInfo.get(WzConstants.KEY_USER_INFO);
                        //把登录用户信息字段重新复制
                        sysUserExt.setUserIcon(requestUrl);

                        //把改好的对象重新放进map
                        Map<String,Object> map = new HashMap<String,Object>();
                        map.put(WzConstants.KEY_USER_INFO,sysUserExt);
                        //设置超时时间
                        Integer overtime = 300;
                        if (WzStringUtil.isNumeric(loginOvertime)) {
                            overtime = Integer.parseInt(loginOvertime);
                        }
                        //重新塞回缓存
                        redisClient.valueOperations().set(WzConstants.GK_LOGIN_INFO + userToken, map, overtime, TimeUnit.MINUTES);

                        //保存成功后将全路径返回给移动端
                        return new MessageResponse(RunningResult.SUCCESS,requestUrl);
                    }else{
                        MessageResponse mr = new MessageResponse(RunningResult.PARAM_VERIFY_ERROR);
                        return mr;
                    }
                }else{
                    //ID为空的话，参数解析失败
                    MessageResponse mr = new MessageResponse(RunningResult.PARAM_VERIFY_ERROR);
                    return mr;
                }

        }
    }

    /**
     * 用户实名认证.
     * @param userIdAndIconBase64Data 用户ID和用户相关图片BASE64
     * <pre>
     *     {
     *         id:ID,
     *         userPid:用户身份证号
     *         userIcFront:身份证正面照片BASE64码,
     *         userIcBack:身份证背面照片BASE64码,
     *         userIcGroup:用户手举身份证合照,
     *         updateUser:更新人
     *     }
     * </pre>
     * @return 用户实名认证结果
     * <pre>
     *     {
     *          code:处理Code,
     *          message:处理消息,
     *          respData:""
     *     }
     * </pre>
     */
    @RequestMapping(path = {"/userrealnameauth"})
    public MessageResponse userRealNameAuth(@RequestBody String userIdAndIconBase64Data){
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(userIdAndIconBase64Data)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            SysUser resetParam = null;
            try{
                String paramStr = URLDecoder.decode(userIdAndIconBase64Data, "utf-8");
                resetParam = JSON.parseObject(paramStr, SysUser.class);
                if(null == resetParam){
                    throw new BizException(RunningResult.PARAM_VERIFY_ERROR);
                }
                if(WzStringUtil.isBlank(resetParam.getUpdateUser())){
                    throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "更新人不能为空");
                }
            }catch (BizException ex) {
                throw ex;
            }catch(Exception ex){
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
                if(WzStringUtil.isNotBlank(resetParam.getId())){
                    SysUser userTemp = sysUserService.getSysUserByPrimaryKey(resetParam.getId());
                    if(userTemp == null){
                            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "用户不存在");
                    }
                        //如果用户是已实名认证或待验证的状态，则不可更改信息
                    if(RealNameAuthFlag.AUTHORIZED.toString().equals(userTemp.getUserRealNameAuthFlag().toString()) ||
                            RealNameAuthFlag.TOAUTHORIZED.toString().equals(userTemp.getUserRealNameAuthFlag().toString())){
                        throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "用户已实名认证或待验证中");
                    }
                    //用户的身份证号与照片信息不可为空
                    if(WzStringUtil.isBlank(resetParam.getUserPid())
                            || WzStringUtil.isBlank(resetParam.getUserIcFront())
                            || WzStringUtil.isBlank(resetParam.getUserIcBack())
                            || WzStringUtil.isBlank(resetParam.getUserIcGroup())){
                        throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "用户照片信息不可为空");
                    }
                    //将身份证正面照片保存到图片服务器中
                    String frontImageName = WzUniqueValUtil.makeUniqueTimes();
                    WzFileUtil.save(resetParam.getUserIcFront().replace(" ","+"), uploadUserRealnameRoot, "", frontImageName, WzFileUtil.EXT_JPG);
                    //String requestUrl = WzFileUtil.makeRequestUrl(downloadUserIconPrefix,"", frontImageName + WzFileUtil.EXT_JPG);
                    //将身份证背面照片保存到图片服务器中
                    String backImageName = WzUniqueValUtil.makeUniqueTimes();
                    WzFileUtil.save(resetParam.getUserIcBack().replace(" ","+"), uploadUserRealnameRoot, "", backImageName, WzFileUtil.EXT_JPG);
                    //String requestUrl = WzFileUtil.makeRequestUrl(downloadUserIconPrefix,"", frontImageName + WzFileUtil.EXT_JPG);
                    //将用户手持身份证的照片保存到图片服务器中
                    String groupImageName = WzUniqueValUtil.makeUniqueTimes();
                    WzFileUtil.save(resetParam.getUserIcGroup().replace(" ","+"), uploadUserRealnameRoot, "", groupImageName, WzFileUtil.EXT_JPG);
                    //String requestUrl = WzFileUtil.makeRequestUrl(downloadUserIconPrefix,"", frontImageName + WzFileUtil.EXT_JPG);
                    userTemp.setUserPid(resetParam.getUserPid());
                    userTemp.setUpdateUser(resetParam.getUpdateUser());
                    //数据库只保存文件名
                    userTemp.setUserIcFront(frontImageName + WzFileUtil.EXT_JPG);
                    userTemp.setUserIcBack(backImageName + WzFileUtil.EXT_JPG);
                    userTemp.setUserIcGroup(groupImageName + WzFileUtil.EXT_JPG);
                    //用户进入待实名认证状态
                    userTemp.setUserRealNameAuthFlag(RealNameAuthFlag.TOAUTHORIZED);
                    sysUserService.updateSysUser(userTemp);
                    return new MessageResponse(RunningResult.SUCCESS);
                }else{
                    //ID为空的话，参数解析失败
                    MessageResponse mr = new MessageResponse(RunningResult.PARAM_VERIFY_ERROR);
                    return mr;
                }
        }
    }

    /**
     *  手机端用户注册时，需要看的所有企业
     * @return 所有企业的列表
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:[
     *             {
     *                 id:ID,
     *                 orgCode:组织Code,
     *                 orgName:组织名称,
     *                 orgType:组织类别（平台、企业）,
     *                 orgIntroduce:组织介绍,
     *                 orgAddress:组织地址,
     *                 orgContacts:联系人（多人用 , 分割）,
     *                 orgPhone:联系电话（多个电话用 , 分割）,
     *                 orgBusinessLicences:营业执照号码,
     *                 orgBusinessLicenceFront:营业执照正面照片路径,
     *                 orgBusinessLicenceBack:营业执照背面照片路径,
     *                 orgStatus:组织状态（正常、冻结、作废）,
     *                 createUser:创建人,
     *                 createTime:创建时间,
     *                 updateUser:更新人,
     *                 updateTime:更新时间
     *             },
     *             ... ...
     *         ]
     *     }
     * </pre>
     */
    @RequestMapping(value = "/userBindOrg",method = RequestMethod.GET)
    public MessageResponse userBindOrg(){
        return new MessageResponse(RunningResult.SUCCESS,bizOrganizationService.orgList());
    }

}
