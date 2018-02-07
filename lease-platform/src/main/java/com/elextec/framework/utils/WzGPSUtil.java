package com.elextec.framework.utils;

/**
 * GPS相关工具类.
 * Created by wangtao on 2018/2/5.
 */
public class WzGPSUtil {
    /** 地球半径（千米）. */
    private static double EARTH_RADIUS = 6378.137;
    /** PI. */
    private static double PI = Math.PI;
    /** 卫星椭球坐标投影到平面地图坐标系的投影因子. */
    private static double A = 6378245.0;
    /** 椭球的偏心率. */
    private static double EE = 0.00669342162296594323;
    /** 圆周率转换量. */
    private static double X_PI = PI * 3000.0 / 180.0;

    /*
     * WGS-84坐标：国际标准，GPS标准
     * BD-09坐标：百度坐标偏移量，Baidu Map使用
     * GCJ-02坐标：中国坐标偏移标准，Google Map、高德、腾讯使用
     */

    /**
     * WGS-84坐标转换为BD-09坐标.
     * @param lat WGS纬度
     * @param lng WGS经度
     * @return BD经纬度[BD纬度, BD经度]
     */
    public static double[] wgs2bd(double lat, double lng) {
        double[] wgs2gcj = wgs2gcj(lat, lng);
        double[] gcj2bd = gcj2bd(wgs2gcj[0], wgs2gcj[1]);
        return gcj2bd;
    }

    /**
     * GCJ-02坐标转换为BD-09坐标.
     * @param lat GCJ纬度
     * @param lng GCJ经度
     * @return BD经纬度[BD纬度, BD经度]
     */
    public static double[] gcj2bd(double lat, double lng) {
        double x = lng, y = lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * X_PI);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new double[] { bd_lat, bd_lon };
    }

    /**
     * BD-09坐标转换为GCJ-02坐标.
     * @param lat BD纬度
     * @param lng BD经度
     * @return GCJ经纬度[GCJ纬度, GCJ度]
     */
    public static double[] bd2gcj(double lat, double lng) {
        double x = lng - 0.0065, y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new double[] { gg_lat, gg_lon };
    }

    /**
     * WGS-84坐标转换为GCJ-02坐标.
     * @param lat WGS纬度
     * @param lng WGS经度
     * @return GCJ经纬度[GCJ纬度, GCJ经度]
     */
    public static double[] wgs2gcj(double lat, double lng) {
        double dLat = transformLat(lng - 105.0, lat - 35.0);
        double dLon = transformLng(lng - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (A / sqrtMagic * Math.cos(radLat) * PI);
        double mgLat = lat + dLat;
        double mgLon = lng + dLon;
        double[] loc = { mgLat, mgLon };
        return loc;
    }

    /**
     * 转换纬度.
     * @param lat 转前纬度
     * @param lng 转前经度
     * @return 转后纬度
     */
    private static double transformLat(double lat, double lng) {
        double ret = -100.0 + 2.0 * lat + 3.0 * lng + 0.2 * lng * lng + 0.1 * lat * lng + 0.2 * Math.sqrt(Math.abs(lat));
        ret += (20.0 * Math.sin(6.0 * lat * PI) + 20.0 * Math.sin(2.0 * lat * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lng / 12.0 * PI) + 320 * Math.sin(lng * PI  / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 转换经度.
     * @param lat 转前纬度
     * @param lng 转前经度
     * @return 转后经度
     */
    private static double transformLng(double lat, double lng) {
        double ret = 300.0 + lat + 2.0 * lng + 0.1 * lat * lat + 0.1 * lat * lng + 0.1 * Math.sqrt(Math.abs(lat));
        ret += (20.0 * Math.sin(6.0 * lat * PI) + 20.0 * Math.sin(2.0 * lat * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lat / 12.0 * PI) + 300.0 * Math.sin(lat / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 计算弧度.
     * @param d 角度
     * @return 弧度
     */
    public static double rad(double d) {
        return d * PI / 180.0;
    }

    /**
     * 计算两点距离（千米）.
     * @param lat1 第一点纬度
     * @param lng1 第一点经度
     * @param lat2 第二点纬度
     * @param lng2 第二点经度
     * @return 距离（千米）
     */
    public static double calcDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2),2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }
}
