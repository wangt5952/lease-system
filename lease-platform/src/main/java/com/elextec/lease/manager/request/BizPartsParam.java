package com.elextec.lease.manager.request;

import com.elextec.framework.plugins.paging.PageRequest;

/**
 * 配件查询参数类.
 * Created by YangKun on 2018/2/6
 */
public class BizPartsParam extends PageRequest {

    /*查询关键字包括（parts_code，parts_name，parts_brand，parts_pn，parts_parameters，mfrs_id，mfrs_name）*/
    private String keyStr;

    /*配件类别(车座、车架、车把、车铃、轮胎、脚蹬、仪表盘)*/
    private String partsType;

    /*配件状态（正常、冻结、作废）*/
    private String partsStatus;

    /**用户ID*/
    private String userId;

    /**企业ID*/
    private String orgId;

    /**用户类型*/
    private String userType;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

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

    /*
     * Getter 和 Setter 方法.
     */
    public String getKeyStr() {
        return keyStr;
    }

    public void setKeyStr(String keyStr) {
        this.keyStr = keyStr;
    }

    public String getPartsType() {
        return partsType;
    }

    public void setPartsType(String partsType) {
        this.partsType = partsType;
    }

    public String getPartsStatus() {
        return partsStatus;
    }

    public void setPartsStatus(String partsStatus) {
        this.partsStatus = partsStatus;
    }

}
