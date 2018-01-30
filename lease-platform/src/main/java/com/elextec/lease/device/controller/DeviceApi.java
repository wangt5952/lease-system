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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    private static final String RESP_ERR_CODE = "errorcode";
    private static final String RESP_ERR_MSG = "errormsg";

    @Autowired
    private BizDeviceConfService bizDeviceConfService;

    /*
     * 设备设定参数控制相关Key.
     */
    /** 设备ID. */
    private static final String RESP_DEVICE_CONF_DEVICE_ID = "DeviceID";
    /** 设置设备上传时间间隔. */
    private static final String RESP_DEVICE_CONF_PERSET = "PerSet";
    /** 硬件复位. */
    private static final String RESP_DEVICE_CONF_RESET = "Reset";
    /** 主动请求数据. */
    private static final String RESP_DEVICE_CONF_REQUEST = "Request";

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
            respData.put(RESP_DEVICE_CONF_DEVICE_ID, dc.getDeviceId());
            respData.put(RESP_DEVICE_CONF_PERSET, dc.getPerSet());
            respData.put(RESP_DEVICE_CONF_RESET, dc.getReset());
            respData.put(RESP_DEVICE_CONF_REQUEST, dc.getRequest());
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
        respData.put(RESP_DEVICE_CONF_DEVICE_ID, deviceConfVo.getDeviceId());
        respData.put(RESP_DEVICE_CONF_PERSET, deviceConfVo.getPerSet());
        respData.put(RESP_DEVICE_CONF_RESET, deviceConfVo.getReset());
        respData.put(RESP_DEVICE_CONF_REQUEST, deviceConfVo.getRequest());
        return respData;
    }
}
