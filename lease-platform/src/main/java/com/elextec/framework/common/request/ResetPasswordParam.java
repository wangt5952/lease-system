package com.elextec.framework.common.request;

import com.elextec.framework.BaseModel;

/**
 * 重置密码相关参数.
 * Created by wangtao on 2018/1/22.
 */
public class ResetPasswordParam extends BaseModel {
    /** 短信验证码验证码. */
    private String smsToken;
    /** 短信验证码. */
    private String smsVCode;
    /** 新密码. */
    private String newPassword;

    /*
     * Getter 和 Setter 方法.
     */
    public String getSmsToken() {
        return smsToken;
    }

    public void setSmsToken(String smsToken) {
        this.smsToken = smsToken;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getSmsVCode() {
        return smsVCode;
    }

    public void setSmsVCode(String smsVCode) {
        this.smsVCode = smsVCode;
    }
}
