package com.elextec.framework.common.request;

import com.elextec.framework.BaseModel;

/**
 * 登录参数.
 * Created by wangtao on 2018/1/22.
 */
public class LoginParam extends BaseModel {

    /** SV-UID. */
    private static final long serialVersionUID = -663106961546272341L;
    /** 用户名. */
    private String loginName;
    /** 验证字符串 MD5(loginName+MD5(登录密码).upper()+loginTime).upper(). */
    private String loginAuthStr;
    /** 登录时间的毫秒数. */
    private Long loginTime;
    /** 是否需要验证码，仅true时需要. */
    private String needCaptcha;
    /** 验证码Token. */
    private String captchaToken;
    /** 验证码内容. */
    private String captcha;

    /*
     * Getter 和 Setter 方法.
     */
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginAuthStr() {
        return loginAuthStr;
    }

    public void setLoginAuthStr(String loginAuthStr) {
        this.loginAuthStr = loginAuthStr;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public String getCaptchaToken() {
        return captchaToken;
    }

    public void setCaptchaToken(String captchaToken) {
        this.captchaToken = captchaToken;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getNeedCaptcha() {
        return needCaptcha;
    }

    public void setNeedCaptcha(String needCaptcha) {
        this.needCaptcha = needCaptcha;
    }
}
