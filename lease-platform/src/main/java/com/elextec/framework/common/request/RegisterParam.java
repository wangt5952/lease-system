package com.elextec.framework.common.request;

import com.elextec.framework.BaseModel;

/**
 * Created by js_gg on 2018/1/31.
 */
public class RegisterParam extends BaseModel {

    /** SV-UID. */
    private static final long serialVersionUID = -9099217751993988449L;

    /** 登录名. */
    private String loginName;
    /** 手机号. */
    private String userMobile;
    /** 密码. */
    private String password;
    /** 创建用户. */
    private String createUser;
    /** 更新用户. */
    private String updateUser;
    /** 短信验证码验证码. */
    private String smsToken;
    /** 短信验证码. */
    private String smsVCode;
    /** 企业ID.*/
    private String orgId;

    /*
     * Getter 和 Setter 方法.
     */
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

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
