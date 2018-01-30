package com.elextec.lease.device.controller;

import com.alibaba.fastjson.JSONObject;
import com.elextec.framework.BaseController;
import com.elextec.framework.plugins.redis.RedisClient;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.manager.service.BizDeviceConfService;
import com.elextec.persist.field.enums.DeviceType;
import com.elextec.persist.model.mybatis.BizDeviceConf;
import com.elextec.persist.model.mybatis.BizDeviceConfKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 与硬件设备接口.
 * Created by wangtao on 2018/1/29.
 */
@RestController
@RequestMapping(path = "/device/v1")
public class DeviceApi extends BaseController {
    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(DeviceApi.class);

    private static final String RESP_ERR_CODE = "errorcode";
    private static final String RESP_ERR_MSG = "errormsg";

    @Autowired
    private BizDeviceConfService bizDeviceConfService;

    @Autowired
    private RedisClient redisClient;

    /**
     * 获得控制参数.
     * @param d 登录参数JSON
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
    @RequestMapping(path = {"/getconf"}, method = RequestMethod.GET)
    public JSONObject getDevciceConf(String deviceid, String devicetype) {
        JSONObject respData = new JSONObject();
        // deviceid或devicetype为空则需要报错
        if (WzStringUtil.isBlank(deviceid) || WzStringUtil.isBlank(devicetype)) {
            respData.put(RESP_ERR_CODE, "NONE_ID_AND_TYPE");
            respData.put(RESP_ERR_MSG, "参数deviceid和devicetype不能为空");
            return respData;
        }
        if (!devicetype.toUpperCase().equals(DeviceType.BATTERY.toString())
                && !devicetype.toUpperCase().equals(DeviceType.VEHICLE.toString())
                && !devicetype.toUpperCase().equals(DeviceType.PARTS.toString())) {
            respData.put(RESP_ERR_CODE, "INVALID_DEVICE");
            respData.put(RESP_ERR_MSG, "无效的设备类别");
            return respData;
        }
        BizDeviceConfKey selectKey = new BizDeviceConfKey();
        selectKey.setDeviceId(deviceid);
        selectKey.setDeviceType(DeviceType.valueOf(devicetype));
        BizDeviceConf deviceConfVo = bizDeviceConfService.getBizDeviceConfByPrimaryKey(selectKey);
        if (null == deviceConfVo) {
            respData.put(RESP_ERR_CODE, "NO_DEVICE");
            respData.put(RESP_ERR_MSG, "NO_DEVICE");
            return respData;
        }
        return null;
//        else {
//            // 参数解析错误报“参数解析错误”
//            LoginParam loginParam = null;
//            try {
//                String paramStr = URLDecoder.decode(loginNameAndPassword, "utf-8");
//                loginParam = JSON.parseObject(paramStr, LoginParam.class);
//                if (null == loginParam || WzStringUtil.isBlank(loginParam.getLoginName())
//                        || WzStringUtil.isBlank(loginParam.getLoginAuthStr())
//                        || null == loginParam.getLoginTime()) {
//                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
//                }
//            } catch (Exception ex) {
//                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
//            }
//
//            // 验证用户并返回用户信息
//            Map<String, Object> loginVo = sysAuthService.login(loginParam.getLoginName(), loginParam.getLoginAuthStr(), loginParam.getLoginTime());
//            // 组织登录返回信息
//            Map<String, Object> loginInfo = new HashMap<String, Object>();
//            // 登录Token
//            // 生成登录token
//            String loginToken = WzUniqueValUtil.makeUUID();
//            loginInfo.put(WzConstants.KEY_LOGIN_TOKEN, loginToken);
//            // 设置登录信息
//            loginInfo.put(WzConstants.KEY_USER_INFO, loginVo.get(WzConstants.KEY_USER_INFO));
//            loginInfo.put(WzConstants.KEY_RES_INFO, loginVo.get(WzConstants.KEY_RES_INFO));
//            // 设置超时时间
//            Integer overtime = 5;
//            if (WzStringUtil.isNumeric(loginOvertime)) {
//                overtime = Integer.parseInt(loginOvertime);
//            }
//            // 登录成功，保存到Redis中
//            redisClient.valueOperations().set(WzConstants.GK_PC_LOGIN_INFO + loginToken, loginInfo, overtime, TimeUnit.MINUTES);
//            // 组织返回结果并返回
//            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, loginInfo);
//            return mr;
//        }
    }
}
