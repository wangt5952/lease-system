package com.elextec.lease.manager.request;

import com.elextec.framework.plugins.paging.PageRequest;

/**
 * 申请查询参数类.
 * Created by YangKun on 2018/2/6
 */
public class SysApplyParam extends PageRequest {

    /*查询关键字包括（apply_title，apply_content，examine_content，apply_org_name）*/
    private String keyStr;

    /*申请类别(车辆申请)*/
    private String applyType;

    /*申请状态（待审核、同意、驳回）*/
    private String applyStatus;

    /** 查询类别(仅在操作用户是企业用户的时候生效，"0"为查询待企业审批的所有申请，"1"为查询企业提交给平台的申请) */
    private String flag;

    /*
     * Getter 和 Setter 方法.
     */
    public String getKeyStr() {
        return keyStr;
    }

    public void setKeyStr(String keyStr) {
        this.keyStr = keyStr;
    }

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public String getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(String applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
