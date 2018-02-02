package com.elextec.framework.common.request;

import com.elextec.framework.BaseModel;

/**
 * 修改密码相关参数.
 * Created by wangtao on 2018/1/22.
 */
public class ModifyPasswordParam extends BaseModel {

    /** SV-UID. */
    private static final long serialVersionUID = 1854945029079561421L;

    /** 用户身份验证字符串. */
    private String oldAuthStr;
    /** 登录时间的毫秒数. */
    private Long authTime;
    /** 新密码. */
    private String newPassword;

    /*
     * Getter 和 Setter 方法.
     */
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldAuthStr() {
        return oldAuthStr;
    }

    public void setOldAuthStr(String oldAuthStr) {
        this.oldAuthStr = oldAuthStr;
    }

    public Long getAuthTime() {
        return authTime;
    }

    public void setAuthTime(Long authTime) {
        this.authTime = authTime;
    }
}