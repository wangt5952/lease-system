package com.elextec.lease.manager.controller;

import com.alibaba.fastjson.JSON;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.manager.request.VehicleBatteryParam;
import com.elextec.lease.manager.service.BizVehicleService;
import com.elextec.persist.model.mybatis.BizVehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * 查询车辆
     * @param paramAndPaging 分页参数JSON
     * <pre>
     *     {
     *         currPage:当前页,
     *         pageSize:每页记录数
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
     *                 vehicle_code:车辆编号,
     *                 vehicle_pn:车辆型号,
     *                 vehicle_brand:车辆品牌,
     *                 vehicle_made_in:车辆产地,
     *                 mfrs_id:生产商ID,
     *                 vehicle_status:车辆状态（正常、冻结、报废）,
     *                 create_user:创建人,
     *                 create_time:创建时间,
     *                 update_user:更新人,
     *                 update_time:更新时间
     *             },
     *             ... ...
     *         ]
     *     }
     * </pre>
     */
    @RequestMapping(path = "/listvehicles")
    public MessageResponse listUsers(@RequestBody String paramAndPaging) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(paramAndPaging)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            PageRequest pagingParam = null;
            try {
                String paramStr = URLDecoder.decode(paramAndPaging, "utf-8");
                pagingParam = JSON.parseObject(paramStr, PageRequest.class);
                if (null == pagingParam) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            PageResponse<BizVehicle> resPageResp = bizVehicleService.list(true, pagingParam);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, resPageResp);
            return mr;
        }
    }

    /**
     * 批量增加车辆信息.（车辆与电池信息配对）
     * @param vehicleParams 车辆电池配对信息列表JSON
     * <pre>
     *     [{
     *          "bizVehicleInfo": {
     *               vehicle_code:车辆编号,
     *               vehicle_pn:车辆型号,
     *               vehicle_brand:车辆品牌,
     *               vehicle_made_in:车辆产地,
     *               mfrs_id:生产商ID,
     *               vehicle_status:车辆状态（正常、冻结、报废）,
     *               create_user:创建人,
     *               create_time:创建时间,
     *               update_user:更新人,
     *               update_time:更新时间
     *
     *          },
     *          "flag": 有无电池flag("0"是新车配新电池信息,"1"是新车配旧电池信息,旧电池信息只带ID既可,"2"是只有车辆信息，电池信息不用传),
     *          "batteryInfo": {
     *               id:ID,
     *               battery_code:电池编号,
     *               battery_name:电池货名,
     *               battery_brand:电池品牌,
     *               battery_pn:电池型号,
     *               battery_parameters:电池参数,
     *               mfrs_id:生产商ID,
     *               battery_status:电池状态（正常、冻结、作废）,
     *               create_user:创建人,
     *               create_time:创建时间,
     *               update_user:更新人,
     *               update_time:更新时间
     *          }
     *    }]
     * </pre>
     * @return 批量新增结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:
     *     }
     * </pre>
     */
    @RequestMapping(path = "/addvehicles")
    public MessageResponse addUsers(@RequestBody String vehicleParams) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(vehicleParams)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<VehicleBatteryParam> resInfos = null;
            try {
                String paramStr = URLDecoder.decode(vehicleParams, "utf-8");
                resInfos = JSON.parseArray(paramStr, VehicleBatteryParam.class);
                if (null == resInfos) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizVehicleService.insertVehicles(resInfos);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 增加车辆信息.（车辆与电池信息配对）
     * @param addParams 车辆电池配对信息列表JSON
     * <pre>
     *     {
     *          "bizVehicleInfo": {
     *               vehicle_code:车辆编号,
     *               vehicle_pn:车辆型号,
     *               vehicle_brand:车辆品牌,
     *               vehicle_made_in:车辆产地,
     *               mfrs_id:生产商ID,
     *               vehicle_status:车辆状态（正常、冻结、报废）,
     *               create_user:创建人,
     *               create_time:创建时间,
     *               update_user:更新人,
     *               update_time:更新时间
     *
     *          },
     *          "flag": 有无电池flag("0"是新车配新电池信息,"1"是新车配旧电池信息,旧电池信息只带ID既可,"2"是只有车辆信息，电池信息不用传),
     *          "batteryInfo": {
     *               id:ID,
     *               battery_code:电池编号,
     *               battery_name:电池货名,
     *               battery_brand:电池品牌,
     *               battery_pn:电池型号,
     *               battery_parameters:电池参数,
     *               mfrs_id:生产商ID,
     *               battery_status:电池状态（正常、冻结、作废）,
     *               create_user:创建人,
     *               create_time:创建时间,
     *               update_user:更新人,
     *               update_time:更新时间
     *          }
     *    }
     * </pre>
     * @return 新增结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:
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
            VehicleBatteryParam resInfos = null;
            try {
                String paramStr = URLDecoder.decode(addParams, "utf-8");
                resInfos = JSON.parseObject(paramStr, VehicleBatteryParam.class);
                if (null == resInfos) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizVehicleService.insertVehicle(resInfos);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }



    /**
     * 修改车辆信息.
     * @param vehicle 车辆信息JSON
     * <pre>
     *     {
     *         id:ID,
     *         vehicle_code:车辆编号,
     *         vehicle_pn:车辆型号,
     *         vehicle_brand:车辆品牌,
     *         vehicle_made_in:车辆产地,
     *         mfrs_id:生产商ID,
     *         vehicle_status:车辆状态（正常、冻结、报废）,
     *         update_user:更新人
     *     }
     * </pre>
     * @return 修改车辆信息返回
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:
     *     }
     * </pre>
     */
    @RequestMapping(path = "/modifyvehicle")
    public MessageResponse modifyUser(@RequestBody String vehicle) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(vehicle)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            BizVehicle resInfo = null;
            try {
                String paramStr = URLDecoder.decode(vehicle, "utf-8");
                resInfo = JSON.parseObject(paramStr, BizVehicle.class);
                if (null == resInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizVehicleService.updateVehicle(resInfo);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 批量删除车辆.
     * @param vehicles 待删除的车辆列表JSON
     * <pre>
     *     [ID1,ID2,......]
     * </pre>
     * @return 批量删除车辆返回
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:
     *     }
     * </pre>
     */
    @RequestMapping(path = "/deletevehicles")
    public MessageResponse deleteUsers(@RequestBody String vehicles) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(vehicles)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> resIds = null;
            try {
                String paramStr = URLDecoder.decode(vehicles, "utf-8");
                resIds = JSON.parseArray(paramStr, String.class);
                if (null == resIds) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizVehicleService.deleteVehicles(resIds);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 根据ID获取车辆信息
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
     *                 vehicle_code:车辆编号,
     *                 vehicle_pn:车辆型号,
     *                 vehicle_brand:车辆品牌,
     *                 vehicle_made_in:车辆产地,
     *                 vehicleMfrsId:车辆生产商ID,
     *                 vehicle_status:车辆状态（正常、冻结、报废）,
     *                 vehicleMfrsName:车辆生产商名称,
     *                 batteryId:电池ID,
     *                 battery_code:电池编号,
     *                 battery_name:电池货名,
     *                 battery_brand:电池品牌,
     *                 battery_pn:电池型号,
     *                 battery_parameters:电池参数,
     *                 batteryMfrsId:电池生产商ID,
     *                 batteryMfrsName:电池生产商名称,
     *                 battery_status:电池状态（正常、冻结、作废）,
     *                 create_user:创建人,
     *                 create_time:创建时间,
     *                 update_user:更新人,
     *                 update_time:更新时间
     *             }
     *     }
     * </pre>
     */
    @RequestMapping(path = "/getByPrimaryKey")
    public MessageResponse getByPrimaryKey(@RequestBody String id) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(id)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> resId = null;
            try {
                String paramStr = URLDecoder.decode(id, "utf-8");
                resId = JSON.parseArray(paramStr, String.class);
                if (null == resId) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }

            Map<String,Object> user = bizVehicleService.getByPrimaryKey(resId.get(0));
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS,user);
            return mr;
        }
    }


}
