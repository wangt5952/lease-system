package com.elextec.framework.common.request;

import com.elextec.framework.BaseModel;

/**
 * 短信相关参数.
 * Created by wangtao on 2018/1/22.
 */
public class SmsParam extends BaseModel {
    /** SV-UID. */
    private static final long serialVersionUID = 4153180202013235297L;

    /** 手机号码. */
    private String mobile;
    /** 发送短信用图形验证码. */
    private String captchaToken;
    /** 图形验证码内容. */
    private String captcha;
    /** 是否需要图片验证码. */
    private String needCaptchaToken;

    /*
     * Getter 和 Setter 方法.
     */
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCaptchaToken() {
        return captchaToken;
    }

    public void setCaptchaToken(String captchaToken) {
        this.captchaToken = captchaToken;
    }

    public String getNeedCaptchaToken() {
        return needCaptchaToken;
    }

    public void setNeedCaptchaToken(String needCaptchaToken) {
        this.needCaptchaToken = needCaptchaToken;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
