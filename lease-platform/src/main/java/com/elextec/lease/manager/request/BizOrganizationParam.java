package com.elextec.lease.manager.request;

import com.elextec.framework.plugins.paging.PageRequest;

/**
 * 公司组织查询参数表.
 * Create By Yangkun on 2018/2/7.
 */
public class BizOrganizationParam extends PageRequest {

    /*查询关键字org_code、org_name、org_introduce、org_address、org_contacts、org_phone、org_business_licences*/
    private String keyStr;

    /*组织类别（平台、企业）*/
    private String orgType;

    /*组织状态（正常、冻结、作废）*/
    private String orgStatus;

    /*
     * Getter 和 Setter 方法.
     */
    public String getKeyStr() {
        return keyStr;
    }

    public void setKeyStr(String keyStr) {
        this.keyStr = keyStr;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getOrgStatus() {
        return orgStatus;
    }

    public void setOrgStatus(String orgStatus) {
        this.orgStatus = orgStatus;
    }

}
