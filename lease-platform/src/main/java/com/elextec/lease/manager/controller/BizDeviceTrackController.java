package com.elextec.lease.manager.controller;

import com.alibaba.fastjson.JSONObject;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.utils.WzGPSUtil;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.device.common.DeviceApiConstants;
import com.elextec.lease.manager.request.BizDeviceTrackParam;
import com.elextec.lease.manager.request.LocAndRadiusParam;
import com.elextec.lease.manager.service.BizDeviceConfService;
import com.elextec.lease.manager.service.BizDeviceTrackService;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.model.mybatis.BizDeviceTrack;
import com.elextec.persist.model.mybatis.BizDeviceTrackKey;
import com.elextec.persist.model.mybatis.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

@RestController
@RequestMapping(value = "/manager/deviceTrak")
public class BizDeviceTrackController extends BaseController {

    @Autowired
    private BizDeviceTrackService bizDeviceTrackService;

    /**
     * 查询设备轨迹表（分页）
     * <pre>
     *     {
     *         keyStr:查询关键字（非必填，模糊查询，可填写设备ID）,
     *         needPaging:是否需要分页（仅为false时不需要分页，其余情况均需要分页）,
     *         currPage:当前页（needPaging不为false时必填）,
     *         pageSize:每页记录数（needPaging不为false时必填）
     *     }
     * </pre>
     * @param param
     * @param request
     * @return
     * <pre>
     *   {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:[
     *             {
     *                 deviceId:设备ID,
     *                 location:经度，纬度；... ...
     *             },
     *             ... ...
     *         ]
     *     }
     * </pre>
     *
     */
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public MessageResponse list(@RequestBody String param, HttpServletRequest request) {
        //判断参数非空
        if (WzStringUtil.isBlank(param)) {
            return new MessageResponse(RunningResult.NO_PARAM);
        }
        BizDeviceTrackParam bizDeviceTrackParam = null;
        try {
            String paramStr = URLDecoder.decode(param,"utf-8");
            bizDeviceTrackParam = JSONObject.parseObject(paramStr,BizDeviceTrackParam.class);
            //判断转换对象非空
            if (bizDeviceTrackParam == null) {
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
            }
            SysUser sysUser = super.getLoginUserInfo(request);
            //登录用户不能为空
            if (sysUser == null) {
                return new MessageResponse(RunningResult.AUTH_OVER_TIME);
            }
            //登录用户类型只能是品台
            if (!sysUser.getUserType().toString().equals(OrgAndUserType.PLATFORM.toString())) {
                return new MessageResponse(RunningResult.NO_PERMISSION);
            }
            //needPaging标志为false时，不需要分页，其他情况均需要进行分页
            if (WzStringUtil.isNotBlank(bizDeviceTrackParam.getNeedPaging()) && bizDeviceTrackParam.getNeedPaging().equals("false")) {
                bizDeviceTrackParam.setNeedPaging("false");
            } else {
                if (bizDeviceTrackParam.getCurrPage() == null || bizDeviceTrackParam.getPageSize() == null) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR,"未获得分页参数");
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new MessageResponse(RunningResult.SUCCESS,bizDeviceTrackService.list(Boolean.valueOf(bizDeviceTrackParam.getNeedPaging()),bizDeviceTrackParam));
    }

    /**
     * 设备轨迹添加
     * <pre>
     *     {
     *         deviceId:设备id，
     *         lon:经度，
     *         lat:纬度
     *     }
     * </pre>
     * @param param
     * @param request
     * @return
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public MessageResponse add(@RequestBody String param, HttpServletRequest request) {
        if (WzStringUtil.isBlank(param)) {
            return new MessageResponse(RunningResult.NO_PARAM);
        }
        BizDeviceTrack bizDeviceTrack = null;
        try {
            String paramStr = URLDecoder.decode(param,"utf-8");
            bizDeviceTrack = JSONObject.parseObject(paramStr,BizDeviceTrack.class);
            if (bizDeviceTrack == null) {
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
            }
            SysUser sysUser = super.getLoginUserInfo(request);
            if (sysUser == null) {
                return new MessageResponse(RunningResult.AUTH_OVER_TIME);
            }
            if (!sysUser.getUserType().toString().equals(OrgAndUserType.PLATFORM.toString())) {
                return new MessageResponse(RunningResult.NO_PERMISSION);
            }
            if (WzStringUtil.isBlank(bizDeviceTrack.getDeviceId())
                    || bizDeviceTrack.getLat() == null
                    || bizDeviceTrack.getLon() == null) {
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(),"设备轨迹参数不能为空");
            }
            bizDeviceTrackService.add(bizDeviceTrack);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new MessageResponse(RunningResult.SUCCESS);
    }

    /**
     * 设备参数修改
     * <pre>
     *     {
     *         deviceId:设备id，
     *         locTime:当前时间戳，
     *         lon:经度，
     *         lat:纬度
     *     }
     * </pre>
     * @param param
     * @param request
     * @return
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(value = "/modify",method = RequestMethod.POST)
    public MessageResponse modify(@RequestBody String param, HttpServletRequest request) {
        if (WzStringUtil.isBlank(param)) {
            return new MessageResponse(RunningResult.NO_PARAM);
        }
        BizDeviceTrack bizDeviceTrack = null;
        try {
            String paramStr = URLDecoder.decode(param,"utf-8");
            bizDeviceTrack = JSONObject.parseObject(paramStr,BizDeviceTrack.class);
            if (bizDeviceTrack == null) {
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
            }
            SysUser sysUser = super.getLoginUserInfo(request);
            if (sysUser == null) {
                return new MessageResponse(RunningResult.AUTH_OVER_TIME);
            }
            if (!sysUser.getUserType().toString().equals(OrgAndUserType.PLATFORM.toString())) {
                return new MessageResponse(RunningResult.NO_PERMISSION);
            }
            if (WzStringUtil.isBlank(bizDeviceTrack.getDeviceId())) {
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(),"设备id不能为空");
            }
            bizDeviceTrackService.update(bizDeviceTrack);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new MessageResponse(RunningResult.SUCCESS);
    }

    /**
     * 删除设备轨迹
     * <pre>
     *     [
     *         {
     *             deviceId:设备ID
     *         },
     *         ... ...
     *     ]
     * </pre>
     * @param param
     * @param request
     * @return
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public MessageResponse delete(@RequestBody String param, HttpServletRequest request) {
        if (WzStringUtil.isBlank(param)) {
            return new MessageResponse(RunningResult.NO_PARAM);
        }
        List<BizDeviceTrackKey> list = null;
        try {
            String paramStr = URLDecoder.decode(param,"utf-8");
            list = JSONObject.parseArray(paramStr,BizDeviceTrackKey.class);
            if (list == null || list.size() == 0) {
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
            }
            SysUser sysUser = super.getLoginUserInfo(request);
            if (sysUser == null) {
                return new MessageResponse(RunningResult.AUTH_OVER_TIME);
            }
            if (!sysUser.getUserType().toString().equals(OrgAndUserType.PLATFORM.toString())) {
                return new MessageResponse(RunningResult.NO_PERMISSION);
            }
            bizDeviceTrackService.delete(list);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new MessageResponse(RunningResult.SUCCESS);
    }

    /**
     * 根据时间区间和设备id查询设备轨迹
     * <pre>
     *     {
     *         deviceId:设备id，
     *         startTime:起始时间，
     *         endTime:结束时间
     *     }
     * </pre>
     * @param param
     * @param request
     * @return
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:{
     *             location:[
     *                  [纬度，经度]，... ...
     *             ],
     *             deviceId:设备id
     *         }
     *     }
     * </pre>
     */
    @RequestMapping(value = "/getBypk",method = RequestMethod.POST)
    public MessageResponse getByPk(@RequestBody String param,HttpServletRequest request) {
        if (WzStringUtil.isBlank(param)) {
            return new MessageResponse(RunningResult.NO_PARAM);
        }
        Map<String,Object> map = null;
        try {
            String paramStr = URLDecoder.decode(param,"utf-8");
            map = JSONObject.parseObject(paramStr,Map.class);
            if (map == null || map.size() == 0) {
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
            }
            if (map.get("startTime") == null || map.get("startTime").toString().equals("")
                    || map.get("endTime") == null || map.get("endTime").toString().equals("")) {
               return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(),"起始时间，结束时间不能为空");
            }
            SysUser sysUser = super.getLoginUserInfo(request);
            if (sysUser == null) {
                return new MessageResponse(RunningResult.AUTH_OVER_TIME);
            }
            if (!sysUser.getUserType().toString().equals(OrgAndUserType.PLATFORM.toString())) {
                return new MessageResponse(RunningResult.NO_PERMISSION);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new MessageResponse(RunningResult.SUCCESS,bizDeviceTrackService.getByPk(map));
    }

    /**
     * 根据点位点和半径获取范围内设备信息
     * <pre>
     *     {
     *          lat:纬度，
     *          lng:经度，
     *          radius:半径，
     *     }
     * </pre>
     * @param param
     * @param request
     * @return
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:[
     *              {
     *                  locTime:时间,
     *                  lon:经度,
     *                  lat:纬度,
     *                  deviceId:设备id
     *              }，
     *              ... ...
     *         ]
     *     }
     * </pre>
     */
    @RequestMapping(value = "/getDeviceByLocationAndRadius",method = RequestMethod.POST)
    public MessageResponse getDeviceByLocationAndRadius(@RequestBody String param,HttpServletRequest request) {
        if (WzStringUtil.isBlank(param)) {
            return new MessageResponse(RunningResult.NO_PARAM);
        }
        LocAndRadiusParam locAndRadiusParam = null;
        try {
            String paramStr = URLDecoder.decode(param,"utf-8");
            locAndRadiusParam = JSONObject.parseObject(paramStr,LocAndRadiusParam.class);
            if (locAndRadiusParam == null) {
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
            }
            if (locAndRadiusParam.getLat() == null
                    || locAndRadiusParam.getLng() == null) {//经纬度不能为空
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(),"定位点信息异常");
            }
            if (locAndRadiusParam.getRadius() == null) {
                locAndRadiusParam.setRadius(new Double(1000));//默认给定半径范围是1000
            }
            //获取登录用户信息
            SysUser sysUser = super.getLoginUserInfo(request);
            if (sysUser == null) {
                return new MessageResponse(RunningResult.AUTH_OVER_TIME);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 将定位坐标转换为GPS坐标
        double[] wgsLatLng = WzGPSUtil.bd2wgs(locAndRadiusParam.getLat(), locAndRadiusParam.getLng());
        //获取所有设备的key值
        //Set<String> deviceKey = redisClient.hashOperations().keys(WzConstants.GK_DEVICE_LOC_MAP);
        List<BizDeviceTrack> deviceKey = bizDeviceTrackService.selectDistinctDeviceId();
        //存放范围内设备
        List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
        //判断在范围内的设备
        //for (String key: deviceKey) {
        for (int i = 0; i < deviceKey.size(); i++) {
            Map<String,Object> map = new HashMap<String,Object>();
            //获取当前设备
            //JSONObject jsonObject = (JSONObject)redisClient.hashOperations().get(WzConstants.GK_DEVICE_LOC_MAP,key);
            //获取当前设备最后一次定位
            List<BizDeviceTrack> lastLocation = bizDeviceTrackService.getLocationByDeviceId(deviceKey.get(i).getDeviceId());
            //计算当前定位点和当前设备的距离
            double twoPointDistance =  WzGPSUtil.calcDistanceByM(wgsLatLng[0],wgsLatLng[1],lastLocation.get(0).getLat(),lastLocation.get(0).getLon());
            //两点距离要小于等于给定的范围距离
            if (twoPointDistance <= locAndRadiusParam.getRadius()) {
                //范围内设备进行装配
                map.put("deviceId",lastLocation.get(0).getDeviceId().toString());
                map.put("LAT",lastLocation.get(0).getLat().toString());
                map.put("LON",lastLocation.get(0).getLon().toString());
                map.put("locTime",lastLocation.get(0).getLocTime().doubleValue());
                mapList.add(map);
//                Map<String,Object> deviceInfos = bizDeviceConfService.getRelationInformationByDevice(lastLocation.get(0).getDeviceId());
//                map.put(WzConstants.KEY_BATTERY_INFO,deviceInfos.get(WzConstants.KEY_BATTERY_INFO));
//                map.put(WzConstants.KEY_VEHICLE_INFO,deviceInfos.get(WzConstants.KEY_VEHICLE_INFO));
//                map.put(WzConstants.KEY_USER_INFO,deviceInfos.get(WzConstants.KEY_USER_INFO));
            } else {
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(),"该区域内没有设备");
            }
        }
        //}
        return new MessageResponse(RunningResult.SUCCESS,mapList);
    }

}
