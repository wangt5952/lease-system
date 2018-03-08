package com.elextec.framework.common.constants;

/**
 * Created by wangtao on 2018/1/22.
 */
public class WzConstants {

    /** 逗号. */
    public static final String KEY_COMMA = ",";
    /** 分号. */
    public static final String KEY_SEMICOLON = ";";
    /** Redis关键字分隔符. */
    public static final String KEY_SPLIT = ":";
    /** 行分隔符. */
    public static final String KEY_LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    /** 用户请求Header参数 登录Token. */
    public static final String HEADER_LOGIN_TOKEN = "header-login-token";

    /*
     * 登录相关Key.
     */
    /** 全局Key 登录用户缓存信息前缀. */
    public static final String GK_LOGIN_INFO = "gk_login_info:";
    /** 登录Token. */
    public static final String KEY_LOGIN_TOKEN = "key_login_token";
    /** 登录用户详细信息. */
    public static final String KEY_USER_INFO = "key_user_info";
    /** 登录用户可用资源信息. */
    public static final String KEY_RES_INFO = "key_res_info";
    /** 登录用户关联的角色信息. */
    public static final String KEY_ROLE_INFO = "key_role_info";
    /**登录用户关联车辆信息.*/
    public static final String KEY_VEHICLE_INFO = "key_vehicle_info";

    /*
     * 短信相关Key.
     */
    /** 全局Key 短信验证码前缀. */
    public static final String GK_SMS_VCODE = "gk_sms_vcode:";
    /** 全局Key 接受短信验证的手机号码. */
    public static final String GK_SMS_VCODE_MOBILE = "gk_sms_vcode_mobile";
    /** 短信验证码Token. */
    public static final String KEY_SMS_VCODE_TOKEN = "key_sms_vcode_token";

    /*
     * 图片验证码相关Key.
     */
    /** 全局Key 图片验证码前缀. */
    public static final String GK_CAPTCHA = "gk_captcha:";
    /** 图片验证码Token. */
    public static final String KEY_CAPTCHA_TOKEN = "key_captcha_token";
    /** 图形验证码图像Base64字符串. */
    public static final String KEY_CAPTCHA_BASE64 = "key_captcha_base64";

    /*
     * DeviceApi 设备设定参数控制相关Key.
     */
    /** 全局Key 设备控制设定前缀. */
    public static final String GK_DEVICE_CONF = "gk_device_conf:";

    /** 全局Key 设备PK列表. */
    public static final String GK_DEVICE_PK_SET = "gk_device_pk_set";

    /** 全局Key 设备定位Map. */
    public static final String GK_DEVICE_LOC_MAP = "gk_device_loc_map";

    /** 全局Key 设备轨迹前缀. */
    public static final String GK_DEVICE_TRACK = "gk_device_track:";

    /** 全局Key 设备电量情况Map. */
    public static final String GK_DEVIE_POWER_MAP = "gk_device_power_map";

    /** 全局Key 设备参数Map. */
    public static final String GK_DEVICE_PARAM_MAP = "gk_device_param_map";
}
