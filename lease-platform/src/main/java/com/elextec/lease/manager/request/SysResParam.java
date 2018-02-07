package com.elextec.lease.manager.request;

import com.elextec.framework.plugins.paging.PageRequest;

/**
 * 资源查询条件.
 * Created by js_gg on 2018/2/5.
 */
public class SysResParam extends PageRequest {
    /** 关键字，包括res_code、res_name. */
    private String keyStr;

    /** 资源类别. */
    private String resType;

    /** 显示标志. */
    private String showFlg;

    /*
     * Getter 和 Setter 方法.
     */
    public String getKeyStr() {
        return keyStr;
    }

    public void setKeyStr(String keyStr) {
        this.keyStr = keyStr;
    }

    public String getResType() {
        return resType;
    }

    public void setResType(String resType) {
        this.resType = resType;
    }

    public String getShowFlg() {
        return showFlg;
    }

    public void setShowFlg(String showFlg) {
        this.showFlg = showFlg;
    }
}
