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
import com.elextec.lease.manager.request.BizDeviceConfParam;
import com.elextec.lease.manager.service.BizDeviceConfService;
import com.elextec.persist.field.enums.DeviceType;
import com.elextec.persist.model.mybatis.BizDeviceConf;
import com.elextec.persist.model.mybatis.BizDeviceConfKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 设备参数管理Controller.
 * Created by wangtao on 2018/1/16.
 */
@RestController
@RequestMapping(path = "/manager/device")
public class BizDeviceConfController extends BaseController {

    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(BizDeviceConfController.class);

    @Autowired
    private BizDeviceConfService bizDeviceConfService;

    /**
     * 查询参数设置.
     * @param paramAndPaging 查询及分页参数JSON
     * <pre>
     *     {
     *         keyStr:查询关键字（非必填，模糊查询，可填写设备ID）,
     *         deviceType:设备ID（非必填，包括VEHICLE、BATTERY、PARTS）,
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
     *                 deviceId:设备ID,
     *                 deviceType:设备类别（VEHICLE-车辆、BATTERY-电池、PARTS-配件）,
     *                 perSet:请求间隔时间（单位：秒）,
     *                 reset:硬件复位标志（0：无处理；1；复位重启）,
     *                 request:主动请求数据标志（0：无处理；1：主动请求）
     *             },
     *             ... ...
     *         ]
     *     }
     * </pre>
     */
    @RequestMapping(path = "/list")
    public MessageResponse list(@RequestBody String paramAndPaging) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(paramAndPaging)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
//            PageRequest pagingParam = null;
            BizDeviceConfParam pagingParam = null;
            try {
                String paramStr = URLDecoder.decode(paramAndPaging, "utf-8");
//                pagingParam = JSON.parseObject(paramStr, PageRequest.class);
                pagingParam = JSON.parseObject(paramStr, BizDeviceConfParam.class);
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
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
//            PageResponse<BizDeviceConf> devPageResp = bizDeviceConfService.list(true, pagingParam);
            PageResponse<BizDeviceConf> devPageResp = bizDeviceConfService.listByParam(Boolean.valueOf(pagingParam.getNeedPaging()), pagingParam);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, devPageResp);
            return mr;
        }
    }

    /**
     * 批量增加参数设置.
     * @param addParam 批量新增参数列表JSON
     * <pre>
     *     [
     *         {
     *             deviceId:设备ID,
     *             deviceType:设备类别（VEHICLE-车辆、BATTERY-电池、PARTS-配件）,
     *             perSet:请求间隔时间（单位：秒）,
     *             reset:硬件复位标志（0：无处理；1；复位重启）,
     *             request:主动请求数据标志（0：无处理；1：主动请求）
     *         },
     *         ... ...
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
            List<BizDeviceConf> devConfInfos = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                devConfInfos = JSON.parseArray(paramStr, BizDeviceConf.class);
                if (null == devConfInfos || 0 == devConfInfos.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizDeviceConfService.insertBizDeviceConfs(devConfInfos);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 增加设备参数.
     * @param addParam 新增参数列表JSON
     * <pre>
     *     {
     *         deviceId:设备ID,
     *         deviceType:设备类别（VEHICLE-车辆、BATTERY-电池、PARTS-配件）,
     *         perSet:请求间隔时间（单位：秒）,
     *         reset:硬件复位标志（0：无处理；1；复位重启）,
     *         request:主动请求数据标志（0：无处理；1：主动请求）
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
            BizDeviceConf devConfInfo = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                devConfInfo = JSON.parseObject(paramStr, BizDeviceConf.class);
                if (null == devConfInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if (WzStringUtil.isBlank(devConfInfo.getDeviceId())
                        || null == devConfInfo.getDeviceType()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "设备ID不能为空");
                }
                if (!devConfInfo.getDeviceType().toString().equals(DeviceType.BATTERY.toString())
                        && !devConfInfo.getDeviceType().toString().equals(DeviceType.VEHICLE.toString())
                        && !devConfInfo.getDeviceType().toString().equals(DeviceType.PARTS.toString())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "无效的设备类别");
                }
                if (null == devConfInfo.getPerSet()
                        && null == devConfInfo.getReset()
                        && null == devConfInfo.getRequest()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "无需更新");
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizDeviceConfService.insertBizDeviceConf(devConfInfo);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 修改设备参数信息.
     * @param modifyParam 修改参数JSON
     * <pre>
     *     {
     *         deviceId:设备ID,
     *         deviceType:设备类别（VEHICLE-车辆、BATTERY-电池、PARTS-配件）,
     *         perSet:请求间隔时间（单位：秒）,
     *         reset:硬件复位标志（0：无处理；1；复位重启）,
     *         request:主动请求数据标志（0：无处理；1：主动请求）
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
            BizDeviceConf devConfInfo = null;
            try {
                String paramStr = URLDecoder.decode(modifyParam, "utf-8");
                devConfInfo = JSON.parseObject(paramStr, BizDeviceConf.class);
                if (null == devConfInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if (WzStringUtil.isBlank(devConfInfo.getDeviceId())
                        || null == devConfInfo.getDeviceType()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "设备ID不能为空");
                }
                if (!devConfInfo.getDeviceType().toString().equals(DeviceType.BATTERY.toString())
                        && !devConfInfo.getDeviceType().toString().equals(DeviceType.VEHICLE.toString())
                        && !devConfInfo.getDeviceType().toString().equals(DeviceType.PARTS.toString())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "无效的设备类别");
                }
                if (null == devConfInfo.getPerSet()
                        && null == devConfInfo.getReset()
                        && null == devConfInfo.getRequest()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "无需更新");
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizDeviceConfService.updateBizDeviceConf(devConfInfo);
            // 修改后进行缓存
            redisClient.valueOperations().set(WzConstants.GK_DEVICE_CONF + devConfInfo.getDeviceId() + WzConstants.KEY_SPLIT + devConfInfo.getDeviceType().toString(), devConfInfo, 30, TimeUnit.MINUTES);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 批量删除用户.
     * @param deleteParam 删除ID列表JSON
     * <pre>
     *     [
     *         {
     *             deviceId:设备ID,
     *             deviceType:设备类别（VEHICLE-车辆、BATTERY-电池、PARTS-配件）,
     *         },
     *         ... ...
     *     ]
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
            List<BizDeviceConfKey> devConfIds = null;
            try {
                String paramStr = URLDecoder.decode(deleteParam, "utf-8");
                devConfIds = JSON.parseArray(paramStr, BizDeviceConfKey.class);
                if (null == devConfIds || 0 == devConfIds.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizDeviceConfService.deleteBizDeviceConfs(devConfIds);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 根据设备ID和设备类别获取设置信息.
     * @param paramPK 查询参数
     * <pre>
     *     {
     *         deviceId:设备ID,
     *         deviceType:设备类别（VEHICLE-车辆、BATTERY-电池、PARTS-配件）,
     *     }
     * </pre>
     * @return 查询结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:{
     *             deviceId:设备ID,
     *             deviceType:设备类别（VEHICLE-车辆、BATTERY-电池、PARTS-配件）,
     *             perSet:请求间隔时间（单位：秒）,
     *             reset:硬件复位标志（0：无处理；1；复位重启）,
     *             request:主动请求数据标志（0：无处理；1：主动请求）
     *         }
     *     }
     * </pre>
     */
    @RequestMapping(path = "/getbypk")
    public MessageResponse getByPK(@RequestBody String paramPK) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(paramPK)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            BizDeviceConfKey devConfKey = null;
            try {
                String paramStr = URLDecoder.decode(paramPK, "utf-8");
                devConfKey = JSONObject.parseObject(paramStr, BizDeviceConfKey.class);
                if (null == devConfKey) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if (WzStringUtil.isBlank(devConfKey.getDeviceId())
                        || null == devConfKey.getDeviceType()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "设备ID及类别不能为空");
                }
                if (!devConfKey.getDeviceType().toString().equals(DeviceType.BATTERY.toString())
                        && !devConfKey.getDeviceType().toString().equals(DeviceType.VEHICLE.toString())
                        && !devConfKey.getDeviceType().toString().equals(DeviceType.PARTS.toString())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "无效的设备类别");
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            BizDeviceConf devConf = bizDeviceConfService.getBizDeviceConfByPrimaryKey(devConfKey);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, devConf);
            return mr;
        }
    }
}
