package com.elextec.framework.common.request;

import com.elextec.framework.BaseModel;

/**
 * Created by js_gg on 2018/1/24.
 */
public class RefUserRolesParam extends BaseModel {
    private String userId;
    private String roleIds;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }
}
