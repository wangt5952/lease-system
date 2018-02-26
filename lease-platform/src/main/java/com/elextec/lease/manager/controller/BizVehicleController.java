package com.elextec.lease.manager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzGPSUtil;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.device.common.DeviceApiConstants;
import com.elextec.lease.manager.request.BizVehicleParam;
import com.elextec.lease.manager.request.LocAndRadiusParam;
import com.elextec.lease.manager.request.VehicleBatteryParam;
import com.elextec.lease.manager.service.BizVehicleService;
import com.elextec.persist.field.enums.DeviceType;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.field.enums.RecordStatus;
import com.elextec.persist.model.mybatis.BizVehicle;
import com.elextec.persist.model.mybatis.SysUser;
import com.elextec.persist.model.mybatis.ext.BizVehicleExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.*;

/**
 * 车辆管理Controller.
 * Created by wangtao on 2018/1/19.
 */
@RestController
@RequestMapping(path = "/manager/vehicle")
public class BizVehicleController extends BaseController {
    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(BizVehicleController.class);

    @Autowired
    private BizVehicleService bizVehicleService;

    /**
     * 查询车辆.
     * @param request 请求
     * @param paramAndPaging 分页参数JSON
     * <pre>
     *     {
     *         keyStr:查询关键字（非必填，模糊查询，可填写车辆编号、车辆型号、车辆品牌、车辆产地、生产商ID、生产商名）,
     *         vehicleStatus:车辆状态（非必填，包括NORMAL、FREEZE、INVALID）,
     *         needPaging:是否需要分页（仅为false时不需要分页，其余情况均需要分页）,
     *         currPage:当前页（needPaging不为false时必填）,
     *         pageSize:每页记录数（needPaging不为false时必填）
     *     }
     * </pre>
     * @return 车辆列表
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
    @RequestMapping(path = "/list")
    public MessageResponse list(@RequestBody String paramAndPaging, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(paramAndPaging)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
//            PageRequest pagingParam = null;
            BizVehicleParam pagingParam = null;
            try {
                String paramStr = URLDecoder.decode(paramAndPaging, "utf-8");
//                pagingParam = JSON.parseObject(paramStr, PageRequest.class);
                pagingParam = JSON.parseObject(paramStr, BizVehicleParam.class);
                if (null == pagingParam) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                // 仅needPaging标志为false时，不需要分页，其他情况均需要进行分页
                if (WzStringUtil.isNotBlank(pagingParam.getNeedPaging())
                        && "false".equals(pagingParam.getNeedPaging().toLowerCase())) {
                    pagingParam.setNeedPaging("false");
                } else {
                    if (null == pagingParam.getCurrPage() || null == pagingParam.getPageSize()) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "未获得分页参数");
                    }
                    SysUser userTemp = getPcLoginUserInfo(request);
                    if(userTemp != null){
                        //根据用户类型添加条件
                        //个人用户需要添加userId为条件
                        if(OrgAndUserType.INDIVIDUAL.toString().equals(getPcLoginUserInfo(request).getUserType())){
                            pagingParam.setUserId(getPcLoginUserInfo(request).getId());
                        }
                        //企业用户需要添加orgId为条件
                        if(OrgAndUserType.ENTERPRISE.toString().equals(getPcLoginUserInfo(request).getUserType())){
                            pagingParam.setOrgId(getPcLoginUserInfo(request).getOrgId());
                        }
                    }else{
                        return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                    }
                    pagingParam.setNeedPaging("true");
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
//            PageResponse<BizVehicle> vehiclePageResp = bizVehicleService.list(true, pagingParam);
            PageResponse<BizVehicleExt> vehiclePageResp = bizVehicleService.listExtByParam(Boolean.valueOf(pagingParam.getNeedPaging()), pagingParam);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, vehiclePageResp);
            return mr;
        }
    }

    /**
     * 批量增加车辆信息.（车辆与电池信息配对）.
     * @param addParams 车辆电池配对信息列表JSON
     * <pre>
     *     [{
     *          "bizVehicleInfo": {
     *               vehicleCode:车辆编号,
     *               vehiclePn:车辆型号,
     *               vehicleBrand:车辆品牌,
     *               vehicleMadeIn:车辆产地,
     *               mfrsId:生产商ID,
     *               vehicleStatus:车辆状态（正常、冻结、报废）,
     *               createUser:创建人,
     *               updateUser:更新人
     *
     *          },
     *          "flag": 有无电池flag("0"是新车配新电池信息,"1"是新车配旧电池信息,旧电池信息只带ID既可,"2"是只有车辆信息，电池信息不用传),
     *          "batteryInfo": {
     *               id:ID（仅flag为1时有效，其余电池项仅flag为0时有效）,
     *               batteryCode:电池编号,
     *               batteryName:电池货名,
     *               batteryBrand:电池品牌,
     *               batteryPn:电池型号,
     *               batteryParameters:电池参数,
     *               mfrsId:生产商ID,
     *               batteryStatus:电池状态（正常、冻结、作废）,
     *               createUser:创建人,
     *               updateUser:更新人
     *          }
     *    }]
     * </pre>
     * @return 批量新增结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(path = "/add")
    public MessageResponse add(@RequestBody String addParams) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(addParams)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<VehicleBatteryParam> vehicleInfos = null;
            try {
                String paramStr = URLDecoder.decode(addParams, "utf-8");
                vehicleInfos = JSON.parseArray(paramStr, VehicleBatteryParam.class);
                if (null == vehicleInfos || 0 == vehicleInfos.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                VehicleBatteryParam insVBChkVo = null;
                for (int i = 0; i < vehicleInfos.size(); i++) {
                    insVBChkVo = vehicleInfos.get(i);
                    if(WzStringUtil.isBlank(insVBChkVo.getFlag())){
                        return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "车辆信息参数有误");
                    }else{
                        if (WzStringUtil.isBlank(insVBChkVo.getBizVehicleInfo().getVehicleCode())
                                || null == insVBChkVo.getBizVehicleInfo().getVehicleStatus()
                                || WzStringUtil.isBlank(insVBChkVo.getBizVehicleInfo().getCreateUser())
                                || WzStringUtil.isBlank(insVBChkVo.getBizVehicleInfo().getUpdateUser())) {
                            return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "车辆信息参数有误");
                        }
                        if (!insVBChkVo.getBizVehicleInfo().getVehicleStatus().toString().equals(RecordStatus.FREEZE.toString())
                                && !insVBChkVo.getBizVehicleInfo().getVehicleStatus().toString().equals(RecordStatus.INVALID.toString())
                                && !insVBChkVo.getBizVehicleInfo().getVehicleStatus().toString().equals(RecordStatus.NORMAL.toString())) {
                            return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的车辆状态");
                        }
                        if("0".equals(insVBChkVo.getFlag())){
                            if (WzStringUtil.isBlank(insVBChkVo.getBatteryInfo().getBatteryCode())
                                    || null == insVBChkVo.getBatteryInfo().getBatteryStatus()
                                    || WzStringUtil.isBlank(insVBChkVo.getBatteryInfo().getCreateUser())
                                    || WzStringUtil.isBlank(insVBChkVo.getBatteryInfo().getUpdateUser())) {
                                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "车辆信息参数有误");
                            }
                            if (!insVBChkVo.getBatteryInfo().getBatteryStatus().toString().equals(RecordStatus.FREEZE.toString())
                                    && !insVBChkVo.getBatteryInfo().getBatteryStatus().toString().equals(RecordStatus.INVALID.toString())
                                    && !insVBChkVo.getBatteryInfo().getBatteryStatus().toString().equals(RecordStatus.NORMAL.toString())) {
                                return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的电池状态");
                            }
                        }
                        if("1".equals(insVBChkVo.getFlag())){
                            if (WzStringUtil.isBlank(insVBChkVo.getBatteryInfo().getId())) {
                                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "车辆信息参数有误");
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizVehicleService.insertVehicles(vehicleInfos);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 增加车辆信息.（车辆与电池信息配对）.
     * @param addParams 车辆电池配对信息列表JSON
     * <pre>
     *     {
     *          "bizVehicleInfo": {
     *               vehicleCode:车辆编号,
     *               vehiclePn:车辆型号,
     *               vehicleBrand:车辆品牌,
     *               vehicleMadeIn:车辆产地,
     *               mfrsId:生产商ID,
     *               vehicleStatus:车辆状态（正常、冻结、报废）,
     *               createUser:创建人,
     *               updateUser:更新人
     *
     *          },
     *          "flag": 有无电池flag("0"是新车配新电池信息,"1"是新车配旧电池信息,旧电池信息只带ID既可,"2"是只有车辆信息，电池信息不用传),
     *          "batteryInfo": {
     *               id:ID,
     *               batteryCode:电池编号,
     *               batteryName:电池货名,
     *               batteryBrand:电池品牌,
     *               batteryPn:电池型号,
     *               batteryParameters:电池参数,
     *               mfrsId:生产商ID,
     *               batteryStatus:电池状态（正常、冻结、作废）,
     *               createUser:创建人,
     *               updateUser:更新人
     *          }
     *    }
     * </pre>
     * @return 新增结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(path = "/addone")
    public MessageResponse addone(@RequestBody String addParams) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(addParams)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            VehicleBatteryParam vechcleInfo = null;
            try {
                String paramStr = URLDecoder.decode(addParams, "utf-8");
                vechcleInfo = JSON.parseObject(paramStr, VehicleBatteryParam.class);
                if (null == vechcleInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if(WzStringUtil.isBlank(vechcleInfo.getFlag())){
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "车辆信息参数有误");
                }else{
                    if (WzStringUtil.isBlank(vechcleInfo.getBizVehicleInfo().getVehicleCode())
                            || null == vechcleInfo.getBizVehicleInfo().getVehicleStatus()
                            || WzStringUtil.isBlank(vechcleInfo.getBizVehicleInfo().getCreateUser())
                            || WzStringUtil.isBlank(vechcleInfo.getBizVehicleInfo().getUpdateUser())) {
                        return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "车辆信息参数有误");
                    }
                    if (!vechcleInfo.getBizVehicleInfo().getVehicleStatus().toString().equals(RecordStatus.FREEZE.toString())
                            && !vechcleInfo.getBizVehicleInfo().getVehicleStatus().toString().equals(RecordStatus.INVALID.toString())
                            && !vechcleInfo.getBizVehicleInfo().getVehicleStatus().toString().equals(RecordStatus.NORMAL.toString())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的车辆状态");
                    }
                    if("0".equals(vechcleInfo.getFlag())){
                        if (WzStringUtil.isBlank(vechcleInfo.getBatteryInfo().getBatteryCode())
                                || null == vechcleInfo.getBatteryInfo().getBatteryStatus()
                                || WzStringUtil.isBlank(vechcleInfo.getBatteryInfo().getCreateUser())
                                || WzStringUtil.isBlank(vechcleInfo.getBatteryInfo().getUpdateUser())) {
                            return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "车辆信息参数有误");
                        }
                        if (!vechcleInfo.getBatteryInfo().getBatteryStatus().toString().equals(RecordStatus.FREEZE.toString())
                                && !vechcleInfo.getBatteryInfo().getBatteryStatus().toString().equals(RecordStatus.INVALID.toString())
                                && !vechcleInfo.getBatteryInfo().getBatteryStatus().toString().equals(RecordStatus.NORMAL.toString())) {
                            return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的电池状态");
                        }
                    }
                    if("1".equals(vechcleInfo.getFlag())){
                        if (WzStringUtil.isBlank(vechcleInfo.getBatteryInfo().getId())) {
                            return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "车辆信息参数有误");
                        }
                    }
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizVehicleService.insertVehicle(vechcleInfo);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 修改车辆信息.
     * @param modifyParam 车辆信息JSON
     * <pre>
     *     {
     *         id:ID,
     *         vehicleCode:车辆编号,
     *         vehiclePn:车辆型号,
     *         vehicleBrand:车辆品牌,
     *         vehicleMadeIn:车辆产地,
     *         mfrsId:生产商ID,
     *         vehicleStatus:车辆状态（正常、冻结、报废）,
     *         updateUser:更新人
     *     }
     * </pre>
     * @return 修改车辆信息返回
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(path = "/modify")
    public MessageResponse modifyUser(@RequestBody String modifyParam) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(modifyParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            BizVehicle vehicleInfo = null;
            try {
                String paramStr = URLDecoder.decode(modifyParam, "utf-8");
                vehicleInfo = JSON.parseObject(paramStr, BizVehicle.class);
                if (null == vehicleInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if (WzStringUtil.isBlank(vehicleInfo.getId())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无法确定待修改的记录");
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizVehicleService.updateVehicle(vehicleInfo);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 批量删除车辆.
     * @param deleteParam 待删除的车辆列表JSON
     * <pre>
     *     [ID1,ID2,......]
     * </pre>
     * @return 批量删除车辆返回
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(path = "/delete")
    public MessageResponse deleteUsers(@RequestBody String deleteParam) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(deleteParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> vehicleIds = null;
            try {
                String paramStr = URLDecoder.decode(deleteParam, "utf-8");
                vehicleIds = JSON.parseArray(paramStr, String.class);
                if (null == vehicleIds || 0 == vehicleIds.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizVehicleService.deleteVehicles(vehicleIds);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 根据ID获取车辆信息.
     * @param param 车辆ID以及是否查询在用电池
     * <pre>
     *     {
     *         id:车辆ID,
     *         flag:是否查询在用电池（true是查询在用电池，false是查询全部电池）
     *     }
     * </pre>
     * @return 根据ID获取车辆信息返回
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:[{
     *                 vehicleId:ID,
     *                 vehicleCode:车辆编号,
     *                 vehiclePn:车辆型号,
     *                 vehicleBrand:车辆品牌,
     *                 vehicleMadeIn:车辆产地,
     *                 vehicleMfrsId:车辆生产商ID,
     *                 vehicleStatus:车辆状态（正常、冻结、报废）,
     *                 vehicleMfrsName:车辆生产商名称,
     *                 batteryId:电池ID,
     *                 batteryCode:电池编号,
     *                 batteryName:电池货名,
     *                 batteryBrand:电池品牌,
     *                 batteryPn:电池型号,
     *                 batteryParameters:电池参数,
     *                 batteryMfrsId:电池生产商ID,
     *                 batteryMfrsName:电池生产商名称,
     *                 batteryStatus:电池状态（正常、冻结、作废）,
     *                 createUser:创建人,
     *                 createTime:创建时间,
     *                 updateUser:更新人,
     *                 updateTime:更新时间
     *             },
     *             .....
     *             ]
     *       }
     *
     * </pre>
     */
    @RequestMapping(path = "/getbypk")
    public MessageResponse getByPK(@RequestBody String param) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(param)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            Map<String,Object> paramMap = null;
            try {
                String paramStr = URLDecoder.decode(param, "utf-8");
                paramMap = JSON.parseObject(paramStr, Map.class);
                if (null == paramMap || 0 == paramMap.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if (WzStringUtil.isBlank((String) paramMap.get("id"))) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "查询条件不能为空");
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            String isUsedBatteryStr = (String) paramMap.get("flag");
            Boolean isUsedBattery = true;
            if (WzStringUtil.isNotBlank(isUsedBatteryStr)
                    && ("true".equals(isUsedBatteryStr.toLowerCase()) || "false".equals(isUsedBatteryStr.toLowerCase()))) {
                isUsedBattery = Boolean.valueOf(isUsedBatteryStr);
            }
            List<Map<String,Object>> vehicleInfo = bizVehicleService.getByPrimaryKey((String) paramMap.get("id"), isUsedBattery);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, vehicleInfo);
            return mr;
        }
    }

    /**
     * 根据车辆ID列表获取车辆信息列表.
     * @param ids 车辆ID列表
     * <pre>
     *     [id1,id2,...]
     * </pre>
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
    public MessageResponse getLocByVehiclePK(@RequestBody String ids) {
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
            for (String vId : vehicleIds) {
                if (WzStringUtil.isNotBlank(vId)) {
                    vehicleInfos = bizVehicleService.getByPrimaryKey(vId, true);
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
     * 根据车辆ID列表获取设备电量信息列表.
     * @param ids 车辆ID列表
     * <pre>
     *     [id1,id2,...]
     * </pre>
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
    public MessageResponse getPowerByVehiclePK(@RequestBody String ids) {
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
            for (String vId : vehicleIds) {
                if (WzStringUtil.isNotBlank(vId)) {
                    vehicleInfos = bizVehicleService.getByPrimaryKey(vId, true);
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
     * 车辆与电池绑定接口.
     * @param vehicleIdAndBatteryId 车辆ID与电池
     * <pre>
     *     {
     *         vehicleId:车辆ID,
     *         batteryId:电池ID
     *     }
     * </pre>
     * @return 查询结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(path = "/batterybind")
    public MessageResponse batteryBind(@RequestBody String vehicleIdAndBatteryId) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(vehicleIdAndBatteryId)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            Map<String,String> param = null;
            try {
                String paramStr = URLDecoder.decode(vehicleIdAndBatteryId, "utf-8");
                param = JSON.parseObject(paramStr, Map.class);
                if (null == param || 0 == param.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if (WzStringUtil.isBlank((String) param.get("vehicleId")) || WzStringUtil.isBlank((String) param.get("batteryId"))) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "查询条件不能为空");
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizVehicleService.bind(param.get("vehicleId"), param.get("batteryId"));
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 车辆与电池解绑接口.
     * @param vehicleIdAndBatteryId 用户ID与车辆ID
     * <pre>
     *     {
     *         vehicleId:车辆ID,
     *         batteryId:电池ID
     *     }
     * </pre>
     * @return 查询结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(path = "/batteryunbind")
    public MessageResponse batteryUnbind(@RequestBody String vehicleIdAndBatteryId) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(vehicleIdAndBatteryId)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            Map<String,String> param = null;
            try {
                String paramStr = URLDecoder.decode(vehicleIdAndBatteryId, "utf-8");
                param = JSON.parseObject(paramStr, Map.class);
                if (null == param || 0 == param.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if (WzStringUtil.isBlank((String) param.get("vehicleId")) || WzStringUtil.isBlank((String) param.get("batteryId"))) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "解绑参数不能为空");
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizVehicleService.unBind(param.get("vehicleId"), param.get("batteryId"));
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 根据定位点及半径获得范围内的所有车辆信息及定位、电池电量数据.
     * @param locAndRadius 定位点及半径参数
     * <pre>
     *     {
     *         lat:纬度,
     *         lng:经度,
     *         radius:半径(米)
     *     }
     * </pre>
     * @return 车辆信息及定位、电池电量信息列表
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:[
     *             {
     *                 batteryId:电池ID,
     *                 batteryCode:电池编码,
     *                 vehicleId:车辆ID,
     *                 vehicleCode:车辆编码,
     *                 vehiclePn:车辆型号,
     *                 vehicleBrand:车辆品牌,
     *                 vehicleMadeIn:车辆产地,
     *                 mfrsId:生产商ID,
     *                 vehicleStatus:车辆状态（正常、冻结、报废）,
     *                 createUser:创建人,
     *                 createTime:创建时间,
     *                 updateUser:更新人,
     *                 updateTime:更新时间,
     *                 mfrsName:生产商名,
     *                 RSOC:电池剩余容量百分比,
     *                 LAT:纬度,
     *                 LON:经度,
     *                 KEY_LOC_TIME:定位记录时间
     *             },
     *             ... ...
     *         ]
     *     }
     * </pre>
     */
    @RequestMapping(path = "/listvehiclesbylocandradius")
    public MessageResponse listVehiclesByLocAndRadius(@RequestBody String locAndRadius) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(locAndRadius)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            LocAndRadiusParam locAndRadiusParam = null;
            try {
                String paramStr = URLDecoder.decode(locAndRadius, "utf-8");
                locAndRadiusParam = JSON.parseObject(paramStr, LocAndRadiusParam.class);
                if (null == locAndRadiusParam) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if (null ==  locAndRadiusParam.getLat()
                        || null == locAndRadiusParam.getLng()) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "定位点信息异常");
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            if (null == locAndRadiusParam.getRadius()) {
                locAndRadiusParam.setRadius(new Double(1000));
            }
            // 将中心点转换为GPS坐标
            double[] wgsLatLng = WzGPSUtil.bd2wgs(locAndRadiusParam.getLat(), locAndRadiusParam.getLng());
            // 获得所有在线设备定位信息
            Set<String> deviceKeys = redisClient.hashOperations().keys(WzConstants.GK_DEVICE_LOC_MAP);
            // 循环所有在线设备判断并获得所有在范围内的设备
            List<String> deviceCds = new ArrayList<String>();
            Map<String, JSONObject> deviceLocsCache = new HashMap<String, JSONObject>();
            JSONObject dLoc = null;
            for (String deviceKey : deviceKeys) {
                dLoc = (JSONObject) redisClient.hashOperations().get(WzConstants.GK_DEVICE_LOC_MAP, deviceKey);
                double dist = WzGPSUtil.calcDistanceByM(locAndRadiusParam.getLat(), locAndRadiusParam.getLng(), dLoc.getDouble(DeviceApiConstants.REQ_LAT), dLoc.getDouble(DeviceApiConstants.REQ_LON));
                if (dist < locAndRadiusParam.getRadius()) {
                    deviceLocsCache.put(dLoc.getString(DeviceApiConstants.REQ_RESP_DEVICE_ID), dLoc);
                    deviceCds.add(dLoc.getString(DeviceApiConstants.REQ_RESP_DEVICE_ID));
                }
            }
            // 查询所有范围内的车辆信息
            List<Map<String, Object>> vehicles = bizVehicleService.listByBatteryCode(deviceCds);
            // 循环每个车辆信息并装配位置信息及电量信息
            JSONObject powerInfo = null;
            for (Map<String, Object> vh : vehicles) {
                vh.put(DeviceApiConstants.REQ_LAT, deviceLocsCache.get(vh.get("batteryCode")).getDouble(DeviceApiConstants.REQ_LAT));
                vh.put(DeviceApiConstants.REQ_LON, deviceLocsCache.get(vh.get("batteryCode")).getDouble(DeviceApiConstants.REQ_LON));
                powerInfo = (JSONObject) redisClient.hashOperations().get(WzConstants.GK_DEVIE_POWER_MAP, vh.get("batteryCode") + WzConstants.KEY_SPLIT + DeviceType.BATTERY.toString());
                if (null == powerInfo) {
                    vh.put(DeviceApiConstants.REQ_RSOC, "");
                } else {
                    vh.put(DeviceApiConstants.REQ_RSOC, powerInfo.getString(DeviceApiConstants.REQ_RSOC));
                }
            }
            // 返回结果
            return new MessageResponse(RunningResult.SUCCESS, vehicles);
        }
    }
}
