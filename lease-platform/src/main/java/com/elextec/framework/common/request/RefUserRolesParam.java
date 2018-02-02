package com.elextec.framework.common.request;

import com.elextec.framework.BaseModel;

/**
 * Created by js_gg on 2018/1/24.
 */
public class RefUserRolesParam extends BaseModel {

    private String userId;

    private String roleIds;

    /** 清空标志，仅为true时有效. */
    private String deleteAllFlg;

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

    public String getDeleteAllFlg() {
        return deleteAllFlg;
    }

    public void setDeleteAllFlg(String deleteAllFlg) {
        this.deleteAllFlg = deleteAllFlg;
    }
}
