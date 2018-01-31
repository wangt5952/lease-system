package com.elextec.framework.common.request;

/**
 * Created by js_gg on 2018/1/31.
 */
public class RegisterParam {
    private String loginName;
    private String userMobile;
    private String password;
    private String createUser;
    private String updateUser;
    /** 短信验证码验证码. */
    private String smsToken;
    /** 短信验证码. */
    private String smsVCode;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getSmsToken() {
        return smsToken;
    }

    public void setSmsToken(String smsToken) {
        this.smsToken = smsToken;
    }

    public String getSmsVCode() {
        return smsVCode;
    }

    public void setSmsVCode(String smsVCode) {
        this.smsVCode = smsVCode;
    }
}
