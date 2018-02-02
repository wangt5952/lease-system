package com.elextec.lease.device.controller;

import com.alibaba.fastjson.JSONObject;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.device.common.DeviceRespMsg;
import com.elextec.lease.manager.service.BizDeviceConfService;
import com.elextec.persist.field.enums.DeviceType;
import com.elextec.persist.model.mybatis.BizDeviceConf;
import com.elextec.persist.model.mybatis.BizDeviceConfKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 与硬件设备接口.
 * Created by wangtao on 2018/1/29.
 */
@RestController
@RequestMapping(path = "/device/v1")
public class DeviceApi extends BaseController {
    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(DeviceApi.class);

    /** 错误Code 成功为0. */
    private static final String RESP_ERR_CODE = "errorcode";
    /** 错误消息. */
    private static final String RESP_ERR_MSG = "errormsg";

    @Autowired
    private BizDeviceConfService bizDeviceConfService;

    /** 设备ID. */
    private static final String REQ_RESP_DEVICE_ID = "DeviceID";

    /*
     * 设备设定参数控制相关Key.
     */
    /** 设置设备上传时间间隔. */
    private static final String RESP_PERSET = "PerSet";
    /** 硬件复位. */
    private static final String RESP_RESET = "Reset";
    /** 主动请求数据. */
    private static final String RESP_REQUEST = "Request";

    /*
     * 设备上传参数相关Key.
     */
    /** 设备类别. */
    private static final String REQ_DEVICE_TYPE = "DeviceType";
    /** 设备版本号. */
    private static final String REQ_VERSION = "Version";
    /** 生产日期. */
    private static final String REQ_DATE = "Date";
    /** 电池保护板版本号. */
    private static final String REQ_PV = "PV";
    /** 电池总电压值值 单位10毫伏. */
    private static final String REQ_TV = "TV";
    /** 经度. */
    private static final String REQ_LON = "LON";
    /** 纬度. */
    private static final String REQ_LAT = "LAT";
    /** 车辆信息. */
    private static final String REQ_DEVICE_DATA = "DeviceData";
    /** 剩余容量百分比. */
    private static final String REQ_RSOC = "RSOC";
    /** 保护状态，不同数字保护状态说明不一. */
    private static final String REQ_PS = "PS";
    /** 设备电量. */
    private static final String REQ_QUANITY = "Quanity";

    /** 定位时间. */
    private static final String KEY_LOC_TIME = "LocTime";

    /**
     * 获得控制参数.
     * @param deviceid 设备ID
     * @param devicetype 设备类别（）
     * @return 登录结果及登录账户信息
     * <pre>
     *     {
     *         errorcode:返回Code,
     *         errormsg:返回消息,
     *         DeviceID:设备ID,
     *         PerSet:请求间隔时间（单位：秒）,
     *         Reset:硬件复位标志（0：无处理；1；复位重启）,
     *         Request:主动请求数据标志（0：无处理；1：主动请求）
     *     }
     * </pre>
     */
    @RequestMapping(path = {"/getconf"}, method = RequestMethod.GET)
    public JSONObject getDevciceConf(String deviceid, String devicetype) {
        JSONObject respData = new JSONObject();
        // deviceid或devicetype为空则需要报错
        if (WzStringUtil.isBlank(deviceid) || WzStringUtil.isBlank(devicetype)) {
            respData.put(RESP_ERR_CODE, DeviceRespMsg.NONE_ID_AND_TYPE.code());
            respData.put(RESP_ERR_MSG, DeviceRespMsg.NONE_ID_AND_TYPE.getInfo());
            return respData;
        }
        if (!devicetype.toUpperCase().equals(DeviceType.BATTERY.toString())
                && !devicetype.toUpperCase().equals(DeviceType.VEHICLE.toString())
                && !devicetype.toUpperCase().equals(DeviceType.PARTS.toString())) {
            respData.put(RESP_ERR_CODE, DeviceRespMsg.INVALID_DEVICE.code());
            respData.put(RESP_ERR_MSG, DeviceRespMsg.INVALID_DEVICE.getInfo());
            return respData;
        }
        // Redis中有则直接返回Redis中的数据
        BizDeviceConf dc = (BizDeviceConf) redisClient.valueOperations().get(WzConstants.GK_DEVICE_CONF + deviceid + WzConstants.KEY_SPLIT + devicetype);
        if (null !=  dc) {
            respData.put(RESP_ERR_CODE, DeviceRespMsg.SUCCESS.code());
            respData.put(RESP_ERR_MSG, DeviceRespMsg.SUCCESS.getInfo());
            respData.put(REQ_RESP_DEVICE_ID, dc.getDeviceId());
            respData.put(RESP_PERSET, dc.getPerSet());
            respData.put(RESP_RESET, dc.getReset());
            respData.put(RESP_REQUEST, dc.getRequest());
            return respData;
        }
        // Redis中没有则进行查库
        BizDeviceConfKey selectKey = new BizDeviceConfKey();
        selectKey.setDeviceId(deviceid);
        selectKey.setDeviceType(DeviceType.valueOf(devicetype));
        BizDeviceConf deviceConfVo = bizDeviceConfService.getBizDeviceConfByPrimaryKey(selectKey);
        // 库中没有直接返回错误
        if (null == deviceConfVo) {
            respData.put(RESP_ERR_CODE, DeviceRespMsg.NO_DEVICE.code());
            respData.put(RESP_ERR_MSG, DeviceRespMsg.NO_DEVICE.getInfo());
            return respData;
        }
        // 库中更存在则设置缓存到Redis中并返回
        redisClient.valueOperations().set(WzConstants.GK_DEVICE_CONF + deviceid + WzConstants.KEY_SPLIT + devicetype, deviceConfVo, 30, TimeUnit.MINUTES);
        respData.put(RESP_ERR_CODE, DeviceRespMsg.SUCCESS.code());
        respData.put(RESP_ERR_MSG, DeviceRespMsg.SUCCESS.getInfo());
        respData.put(REQ_RESP_DEVICE_ID, deviceConfVo.getDeviceId());
        respData.put(RESP_PERSET, deviceConfVo.getPerSet());
        respData.put(RESP_RESET, deviceConfVo.getReset());
        respData.put(RESP_REQUEST, deviceConfVo.getRequest());
        return respData;
    }

    /**
     * 获得控制参数.
     * @param data 传输的设备数据
     * <pre>
     *     {
     *         DeviceID:设备ID,
     *         DeviceType:"BATTERY",设备类别（暂时固定为BATTERY）
     *         Version:设备版本号,
     *         Date:生产日期,
     *         PV:电池保护板版本号,
     *         TV:电池总电压值,
     *         LON:纬度,
     *         LAT:经度,
     *         DeviceData:{
     *             RSOC:剩余容量百分比,
     *             Quanity:设备电量,
     *             PS:保护状态，不同数字保护状态说明不一,
     *         }
     *     }
     * </pre>
     * @return 处理结果
     * <pre>
     *     {
     *         errorcode:返回Code,
     *         errormsg:返回消息
     *     }
     * </pre>
     */
    @RequestMapping(path = {"/sensordata"}, method = RequestMethod.POST)
    public JSONObject sensorData(@RequestBody String data) {
        JSONObject respData = new JSONObject();
        // 解析参数
        JSONObject sensorData = null;
        // 无参数情况
        if (WzStringUtil.isBlank(data)) {
            respData.put(RESP_ERR_CODE, DeviceRespMsg.NO_PARAM.code());
            respData.put(RESP_ERR_MSG, DeviceRespMsg.NO_PARAM.getInfo());
            return respData;
            // 有参数情况
        } else {
            try {
                String paramStr = URLDecoder.decode(data, "utf-8");
                sensorData = JSONObject.parseObject(paramStr);
                // 解析失败
                if (null == sensorData) {
                    respData.put(RESP_ERR_CODE, DeviceRespMsg.PARAM_ANALYZE_ERROR.code());
                    respData.put(RESP_ERR_MSG, DeviceRespMsg.PARAM_ANALYZE_ERROR.getInfo());
                    return respData;
                }
            } catch (Exception ex) {
                logger.error(DeviceRespMsg.PARAM_ANALYZE_ERROR.getInfo(), ex);
                respData.put(RESP_ERR_CODE, DeviceRespMsg.PARAM_ANALYZE_ERROR.code());
                respData.put(RESP_ERR_MSG, DeviceRespMsg.PARAM_ANALYZE_ERROR.getInfo());
                return respData;
            }
        }
        // 获得设备数据
        // 设备ID
        String deviceId  = null;
        // 设备类别
        String deviceType = null;
        // 设备版本
        String version = null;
        // 电池生产日期
        String madeDate = null;
        // 电池数据
        JSONObject deviceData = null;
        // 电池总电压值
        String tv = null;
        // 电池保护板版本号
        String pv = null;
        // 电池剩余容量（百分比）
        String rsoc = null;
        // 电路板剩余电量（百分比）
        String quanity = null;
        // 保护状态,不同数字保护状态说明不一
        String ps = null;
        // 经度
        Double lon = null;
        // 纬度
        Double lat = null;
        // 获取固定参数
        deviceId  = sensorData.getString(REQ_RESP_DEVICE_ID);
        deviceType = sensorData.getString(REQ_DEVICE_TYPE);
        version = WzStringUtil.defaultIfBlank(sensorData.getString(REQ_VERSION), "");
        madeDate = WzStringUtil.defaultIfBlank(sensorData.getString(REQ_DATE), "");
        tv = WzStringUtil.defaultIfBlank(sensorData.getString(REQ_TV), "");
        pv = WzStringUtil.defaultIfBlank(sensorData.getString(REQ_PV), "");
        try {
            lon = sensorData.getDouble(REQ_LON);
            lat = sensorData.getDouble(REQ_LAT);
        } catch (Exception ex) {
            lon = null;
            lat = null;
        }
        deviceData = sensorData.getJSONObject(REQ_DEVICE_DATA);
        if (null != deviceData) {
            rsoc = WzStringUtil.defaultIfBlank(deviceData.getString(REQ_RSOC), "");
            quanity = WzStringUtil.defaultIfBlank(deviceData.getString(REQ_QUANITY), "");
            ps = WzStringUtil.defaultIfBlank(deviceData.getString(REQ_PS), "");
        }
        // 关键字不能为空
        if (WzStringUtil.isBlank(deviceId) || WzStringUtil.isBlank(deviceType)) {
            respData.put(RESP_ERR_CODE, DeviceRespMsg.NONE_ID_AND_TYPE.code());
            respData.put(RESP_ERR_MSG, DeviceRespMsg.NONE_ID_AND_TYPE.getInfo());
            return respData;
        }
        // 关键字
        String devicePk = deviceId + WzConstants.KEY_SPLIT + deviceType;
        // 将关键字记录到列表进行缓存
        redisClient.setOperations().add(WzConstants.GK_DEVICE_PK_SET, devicePk);
        // 记录当前位置信息及轨迹信息到缓存
        if (null != lat && null != lon) {
            // 组装定位信息
            JSONObject locVo = new JSONObject();
            long sysTime  = System.currentTimeMillis();
            locVo.put(KEY_LOC_TIME, sysTime);
            locVo.put(REQ_LAT, lat);
            locVo.put(REQ_LON, lon);
            // 记录当前定位
            redisClient.hashOperations().put(WzConstants.GK_DEVICE_LOC_MAP, devicePk, locVo);
            // 记录轨迹信息
            // 轨迹信息
            String trackKey = WzConstants.GK_DEVICE_TRACK + devicePk;
            // 获得上次记录的最终位置
            Set<Object> lastLocSet = redisClient.zsetOperations().reverseRange(trackKey, 0, 0);
            if (null == lastLocSet || 0 == lastLocSet.size()) {
                redisClient.zsetOperations().add(trackKey, locVo, sysTime);
            } else {
                List<Object> lastLocLs = new ArrayList<Object>(lastLocSet);
                JSONObject lastLocVo = (JSONObject) lastLocLs.get(0);
                if (lat.doubleValue() != lastLocVo.getDoubleValue(REQ_LAT)
                        || lon.doubleValue() != lastLocVo.getDoubleValue(REQ_LON)) {
                    redisClient.zsetOperations().add(trackKey, locVo, sysTime);
                }
            }
        }
        // 记录电池等变化信息
        // 整理电量变化相关信息
        JSONObject powerVo = new JSONObject();
        powerVo.put(REQ_RSOC, rsoc);
        powerVo.put(REQ_QUANITY, quanity);
        powerVo.put(REQ_PS, ps);
        JSONObject lastPowerVo  = (JSONObject) redisClient.hashOperations().get(WzConstants.GK_DEVIE_POWER_MAP, devicePk);
        if (null == lastPowerVo) {
            redisClient.hashOperations().put(WzConstants.GK_DEVIE_POWER_MAP, devicePk, powerVo);
        } else {
            if (WzStringUtil.isNotBlank(rsoc)) {
                lastPowerVo.put(REQ_RSOC, rsoc);
            }
            if (WzStringUtil.isNotBlank(quanity)) {
                lastPowerVo.put(REQ_QUANITY, quanity);
            }
            if (WzStringUtil.isNotBlank(ps)) {
                lastPowerVo.put(REQ_PS, ps);
            }
            redisClient.hashOperations().put(WzConstants.GK_DEVIE_POWER_MAP, devicePk, lastPowerVo);
        }
        // 保存该设备的基本信息
        // 整理设备基本信息
        JSONObject deviceParamVo = new JSONObject();
        deviceParamVo.put(REQ_VERSION, version);
        deviceParamVo.put(REQ_DATE, madeDate);
        deviceParamVo.put(REQ_PV, pv);
        deviceParamVo.put(REQ_TV, tv);
        JSONObject lastDeviceParamVo  = (JSONObject) redisClient.hashOperations().get(WzConstants.GK_DEVICE_PARAM_MAP, devicePk);
        if (null == lastDeviceParamVo) {
            redisClient.hashOperations().put(WzConstants.GK_DEVICE_PARAM_MAP, devicePk, deviceParamVo);
        } else {
            if (WzStringUtil.isNotBlank(version)) {
                lastDeviceParamVo.put(REQ_VERSION, version);
            }
            if (WzStringUtil.isNotBlank(madeDate)) {
                lastDeviceParamVo.put(REQ_DATE, madeDate);
            }
            if (WzStringUtil.isNotBlank(pv)) {
                lastDeviceParamVo.put(REQ_PV, pv);
            }
            if (WzStringUtil.isNotBlank(tv)) {
                lastDeviceParamVo.put(REQ_TV, tv);
            }
            redisClient.hashOperations().put(WzConstants.GK_DEVICE_PARAM_MAP, devicePk, lastDeviceParamVo);
        }
        // 返回结果
        respData.put(RESP_ERR_CODE, DeviceRespMsg.SUCCESS.code());
        respData.put(RESP_ERR_MSG, DeviceRespMsg.SUCCESS.getInfo());
        return respData;
    }
}
