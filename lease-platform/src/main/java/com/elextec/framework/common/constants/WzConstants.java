package com.elextec.framework.common.constants;

/**
 * Created by wangtao on 2018/1/22.
 */
public class WzConstants {
    /** 用户请求Header参数 登录Token. */
    public static final String HEADER_LOGIN_TOKEN = "header_login_token";

    /*
     * 登录相关Key.
     */
    /** 全局Key 登录用户信息前缀. */
    public static final String GK_PC_LOGIN_INFO = "gk_pc_login_info:";
    /** Key 登录Token. */
    public static final String KEY_LOGIN_TOKEN = "key_login_token";
    /** Key 登录用户详细信息. */
    public static final String KEY_USER_INFO = "key_user_info";
    /** Key 登录用户可用资源信息. */
    public static final String KEY_RES_INFO = "key_res_info";

    /*
     * 短信相关Key.
     */
    /** 全局Key 短信验证码前缀. */
    public static final String GK_SMS_VCODE = "gk_sms_vcode:";
    /** 短信验证码Token. */
    public static final String KEY_SMS_VCODE_TOKEN = "key_sms_vcode_token";

    /*
     * 图片验证码相关Key.
     */
    /** 全局Key 图片验证码前缀. */
    public static final String GK_CAPTCHA = "gk_captcha:";
    /** 图片验证码Token. */
    public static final String KEY_CAPTCHA_TOKEN = "key_captcha_token";
    /** 图形验证码图像URL. */
    public static final String KEY_CAPTCHA_URL = "key_captcha_url";
}
