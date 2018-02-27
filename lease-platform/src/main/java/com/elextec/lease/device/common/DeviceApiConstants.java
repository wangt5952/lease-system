package com.elextec.lease.device.common;

/**
 * 硬件通讯常量定义.
 * Created by wangtao on 2018/2/24.
 */
public class DeviceApiConstants {

    /** 轨迹分段间隔时间，单位：毫秒，默认15分钟. */
    public static final Long TRACK_STAY_TIME = new Long(900000);

    /** 车辆ID. */
    public static final String REQ_RESP_VEHICLE_ID = "VehicleID";
    /** 电池ID. */
    public static final String REQ_RESP_BATTERY_ID = "BatteryID";
    /** 设备ID. */
    public static final String REQ_RESP_DEVICE_ID = "DeviceID";

    /*
     * DeviceApi 设备设定参数控制相关Key.
     */
    /** 设置设备上传时间间隔. */
    public static final String RESP_PERSET = "PerSet";
    /** 硬件复位. */
    public static final String RESP_RESET = "Reset";
    /** 主动请求数据. */
    public static final String RESP_REQUEST = "Request";

    /*
     * DeviceApi 设备上传参数相关Key.
     */
    /** 设备类别. */
    public static final String REQ_DEVICE_TYPE = "DeviceType";
    /** 设备版本号. */
    public static final String REQ_VERSION = "Version";
    /** 生产日期. */
    public static final String REQ_DATE = "Date";
    /** 电池保护板版本号. */
    public static final String REQ_PV = "PV";
    /** 电池总电压值值 单位10毫伏. */
    public static final String REQ_TV = "TV";
    /** 经度. */
    public static final String REQ_LON = "LON";
    /** 纬度. */
    public static final String REQ_LAT = "LAT";
    /** 车辆信息. */
    public static final String REQ_DEVICE_DATA = "DeviceData";
    /** 剩余容量百分比. */
    public static final String REQ_RSOC = "RSOC";
    /** 保护状态，不同数字保护状态说明不一. */
    public static final String REQ_PS = "PS";
    /** 设备电量. */
    public static final String REQ_QUANITY = "Quanity";

    /** 定位时间. */
    public static final String KEY_LOC_TIME = "LocTime";
    /** 停留时间（单位:毫秒）. */
    public static final String KEY_STAY_TIME = "StayTime";
}
