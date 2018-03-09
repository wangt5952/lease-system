package com.elextec.framework.utils;

import com.alibaba.fastjson.JSONObject;
import com.elextec.lease.device.common.DeviceApiConstants;

import java.util.ArrayList;
import java.util.List;

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
     * BD-09坐标转换为WGS-84坐标.
     * @param lat BD-09纬度
     * @param lng BD-09经度
     * @return WGS经纬度[WGS纬度, WGS经度]
     */
    public static double[] bd2wgs(double lat, double lng) {
        double[] bd2gcj = bd2gcj(lat, lng);
//        double[] gcj2wgs = gcj2wgs(bd2gcj[0], bd2gcj[1]);
        double[] gcj2wgs = gcj2wgsExact(bd2gcj[0], bd2gcj[1]);
        return gcj2wgs;
    }

    /**
     * Krasovsky_1940椭球体计算.
     * @param lat 纬度
     * @param lng 经度
     * @return 计算结果经纬度[纬度, 经度]
     */
    private static double[] delta(double lat, double lng) {
        double dLat = transformLat(lng - 105.0, lat - 35.0);
        double dLng = transformLng(lng - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLng = (dLng * 180.0) / (A / sqrtMagic * Math.cos(radLat) * PI);
        return new double[] {dLat, dLng};
    }

    /**
     * WGS-84坐标转换为GCJ-02坐标.
     * @param lat WGS纬度
     * @param lng WGS经度
     * @return GCJ经纬度[GCJ纬度, GCJ经度]
     */
    public static double[] wgs2gcj(double lat, double lng) {
//        double dLat = transformLat(lng - 105.0, lat - 35.0);
//        double dLon = transformLng(lng - 105.0, lat - 35.0);
//        double radLat = lat / 180.0 * PI;
//        double magic = Math.sin(radLat);
//        magic = 1 - EE * magic * magic;
//        double sqrtMagic = Math.sqrt(magic);
//        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
//        dLon = (dLon * 180.0) / (A / sqrtMagic * Math.cos(radLat) * PI);
        if (outOfChina(lat, lng)) {
            return new double[] {lat, lng};
        }
        double[] dLatLng = delta(lat, lng);
        double mgLat = lat + dLatLng[0];
        double mgLon = lng + dLatLng[1];
        double[] loc = { mgLat, mgLon };
        return loc;
    }

    /**
     * GCJ-02坐标转换为WGS-84坐标.
     * @param lat GCJ纬度
     * @param lng GCJ经度
     * @return WGS经纬度[WGS纬度, WGS经度]
     */
    public static double[] gcj2wgs(double lat, double lng) {
        if (outOfChina(lat, lng)) {
            return new double[] {lat, lng};
        }
        double[] dLatLng = delta(lat, lng);
        double mgLat = lat - dLatLng[0];
        double mgLon = lng - dLatLng[1];
        double[] loc = { mgLat, mgLon };
        return loc;
    }

    /**
     * GCJ-02坐标转换为WGS-84坐标（精确）.
     * @param lat GCJ纬度
     * @param lng GCJ经度
     * @return WGS经纬度[WGS纬度, WGS经度]
     */
    public static double[] gcj2wgsExact(double lat, double lng) {
        double initDelta = 0.01;
        // 精度小数点后6位
        double threshold = 0.000001;
        double dLat = initDelta, dLng = initDelta;
        double mLat = lat - dLat, mLng = lng - dLng;
        double pLat = lat + dLat, pLng = lng + dLng;
        double wgsLat, wgsLng;
        int i = 0;
        while (true) {
            wgsLat = (mLat + pLat) / 2;
            wgsLng = (mLng + pLng) / 2;
            double[] tmp = wgs2gcj(wgsLat, wgsLng);
            dLat = tmp[0] - lat;
            dLng = tmp[1] - lng;
            if ((Math.abs(dLat) < threshold) && (Math.abs(dLng) < threshold)) {
                break;
            }
            if (dLat > 0){
                pLat = wgsLat;
            } else{
                mLat = wgsLat;
            }
            if (dLng > 0) {
                pLng = wgsLng;
            } else {
                mLng = wgsLng;
            }
            if (++i > 10000) {
                break;
            }
        }
        return new double[] {wgsLat, wgsLng};
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
     * 坐标是否在中国以外.
     * @param lat 纬度
     * @param lng 经度
     * @return true:中国以外;false:中国以内
     */
    public static boolean outOfChina(double lat, double lng) {
        if (lng < 72.004 || lng > 137.8347) {
            return true;
        }
        if (lat < 0.8293 || lat > 55.8271) {
            return true;
        }
        return false;
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
    public static double calcDistanceByKm(double lat1, double lng1, double lat2, double lng2) {
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

    /**
     * 计算两点距离（米）.
     * @param lat1 第一点纬度
     * @param lng1 第一点经度
     * @param lat2 第二点纬度
     * @param lng2 第二点经度
     * @return 距离（米）
     */
    public static double calcDistanceByM(double lat1, double lng1, double lat2, double lng2) {
        return calcDistanceByKm(lat1, lng1, lat2, lng2) * 1000;
    }

    /**
     * 轨迹降噪.
     * @param locs 轨迹
     * <pre>
     *     [
     *         {
     *             LocTime:定位记录时间,
     *             LAT:纬度,
     *             LON:经度,
     *             StayTime:停留时间
     *         },
     *         ... ...
     *     ]
     * </pre>
     * @param speedThreshold 去噪速度阈值
     * @return 去噪后的轨迹
     * <pre>
     *     [
     *         {
     *             LocTime:定位记录时间,
     *             LAT:纬度,
     *             LON:经度,
     *             StayTime:停留时间
     *         },
     *         ... ...
     *     ]
     * </pre>
     */
    public static List<JSONObject> denoise(List<JSONObject> locs, Double speedThreshold) {
        // 记录点小于5个则直接返回
        if (null == locs || 5 >= locs.size() || null == speedThreshold) {
            return locs;
        }
        // 确定首点
        int firstIdx = -1;
        double tmpDistance1 = 0;
        long tmpIntervalTime1 = 0;
        double tmpDistance2 = 0;
        long tmpIntervalTime2 = 0;
        // 连续三个点的速度均正常的情况，此三点第一个点为首点
        for (int i = 0; i < locs.size() - 2; i++) {
            tmpDistance1 = calcDistanceByM(locs.get(i).getDoubleValue(DeviceApiConstants.REQ_LAT),
                    locs.get(i).getDoubleValue(DeviceApiConstants.REQ_LON),
                    locs.get(i + 1).getDoubleValue(DeviceApiConstants.REQ_LAT),
                    locs.get(i + 1).getDoubleValue(DeviceApiConstants.REQ_LON));
            tmpIntervalTime1 = (locs.get(i + 1).getLongValue(DeviceApiConstants.KEY_LOC_TIME) - locs.get(i).getLongValue(DeviceApiConstants.KEY_LOC_TIME)) * 1000;
            tmpDistance2 = calcDistanceByM(locs.get(i + 1).getDoubleValue(DeviceApiConstants.REQ_LAT),
                    locs.get(i + 1).getDoubleValue(DeviceApiConstants.REQ_LON),
                    locs.get(i + 2).getDoubleValue(DeviceApiConstants.REQ_LAT),
                    locs.get(i + 2).getDoubleValue(DeviceApiConstants.REQ_LON));
            tmpIntervalTime2 = (locs.get(i + 2).getLongValue(DeviceApiConstants.KEY_LOC_TIME) - locs.get(i + 1).getLongValue(DeviceApiConstants.KEY_LOC_TIME)) * 1000;
            if (speedThreshold >= (tmpDistance1 / tmpIntervalTime1)
                    && speedThreshold >= (tmpDistance2 / tmpIntervalTime2)) {
                firstIdx = i;
                break;
            }
        }
        // 如果到最后也没有找到首点，则直接返回全部点
        if (-1 == firstIdx) {
            return locs;
        }
        // 从首点开始对其后续点进行去噪
        List<JSONObject> usableLocs = new ArrayList<JSONObject>();
        for (int i = firstIdx; i < locs.size(); i++) {
            if (i == firstIdx) {
                usableLocs.add(locs.get(i));
            } else {
                tmpDistance1 = calcDistanceByM(locs.get(i).getDoubleValue(DeviceApiConstants.REQ_LAT),
                        locs.get(i).getDoubleValue(DeviceApiConstants.REQ_LON),
                        locs.get(i - 1).getDoubleValue(DeviceApiConstants.REQ_LAT),
                        locs.get(i - 1).getDoubleValue(DeviceApiConstants.REQ_LON));
                tmpIntervalTime1 = (locs.get(i).getLongValue(DeviceApiConstants.KEY_LOC_TIME) - locs.get(i - 1).getLongValue(DeviceApiConstants.KEY_LOC_TIME)) * 1000;
                if (speedThreshold >= (tmpDistance1 / tmpIntervalTime1)) {
                    usableLocs.add(locs.get(i));
                }
            }
        }
        return usableLocs;
    }
}
