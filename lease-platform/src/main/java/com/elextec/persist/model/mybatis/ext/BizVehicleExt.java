package com.elextec.persist.model.mybatis.ext;

import com.elextec.persist.model.mybatis.SysUser;

/**
 * 用户扩展类.
 * Created by wangtao on 2018/1/16.
 */
public class BizVehicleExt extends SysUser {
    /** 组织Code. */
    private String orgCode;
    /** 组织名称. */
    private String orgName;

    /*
     * Getter 及 Setter 方法.
     */
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
