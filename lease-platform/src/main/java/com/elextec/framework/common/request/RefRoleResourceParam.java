package com.elextec.framework.common.request;

import com.elextec.framework.BaseModel;

/**
 * 角色授权资源参数接收类.
 * Created by js_gg on 2018/1/24.
 */
public class RefRoleResourceParam extends BaseModel {

    /** SV-UID. */
    private static final long serialVersionUID = -8021782752900554393L;

    /** 清空标志，仅为true时有效. */
    private String deleteAllFlg;
    /** 角色ID. */
    private String roleId;
    /** 资源ID列表字符串. */
    private String resourceIds;

    /*
     * Getter 和 Setter 方法.
     */
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(String resourceIds) {
        this.resourceIds = resourceIds;
    }

    public String getDeleteAllFlg() {
        return deleteAllFlg;
    }

    public void setDeleteAllFlg(String deleteAllFlg) {
        this.deleteAllFlg = deleteAllFlg;
    }
}
