package com.elextec.lease.manager.controller;

import com.alibaba.fastjson.JSON;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.manager.request.BizVehicleParam;
import com.elextec.lease.manager.request.VehicleBatteryParam;
import com.elextec.lease.manager.service.BizVehicleService;
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
import java.util.List;
import java.util.Map;

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
    public MessageResponse list(@RequestBody String paramAndPaging,HttpServletRequest request) {
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
                        return new MessageResponse(RunningResult.AUTH_OVER_TIME.code(),"登录信息已失效");
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
     *               createTime:创建时间,
     *               updateUser:更新人,
     *               updateTime:更新时间
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
     *               createTime:创建时间,
     *               updateUser:更新人,
     *               updateTime:更新时间
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
                VehicleBatteryParam insResChkVo = null;
                for (int i = 0; i < vehicleInfos.size(); i++) {
                    insResChkVo = vehicleInfos.get(i);
                    if(WzStringUtil.isBlank(insResChkVo.getFlag())){
                        return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "车辆信息参数有误");
                    }else{
                        if (WzStringUtil.isBlank(insResChkVo.getBizVehicleInfo().getVehicleCode())
                                || null == insResChkVo.getBizVehicleInfo().getVehicleStatus()
                                || WzStringUtil.isBlank(insResChkVo.getBizVehicleInfo().getCreateUser())
                                || WzStringUtil.isBlank(insResChkVo.getBizVehicleInfo().getUpdateUser())) {
                            return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "车辆信息参数有误");
                        }
                        if (!insResChkVo.getBizVehicleInfo().getVehicleStatus().toString().equals(RecordStatus.FREEZE.toString())
                                && !insResChkVo.getBizVehicleInfo().getVehicleStatus().toString().equals(RecordStatus.INVALID.toString())
                                && !insResChkVo.getBizVehicleInfo().getVehicleStatus().toString().equals(RecordStatus.NORMAL.toString())) {
                            return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的车辆状态");
                        }
                        if("0".equals(insResChkVo.getFlag())){
                            if (WzStringUtil.isBlank(insResChkVo.getBatteryInfo().getBatteryCode())
                                    || null == insResChkVo.getBatteryInfo().getBatteryStatus()
                                    || WzStringUtil.isBlank(insResChkVo.getBatteryInfo().getCreateUser())
                                    || WzStringUtil.isBlank(insResChkVo.getBatteryInfo().getUpdateUser())) {
                                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "车辆信息参数有误");
                            }
                            if (!insResChkVo.getBatteryInfo().getBatteryStatus().toString().equals(RecordStatus.FREEZE.toString())
                                    && !insResChkVo.getBatteryInfo().getBatteryStatus().toString().equals(RecordStatus.INVALID.toString())
                                    && !insResChkVo.getBatteryInfo().getBatteryStatus().toString().equals(RecordStatus.NORMAL.toString())) {
                                return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的电池状态");
                            }
                        }
                        if("1".equals(insResChkVo.getFlag())){
                            if (WzStringUtil.isBlank(insResChkVo.getBatteryInfo().getId())) {
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
     *               createTime:创建时间,
     *               updateUser:更新人,
     *               updateTime:更新时间
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
     *               createTime:创建时间,
     *               updateUser:更新人,
     *               updateTime:更新时间
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
     * @param id 车辆ID
     * <pre>
     *     [id]
     * </pre>
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
     *             }
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
            List<String> vehicleId = null;
            try {
                String paramStr = URLDecoder.decode(id, "utf-8");
                vehicleId = JSON.parseArray(paramStr, String.class);
                if (null == vehicleId || 0 == vehicleId.size() || WzStringUtil.isBlank(vehicleId.get(0))) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }

            Map<String,Object> vehicleInfo = bizVehicleService.getByPrimaryKey(vehicleId.get(0));
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, vehicleInfo);
            return mr;
        }
    }


}
