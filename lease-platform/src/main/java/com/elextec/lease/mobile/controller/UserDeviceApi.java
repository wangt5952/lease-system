package com.elextec.lease.mobile.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.common.request.ModifyPasswordParam;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.utils.WzGPSUtil;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.device.common.DeviceApiConstants;
import com.elextec.lease.manager.service.BizVehicleService;
import com.elextec.lease.manager.service.BizVehicleTrackService;
import com.elextec.lease.manager.service.SysAuthService;
import com.elextec.lease.manager.service.SysUserService;
import com.elextec.lease.model.BizVehicleBatteryParts;
import com.elextec.persist.field.enums.DeviceType;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.model.mybatis.BizVehicleTrack;
import com.elextec.persist.model.mybatis.SysUser;
import com.elextec.persist.model.mybatis.ext.BizPartsExt;
import com.elextec.persist.model.mybatis.ext.SysUserExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    private SysAuthService sysAuthService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private BizVehicleTrackService bizVehicleTrackService;


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

    /**
     * 根据ID获取车辆电池信息.
     *
     * @param param   车辆ID以及是否查询在用电池
     *                <pre>
     *                    {
     *                        id:车辆ID,
     *                        flag:是否查询在用电池（true是查询在用电池，false是查询全部电池）
     *                    }
     *                </pre>
     * @param request HttpServletRequest
     * @return 根据ID获取车辆信息返回
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:{
     *                 vehicleId:ID,
     *                 vehicleCode:车辆编号,
     *                 vehiclePn:车辆型号,
     *                 vehicleBrand:车辆品牌,
     *                 vehicleMadeIn:车辆产地,
     *                 vehicleMfrsId:车辆生产商ID,
     *                 vehicleStatus:车辆状态（正常、冻结、报废）,
     *                 vehicleMfrsName:车辆生产商名称,
     *                 bizBatteries:[
     *                     {
     *                         batteryId:电池ID,
     *                         batteryCode:电池编号,
     *                         batteryName:电池货名,
     *                         batteryBrand:电池品牌,
     *                         batteryPn:电池型号,
     *                         batteryParameters:电池参数,
     *                         batteryMfrsId:电池生产商ID,
     *                         batteryMfrsName:电池生产商名称,
     *                         batteryStatus:电池状态（正常、冻结、作废）
     *                         bindTime:绑定时间,
     *                         unbindTime:解绑时间
     *                     }
     *                     ....
     *                 ]
     *                 createUser:创建人,
     *                 createTime:创建时间,
     *                 updateUser:更新人,
     *                 updateTime:更新时间
     *             }
     *       }
     *
     * </pre>
     */
    @RequestMapping(path = "/getbypk")
    public MessageResponse getByPK(@RequestBody String param, HttpServletRequest request){
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(param)) {
            return new MessageResponse(RunningResult.NO_PARAM);
        } else {
            // 参数解析错误报“参数解析错误”
            Map<String, Object> paramMap = null;
            try {
                String paramStr = URLDecoder.decode(param, "utf-8");
                paramMap = JSON.parseObject(paramStr, Map.class);
                if (null == paramMap || 0 == paramMap.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if (WzStringUtil.isBlank((String) paramMap.get("id"))) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "查询条件不能为空");
                }
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            String isUsedBatteryStr = (String) paramMap.get("flag");
            Boolean isUsedBattery = true;
            if (WzStringUtil.isNotBlank(isUsedBatteryStr) && ("true".equals(isUsedBatteryStr.toLowerCase()) || "false".equals(isUsedBatteryStr.toLowerCase()))) {
                isUsedBattery = Boolean.valueOf(isUsedBatteryStr);
            }
            SysUser userTemp = getLoginUserInfo(request);
            if (userTemp == null) {
                return new MessageResponse(RunningResult.AUTH_OVER_TIME);
            }
            if (OrgAndUserType.ENTERPRISE.toString().equals(userTemp.getUserType().toString())) {
                paramMap.put("orgId", userTemp.getOrgId());
            }
            if (OrgAndUserType.INDIVIDUAL.toString().equals(userTemp.getUserType().toString())) {
                paramMap.put("userId", userTemp.getId());
            }
            // 组织返回结果并返回
            return new MessageResponse(RunningResult.SUCCESS, bizVehicleService.queryBatteryInfoByVehicleId(paramMap, isUsedBattery));
        }
    }

    /**
     * <pre>
     *     {
     *         id:当前用户登录id
     *     }
     * </pre>
     *
     * @param sysUserId 登录用户
     * @param request   HttpServletRequest
     * @return 根据当前登录用户查询当前用户下的所有车辆
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:[
     *             {
     *                 id:ID,
     *                 vehicleCode:车辆编号,
     *                 vehiclePn:车辆型号,
     *                 vehicleBrand:车辆品牌,
     *                 vehicleMadeIn:车辆产地,
     *                 mfrsId:生产商ID,
     *                 mfrsName:生产商名,
     *                 vehicleStatus:车辆状态（正常、冻结、报废）,
     *                 batteryId:绑定该车辆的电池ID（不为NULL就是已绑定，NULL就是未绑定）
     *                 createUser:创建人,
     *                 createTime:创建时间,
     *                 updateUser:更新人,
     *                 updateTime:更新时间
     *             },
     *             ... ...
     *         ]
     *     }
     * </pre>
     */
    @RequestMapping(value = "/getVehicleByUserId", method = RequestMethod.POST)
    public MessageResponse getVehicleByUserId(@RequestBody String sysUserId, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(sysUserId)) {
            return new MessageResponse(RunningResult.NO_PARAM);
        } else {
            // 参数解析错误报“参数解析错误”
            Map<String, Object> map = null;
            try {
                String paramStr = URLDecoder.decode(sysUserId, "utf-8");
                map = JSONObject.parseObject(paramStr, Map.class);
                if (WzStringUtil.isBlank(map.get("id").toString())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                //获取登录对象的信息
                SysUser userTemp = getLoginUserInfo(request);
                if (userTemp == null) {
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                //判断登录用户的类型
                if (userTemp.getUserType().equals(OrgAndUserType.PLATFORM)) {
                    return new MessageResponse(RunningResult.SUCCESS, bizVehicleService.getVehicleByUserId(map.get("id").toString(), null));
                } else if (userTemp.getUserType().equals(OrgAndUserType.INDIVIDUAL)) {
                    if (userTemp.getId().equals(map.get("id"))) {
                        return new MessageResponse(RunningResult.SUCCESS, bizVehicleService.getVehicleByUserId(map.get("id").toString(), null));
                    } else {
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                } else if (userTemp.getUserType().equals(OrgAndUserType.ENTERPRISE)) {
                    return new MessageResponse(RunningResult.SUCCESS, bizVehicleService.getVehicleByUserId(map.get("id").toString(), userTemp.getOrgId()));
                } else {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR.code(), ex.getMessage(), ex);
            }
        }
    }

    /**
     * 根据ID获取车辆配件信息.
     *
     * @param param   车辆ID
     *                <pre>
     *                [id]
     *                </pre>
     * @param request HttpServletRequest
     * @return 根据ID获取车辆信息返回
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:[{
     *                 id:ID,
     *                 partsCode:配件编码,
     *                 partsName:配件货名,
     *                 partsBrand:配件品牌,
     *                 partsPn:配件型号,
     *                 partsType:配件类别（车座、车架、车把、车铃、轮胎、脚蹬、仪表盘）,
     *                 partsParameters:配件参数,
     *                 mfrsId:生产商ID,
     *                 mfrsName:生产商名,
     *                 partsStatus:配件状态（正常、冻结、作废）,
     *                 createUser:创建人,
     *                 createTime:创建时间,
     *                 updateUser:更新人,
     *                 updateTime:更新时间
     *                 bindTime:绑定时间,
     *                 unbindTime:解绑时间
     *               },
     *               ....
     *           ]
     *       }
     *
     *
     * </pre>
     */
    @RequestMapping(path = "/getbypr")
    public MessageResponse getByPR(@RequestBody String param, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(param)) {
            return new MessageResponse(RunningResult.NO_PARAM);
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> paramMap = null;
            try {
                String paramStr = URLDecoder.decode(param, "utf-8");
                paramMap = JSON.parseArray(paramStr, String.class);
                if (null == paramMap || 0 == paramMap.size()) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "查询条件不能为空");
                }
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            SysUser userTemp = getLoginUserInfo(request);
            if (userTemp == null) {
                return new MessageResponse(RunningResult.AUTH_OVER_TIME);
            }
            Map<String, Object> paramTemp = new HashMap<String, Object>();
            paramTemp.put("id", paramMap.get(0));
            if (OrgAndUserType.ENTERPRISE.toString().equals(userTemp.getUserType().toString())) {
                paramTemp.put("orgId", userTemp.getOrgId());
            }
            if (OrgAndUserType.INDIVIDUAL.toString().equals(userTemp.getUserType().toString())) {
                paramTemp.put("userId", userTemp.getId());
            }
            // 组织返回结果并返回
            return new MessageResponse(RunningResult.SUCCESS, bizVehicleService.getBizPartsByVehicle(paramTemp));
        }
    }

    /**
     * 修改密码.
     * @param request 请求
     * @param oldAndNewPassword 旧密码及新密码
     * <pre>
     *     {
     *          oldAuthStr:同登录，旧密码验证字符串 MD5(loginName(登录用户名)+MD5(登录密码).upper()+authTime).upper(),
     *          authTime:验证时间,
     *          newPassword:新密码
     *     }
     * </pre>
     * @return 密码修改结果
     * <pre>
     *     {
     *          code:处理Code,
     *          message:处理消息,
     *          respData:""
     *     }
     * </pre>
     */
    @RequestMapping(path = {"/modifypassword"})
    public MessageResponse modifyPassword(@RequestBody String oldAndNewPassword, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(oldAndNewPassword)) {
            return new MessageResponse(RunningResult.NO_PARAM);
        } else {
            // 参数解析错误报“参数解析错误”
            ModifyPasswordParam modifyPasswordParam = null;
            try {
                String paramStr = URLDecoder.decode(oldAndNewPassword, "utf-8");
                modifyPasswordParam = JSON.parseObject(paramStr, ModifyPasswordParam.class);
                if (null == modifyPasswordParam
                        || WzStringUtil.isBlank(modifyPasswordParam.getNewPassword())
                        || WzStringUtil.isBlank(modifyPasswordParam.getOldAuthStr())
                        || null == modifyPasswordParam.getAuthTime()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                SysUserExt sue = getLoginUserInfo(request);
                // 验证用户并返回用户信息
                if (sysAuthService.verifyUser(sue.getLoginName(), sue.getPassword(), modifyPasswordParam.getOldAuthStr(), modifyPasswordParam.getAuthTime())) {
                    SysUser updateVo = new SysUser();
                    updateVo.setId(sue.getId());
                    updateVo.setPassword(modifyPasswordParam.getNewPassword());
                    updateVo.setUpdateUser(sue.getId());
                    sysUserService.updatePassword(updateVo);
                }
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }

        }
        // 组织返回结果并返回
        return new MessageResponse(RunningResult.SUCCESS);
    }

    /**
     * 根据车辆ID以及时间区间获取车辆轨迹信息
     *
     * @param idAndTimeInterval 车辆ID与时间区间
     *                          <pre>
     *                              {
     *                                  id:车辆ID,
     *                                  startTime:开始时间,
     *                                  endTime:结束时间
     *                              }
     *                          </pre>
     * @param request           HttpServletRequest
     * @return 根据车辆ID和时间区间获取车辆的轨迹信息
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:[
     *             {
     *                 LocTime:记录时间,
     *                 LAT:纬度,
     *                 LON:经度,
     *                 StayTime:停留时间
     *             },
     *             ... ...
     *         ]
     *     }
     * </pre>
     */
    @RequestMapping(path = "/gettrackbytime")
    public MessageResponse getTrackByTime(@RequestBody String idAndTimeInterval, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(idAndTimeInterval)) {
            return new MessageResponse(RunningResult.NO_PARAM);
        } else {
            // 参数解析错误报“参数解析错误”
            Map<String, String> idAndTimeIntervalInfo = null;
            long startTime = 0;
            long endTime = 0;
            try {
                String paramStr = URLDecoder.decode(idAndTimeInterval, "utf-8");
                idAndTimeIntervalInfo = JSON.parseObject(paramStr, Map.class);
                if (null == idAndTimeIntervalInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if (null == idAndTimeIntervalInfo.get("id")) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "车辆ID参数不能为空");
                }
                if (null == idAndTimeIntervalInfo.get("startTime")
                        || null == idAndTimeIntervalInfo.get("endTime")) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "时间区间参数不能为空");
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                startTime = sdf.parse(idAndTimeIntervalInfo.get("startTime")).getTime();
                endTime = sdf.parse(idAndTimeIntervalInfo.get("endTime")).getTime();
                //判定时间参数的正确性
                if (startTime == 0 || endTime == 0 || startTime > endTime) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "时间区间参数异常");
                }
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 循环获得车辆电池信息并获得定位信息
            List<Map<String, Object>> vehicleInfos = null;
            String batteryId = null;
            String batteryCode = null;
            String devicePk = null;
            List<JSONObject> locDatas = new ArrayList<JSONObject>();
            StringBuffer errMsgs = new StringBuffer("");
            if (WzStringUtil.isNotBlank(idAndTimeIntervalInfo.get("id"))) {
                SysUser userTemp = getLoginUserInfo(request);
                if (userTemp == null) {
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                Map<String, Object> paramTemp = new HashMap<String, Object>();
                if (OrgAndUserType.ENTERPRISE.toString().equals(userTemp.getUserType().toString())) {
                    paramTemp.put("orgId", userTemp.getOrgId());
                }
                if (OrgAndUserType.INDIVIDUAL.toString().equals(userTemp.getUserType().toString())) {
                    paramTemp.put("userId", userTemp.getId());
                }
                paramTemp.put("id", idAndTimeIntervalInfo.get("id"));
                vehicleInfos = bizVehicleService.getByPrimaryKey(paramTemp, true);
                if (null == vehicleInfos || 0 == vehicleInfos.size()) {
                    errMsgs.append("未查询到车辆[ID:" + idAndTimeIntervalInfo.get("id") + "]信息;");
                } else {
                    for (Map<String, Object> vehicleInfo : vehicleInfos) {
                        batteryId = (String) vehicleInfo.get("batteryId");
                        batteryCode = (String) vehicleInfo.get("batteryCode");
                        if (WzStringUtil.isBlank(batteryCode)) {
                            errMsgs.append("未查询到车辆[ID:" + idAndTimeIntervalInfo.get("id") + "]对应的设备;");
                        }
                        // 根据设备ID查询设备当前位置
                        devicePk = batteryCode + WzConstants.KEY_SPLIT + DeviceType.BATTERY.toString();
                        // 轨迹信息
                        String trackKey = WzConstants.GK_DEVICE_TRACK + devicePk;

                        List<JSONObject> locListTemp = new ArrayList<JSONObject>();

                        //获取数据库中时间区间内的数据
                        List<BizVehicleTrack> locList = bizVehicleTrackService.getVehicleTracksByTime(batteryCode, DeviceType.BATTERY.toString(), startTime, endTime);
                        //转换封装数据库的数据
                        if (null != locList && locList.size() > 0) {
                            for (int i = 0; i < locList.size(); i++) {
                                //将数据分割
                                String[] locByte = locList.get(i).getLocations().split(";");
                                if (null != locByte && locByte.length > 0) {
                                    for (int j = 0; j < locByte.length; j++) {
                                        String[] locInfo = locByte[j].split(",");
                                        //判断参数格式是否正确
                                        if (locInfo.length == 3) {
                                            //判断数据是否在时间区间内
                                            if (Long.valueOf(locInfo[0]) >= startTime && Long.valueOf(locInfo[0]) <= endTime) {
                                                JSONObject temp = new JSONObject();
                                                temp.put(DeviceApiConstants.KEY_LOC_TIME, Long.valueOf(locInfo[0]));
                                                //将WGS坐标系转换为BD坐标
                                                double[] douTemp = WzGPSUtil.wgs2bd(Double.valueOf(locInfo[1]), Double.valueOf(locInfo[2]));
                                                temp.put(DeviceApiConstants.REQ_LAT, douTemp[0]);
                                                temp.put(DeviceApiConstants.REQ_LON, douTemp[1]);
                                                locListTemp.add(temp);
                                            }
                                        } else {
                                            errMsgs.append("车辆[ID:" + idAndTimeIntervalInfo.get("id") + "]对应的轨迹参数格式有误;");
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        // 获得缓存中在时间区间内的数据
                        Set<Object> lastLocSet = redisClient.zsetOperations().rangeByScore(trackKey, startTime, endTime);
                        //转换封装缓存中的数据
                        if (null != lastLocSet && lastLocSet.size() > 0) {
                            List<Object> lastLocLs = new ArrayList<Object>(lastLocSet);
                            for (int i = 0; i < lastLocLs.size(); i++) {
                                JSONObject locVo = (JSONObject) lastLocLs.get(i);
                                locListTemp.add(locVo);
                            }
                        }

                        //计算点与点之间的停留时间重新封装LIST
                        if (locListTemp.size() > 1) {
                            for (int i = 0; i < locListTemp.size(); i++) {
                                if (i + 1 < locListTemp.size()) {
                                    JSONObject temp = new JSONObject();
                                    temp.put(DeviceApiConstants.KEY_LOC_TIME, locListTemp.get(i).getLongValue(DeviceApiConstants.KEY_LOC_TIME));
                                    //将WGS坐标系转换为BD坐标
                                    double[] douTemp = WzGPSUtil.wgs2bd(locListTemp.get(i).getDoubleValue(DeviceApiConstants.REQ_LAT), locListTemp.get(i).getDoubleValue(DeviceApiConstants.REQ_LON));
                                    temp.put(DeviceApiConstants.REQ_LAT, douTemp[0]);
                                    temp.put(DeviceApiConstants.REQ_LON, douTemp[1]);
                                    long residenceTime = locListTemp.get(i + 1).getLongValue(DeviceApiConstants.KEY_LOC_TIME) - locListTemp.get(i).getLongValue(DeviceApiConstants.KEY_LOC_TIME);
                                    temp.put(DeviceApiConstants.KEY_STAY_TIME, residenceTime);
                                    locDatas.add(temp);
                                } else {
                                    JSONObject temp = new JSONObject();
                                    temp.put(DeviceApiConstants.KEY_LOC_TIME, locListTemp.get(i).getLongValue(DeviceApiConstants.KEY_LOC_TIME));
                                    //将WGS坐标系转换为BD坐标
                                    double[] douTemp = WzGPSUtil.wgs2bd(locListTemp.get(i).getDoubleValue(DeviceApiConstants.REQ_LAT), locListTemp.get(i).getDoubleValue(DeviceApiConstants.REQ_LON));
                                    temp.put(DeviceApiConstants.REQ_LAT, douTemp[0]);
                                    temp.put(DeviceApiConstants.REQ_LON, douTemp[1]);
                                    locDatas.add(temp);
                                }
                            }
                        }
                    }
                }
            }
            // 返回结果
            if (0 == errMsgs.length()) {
                return new MessageResponse(RunningResult.SUCCESS, locDatas);
            } else {
                return new MessageResponse(RunningResult.NOT_FOUND.code(), errMsgs.toString());
            }
        }
    }

}
