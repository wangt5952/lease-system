package com.elextec.framework.common.request;

import com.elextec.framework.BaseModel;

import java.io.Serializable;

/**
 * 数据权限扩展类.
 * Created by wangtao on 2018/2/9.
 */
public class DataPermissionRequest extends BaseModel {
    /** SV-UID. */
    private static final long serialVersionUID = 9024030804972659962L;
    /**用户ID*/
    private String userId;

    /**企业ID*/
    private String orgId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
