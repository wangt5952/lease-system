package com.elextec.lease.mobile.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.device.common.DeviceApiConstants;
import com.elextec.lease.manager.service.BizVehicleService;
import com.elextec.persist.field.enums.DeviceType;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.model.mybatis.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 移动端用户查询设备信息接口
 * Created by wangtao on 2018/1/30.
 */
@RestController
@RequestMapping(path = "/mobile/v1/device")
public class UserDeviceApi extends BaseController {
    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(UserDeviceApi.class);

    @Autowired
    private BizVehicleService bizVehicleService;


    /**
     * 根据车辆ID列表获取设备电量信息列表.
     * @param ids 车辆ID列表
     * <pre>
     *     [id1,id2,...]
     * </pre>
     * @param request HttpServletRequest
     * @return 根据ID获取设备电量信息
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:[
     *             {
     *                 VehicleID:车辆ID,
     *                 BatteryID:电池ID,
     *                 DeviceID:设备ID,
     *                 DeviceType:设备类型,
     *                 RSOC:电池剩余容量百分比,
     *                 Quanity:设备电量,
     *                 PS:保护状态
     *             },
     *             ... ...
     *         ]
     *     }
     * </pre>
     */
    @RequestMapping(path = "/getpowerbyvehiclepk")
    public MessageResponse getPowerByVehiclePK(@RequestBody String ids, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(ids)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> vehicleIds = null;
            try {
                String paramStr = URLDecoder.decode(ids, "utf-8");
                vehicleIds = JSON.parseArray(paramStr, String.class);
                if (null == vehicleIds || 0 == vehicleIds.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            }catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 循环获得车辆电池信息并获得定位信息
            List<Map<String,Object>> vehicleInfos = null;
            String batteryId = null;
            String batteryCode = null;
            String devicePk = null;
            JSONObject powerData = null;
            List<JSONObject> powerDatas = new ArrayList<JSONObject>();
            StringBuffer errMsgs = new StringBuffer("");
            SysUser userTemp = getLoginUserInfo(request);
            if(userTemp == null){
                return new MessageResponse(RunningResult.AUTH_OVER_TIME);
            }
            Map<String,Object> paramTemp = new HashMap<String,Object>();
            if(OrgAndUserType.ENTERPRISE.toString().equals(userTemp.getUserType())){
                paramTemp.put("orgId",userTemp.getOrgId());
            }
            if(OrgAndUserType.INDIVIDUAL.toString().equals(userTemp.getUserType())){
                paramTemp.put("userId",userTemp.getId());
            }
            for (String vId : vehicleIds) {
                if (WzStringUtil.isNotBlank(vId)) {
                    paramTemp.put("id",vId);
                    vehicleInfos = bizVehicleService.getByPrimaryKey(paramTemp, true);
                    if (null == vehicleInfos || 0 == vehicleInfos.size()) {
                        errMsgs.append("未查询到车辆[ID:" + vId + "]信息;");
                        continue;
                    } else {
                        for (Map<String,Object> vehicleInfo : vehicleInfos) {
                            batteryId = (String) vehicleInfo.get("batteryId");
                            batteryCode = (String) vehicleInfo.get("batteryCode");
                            if (WzStringUtil.isBlank(batteryCode)) {
                                errMsgs.append("未查询到车辆[ID:" + vId + "]对应的设备;");
                                continue;
                            }
                            // 根据设备ID查询设备当前位置
                            devicePk = batteryCode + WzConstants.KEY_SPLIT + DeviceType.BATTERY.toString();
                            powerData = (JSONObject) redisClient.hashOperations().get(WzConstants.GK_DEVIE_POWER_MAP, devicePk);
                            // 组织返回结果
                            if (null == powerData) {
                                powerData = new JSONObject();
                                powerData.put(DeviceApiConstants.REQ_RSOC, 0);
                                powerData.put(DeviceApiConstants.REQ_QUANITY, 0);
                                powerData.put(DeviceApiConstants.REQ_PS, 0);
                            }
                            powerData.put(DeviceApiConstants.REQ_RESP_VEHICLE_ID, vId);
                            powerData.put(DeviceApiConstants.REQ_RESP_BATTERY_ID, batteryId);
                            powerData.put(DeviceApiConstants.REQ_RESP_DEVICE_ID, batteryCode);
                            powerData.put(DeviceApiConstants.REQ_DEVICE_TYPE, DeviceType.BATTERY.toString());
                            powerDatas.add(powerData);
                        }
                    }
                }
            }
            // 返回结果
            MessageResponse mr = null;
            if (0 == errMsgs.length()) {
                mr = new MessageResponse(RunningResult.SUCCESS, powerDatas);
            } else {
                mr = new MessageResponse(RunningResult.NOT_FOUND.code(), errMsgs.toString(), powerDatas);
            }
            return mr;
        }
    }

    /**
     * 根据车辆ID列表获取车辆信息列表.
     * @param ids 车辆ID列表
     * <pre>
     *     [id1,id2,...]
     * </pre>
     * @param request HttpServletRequest
     * @return 根据ID获取设备定位信息
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:[
     *             {
     *                 VehicleID:车辆ID,
     *                 BatteryID:电池ID,
     *                 DeviceID:设备ID,
     *                 DeviceType:设备类型,
     *                 LocTime:记录时间,
     *                 LAT:纬度,
     *                 LON:经度
     *             },
     *             ... ...
     *         ]
     *     }
     * </pre>
     */
    @RequestMapping(path = "/getlocbyvehiclepk")
    public MessageResponse getLocByVehiclePK(@RequestBody String ids, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(ids)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> vehicleIds = null;
            try {
                String paramStr = URLDecoder.decode(ids, "utf-8");
                vehicleIds = JSON.parseArray(paramStr, String.class);
                if (null == vehicleIds || 0 == vehicleIds.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            }catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 循环获得车辆电池信息并获得定位信息
            List<Map<String,Object>> vehicleInfos = null;
            String batteryId = null;
            String batteryCode = null;
            String devicePk = null;
            JSONObject locData = null;
            List<JSONObject> locDatas = new ArrayList<JSONObject>();
            StringBuffer errMsgs = new StringBuffer("");
            SysUser userTemp = getLoginUserInfo(request);
            if(userTemp == null){
                return new MessageResponse(RunningResult.AUTH_OVER_TIME);
            }
            Map<String,Object> paramTemp = new HashMap<String,Object>();
            if(OrgAndUserType.ENTERPRISE.toString().equals(userTemp.getUserType())){
                paramTemp.put("orgId",userTemp.getOrgId());
            }
            if(OrgAndUserType.INDIVIDUAL.toString().equals(userTemp.getUserType())){
                paramTemp.put("userId",userTemp.getId());
            }
            for (String vId : vehicleIds) {
                if (WzStringUtil.isNotBlank(vId)) {
                    paramTemp.put("id",vId);
                    vehicleInfos = bizVehicleService.getByPrimaryKey(paramTemp, true);
                    if (null == vehicleInfos || 0 == vehicleInfos.size()) {
                        errMsgs.append("未查询到车辆[ID:" + vId + "]信息;");
                        continue;
                    } else {
                        for (Map<String,Object> vehicleInfo : vehicleInfos) {
                            batteryId = (String) vehicleInfo.get("batteryId");
                            batteryCode = (String) vehicleInfo.get("batteryCode");
                            if (WzStringUtil.isBlank(batteryCode)) {
                                errMsgs.append("未查询到车辆[ID:" + vId + "]对应的设备;");
                                continue;
                            }
                            // 根据设备ID查询设备当前位置
                            devicePk = batteryCode + WzConstants.KEY_SPLIT + DeviceType.BATTERY.toString();
                            locData = (JSONObject) redisClient.hashOperations().get(WzConstants.GK_DEVICE_LOC_MAP, devicePk);
                            // 组织返回结果
                            if (null == locData
//                    || null == locData.getString(DeviceApiConstants.KEY_LOC_TIME)
                                    || null == locData.getDouble(DeviceApiConstants.REQ_LAT)
                                    || null == locData.getDouble(DeviceApiConstants.REQ_LON)) {
                                errMsgs.append("未查询到车辆[ID:" + batteryCode + "]对应设备的定位信息;");
                                continue;
                            }
                            locData.put(DeviceApiConstants.REQ_RESP_VEHICLE_ID, vId);
                            locData.put(DeviceApiConstants.REQ_RESP_BATTERY_ID, batteryId);
                            locData.put(DeviceApiConstants.REQ_RESP_DEVICE_ID, batteryCode);
                            locData.put(DeviceApiConstants.REQ_DEVICE_TYPE, DeviceType.BATTERY.toString());
                            locDatas.add(locData);
                        }
                    }
                }
            }
            // 返回结果
            MessageResponse mr = null;
            if (0 == errMsgs.length()) {
                mr = new MessageResponse(RunningResult.SUCCESS, locDatas);
            } else {
                mr = new MessageResponse(RunningResult.NOT_FOUND.code(), errMsgs.toString(), locDatas);
            }
            return mr;
        }
    }

}
