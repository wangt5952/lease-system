package com.elextec.lease.device.controller;

import com.alibaba.fastjson.JSONObject;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.utils.WzGPSUtil;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.device.common.DeviceApiConstants;
import com.elextec.lease.device.common.DeviceManager;
import com.elextec.lease.device.common.DeviceRespMsg;
import com.elextec.lease.manager.service.BizDeviceConfService;
import com.elextec.lease.manager.service.BizVehicleTrackService;
import com.elextec.persist.field.enums.DeviceType;
import com.elextec.persist.model.mybatis.BizDeviceConf;
import com.elextec.persist.model.mybatis.BizDeviceConfKey;
import com.elextec.persist.model.mybatis.BizVehicleTrack;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTempTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
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

    @Value("${localsetting.track-stay-time}")
    private Long trackStayTime;

    @Autowired
    private BizDeviceConfService bizDeviceConfService;

    @Autowired
    private DeviceManager deviceManager;

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
        try {
            // Redis中有则直接返回Redis中的数据
            BizDeviceConf dc = (BizDeviceConf) redisClient.valueOperations().get(WzConstants.GK_DEVICE_CONF + deviceid + WzConstants.KEY_SPLIT + devicetype);
            if (null !=  dc) {
                respData.put(RESP_ERR_CODE, DeviceRespMsg.SUCCESS.code());
                respData.put(RESP_ERR_MSG, DeviceRespMsg.SUCCESS.getInfo());
                respData.put(DeviceApiConstants.REQ_RESP_DEVICE_ID, dc.getDeviceId());
                respData.put(DeviceApiConstants.RESP_PERSET, dc.getPerSet());
                respData.put(DeviceApiConstants.RESP_RESET, dc.getReset());
                respData.put(DeviceApiConstants.RESP_REQUEST, dc.getRequest());
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
            respData.put(DeviceApiConstants.REQ_RESP_DEVICE_ID, deviceConfVo.getDeviceId());
            respData.put(DeviceApiConstants.RESP_PERSET, deviceConfVo.getPerSet());
            respData.put(DeviceApiConstants.RESP_RESET, deviceConfVo.getReset());
            respData.put(DeviceApiConstants.RESP_REQUEST, deviceConfVo.getRequest());
            return respData;
        } catch (BizException ex) {
            logger.error("接口调用出现异常", ex);
            respData.put(RESP_ERR_CODE, ex.getInfoCode());
            respData.put(RESP_ERR_MSG, ex.getMessage());
            return respData;
        } catch (Exception ex) {
            logger.error("接口调用出现异常", ex);
            respData.put(RESP_ERR_CODE, DeviceRespMsg.SERVER_ERROR.code());
            respData.put(RESP_ERR_MSG, "接口调用出现异常");
            return respData;
        }

    }

    /**
     * 上传设备数据.
     * 新增消息队列
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
//        JSONObject respData = new JSONObject();
//        // 解析参数
//        JSONObject sensorData = null;
//        // 无参数情况
//        if (WzStringUtil.isBlank(data)) {
//            respData.put(RESP_ERR_CODE, DeviceRespMsg.NO_PARAM.code());
//            respData.put(RESP_ERR_MSG, DeviceRespMsg.NO_PARAM.getInfo());
//            return respData;
//            // 有参数情况
//        } else {
//            try {
//                String paramStr = URLDecoder.decode(data, "utf-8");
//                sensorData = JSONObject.parseObject(paramStr);
//                // 解析失败
//                if (null == sensorData) {
//                   respData.put(RESP_ERR_CODE, DeviceRespMsg.PARAM_ANALYZE_ERROR.code());
//                    respData.put(RESP_ERR_MSG, DeviceRespMsg.PARAM_ANALYZE_ERROR.getInfo());
//                    return respData;
//                }
//            } catch (Exception ex) {
//                logger.error(DeviceRespMsg.PARAM_ANALYZE_ERROR.getInfo(), ex);
//                respData.put(RESP_ERR_CODE, DeviceRespMsg.PARAM_ANALYZE_ERROR.code());
//                respData.put(RESP_ERR_MSG, DeviceRespMsg.PARAM_ANALYZE_ERROR.getInfo());
//                return respData;
//            }
//        }
//        try {
//            // 获得设备数据
//            // 设备ID
//            String deviceId  = null;
//            // 设备类别
//            String deviceType = null;
//            // 设备版本
//            String version = null;
//            // 电池生产日期
//            String madeDate = null;
//            // 电池数据
//            JSONObject deviceData = null;
//            // 电池总电压值
//            String tv = null;
//            // 电池保护板版本号
//            String pv = null;
//            // 电池剩余容量（百分比）
//            String rsoc = null;
//            // 电路板剩余电量（百分比）
//            String quanity = null;
//            // 保护状态,不同数字保护状态说明不一
//            String ps = null;
//            // 经度
//            Double lon = null;
//            // 纬度
//            Double lat = null;
//            // 获取固定参数
//            deviceId  = sensorData.getString(DeviceApiConstants.REQ_RESP_DEVICE_ID);
//            deviceType = sensorData.getString(DeviceApiConstants.REQ_DEVICE_TYPE);
//            version = WzStringUtil.defaultIfBlank(sensorData.getString(DeviceApiConstants.REQ_VERSION), "");
//            madeDate = WzStringUtil.defaultIfBlank(sensorData.getString(DeviceApiConstants.REQ_DATE), "");
//            tv = WzStringUtil.defaultIfBlank(sensorData.getString(DeviceApiConstants.REQ_TV), "");
//            pv = WzStringUtil.defaultIfBlank(sensorData.getString(DeviceApiConstants.REQ_PV), "");
//            try {
//                lon = sensorData.getDouble(DeviceApiConstants.REQ_LON);
//                lat = sensorData.getDouble(DeviceApiConstants.REQ_LAT);
//            } catch (Exception ex) {
//                lon = null;
//                lat = null;
//            }
//            deviceData = sensorData.getJSONObject(DeviceApiConstants.REQ_DEVICE_DATA);
//            if (null != deviceData) {
//                rsoc = WzStringUtil.defaultIfBlank(deviceData.getString(DeviceApiConstants.REQ_RSOC), "");
//                quanity = WzStringUtil.defaultIfBlank(deviceData.getString(DeviceApiConstants.REQ_QUANITY), "");
//                ps = WzStringUtil.defaultIfBlank(deviceData.getString(DeviceApiConstants.REQ_PS), "");
//            }
//            // 关键字不能为空
//            if (WzStringUtil.isBlank(deviceId) || WzStringUtil.isBlank(deviceType)) {
//                respData.put(RESP_ERR_CODE, DeviceRespMsg.NONE_ID_AND_TYPE.code());
//                respData.put(RESP_ERR_MSG, DeviceRespMsg.NONE_ID_AND_TYPE.getInfo());
//                return respData;
//            }
//            // 关键字
//            String devicePk = deviceId + WzConstants.KEY_SPLIT + deviceType;
//            // 将关键字记录到列表进行缓存
//            redisClient.setOperations().add(WzConstants.GK_DEVICE_PK_SET, devicePk);
//            // 记录当前位置信息及轨迹信息到缓存
//            if (null != lat && null != lon) {
//                long sysTime  = System.currentTimeMillis();
//                // 组装当前位置信息
//                JSONObject locVo = new JSONObject();
//                locVo.put(DeviceApiConstants.REQ_RESP_DEVICE_ID, deviceId);
//                locVo.put(DeviceApiConstants.REQ_DEVICE_TYPE, deviceType);
//                locVo.put(DeviceApiConstants.KEY_LOC_TIME, new Long(sysTime));
//                locVo.put(DeviceApiConstants.REQ_LAT, lat);
//                locVo.put(DeviceApiConstants.REQ_LON, lon);
//                // 记录当前定位
//                JSONObject nowLocVo = (JSONObject) redisClient.hashOperations().get(WzConstants.GK_DEVICE_LOC_MAP, devicePk);
//                if (null == nowLocVo
//                        || (!WzGPSUtil.outOfChina(lat.doubleValue(), lon.doubleValue())
//                            && (lat.doubleValue() != nowLocVo.getDoubleValue(DeviceApiConstants.REQ_LAT)
//                                || lon.doubleValue() != nowLocVo.getDoubleValue(DeviceApiConstants.REQ_LON)))) {
//                    redisClient.hashOperations().put(WzConstants.GK_DEVICE_LOC_MAP, devicePk, locVo);
//                }
//                // 记录轨迹信息
//                if (null == trackStayTime) {
//                    trackStayTime = DeviceApiConstants.TRACK_STAY_TIME;
//                }
//                // 轨迹信息
//                String trackKey = WzConstants.GK_DEVICE_TRACK + devicePk;
//                // 组装轨迹定位信息
//                JSONObject trackLocVo = new JSONObject();
//                trackLocVo.put(DeviceApiConstants.KEY_LOC_TIME, new Long(sysTime));
//                trackLocVo.put(DeviceApiConstants.REQ_LAT, lat);
//                trackLocVo.put(DeviceApiConstants.REQ_LON, lon);
//                // 获得上次记录的最终位置
//                Set<Object> lastLocSet = redisClient.zsetOperations().reverseRange(trackKey, 0, 0);
//                if (null == lastLocSet || 0 == lastLocSet.size()) {
//                    redisClient.zsetOperations().add(trackKey, trackLocVo, sysTime);
//                } else {
//                    List<Object> lastLocLs = new ArrayList<Object>(lastLocSet);
//                    JSONObject lastLocVo = (JSONObject) lastLocLs.get(0);
//                    if (!WzGPSUtil.outOfChina(lat.doubleValue(), lon.doubleValue())
//                            && (lat.doubleValue() != lastLocVo.getDoubleValue(DeviceApiConstants.REQ_LAT)
//                                || lon.doubleValue() != lastLocVo.getDoubleValue(DeviceApiConstants.REQ_LON))) {
//                        // 停留时间超过设定的时间则认为之前的轨迹为一个完成轨迹链，需要从缓存中取出并存到数据库中
//                        if (trackStayTime < (sysTime - lastLocVo.getLongValue(DeviceApiConstants.KEY_LOC_TIME))) {
//                            Set<Object> lastLocSetForSave = redisClient.zsetOperations().range(trackKey, 0, -1);
//                            // 整理并保存轨迹链
//                            int trackSize = lastLocSetForSave.size();
//                            int i = 0;
//                            long sTime = System.currentTimeMillis();
//                            long eTime = System.currentTimeMillis();
//                            JSONObject locVoForSave = null;
//                            StringBuffer trackStr = new StringBuffer("");
//                            for (Object locForSave : lastLocSetForSave) {
//                                locVoForSave = (JSONObject) locForSave;
//                                trackStr.append(locVoForSave.getLongValue(DeviceApiConstants.KEY_LOC_TIME))
//                                        .append(WzConstants.KEY_COMMA)
//                                        .append(locVoForSave.getDoubleValue(DeviceApiConstants.REQ_LAT))
//                                        .append(WzConstants.KEY_COMMA)
//                                        .append(locVoForSave.getDoubleValue(DeviceApiConstants.REQ_LON))
//                                        .append(WzConstants.KEY_SEMICOLON);
//                                if (0 == i) {
//                                    sTime = locVoForSave.getLongValue(DeviceApiConstants.KEY_LOC_TIME);
//                                }
//                                if ((trackSize -1) == i) {
//                                    eTime = locVoForSave.getLongValue(DeviceApiConstants.KEY_LOC_TIME);
//                                }
//                                i++;
//                            }
//                            BizVehicleTrack bvt = new BizVehicleTrack();
//                            bvt.setDeviceId(deviceId);
//                            bvt.setDeviceType(WzStringUtil.isBlank(deviceType) ? DeviceType.BATTERY.toString() : deviceType);
//                            bvt.setStartTime(sTime);
//                            bvt.setEndTime(eTime);
//                            bvt.setTaskInfo(WzUniqueValUtil.makeUUID());
//                            bvt.setLocations(trackStr.toString());
//                            // 保存轨迹链
//                            bizVehicleTrackService.insertVehicleTrack(bvt);
//                            // 清空已保存的轨迹链
//                            redisClient.zsetOperations().removeRange(trackKey, 0, -1);
//                        }
//                        // 插入该次定为数据
//                        redisClient.zsetOperations().add(trackKey, trackLocVo, sysTime);
//                    }
//                }
//            }
//            // 记录电池等变化信息
//            // 整理电量变化相关信息
//            JSONObject powerVo = new JSONObject();
//            powerVo.put(DeviceApiConstants.REQ_RSOC, rsoc);
//            powerVo.put(DeviceApiConstants.REQ_QUANITY, quanity);
//            powerVo.put(DeviceApiConstants.REQ_PS, ps);
//            JSONObject lastPowerVo  = (JSONObject) redisClient.hashOperations().get(WzConstants.GK_DEVIE_POWER_MAP, devicePk);
//            if (null == lastPowerVo) {
//                redisClient.hashOperations().put(WzConstants.GK_DEVIE_POWER_MAP, devicePk, powerVo);
//            } else {
//                if (WzStringUtil.isNotBlank(rsoc)) {
//                    lastPowerVo.put(DeviceApiConstants.REQ_RSOC, rsoc);
//                }
//                if (WzStringUtil.isNotBlank(quanity)) {
//                    lastPowerVo.put(DeviceApiConstants.REQ_QUANITY, quanity);
//                }
//                if (WzStringUtil.isNotBlank(ps)) {
//                    lastPowerVo.put(DeviceApiConstants.REQ_PS, ps);
//                }
//                redisClient.hashOperations().put(WzConstants.GK_DEVIE_POWER_MAP, devicePk, lastPowerVo);
//            }
//            // 保存该设备的基本信息
//            // 整理设备基本信息
//            JSONObject deviceParamVo = new JSONObject();
//            deviceParamVo.put(DeviceApiConstants.REQ_VERSION, version);
//            deviceParamVo.put(DeviceApiConstants.REQ_DATE, madeDate);
//            deviceParamVo.put(DeviceApiConstants.REQ_PV, pv);
//            deviceParamVo.put(DeviceApiConstants.REQ_TV, tv);
//            JSONObject lastDeviceParamVo  = (JSONObject) redisClient.hashOperations().get(WzConstants.GK_DEVICE_PARAM_MAP, devicePk);
//            if (null == lastDeviceParamVo) {
//                redisClient.hashOperations().put(WzConstants.GK_DEVICE_PARAM_MAP, devicePk, deviceParamVo);
//            } else {
//                if (WzStringUtil.isNotBlank(version)) {
//                    lastDeviceParamVo.put(DeviceApiConstants.REQ_VERSION, version);
//                }
//                if (WzStringUtil.isNotBlank(madeDate)) {
//                    lastDeviceParamVo.put(DeviceApiConstants.REQ_DATE, madeDate);
//                }
//                if (WzStringUtil.isNotBlank(pv)) {
//                    lastDeviceParamVo.put(DeviceApiConstants.REQ_PV, pv);
//                }
//                if (WzStringUtil.isNotBlank(tv)) {
//                    lastDeviceParamVo.put(DeviceApiConstants.REQ_TV, tv);
//                }
//                redisClient.hashOperations().put(WzConstants.GK_DEVICE_PARAM_MAP, devicePk, lastDeviceParamVo);
//            }
//            // 返回结果
//            respData.put(RESP_ERR_CODE, DeviceRespMsg.SUCCESS.code());
//            respData.put(RESP_ERR_MSG, DeviceRespMsg.SUCCESS.getInfo());
//            return respData;
//        } catch (BizException ex) {
//            logger.error("接口调用出现异常", ex);
//            respData.put(RESP_ERR_CODE, ex.getInfoCode());
//            respData.put(RESP_ERR_MSG, ex.getMessage());
//            return respData;
//        } catch (Exception ex) {
//            logger.error("接口调用出现异常", ex);
//            respData.put(RESP_ERR_CODE, DeviceRespMsg.SERVER_ERROR.code());
//            respData.put(RESP_ERR_MSG, "接口调用出现异常");
//            return respData;
//        }
        //新增消息队列
        Destination destination = new ActiveMQQueue("sensorData");
        //传入队列以及值
        deviceManager.send(destination,data);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(RESP_ERR_CODE,DeviceRespMsg.SUCCESS.code());
        jsonObject.put(RESP_ERR_MSG, DeviceRespMsg.SUCCESS.getInfo());
        return  jsonObject;
    }
}
