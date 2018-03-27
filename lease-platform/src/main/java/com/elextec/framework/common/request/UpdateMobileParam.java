package com.elextec.framework.common.request;

import com.elextec.framework.BaseModel;

/**
 * 更换手机号码相关参数.
 * Created by wangtao on 2018/1/22.
 */
public class UpdateMobileParam extends BaseModel {

    /** SV-UID. */
    private static final long serialVersionUID = -9132113875511073878L;

    /** 短信验证码验证码. */
    private String smsToken;
    /** 短信验证码. */
    private String smsVCode;
    /** 新密码. */
    private String newMobile;

    /*
     * Getter 和 Setter 方法.
     */
    public String getSmsToken() {
        return smsToken;
    }

    public void setSmsToken(String smsToken) {
        this.smsToken = smsToken;
    }

    public String getNewMobile() {
        return newMobile;
    }

    public void setNewMobile(String newMobile) {
        this.newMobile = newMobile;
    }

    public String getSmsVCode() {
        return smsVCode;
    }

    public void setSmsVCode(String smsVCode) {
        this.smsVCode = smsVCode;
    }
}
