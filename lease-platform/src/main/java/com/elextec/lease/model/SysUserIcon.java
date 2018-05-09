package com.elextec.lease.model;

import java.io.Serializable;

/**
 * 用户资源Icon.
 */
public class SysUserIcon implements Serializable {

    /** Icon文件名. */
    private String iconName;

    /** Icon访问URL. */
    private String iconUrl;

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getIconName() {

        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
}
