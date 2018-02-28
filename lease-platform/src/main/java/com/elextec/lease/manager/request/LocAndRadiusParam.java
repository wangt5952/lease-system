package com.elextec.lease.manager.request;

import com.elextec.framework.plugins.paging.PageRequest;

/**
 * 定位点及半径参数.
 * Created by wangtao on 2018/2/25.
 */
public class LocAndRadiusParam extends PageRequest {
    /** 纬度. */
    private Double lat;
    /** 经度. */
    private Double lng;
    /** 半径(km). */
    private Double radius;

    /*
     * Getter 和 Setter 方法.
     */
    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }
}
