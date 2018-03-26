package com.elextec.persist.model.mybatis.ext;

import com.elextec.persist.model.mybatis.SysApply;


/**
 * 申请扩展类.
 * Created by wangtao on 2018/1/16.
 */
public class SysApplyExt extends SysApply {
    /** 申请组织名称. */
    private String applyOrgName;
    /** 申请人名称. */
    private String applyUserName;

    /** 审批人名称 */
    private String examineUserName;
    /*
     * Getter 及 Setter 方法.
     */

    public String getApplyOrgName() {
        return applyOrgName;
    }

    public void setApplyOrgName(String applyOrgName) {
        this.applyOrgName = applyOrgName;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public String getExamineUserName() {
        return examineUserName;
    }

    public void setExamineUserName(String examineUserName) {
        this.examineUserName = examineUserName;
    }
}
