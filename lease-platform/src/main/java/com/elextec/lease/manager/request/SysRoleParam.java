package com.elextec.lease.manager.request;

import com.elextec.framework.plugins.paging.PageRequest;

/**
 * 角色查询条件.
 * Created by js_gg on 2018/2/5.
 */
public class SysRoleParam extends PageRequest {
    /** 关键字，包括role_name. */
    private String keyStr;

    /*
     * Getter 和 Setter 方法.
     */
    public String getKeyStr() {
        return keyStr;
    }

    public void setKeyStr(String keyStr) {
        this.keyStr = keyStr;
    }

}
