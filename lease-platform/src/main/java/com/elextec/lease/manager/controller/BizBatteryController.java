package com.elextec.lease.manager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.device.common.DeviceApiConstants;
import com.elextec.lease.manager.request.BizBatteryParam;
import com.elextec.lease.manager.service.BizBatteryService;
import com.elextec.persist.field.enums.DeviceType;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.field.enums.RecordStatus;
import com.elextec.persist.model.mybatis.BizBattery;
import com.elextec.persist.model.mybatis.SysUser;
import com.elextec.persist.model.mybatis.ext.BizBatteryExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 电池管理Controller.
 * Created by wangtao on 2018/1/19.
 */
@RestController
@RequestMapping(path = "/manager/battery")
public class BizBatteryController extends BaseController {

    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(BizBatteryController.class);

    @Autowired
    private BizBatteryService bizBatteryService;

    /**
     * 查询电池.
     * @param request 请求
     * @param paramAndPaging 查询及分页参数JSON
     * <pre>
     *     {
     *         keyStr:查询关键字（非必填，模糊查询，可填写电池编号、电池货名、电池品牌、电池型号、电池参数、生产商ID、生产商名）,
     *         batteryStatus:电池状态（非必填，包括NORMAL、FREEZE、INVALID）,
     *         needPaging:是否需要分页（仅为false时不需要分页，其余情况均需要分页）,
     *         currPage:当前页（needPaging不为false时必填）,
     *         pageSize:每页记录数（needPaging不为false时必填）
     *     }
     * </pre>
     * @return 查询结果列表
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:[
     *             {
     *                 id:ID,
     *                 batteryCode:电池编号,
     *                 batteryName:电池货名,
     *                 batteryBrand:电池品牌,
     *                 batteryPn:电池型号,
     *                 batteryParameters:电池参数,
     *                 mfrsId:生产商ID,
     *                 mfrsName:生产商名,
     *                 batteryStatus:电池状态（正常、冻结、作废）,
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
            BizBatteryParam pagingParam = null;
            try {
                String paramStr = URLDecoder.decode(paramAndPaging, "utf-8");
//                pagingParam = JSON.parseObject(paramStr, PageRequest.class);
                pagingParam = JSON.parseObject(paramStr, BizBatteryParam.class);
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
                        return new MessageResponse(RunningResult.AUTH_OVER_TIME.code(),"登录信息已失效");
                    }
                    pagingParam.setNeedPaging("true");
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR.code(), ex.getMessage(), ex);
            }
//            PageResponse<BizBattery> batteryPageResp = bizBatteryService.list(Boolean.valueOf(pagingParam.getNeedPaging()), pagingParam);
            PageResponse<BizBatteryExt> batteryPageResp = bizBatteryService.listExtByParam(Boolean.valueOf(pagingParam.getNeedPaging()), pagingParam);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, batteryPageResp);
            return mr;
        }
    }

    /**
     * 批量增加电池.
     * @param addParam 批量新增参数列表JSON
     * <pre>
     *     [
     *         {
     *              batteryCode:电池编号,
     *              batteryName:电池货名,
     *              batteryBrand:电池品牌,
     *              batteryPn:电池型号,
     *              batteryParameters:电池参数,
     *              mfrsId:生产商ID,
     *              batteryStatus:电池状态（正常、冻结、作废）,
     *              createUser:创建人,
     *              updateUser:更新人
     *         }
     *     ]
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
    public MessageResponse add(@RequestBody String addParam) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(addParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<BizBattery> batteryInfos = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                batteryInfos = JSON.parseArray(paramStr, BizBattery.class);
                if (null == batteryInfos || 0 == batteryInfos.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                BizBattery insResChkVo = null;
                for (int i = 0; i < batteryInfos.size(); i++) {
                    insResChkVo = batteryInfos.get(i);
                    if (WzStringUtil.isBlank(insResChkVo.getBatteryCode())
                            || null == insResChkVo.getBatteryStatus()
                            || WzStringUtil.isBlank(insResChkVo.getCreateUser())
                            || WzStringUtil.isBlank(insResChkVo.getUpdateUser())) {
                        return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "电池信息参数有误");
                    }
                    if (!insResChkVo.getBatteryStatus().toString().equals(RecordStatus.FREEZE.toString())
                            && !insResChkVo.getBatteryStatus().toString().equals(RecordStatus.INVALID.toString())
                            && !insResChkVo.getBatteryStatus().toString().equals(RecordStatus.NORMAL.toString())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的电池状态");
                    }
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizBatteryService.insertBatterys(batteryInfos);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 增加电池.
     * @param addParam 新增参数列表JSON
     * <pre>
     *     {
     *         batteryCode:电池编号,
     *         batteryName:电池货名,
     *         batteryBrand:电池品牌,
     *         batteryPn:电池型号,
     *         batteryPrameters:电池参数,
     *         mfrsId:生产商ID,
     *         batteryStatus:电池状态（正常、冻结、作废）,
     *         createUser:创建人,
     *         updateUser:更新人
     *     }
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
    public MessageResponse addOne(@RequestBody String addParam) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(addParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            BizBattery batteryInfo = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                batteryInfo = JSON.parseObject(paramStr, BizBattery.class);
                if (null == batteryInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if (WzStringUtil.isBlank(batteryInfo.getBatteryCode())
                        || null == batteryInfo.getBatteryStatus()
                        || WzStringUtil.isBlank(batteryInfo.getCreateUser())
                        || WzStringUtil.isBlank(batteryInfo.getUpdateUser())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "电池信息参数有误");
                }
                if (!batteryInfo.getBatteryStatus().toString().equals(RecordStatus.FREEZE.toString())
                        && !batteryInfo.getBatteryStatus().toString().equals(RecordStatus.INVALID.toString())
                        && !batteryInfo.getBatteryStatus().toString().equals(RecordStatus.NORMAL.toString())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的电池状态");
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizBatteryService.insertBattery(batteryInfo);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 修改电池信息.
     * @param modifyParam 修改参数JSON
     * <pre>
     *     {
     *         id:ID（必填）,
     *         batteryCode:电池编号,
     *         batteryName:电池货名,
     *         batteryBrand:电池品牌,
     *         batteryPn:电池型号,
     *         batteryParameters:电池参数,
     *         mfrsId:生产商ID,
     *         batteryStatus:电池状态（正常、冻结、作废）,
     *         updateUser:更新人
     *     }
     * </pre>
     * @return 修改结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(path = "/modify")
    public MessageResponse modify(@RequestBody String modifyParam) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(modifyParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            BizBattery batteryInfo = null;
            try {
                String paramStr = URLDecoder.decode(modifyParam, "utf-8");
                batteryInfo = JSON.parseObject(paramStr, BizBattery.class);
                if (null == batteryInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if (WzStringUtil.isBlank(batteryInfo.getId())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "无法确定待修改的记录");
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizBatteryService.updateBattery(batteryInfo);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 批量删除电池.
     * @param deleteParam 删除ID列表JSON
     * <pre>
     *     [ID1,ID2,......]
     * </pre>
     * @return 批量删除结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(path = "/delete")
    public MessageResponse delete(@RequestBody String deleteParam) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(deleteParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> batteryIds = null;
            try {
                String paramStr = URLDecoder.decode(deleteParam, "utf-8");
                batteryIds = JSON.parseArray(paramStr, String.class);
                if (null == batteryIds || 0 == batteryIds.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizBatteryService.deleteBattery(batteryIds);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 根据ID获取电池信息.
     * @param id 查询ID
     * <pre>
     *     [id]
     * </pre>
     * @return 查询结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:{
     *             id:ID,
     *             batteryCode:电池编号,
     *             batteryName:电池货名,
     *             batteryBrand:电池品牌,
     *             batteryPn:电池型号,
     *             batteryParameters:电池参数,
     *             mfrsId:生产商ID,
     *             batteryStatus:电池状态（正常、冻结、作废）,
     *             createUser:创建人,
     *             createTime:创建时间,
     *             updateUser:更新人,
     *             updateTime:更新时间
     *         }
     *     }
     * </pre>
     */
    @RequestMapping(path = "/getbypk")
    public MessageResponse getByPK(@RequestBody String id) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(id)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> batteryId = null;
            try {
                String paramStr = URLDecoder.decode(id, "utf-8");
                batteryId = JSON.parseArray(paramStr, String.class);
                if (null == batteryId || 0 == batteryId.size() || WzStringUtil.isBlank(batteryId.get(0))) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            BizBattery battery = bizBatteryService.getByPrimaryKey(batteryId.get(0));
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS,battery);
            return mr;
        }
    }

    /**
     * 根据电池ID列表获取车辆信息列表.
     * @param ids 电池ID列表
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
    @RequestMapping(path = "/getlocbybatterypk")
    public MessageResponse getLocByBatteryPK(@RequestBody String ids) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(ids)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> batteryIds = null;
            try {
                String paramStr = URLDecoder.decode(ids, "utf-8");
                batteryIds = JSON.parseArray(paramStr, String.class);
                if (null == batteryIds || 0 == batteryIds.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 循环获得车辆电池信息并获得定位信息
            BizBattery batteryInfo = null;
            String devicePk = null;
            JSONObject locData = null;
            List<JSONObject> locDatas = new ArrayList<JSONObject>();
            StringBuffer errMsgs = new StringBuffer("");
            for (String bId : batteryIds) {
                if (WzStringUtil.isNotBlank(bId)) {
                    batteryInfo = bizBatteryService.getByPrimaryKey(bId);
                    if (WzStringUtil.isBlank(batteryInfo.getBatteryCode())) {
                        errMsgs.append("未查询到电池[ID:" + bId + "]对应的设备;");
                        continue;
                    }
                    // 根据设备ID查询设备当前位置
                    devicePk = batteryInfo.getBatteryCode() + WzConstants.KEY_SPLIT + DeviceType.BATTERY.toString();
                    locData = (JSONObject) redisClient.hashOperations().get(WzConstants.GK_DEVICE_LOC_MAP, devicePk);
                    // 组织返回结果
                    if (null == locData
//                    || null == locData.getString(DeviceApiConstants.KEY_LOC_TIME)
                            || null == locData.getDouble(DeviceApiConstants.REQ_LAT)
                            || null == locData.getDouble(DeviceApiConstants.REQ_LON)) {
                        errMsgs.append("未查询到电池[ID:" + bId + "]对应设备的定位信息;");
                        continue;
                    }
                    locData.put(DeviceApiConstants.REQ_RESP_VEHICLE_ID, "");
                    locData.put(DeviceApiConstants.REQ_RESP_BATTERY_ID, bId);
                    locData.put(DeviceApiConstants.REQ_RESP_DEVICE_ID, batteryInfo.getBatteryCode());
                    locData.put(DeviceApiConstants.REQ_DEVICE_TYPE, DeviceType.BATTERY.toString());
                    locDatas.add(locData);
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
     * 根据电池ID列表获取设备电量信息列表.
     * @param ids 电池ID列表
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
    @RequestMapping(path = "/getpowerbybatterypk")
    public MessageResponse getPowerByBatteryPK(@RequestBody String ids) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(ids)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> batteryIds = null;
            try {
                String paramStr = URLDecoder.decode(ids, "utf-8");
                batteryIds = JSON.parseArray(paramStr, String.class);
                if (null == batteryIds || 0 == batteryIds.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 循环获得车辆电池信息并获得定位信息
            BizBattery batteryInfo = null;
            String devicePk = null;
            JSONObject powerData = null;
            List<JSONObject> powerDatas = new ArrayList<JSONObject>();
            StringBuffer errMsgs = new StringBuffer("");
            for (String bId : batteryIds) {
                if (WzStringUtil.isNotBlank(bId)) {
                    batteryInfo = bizBatteryService.getByPrimaryKey(bId);
                    if (WzStringUtil.isBlank(batteryInfo.getBatteryCode())) {
                        errMsgs.append("未查询到电池[ID:" + bId + "]对应的设备;");
                        continue;
                    }
                    // 根据设备ID查询设备当前位置
                    devicePk = batteryInfo.getBatteryCode() + WzConstants.KEY_SPLIT + DeviceType.BATTERY.toString();
                    powerData = (JSONObject) redisClient.hashOperations().get(WzConstants.GK_DEVIE_POWER_MAP, devicePk);
                    // 组织返回结果
                    if (null == powerData) {
                        powerData = new JSONObject();
                        powerData.put(DeviceApiConstants.REQ_RSOC, 0);
                        powerData.put(DeviceApiConstants.REQ_QUANITY, 0);
                        powerData.put(DeviceApiConstants.REQ_PS, 0);
                    }
                    powerData.put(DeviceApiConstants.REQ_RESP_VEHICLE_ID, "");
                    powerData.put(DeviceApiConstants.REQ_RESP_BATTERY_ID, bId);
                    powerData.put(DeviceApiConstants.REQ_RESP_DEVICE_ID, batteryInfo.getBatteryCode());
                    powerData.put(DeviceApiConstants.REQ_DEVICE_TYPE, DeviceType.BATTERY.toString());
                    powerDatas.add(powerData);
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
}
