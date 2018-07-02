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
import com.elextec.lease.manager.service.BizVehicleTrackService;
import com.elextec.lease.model.BizVehicleBatteryParts;
import com.elextec.persist.field.enums.DeviceType;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.field.enums.RecordStatus;
import com.elextec.persist.model.mybatis.BizVehicle;
import com.elextec.persist.model.mybatis.BizVehicleTrack;
import com.elextec.persist.model.mybatis.SysUser;
import com.elextec.persist.model.mybatis.ext.BizPartsExt;
import com.elextec.persist.model.mybatis.ext.BizVehicleExt;
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
 * 车辆管理Controller.
 * Created by wangtao on 2018/1/19.
 */
@RestController
@RequestMapping(path = "/manager/vehicle")
public class BizVehicleController extends BaseController {
    /**
     * 日志.
     */
    private final Logger logger = LoggerFactory.getLogger(BizVehicleController.class);

    @Autowired
    private BizVehicleService bizVehicleService;

    @Autowired
    private BizVehicleTrackService bizVehicleTrackService;

    /**
     * 查询车辆.
     *
     * @param request        请求
     * @param paramAndPaging 分页参数JSON
     * <pre>
     *        {
     *                       keyStr:查询关键字（非必填，模糊查询，可填写车辆编号、车辆型号、车辆品牌、车辆产地、生产商ID、生产商名）,
     *                       vehicleStatus:车辆状态（非必填，包括NORMAL、FREEZE、INVALID）,
     *                       needPaging:是否需要分页（仅为false时不需要分页，其余情况均需要分页）,
     *                       isBind:绑定未绑定状态（BIND为查已绑定，UNBIND为查未绑定,空的话则查全部）,
     *                       currPage:当前页（needPaging不为false时必填）,
     *                       pageSize:每页记录数（needPaging不为false时必填）
     *         }
     * </pre>
     * @param request HttpServletRequest
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
                    System.out.print(pagingParam.getCurrPage());
                    if (null == pagingParam.getCurrPage() || null == pagingParam.getPageSize()) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "未获得分页参数");
                    }
                    pagingParam.setNeedPaging("true");
                }
                SysUser userTemp = getLoginUserInfo(request);
                if (userTemp != null) {
                    //根据用户类型添加条件
                    //个人用户需要添加userId为条件
                    if (OrgAndUserType.INDIVIDUAL.toString().equals(userTemp.getUserType().toString())) {
                        pagingParam.setUserId(userTemp.getId());
                    }
                    //企业用户需要添加orgId为条件
                    if (OrgAndUserType.ENTERPRISE.toString().equals(userTemp.getUserType().toString())) {
                        pagingParam.setOrgId(userTemp.getOrgId());
                    }
                } else {
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR.code(), ex.getMessage(), ex);
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
     *
     * @param addParams 车辆电池配对信息列表JSON
     *                  <pre>
     *                      [{
     *                           "bizVehicleInfo": {
     *                                vehicleCode:车辆编号,
     *                                vehiclePn:车辆型号,
     *                                vehicleBrand:车辆品牌,
     *                                vehicleMadeIn:车辆产地,
     *                                mfrsId:生产商ID,
     *                                vehicleStatus:车辆状态（正常、冻结、报废）,
     *                                createUser:创建人,
     *                                updateUser:更新人
     *
     *                           },
     *                           "flag": 有无电池flag("0"是新车配新电池信息,"1"是新车配旧电池信息,旧电池信息只带ID既可,"2"是只有车辆信息，电池信息不用传),
     *                           "batteryInfo": {
     *                                id:ID（仅flag为1时有效，其余电池项仅flag为0时有效）,
     *                                batteryCode:电池编号,
     *                                batteryName:电池货名,
     *                                batteryBrand:电池品牌,
     *                                batteryPn:电池型号,
     *                                batteryParameters:电池参数,
     *                                mfrsId:生产商ID,
     *                                batteryStatus:电池状态（正常、冻结、作废）,
     *                                createUser:创建人,
     *                                updateUser:更新人
     *                           }
     *                     }]
     *                  </pre>
     * @param request   HttpServletRequest
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
    public MessageResponse add(@RequestBody String addParams, HttpServletRequest request) {
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
                SysUser userTemp = getLoginUserInfo(request);
                if (userTemp != null) {
                    //只有平台用户可以增加车辆
                    if (!OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())) {
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                } else {
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                VehicleBatteryParam insVBChkVo = null;
                for (int i = 0; i < vehicleInfos.size(); i++) {
                    insVBChkVo = vehicleInfos.get(i);
                    if (WzStringUtil.isBlank(insVBChkVo.getFlag())) {
                        return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "车辆信息参数有误");
                    } else {
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
                        if ("0".equals(insVBChkVo.getFlag())) {
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
                        if ("1".equals(insVBChkVo.getFlag())) {
                            if (WzStringUtil.isBlank(insVBChkVo.getBatteryInfo().getId())) {
                                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "车辆信息参数有误");
                            }
                        }
                    }
                }
                bizVehicleService.insertVehicles(vehicleInfos);
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 增加车辆信息.（车辆与电池信息配对）.
     *
     * @param addParams 车辆电池配对信息列表JSON
     *                  <pre>
     *                      {
     *                           "bizVehicleInfo": {
     *                                vehicleCode:车辆编号,
     *                                vehiclePn:车辆型号,
     *                                vehicleBrand:车辆品牌,
     *                                vehicleMadeIn:车辆产地,
     *                                mfrsId:生产商ID,
     *                                vehicleStatus:车辆状态（正常、冻结、报废）,
     *                                createUser:创建人,
     *                                updateUser:更新人
     *
     *                           },
     *                           "flag": 有无电池flag("0"是新车配新电池信息,"1"是新车配旧电池信息,旧电池信息只带ID既可,"2"是只有车辆信息，电池信息不用传),
     *                           "batteryInfo": {
     *                                id:ID,
     *                                batteryCode:电池编号,
     *                                batteryName:电池货名,
     *                                batteryBrand:电池品牌,
     *                                batteryPn:电池型号,
     *                                batteryParameters:电池参数,
     *                                mfrsId:生产商ID,
     *                                batteryStatus:电池状态（正常、冻结、作废）,
     *                                createUser:创建人,
     *                                updateUser:更新人
     *                           }
     *                     }
     *                  </pre>
     * @param request   HttpServletRequest
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
    public MessageResponse addone(@RequestBody String addParams, HttpServletRequest request) {
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
                SysUser userTemp = getLoginUserInfo(request);
                if (userTemp != null) {
                    //只有平台用户可以增加车辆
                    if (!OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())) {
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                } else {
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }

                if (WzStringUtil.isBlank(vechcleInfo.getFlag())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "车辆信息参数有误");
                } else {
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
                    if ("0".equals(vechcleInfo.getFlag())) {
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
                    if ("1".equals(vechcleInfo.getFlag())) {
                        if (WzStringUtil.isBlank(vechcleInfo.getBatteryInfo().getId())) {
                            return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "车辆信息参数有误");
                        }
                    }
                }
                bizVehicleService.insertVehicle(vechcleInfo);
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }


    /**
     * 修改车辆信息.
     *
     * @param modifyParam 车辆信息JSON
     *                    <pre>
     *                        {
     *                            id:ID,
     *                            vehicleCode:车辆编号,
     *                            vehiclePn:车辆型号,
     *                            vehicleBrand:车辆品牌,
     *                            vehicleMadeIn:车辆产地,
     *                            mfrsId:生产商ID,
     *                            vehicleStatus:车辆状态（正常、冻结、报废）,
     *                            updateUser:更新人
     *                        }
     *                    </pre>
     * @param request     HttpServletRequest
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
    public MessageResponse modifyUser(@RequestBody String modifyParam, HttpServletRequest request) {
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
                if (null == vehicleInfo
                        || WzStringUtil.isBlank(vehicleInfo.getUpdateUser())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }

                SysUser userTemp = getLoginUserInfo(request);
                if (userTemp != null) {
                    //只有平台用户可以修改车辆信息
                    if (!OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())) {
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                } else {
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }

                if (WzStringUtil.isBlank(vehicleInfo.getId())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无法确定待修改的记录");
                }
                if(null != vehicleInfo.getVehicleStatus()){
                    if (!vehicleInfo.getVehicleStatus().toString().equals(RecordStatus.FREEZE.toString())
                            && !vehicleInfo.getVehicleStatus().toString().equals(RecordStatus.INVALID.toString())
                            && !vehicleInfo.getVehicleStatus().toString().equals(RecordStatus.NORMAL.toString())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的车辆状态");
                    }
                }
                bizVehicleService.updateVehicle(vehicleInfo);
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 批量删除车辆.
     *
     * @param deleteParam 待删除的车辆列表JSON
     *                    <pre>
     *                        [ID1,ID2,......]
     *                    </pre>
     * @param request     HttpServletRequest
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
    public MessageResponse deleteUsers(@RequestBody String deleteParam, HttpServletRequest request) {
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
                SysUser userTemp = getLoginUserInfo(request);
                if (userTemp != null) {
                    //只有平台用户可以删除车辆
                    if (!OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())) {
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                } else {
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                bizVehicleService.deleteVehicles(vehicleIds);
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
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
    public MessageResponse getByPK(@RequestBody String param, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(param)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
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
            if (WzStringUtil.isNotBlank(isUsedBatteryStr)
                    && ("true".equals(isUsedBatteryStr.toLowerCase()) || "false".equals(isUsedBatteryStr.toLowerCase()))) {
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
//            List<Map<String,Object>> vehicleInfo = bizVehicleService.getByPrimaryKey((String) paramMap.get("id"), isUsedBattery);
            BizVehicleBatteryParts vehicleInfo = bizVehicleService.queryBatteryInfoByVehicleId(paramMap, isUsedBattery);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, vehicleInfo);
            return mr;
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
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
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
            List<BizPartsExt> parts = bizVehicleService.getBizPartsByVehicle(paramTemp);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, parts);
            return mr;
        }
    }

    /**
     * 根据车辆ID列表获取车辆信息列表.
     *
     * @param ids     车辆ID列表
     *                <pre>
     *                    [id1,id2,...]
     *                </pre>
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
            JSONObject locData = null;
            List<JSONObject> locDatas = new ArrayList<JSONObject>();
            StringBuffer errMsgs = new StringBuffer("");
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
            for (String vId : vehicleIds) {
                if (WzStringUtil.isNotBlank(vId)) {
                    paramTemp.put("id", vId);
                    vehicleInfos = bizVehicleService.getByPrimaryKey(paramTemp, true);
                    if (null == vehicleInfos || 0 == vehicleInfos.size()) {
                        errMsgs.append("未查询到车辆[ID:" + vId + "]信息;");
                        continue;
                    } else {
                        for (Map<String, Object> vehicleInfo : vehicleInfos) {
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
                            double latTmp = locData.getDoubleValue(DeviceApiConstants.REQ_LAT);
                            double lngTmp = locData.getDoubleValue(DeviceApiConstants.REQ_LON);
                            double[] bdLatLng = WzGPSUtil.wgs2bd(latTmp, lngTmp);
                            locData.put(DeviceApiConstants.REQ_LAT, bdLatLng[0]);
                            locData.put(DeviceApiConstants.REQ_LON, bdLatLng[1]);
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
     * 根据车辆ID列表获取车辆定位字典.
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
     *         respData:{
     *             车辆ID: {
     *                 VehicleID:车辆ID,
     *                 BatteryID:电池ID,
     *                 DeviceID:设备ID,
     *                 DeviceType:设备类型,
     *                 LocTime:记录时间,
     *                 LAT:纬度,
     *                 LON:经度
     *             },
     *             ... ...
     *         }
     *     }
     * </pre>
     */
    @RequestMapping(path = "/getlocmapbyvehiclepk")
    public MessageResponse getLocMapByVehiclePK(@RequestBody String ids, HttpServletRequest request) {
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
            JSONObject locData = null;
            Map<String, JSONObject> locDatas = new HashMap<String, JSONObject>();
            StringBuffer errMsgs = new StringBuffer("");
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
            for (String vId : vehicleIds) {
                if (WzStringUtil.isNotBlank(vId)) {
                    paramTemp.put("id", vId);
                    vehicleInfos = bizVehicleService.getByPrimaryKey(paramTemp, true);
                    if (null == vehicleInfos || 0 == vehicleInfos.size()) {
                        errMsgs.append("未查询到车辆[ID:" + vId + "]信息;");
                        continue;
                    } else {
                        for (Map<String, Object> vehicleInfo : vehicleInfos) {
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
                            double latTmp = locData.getDoubleValue(DeviceApiConstants.REQ_LAT);
                            double lngTmp = locData.getDoubleValue(DeviceApiConstants.REQ_LON);
                            double[] bdLatLng = WzGPSUtil.wgs2bd(latTmp, lngTmp);
                            locData.put(DeviceApiConstants.REQ_LAT, bdLatLng[0]);
                            locData.put(DeviceApiConstants.REQ_LON, bdLatLng[1]);
                            locData.put(DeviceApiConstants.REQ_RESP_VEHICLE_ID, vId);
                            locData.put(DeviceApiConstants.REQ_RESP_BATTERY_ID, batteryId);
                            locData.put(DeviceApiConstants.REQ_RESP_DEVICE_ID, batteryCode);
                            locData.put(DeviceApiConstants.REQ_DEVICE_TYPE, DeviceType.BATTERY.toString());
                            locDatas.put(vId, locData);
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
     *
     * @param ids     车辆ID列表
     *                <pre>
     *                    [id1,id2,...]
     *                </pre>
     * @param request HttpServletRequest
     * @return 根据ID获取设备电量信息
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:[
     *             {
     *                 VehicleID:车辆ID,
     *                 VehicleCode:车辆Code,
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
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 循环获得车辆电池信息并获得定位信息
            List<Map<String, Object>> vehicleInfos = null;
            String vehicleCode = null;
            String batteryId = null;
            String batteryCode = null;
            String devicePk = null;
            JSONObject powerData = null;
            List<JSONObject> powerDatas = new ArrayList<JSONObject>();
            StringBuffer errMsgs = new StringBuffer("");
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
            for (String vId : vehicleIds) {
                if (WzStringUtil.isNotBlank(vId)) {
                    paramTemp.put("id", vId);
                    vehicleInfos = bizVehicleService.getByPrimaryKey(paramTemp, true);
                    if (null == vehicleInfos || 0 == vehicleInfos.size()) {
                        errMsgs.append("未查询到车辆[ID:" + vId + "]信息;");
                        continue;
                    } else {
                        for (Map<String, Object> vehicleInfo : vehicleInfos) {
                            vehicleCode = (String) vehicleInfo.get("vehicleCode");
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
                            powerData.put(DeviceApiConstants.REQ_RESP_VEHICLE_CODE, vehicleCode);
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
     * 根据车辆ID列表获取设备电量信息字典.
     * @param ids 车辆ID列表
     * <pre>
     *    [id1,id2,...]
     * </pre>
     * @param request HttpServletRequest
     * @return 根据ID获取设备电量信息
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:{
     *             车辆ID:{
     *                 VehicleID:车辆ID,
     *                 VehicleCode:车辆Code,
     *                 BatteryID:电池ID,
     *                 DeviceID:设备ID,
     *                 DeviceType:设备类型,
     *                 RSOC:电池剩余容量百分比,
     *                 Quanity:设备电量,
     *                 PS:保护状态
     *             },
     *             ... ...
     *         }
     *     }
     * </pre>
     */
    @RequestMapping(path = "/getpowermapbyvehiclepk")
    public MessageResponse getPowerMapByVehiclePK(@RequestBody String ids, HttpServletRequest request) {
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
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 循环获得车辆电池信息并获得定位信息
            List<Map<String, Object>> vehicleInfos = null;
            String vehicleCode = null;
            String batteryId = null;
            String batteryCode = null;
            String devicePk = null;
            JSONObject powerData = null;
            Map<String, JSONObject> powerDatas = new HashMap<String, JSONObject>();
            StringBuffer errMsgs = new StringBuffer("");
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
            for (String vId : vehicleIds) {
                if (WzStringUtil.isNotBlank(vId)) {
                    paramTemp.put("id", vId);
                    vehicleInfos = bizVehicleService.getByPrimaryKey(paramTemp, true);
                    if (null == vehicleInfos || 0 == vehicleInfos.size()) {
                        errMsgs.append("未查询到车辆[ID:" + vId + "]信息;");
                        continue;
                    } else {
                        for (Map<String, Object> vehicleInfo : vehicleInfos) {
                            vehicleCode = (String) vehicleInfo.get("vehicleCode");
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
                            powerData.put(DeviceApiConstants.REQ_RESP_VEHICLE_CODE, vehicleCode);
                            powerData.put(DeviceApiConstants.REQ_RESP_BATTERY_ID, batteryId);
                            powerData.put(DeviceApiConstants.REQ_RESP_DEVICE_ID, batteryCode);
                            powerData.put(DeviceApiConstants.REQ_DEVICE_TYPE, DeviceType.BATTERY.toString());
                            powerDatas.put(vId, powerData);
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
     *
     * @param vehicleIdAndBatteryId 车辆ID与电池
     *                              <pre>
     *                                  {
     *                                      vehicleId:车辆ID,
     *                                      batteryId:电池ID
     *                                  }
     *                              </pre>
     * @param request               HttpServletRequest
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
    public MessageResponse batteryBind(@RequestBody String vehicleIdAndBatteryId, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(vehicleIdAndBatteryId)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            Map<String, String> param = null;
            try {
                String paramStr = URLDecoder.decode(vehicleIdAndBatteryId, "utf-8");
                param = JSON.parseObject(paramStr, Map.class);
                if (null == param || 0 == param.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                SysUser userTemp = getLoginUserInfo(request);
                if (userTemp != null) {
                    //个人与企业用户无权执行该操作
                    if (!OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())) {
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                } else {
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                if (WzStringUtil.isBlank((String) param.get("vehicleId")) || WzStringUtil.isBlank((String) param.get("batteryId"))) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "查询条件不能为空");
                }
                bizVehicleService.bind(param.get("vehicleId"), param.get("batteryId"));
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 车辆与电池解绑接口.
     *
     * @param vehicleIdAndBatteryId 用户ID与车辆ID
     *                              <pre>
     *                                  {
     *                                      vehicleId:车辆ID,
     *                                      batteryId:电池ID
     *                                  }
     *                              </pre>
     * @param request               HttpServletRequest
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
    public MessageResponse batteryUnbind(@RequestBody String vehicleIdAndBatteryId, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(vehicleIdAndBatteryId)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            Map<String, String> param = null;
            try {
                String paramStr = URLDecoder.decode(vehicleIdAndBatteryId, "utf-8");
                param = JSON.parseObject(paramStr, Map.class);
                if (null == param || 0 == param.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                SysUser userTemp = getLoginUserInfo(request);
                if (userTemp != null) {
                    //个人与企业用户无权执行该操作
                    if (!OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())) {
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                } else {
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                if (WzStringUtil.isBlank((String) param.get("vehicleId")) || WzStringUtil.isBlank((String) param.get("batteryId"))) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "解绑参数不能为空");
                }
                bizVehicleService.unBind(param.get("vehicleId"), param.get("batteryId"));
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 根据定位点及半径获得范围内的所有车辆信息及定位、电池电量数据.
     *
     * @param locAndRadius 定位点及半径参数
     *                     <pre>
     *                         {
     *                             lat:纬度,
     *                             lng:经度,
     *                             radius:半径(米)
     *                         }
     *                     </pre>
     * @param request      HttpServletRequest
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
    public MessageResponse listVehiclesByLocAndRadius(@RequestBody String locAndRadius, HttpServletRequest request) {
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
                if (null == locAndRadiusParam.getLat()
                        || null == locAndRadiusParam.getLng()) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "定位点信息异常");
                }
            } catch (BizException ex) {
                throw ex;
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
                double dist = WzGPSUtil.calcDistanceByM(wgsLatLng[0], wgsLatLng[1], dLoc.getDouble(DeviceApiConstants.REQ_LAT), dLoc.getDouble(DeviceApiConstants.REQ_LON));
                if (dist <= locAndRadiusParam.getRadius()) {
                    deviceLocsCache.put(dLoc.getString(DeviceApiConstants.REQ_RESP_DEVICE_ID), dLoc);
                    deviceCds.add(dLoc.getString(DeviceApiConstants.REQ_RESP_DEVICE_ID));
                }
            }
            if (null == deviceCds || 0 == deviceCds.size()) {
                return new MessageResponse(RunningResult.NOT_FOUND.code(), "未发现车辆");
            }
            SysUser userTemp = getLoginUserInfo(request);
            if (userTemp == null) {
                return new MessageResponse(RunningResult.AUTH_OVER_TIME);
            }
            Map<String, Object> paramTemp = new HashMap<String, Object>();
//            deviceCds.add("123456");
            paramTemp.put("batteryCodes", deviceCds);
            if (OrgAndUserType.ENTERPRISE.toString().equals(userTemp.getUserType().toString())) {
                paramTemp.put("orgId", userTemp.getOrgId());
            }
            if (OrgAndUserType.INDIVIDUAL.toString().equals(userTemp.getUserType().toString())) {
                paramTemp.put("userId", userTemp.getId());
            }
            // 查询所有范围内的车辆信息
            List<Map<String, Object>> vehicles = bizVehicleService.listByBatteryCode(paramTemp);
            // 循环每个车辆信息并装配位置信息及电量信息
            JSONObject powerInfo = null;
            for (Map<String, Object> vh : vehicles) {
                //将GPS坐标转换成百度坐标
                double[] wgs2bd = WzGPSUtil.wgs2bd(deviceLocsCache.get(vh.get("batteryCode")).getDouble(DeviceApiConstants.REQ_LAT),deviceLocsCache.get(vh.get("batteryCode")).getDouble(DeviceApiConstants.REQ_LON));
                vh.put(DeviceApiConstants.REQ_LAT, wgs2bd[0]);//lat
                vh.put(DeviceApiConstants.REQ_LON, wgs2bd[1]);//lon
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
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
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
//                        List<BizVehicleTrack> locList = bizVehicleTrackService.getVehicleTracksByTime(batteryCode, DeviceType.BATTERY.toString(), startTime, endTime);
                        List<BizVehicleTrack> locList = bizVehicleTrackService.getVehicleTracksByVehicleIdAndTime(idAndTimeIntervalInfo.get("id"), startTime, endTime);
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
            MessageResponse mr = null;
            if (0 == errMsgs.length()) {
                mr = new MessageResponse(RunningResult.SUCCESS, locDatas);
            } else {
                mr = new MessageResponse(RunningResult.NOT_FOUND.code(), errMsgs.toString());
            }
            return mr;
        }
    }

    /**
     * 平台与企业用户查询绑定车辆数量接口
     *
     * @param request HttpServletRequest
     * @return 查询结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(path = "/orgbindvehicle")
    public MessageResponse orgBindVehicle(HttpServletRequest request) {
        int vehiclesCot = 0;
        try {
            SysUser userTemp = getLoginUserInfo(request);
            if (userTemp != null) {
                //个人无权执行该操作
                if (OrgAndUserType.ENTERPRISE.toString().equals(userTemp.getUserType().toString())
                        || OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())) {
                    vehiclesCot = bizVehicleService.getOrgBindVehicle(userTemp.getOrgId());
                } else {
                    return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                }
            } else {
                return new MessageResponse(RunningResult.AUTH_OVER_TIME);
            }
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
        }
        // 返回结果
        return new MessageResponse(RunningResult.SUCCESS, vehiclesCot);
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
     * 该企业下未绑定的车辆
     * <pre>
     *     {
     *         orgId:企业id，
     *         keyStr:查询关键字（非必填，模糊查询，可填写车辆编号、车辆型号、车辆品牌、车辆产地、生产商ID、生产商名）,
     *         vehicleStatus:车辆状态（非必填，包括NORMAL、FREEZE、INVALID）,
     *         needPaging:是否需要分页（仅为false时不需要分页，其余情况均需要分页）,
     *         isBind:绑定未绑定状态（BIND为查已绑定，UNBIND为查未绑定,空的话则查全部）,
     *         currPage:当前页（needPaging不为false时必填）,
     *         pageSize:每页记录数（needPaging不为false时必填）
     *     }
     * </pre>
     *
     * @param paramAndPaging 分页参数
     * @param request 登录信息
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
    @RequestMapping(value = "/selectExtUnbindExtByParams", method = RequestMethod.POST)
    public MessageResponse selectExtUnbindExtByParams(@RequestBody String paramAndPaging, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(paramAndPaging)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            BizVehicleParam pagingParam = null;
            try {
                String paramStr = URLDecoder.decode(paramAndPaging, "utf-8");
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
                    pagingParam.setNeedPaging("true");
                }
                SysUser userTemp = getLoginUserInfo(request);
                if (userTemp != null) {
                    if (userTemp.getUserType().toString().equals(OrgAndUserType.INDIVIDUAL.toString())) {
                            return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                    //根据用户类型添加条件
                    //平台用户需要添加上传的orgId为条件
                    if (OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())) {
                        if (WzStringUtil.isBlank(pagingParam.getOrgId())) {
                            return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "企业id参数不能为空");
                        }
                        pagingParam.setOrgId(pagingParam.getOrgId());
                    }
                    //企业用户需要添加orgId为条件
                    if (OrgAndUserType.ENTERPRISE.toString().equals(userTemp.getUserType().toString())) {
                        pagingParam.setOrgId(userTemp.getOrgId());
                    }
                } else {
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
            } catch(BizException ex){
                throw ex;
            } catch(Exception ex){
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR.code(), ex.getMessage(), ex);
            }
            PageResponse<BizVehicleExt> vehiclePageResp = bizVehicleService.selectExtUnbindExtByParams(Boolean.valueOf(pagingParam.getNeedPaging()), pagingParam);
            // 组织返回结果并返回
            return new MessageResponse(RunningResult.SUCCESS, vehiclePageResp);
        }
    }

    /**
     * <pre>
     *     {
     *         orgId:企业Id
     *     }
     * </pre>
     * 查询该企业下有多少闲置车辆
     * @param orgInfo 企业id
     * @param request 登录信息
     * @return 车辆个数
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:[
     *
     *         ]
     *     }
     * </pre>
     */
    @RequestMapping(value = "/orgCountVehicle",method = RequestMethod.POST)
    public MessageResponse orgCountVehicle(@RequestBody String orgInfo,HttpServletRequest request){
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(orgInfo)) {
            return new MessageResponse(RunningResult.NO_PARAM);
        } else {
            // 参数解析错误报“参数解析错误”
            try {
                BizVehicleParam pagingParam = null;
                String orgStr = URLDecoder.decode(orgInfo, "utf-8");
                pagingParam = JSONObject.parseObject(orgStr,BizVehicleParam.class);
                if (WzStringUtil.isBlank(pagingParam.getOrgId())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                SysUser sysUser = this.getLoginUserInfo(request);
                if (sysUser == null) {
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                if (sysUser.getUserType().toString().equals(OrgAndUserType.INDIVIDUAL.toString())) {
                    return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                }
                return new MessageResponse(RunningResult.SUCCESS,bizVehicleService.orgCountVehicle(pagingParam));
            } catch(BizException ex){
                throw ex;
            } catch(Exception ex){
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR.code(), ex.getMessage(), ex);
            }
        }
    }

    /**
     * <pre>
     *     {
     *         orgId:企业id
     *     }
     * </pre>
     * @param orgId 企业id
     * @param request 用户登录信息
     * @return 是否已批量收回
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:[
     *
     *         ]
     *     }
     * </pre>
     */
    @RequestMapping(value = "/vehicleRecovery",method = RequestMethod.POST)
    public MessageResponse vehicleRecovery(@RequestBody String orgId,HttpServletRequest request){
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(orgId)) {
            return new MessageResponse(RunningResult.NO_PARAM);
        } else {
            // 参数解析错误报“参数解析错误”
            try {
                String orgStr = URLDecoder.decode(orgId,"utf-8");
                Map<String,Object> map = JSONObject.parseObject(orgStr,Map.class);
                //只有平台才能操作
                if (!super.getLoginUserInfo(request).getUserType().toString().equals(OrgAndUserType.PLATFORM.toString())) {
                    return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                }
                //企业用户id不能为空
                if (WzStringUtil.isBlank(map.get("orgId").toString())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                //登录用户的企业id不能为空
                if (WzStringUtil.isBlank(super.getLoginUserInfo(request).getOrgId())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                bizVehicleService.vehicleRecovery(map.get("orgId").toString(),super.getLoginUserInfo(request).getOrgId());
                return new MessageResponse(RunningResult.SUCCESS,"批量归还成功");
            }catch(BizException ex){
                throw ex;
            } catch(Exception ex){
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR.code(), ex.getMessage(), ex);
            }
        }
    }

}
