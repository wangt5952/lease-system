package com.elextec.framework.common.request;

import com.elextec.framework.BaseModel;

/**
 * 登录参数.
 * Created by wangtao on 2018/1/22.
 */
public class LoginParam extends BaseModel {
    /** 用户名. */
    private String loginName;
    /** 验证字符串(MD5(用户名+密码)). */
    private String loginAuthStr;
    /** 登录时间的毫秒数. */
    private Long loginTime;

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
}
