package com.elextec.lease.model;

import java.io.Serializable;

/**
 * 资源Icon.
 */
public class SysResourcesIcon implements Serializable {

    /** SV-UID. */
    private static final long serialVersionUID = 2194651262407139828L;

    /** Icon文件名. */
    private String iconName;

    /** Icon访问URL. */
    private String iconUrl;

    /*
     * Getter 和 Setter 方法.
     */
    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}