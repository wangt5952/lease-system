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

    /*绑定状态.*/
    private String isBind;

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

    public String getIsBind() {
        return isBind;
    }

    public void setIsBind(String isBind) {
        this.isBind = isBind;
    }

    public void setPartsStatus(String partsStatus) {
        this.partsStatus = partsStatus;
    }

}
