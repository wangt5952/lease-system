package com.elextec.lease.manager.request;

import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.field.enums.RecordStatus;

/**
 * Created by js_gg on 2018/2/5.
 */
public class SysUserParam extends PageRequest {
    /** 关键字，包括login_name、user_mobile、nick_name、user_name、user_pid、org_name、org_code. */
    private String keyStr;
    /** 用户类别. */
    private String userType;
    /** 用户状态. */
    private String userStatus;

    /*
     * Getter 和 Setter 方法.
     */
    public String getKeyStr() {
        return keyStr;
    }

    public void setKeyStr(String keyStr) {
        this.keyStr = keyStr;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

}
