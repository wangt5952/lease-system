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
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.model.mybatis.BizDeviceConf;
import com.elextec.persist.model.mybatis.BizDeviceConfKey;
import com.elextec.persist.model.mybatis.SysUser;
import com.sun.org.apache.regexp.internal.RE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
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
     * @param request
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
    public MessageResponse list(@RequestBody String paramAndPaging,HttpServletRequest request) {
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
                SysUser sysUser = getLoginUserInfo(request);
                if(sysUser != null){
                    //只有平台用户可以操作
                    if(!OrgAndUserType.PLATFORM.toString().equals(sysUser.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
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
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR.code(), ex.getMessage(), ex);
            }
//            PageResponse<BizDeviceConf> devPageResp = bizDeviceConfService.list(true, pagingParam);
            PageResponse<BizDeviceConf> devPageResp = bizDeviceConfService.listByParam(Boolean.valueOf(pagingParam.getNeedPaging()), pagingParam);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, devPageResp);
            return mr;
        }
    }

    /**
     * 查询结果列表（增加电量和定位）
     * @param param 查询及分页参数
     * @param request
     * <pre>
     *     {
     *         keyStr:查询关键字（非必填，模糊查询，可填写设备ID）,
     *         deviceType:设备ID（非必填，包括VEHICLE、BATTERY、PARTS）,
     *         needPaging:是否需要分页（仅为false时不需要分页，其余情况均需要分页）,
     *         currPage:当前页（needPaging不为false时必填）,
     *         pageSize:每页记录数（needPaging不为false时必填）
     *     }
     * </pre>
     * @return
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
     *                 request:主动请求数据标志（0：无处理；1：主动请求）,
     *                 RSOC:电量，
     *                 LON:经度，
     *                 LAT:纬度
     *             },
     *             ... ...
     *         ]
     *     }
     * </pre>
     */
    @RequestMapping(value = "/lists",method = RequestMethod.POST)
    public MessageResponse lists(@RequestBody String param,HttpServletRequest request){
        if (WzStringUtil.isBlank(param)) {
            //参数为空，则报无参数
            return new MessageResponse(RunningResult.NO_PARAM);
        }
        BizDeviceConfParam bizDeviceConfParam = null;
        try {
            String paramStr = URLDecoder.decode(param, "utf-8");
            bizDeviceConfParam = JSONObject.parseObject(paramStr,BizDeviceConfParam.class);
            if (bizDeviceConfParam == null) {
                //参数解析失败
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
            }
            SysUser sysUser = super.getLoginUserInfo(request);
            if (sysUser == null) {
                //用户认证超时
                return new MessageResponse(RunningResult.AUTH_OVER_TIME);
            }
            if (sysUser.getUserType().toString().equals(OrgAndUserType.ENTERPRISE.toString())
                    || sysUser.getUserType().toString().equals(OrgAndUserType.INDIVIDUAL.toString())) {
                //如果是企业和个人则无法调用该接口
                return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
            }
            if (WzStringUtil.isNotBlank(bizDeviceConfParam.getNeedPaging())
                    && bizDeviceConfParam.getNeedPaging().equals("false")) {
                //是否分页不能为空并且是false
                bizDeviceConfParam.setNeedPaging("false");
            }
            if (bizDeviceConfParam.getCurrPage() == null
                    || bizDeviceConfParam.getPageSize() == null) {
                //如果当前页和每页显示数量为空的话则提示未获得参数
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(),"未获得分页参数");
            }
            //分页参数为true
            bizDeviceConfParam.setNeedPaging("true");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new MessageResponse(RunningResult.SUCCESS,bizDeviceConfService.lists(Boolean.valueOf(bizDeviceConfParam.getNeedPaging()),bizDeviceConfParam));
    }

    /**
     * 批量增加参数设置.
     * @param addParam 批量新增参数列表JSON
     * @param request
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
    public MessageResponse add(@RequestBody String addParam,HttpServletRequest request) {
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
                SysUser sysUser = getLoginUserInfo(request);
                if(sysUser != null){
                    //只有平台用户可以操作
                    if(!OrgAndUserType.PLATFORM.toString().equals(sysUser.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                BizDeviceConf insDevVo = null;
                for (int i = 0; i < devConfInfos.size(); i++) {
                    insDevVo = devConfInfos.get(i);
                    if (WzStringUtil.isBlank(insDevVo.getDeviceId())
                            || null == insDevVo.getDeviceType()) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "第" + i + "条记录设备ID及类别不能为空");
                    }
                    if (!insDevVo.getDeviceType().toString().equals(DeviceType.BATTERY.toString())
                            && !insDevVo.getDeviceType().toString().equals(DeviceType.VEHICLE.toString())
                            && !insDevVo.getDeviceType().toString().equals(DeviceType.PARTS.toString())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "第" + i + "条记录设备类别无效");
                    }
                    if (null == insDevVo.getPerSet()
                            && null == insDevVo.getReset()
                            && null == insDevVo.getRequest()) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "第" + i + "条记录无需更新");
                    }
                }
                bizDeviceConfService.insertBizDeviceConfs(devConfInfos);
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
     * 增加设备参数.
     * @param addParam 新增参数列表JSON
     * @param request
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
    public MessageResponse addOne(@RequestBody String addParam,HttpServletRequest request) {
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
                SysUser sysUser = getLoginUserInfo(request);
                if(sysUser != null){
                    //只有平台用户可以操作
                    if(!OrgAndUserType.PLATFORM.toString().equals(sysUser.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                if (WzStringUtil.isBlank(devConfInfo.getDeviceId())
                        || null == devConfInfo.getDeviceType()) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "设备ID及类别不能为空");
                }
                if (!devConfInfo.getDeviceType().toString().equals(DeviceType.BATTERY.toString())
                        && !devConfInfo.getDeviceType().toString().equals(DeviceType.VEHICLE.toString())
                        && !devConfInfo.getDeviceType().toString().equals(DeviceType.PARTS.toString())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的设备类别");
                }
                if (null == devConfInfo.getPerSet()
                        && null == devConfInfo.getReset()
                        && null == devConfInfo.getRequest()) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无需更新");
                }
                bizDeviceConfService.insertBizDeviceConf(devConfInfo);
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
     * 修改设备参数信息.
     * @param modifyParam 修改参数JSON
     * @param request
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
    public MessageResponse modify(@RequestBody String modifyParam,HttpServletRequest request) {
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
                SysUser sysUser = getLoginUserInfo(request);
                if(sysUser != null){
                    //只有平台用户可以操作
                    if(!OrgAndUserType.PLATFORM.toString().equals(sysUser.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                if (WzStringUtil.isBlank(devConfInfo.getDeviceId())
                        || null == devConfInfo.getDeviceType()) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "设备ID及类别不能为空");
                }
                if (!devConfInfo.getDeviceType().toString().equals(DeviceType.BATTERY.toString())
                        && !devConfInfo.getDeviceType().toString().equals(DeviceType.VEHICLE.toString())
                        && !devConfInfo.getDeviceType().toString().equals(DeviceType.PARTS.toString())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的设备类别");
                }
                if (null == devConfInfo.getPerSet()
                        && null == devConfInfo.getReset()
                        && null == devConfInfo.getRequest()) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无需更新");
                }
                bizDeviceConfService.updateBizDeviceConf(devConfInfo);
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 修改后进行缓存
            redisClient.valueOperations().set(WzConstants.GK_DEVICE_CONF + devConfInfo.getDeviceId() + WzConstants.KEY_SPLIT + devConfInfo.getDeviceType().toString(), devConfInfo, 30, TimeUnit.MINUTES);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 批量删除设备参数.
     * @param deleteParam 删除ID列表JSON
     * @param request
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
    public MessageResponse delete(@RequestBody String deleteParam,HttpServletRequest request) {
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
                SysUser sysUser = getLoginUserInfo(request);
                if(sysUser != null){
                    //只有平台用户可以操作
                    if(!OrgAndUserType.PLATFORM.toString().equals(sysUser.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                bizDeviceConfService.deleteBizDeviceConfs(devConfIds);
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
     * 根据设备ID和设备类别获取设置信息.
     * @param paramPK 查询参数
     * @param request
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
    public MessageResponse getByPK(@RequestBody String paramPK,HttpServletRequest request) {
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
                SysUser sysUser = getLoginUserInfo(request);
                if(sysUser != null){
                    //只有平台用户可以操作
                    if(!OrgAndUserType.PLATFORM.toString().equals(sysUser.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
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
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            BizDeviceConf devConf = bizDeviceConfService.getBizDeviceConfByPrimaryKey(devConfKey);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, devConf);
            return mr;
        }
    }

    /**
     * 根据设备id查询当前设备的电量
     * <pre>
     *     [deviceId]
     * </pre>
     * @param param 设备id
     * @param request 登录
     * @return
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:{
     *             RSOC:电量
     *         }
     *     }
     * </pre>
     */
    @RequestMapping(value = "/getElectricByDevice",method = RequestMethod.POST)
    public MessageResponse getElectricByDevice(@RequestBody String param,HttpServletRequest request) {
        if (WzStringUtil.isBlank(param)) {
            return new MessageResponse(RunningResult.NO_PARAM);
        }
        List<String> list = null;
        try {
            String paramStr = URLDecoder.decode(param,"utf-8");
            list = JSONObject.parseObject(paramStr,List.class);
            if (WzStringUtil.isBlank(list.get(0))) {
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
            }
            SysUser sysUser = super.getLoginUserInfo(request);
            if (sysUser == null) {
                return new MessageResponse(RunningResult.AUTH_OVER_TIME);
            }
            if (sysUser.getUserType().toString().equals(OrgAndUserType.INDIVIDUAL.toString())
                    || sysUser.getUserType().toString().equals(OrgAndUserType.ENTERPRISE.toString())) {
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(),"无权限操作");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new MessageResponse(RunningResult.SUCCESS,bizDeviceConfService.getElectricByDevice(list.get(0)));
    }

    /**
     * 根据设备id查询当前设备的定位
     * <pre>
     *     {
     *         deviceId:设备ID,
     *         deviceType:设备类别（VEHICLE-车辆、BATTERY-电池、PARTS-配件）,
     *     }
     * </pre>
     * @param param 设备id
     * @param request 登录
     * @return
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:{
     *
     *         }
     *     }
     * </pre>
     */
    @RequestMapping(value = "/getLocationByDevice",method = RequestMethod.POST)
    public MessageResponse getLocationByDevice(@RequestBody String param,HttpServletRequest request) {
        if (WzStringUtil.isBlank(param)) {
            return new MessageResponse(RunningResult.NO_PARAM);
        }
        BizDeviceConfKey bizDeviceConfKey = null;
        try {
            String paramStr = URLDecoder.decode(param,"utf-8");
            bizDeviceConfKey = JSONObject.parseObject(paramStr,BizDeviceConfKey.class);
            if (bizDeviceConfKey == null){
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
            }
            SysUser sysUser = super.getLoginUserInfo(request);
            if (sysUser == null) {
                return new MessageResponse(RunningResult.AUTH_OVER_TIME);
            }
            if (sysUser.getUserType().toString().equals(OrgAndUserType.ENTERPRISE.toString())
                    || sysUser.getUserType().toString().equals(OrgAndUserType.INDIVIDUAL.toString())) {
                return new MessageResponse(RunningResult.NO_PERMISSION);
            }
            if (WzStringUtil.isBlank(bizDeviceConfKey.getDeviceId())) {
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(),"设备id不能为空");
            }
            if (!bizDeviceConfKey.getDeviceType().toString().equals(DeviceType.BATTERY.toString())
                    && !bizDeviceConfKey.getDeviceType().toString().equals(DeviceType.VEHICLE.toString())
                    && !bizDeviceConfKey.getDeviceType().toString().equals(DeviceType.PARTS.toString())) {
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(),"设备类型错误");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new MessageResponse(RunningResult.SUCCESS,bizDeviceConfService.getLocationByDevice(bizDeviceConfKey));
    }
    

}
