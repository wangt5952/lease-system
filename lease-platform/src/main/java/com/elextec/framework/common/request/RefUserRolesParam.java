package com.elextec.framework.common.request;

import com.elextec.framework.BaseModel;

/**
 * 用户授权角色参数类.
 * Created by js_gg on 2018/1/24.
 */
public class RefUserRolesParam extends BaseModel {

    /** SV-UID. */
    private static final long serialVersionUID = -4486320669764051090L;

    /** 用户ID. */
    private String userId;

    /** 授权的角色ID列表字符串. */
    private String roleIds;

    /** 清空标志，仅为true时有效. */
    private String deleteAllFlg;

    /*
     * Getter 和 Setter 方法.
     */
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
