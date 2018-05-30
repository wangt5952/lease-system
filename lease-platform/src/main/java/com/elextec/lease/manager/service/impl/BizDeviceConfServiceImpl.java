package com.elextec.lease.manager.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzGPSUtil;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.device.common.DeviceApiConstants;
import com.elextec.lease.device.common.DeviceRespMsg;
import com.elextec.lease.manager.request.BizDeviceConfParam;
import com.elextec.lease.manager.service.BizDeviceConfService;
import com.elextec.lease.manager.service.BizVehicleTrackService;
import com.elextec.persist.dao.mybatis.BizDeviceConfMapperExt;
import com.elextec.persist.field.enums.DeviceType;
import com.elextec.persist.model.mybatis.BizDeviceConf;
import com.elextec.persist.model.mybatis.BizDeviceConfExample;
import com.elextec.persist.model.mybatis.BizDeviceConfKey;
import com.elextec.persist.model.mybatis.BizVehicleTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 设备参数设置管理Service实现类.
 * Created by wangtao on 2018/1/16.
 */
@Service
public class BizDeviceConfServiceImpl extends BaseController implements BizDeviceConfService {
    /**
     * 日志.
     */
    private final Logger logger = LoggerFactory.getLogger(BizDeviceConfServiceImpl.class);

    /**
     * 错误Code 成功为0.
     */
    private static final String RESP_ERR_CODE = "errorcode";

    /**
     * 错误消息.
     */
    private static final String RESP_ERR_MSG = "errormsg";

    @Value("${localsetting.track-stay-time}")
    private Long trackStayTime;

    @Autowired
    private BizDeviceConfMapperExt bizDeviceConfMapperExt;

    @Autowired
    private BizVehicleTrackService bizVehicleTrackService;

    @Override
    public PageResponse<BizDeviceConf> list(boolean needPaging, PageRequest pr) {
        // 查询总记录数
        int devTotal = 0;
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
            devTotal = pr.getTotal();
        } else {
            BizDeviceConfExample devCountExample = new BizDeviceConfExample();
            devCountExample.setDistinct(true);
            devTotal = bizDeviceConfMapperExt.countByExample(devCountExample);
        }
        // 分页查询
        BizDeviceConfExample devLsExample = new BizDeviceConfExample();
        devLsExample.setDistinct(true);
        if (needPaging) {
            devLsExample.setPageBegin(pr.getPageBegin());
            devLsExample.setPageSize(pr.getPageSize());
        }
        List<BizDeviceConf> devLs = bizDeviceConfMapperExt.selectByExample(devLsExample);
        // 组织并返回结果
        PageResponse<BizDeviceConf> presp = new PageResponse<BizDeviceConf>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(devTotal);
        if (null == devLs) {
            presp.setRows(new ArrayList<BizDeviceConf>());
        } else {
            presp.setRows(devLs);
        }
        return presp;
    }

    @Override
    public PageResponse<BizDeviceConf> listByParam(boolean needPaging, BizDeviceConfParam pr) {
        // 查询总记录数
        int devTotal = 0;
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
            devTotal = pr.getTotal();
        } else {
            BizDeviceConfExample devCountExample = new BizDeviceConfExample();
            devCountExample.setDistinct(true);
            BizDeviceConfExample.Criteria devCountCri = devCountExample.createCriteria();
            if (WzStringUtil.isNotBlank(pr.getKeyStr())) {
                devCountCri.andDeviceIdLike("%" + pr.getKeyStr() + "%");
            }
            if (WzStringUtil.isNotBlank(pr.getDeviceType())) {
                devCountCri.andDeviceIdEqualTo(pr.getDeviceType());
            }
            devTotal = bizDeviceConfMapperExt.countByExample(devCountExample);
        }
        // 分页查询
        BizDeviceConfExample devLsExample = new BizDeviceConfExample();
        devLsExample.setDistinct(true);
        BizDeviceConfExample.Criteria devLsCri = devLsExample.createCriteria();
        if (WzStringUtil.isNotBlank(pr.getKeyStr())) {
            devLsCri.andDeviceIdLike("%" + pr.getKeyStr() + "%");
        }
        if (WzStringUtil.isNotBlank(pr.getDeviceType())) {
            devLsCri.andDeviceIdEqualTo(pr.getDeviceType());
        }
        if (needPaging) {
            devLsExample.setPageBegin(pr.getPageBegin());
            devLsExample.setPageSize(pr.getPageSize());
        }
        List<BizDeviceConf> devLs = bizDeviceConfMapperExt.selectByExample(devLsExample);
        // 组织并返回结果
        PageResponse<BizDeviceConf> presp = new PageResponse<BizDeviceConf>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(devTotal);
        if (null == devLs) {
            presp.setRows(new ArrayList<BizDeviceConf>());
        } else {
            presp.setRows(devLs);
        }
        return presp;
    }

    @Override
    public void insertBizDeviceConfs(List<BizDeviceConf> deviceConfs) {
        int i = 0;
        BizDeviceConf insertVo = null;
        try {
            for (; i < deviceConfs.size(); i++) {
                insertVo = deviceConfs.get(i);
                bizDeviceConfMapperExt.insertSelective(insertVo);
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录插入时发生错误", ex);
        }
    }

    @Override
    public void insertBizDeviceConf(BizDeviceConf deviceConf) {
        // 设备重复提示错误
        BizDeviceConfExample devExample = new BizDeviceConfExample();
        BizDeviceConfExample.Criteria devCriteria = devExample.createCriteria();
        devCriteria.andDeviceIdEqualTo(deviceConf.getDeviceId());
        devCriteria.andDeviceTypeEqualTo(deviceConf.getDeviceType());
        int devCnt = bizDeviceConfMapperExt.countByExample(devExample);
        if (0 < devCnt) {
            throw new BizException(RunningResult.MULTIPLE_RECORD.code(), "该设备已存在");
        }
        // 保存用户信息
        try {
            bizDeviceConfMapperExt.insertSelective(deviceConf);
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "记录插入时发生错误", ex);
        }
    }

    @Override
    public void updateBizDeviceConf(BizDeviceConf deviceConf) {
        bizDeviceConfMapperExt.updateByPrimaryKeySelective(deviceConf);
    }

    @Override
    public void deleteBizDeviceConfs(List<BizDeviceConfKey> deviceConfKeys) {
        int i = 0;
        try {
            for (; i < deviceConfKeys.size(); i++) {
                bizDeviceConfMapperExt.deleteByPrimaryKey(deviceConfKeys.get(i));
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
        }
    }

    @Override
    public BizDeviceConf getBizDeviceConfByPrimaryKey(BizDeviceConfKey key) {
        return bizDeviceConfMapperExt.selectByPrimaryKey(key);
    }

    @Override
    @JmsListener(destination = "sensorData")
    public void sensorData(String info) {
        System.out.println("成功监听sensorData消息队列，传来的值为:" + info);
        // 解析参数
        JSONObject sensorData = null;
        // 无参数情况
        if (WzStringUtil.isBlank(info)) {
            throw new BizException(RunningResult.NO_PARAM.code(), "请求参数不能为空");
            // 有参数情况
        } else {
            try {
                String paramStr = URLDecoder.decode(info, "utf-8");
                sensorData = JSONObject.parseObject(paramStr);
                // 解析失败
                if (null == sensorData) {
                    throw new BizException(RunningResult.PARAM_ANALYZE_ERROR.code(), "参数解析失败");
                }
            } catch (Exception ex) {
                logger.error(DeviceRespMsg.PARAM_ANALYZE_ERROR.getInfo(), ex);

            }
            try {
                // 获得设备数据
                // 设备ID
                String deviceId = null;
                // 设备类别
                String deviceType = null;
                // 设备版本
                String version = null;
                // 电池生产日期
                String madeDate = null;
                // 电池数据
                JSONObject deviceData = null;
                // 电池总电压值
                String tv = null;
                // 电池保护板版本号
                String pv = null;
                // 电池剩余容量（百分比）
                String rsoc = null;
                // 电路板剩余电量（百分比）
                String quanity = null;
                // 保护状态,不同数字保护状态说明不一
                String ps = null;
                // 经度
                Double lon = null;
                // 纬度
                Double lat = null;
                // 获取固定参数
                deviceId = sensorData.getString(DeviceApiConstants.REQ_RESP_DEVICE_ID);
                deviceType = sensorData.getString(DeviceApiConstants.REQ_DEVICE_TYPE);
                version = WzStringUtil.defaultIfBlank(sensorData.getString(DeviceApiConstants.REQ_VERSION), "");
                madeDate = WzStringUtil.defaultIfBlank(sensorData.getString(DeviceApiConstants.REQ_DATE), "");
                tv = WzStringUtil.defaultIfBlank(sensorData.getString(DeviceApiConstants.REQ_TV), "");
                pv = WzStringUtil.defaultIfBlank(sensorData.getString(DeviceApiConstants.REQ_PV), "");
                try {
                    lon = sensorData.getDouble(DeviceApiConstants.REQ_LON);
                    lat = sensorData.getDouble(DeviceApiConstants.REQ_LAT);
                } catch (Exception ex) {
                    lon = null;
                    lat = null;
                }
                deviceData = sensorData.getJSONObject(DeviceApiConstants.REQ_DEVICE_DATA);
                if (null != deviceData) {
                    rsoc = WzStringUtil.defaultIfBlank(deviceData.getString(DeviceApiConstants.REQ_RSOC), "");
                    quanity = WzStringUtil.defaultIfBlank(deviceData.getString(DeviceApiConstants.REQ_QUANITY), "");
                    ps = WzStringUtil.defaultIfBlank(deviceData.getString(DeviceApiConstants.REQ_PS), "");
                }
                // 关键字不能为空
                if (WzStringUtil.isBlank(deviceId) || WzStringUtil.isBlank(deviceType)) {
                    throw new BizException(RunningResult.NO_PARAM.code(), "关键字不能为空");
                }
                // 关键字
                String devicePk = deviceId + WzConstants.KEY_SPLIT + deviceType;
                // 将关键字记录到列表进行缓存
                super.redisClient.setOperations().add(WzConstants.GK_DEVICE_PK_SET, devicePk);
                // 记录当前位置信息及轨迹信息到缓存
                if (null != lat && null != lon) {
                    long sysTime = System.currentTimeMillis();
                    // 组装当前位置信息
                    JSONObject locVo = new JSONObject();
                    locVo.put(DeviceApiConstants.REQ_RESP_DEVICE_ID, deviceId);
                    locVo.put(DeviceApiConstants.REQ_DEVICE_TYPE, deviceType);
                    locVo.put(DeviceApiConstants.KEY_LOC_TIME, new Long(sysTime));
                    locVo.put(DeviceApiConstants.REQ_LAT, lat);
                    locVo.put(DeviceApiConstants.REQ_LON, lon);
                    // 记录当前定位
                    JSONObject nowLocVo = (JSONObject) redisClient.hashOperations().get(WzConstants.GK_DEVICE_LOC_MAP, devicePk);
                    if (null == nowLocVo
                            || (!WzGPSUtil.outOfChina(lat.doubleValue(), lon.doubleValue())
                            && (lat.doubleValue() != nowLocVo.getDoubleValue(DeviceApiConstants.REQ_LAT)
                            || lon.doubleValue() != nowLocVo.getDoubleValue(DeviceApiConstants.REQ_LON)))) {
                        redisClient.hashOperations().put(WzConstants.GK_DEVICE_LOC_MAP, devicePk, locVo);
                    }
                    // 记录轨迹信息
                    if (null == trackStayTime) {
                        trackStayTime = DeviceApiConstants.TRACK_STAY_TIME;
                    }
                    // 轨迹信息
                    String trackKey = WzConstants.GK_DEVICE_TRACK + devicePk;
                    // 组装轨迹定位信息
                    JSONObject trackLocVo = new JSONObject();
                    trackLocVo.put(DeviceApiConstants.KEY_LOC_TIME, new Long(sysTime));
                    trackLocVo.put(DeviceApiConstants.REQ_LAT, lat);
                    trackLocVo.put(DeviceApiConstants.REQ_LON, lon);
                    // 获得上次记录的最终位置
                    Set<Object> lastLocSet = redisClient.zsetOperations().reverseRange(trackKey, 0, 0);
                    if (null == lastLocSet || 0 == lastLocSet.size()) {
                        redisClient.zsetOperations().add(trackKey, trackLocVo, sysTime);
                    } else {
                        List<Object> lastLocLs = new ArrayList<Object>(lastLocSet);
                        JSONObject lastLocVo = (JSONObject) lastLocLs.get(0);
                        if (!WzGPSUtil.outOfChina(lat.doubleValue(), lon.doubleValue())
                                && (lat.doubleValue() != lastLocVo.getDoubleValue(DeviceApiConstants.REQ_LAT)
                                || lon.doubleValue() != lastLocVo.getDoubleValue(DeviceApiConstants.REQ_LON))) {
                            // 停留时间超过设定的时间则认为之前的轨迹为一个完成轨迹链，需要从缓存中取出并存到数据库中
                            if (trackStayTime < (sysTime - lastLocVo.getLongValue(DeviceApiConstants.KEY_LOC_TIME))) {
                                Set<Object> lastLocSetForSave = redisClient.zsetOperations().range(trackKey, 0, -1);
                                // 整理并保存轨迹链
                                int trackSize = lastLocSetForSave.size();
                                int i = 0;
                                long sTime = System.currentTimeMillis();
                                long eTime = System.currentTimeMillis();
                                JSONObject locVoForSave = null;
                                StringBuffer trackStr = new StringBuffer("");
                                for (Object locForSave : lastLocSetForSave) {
                                    locVoForSave = (JSONObject) locForSave;
                                    trackStr.append(locVoForSave.getLongValue(DeviceApiConstants.KEY_LOC_TIME))
                                            .append(WzConstants.KEY_COMMA)
                                            .append(locVoForSave.getDoubleValue(DeviceApiConstants.REQ_LAT))
                                            .append(WzConstants.KEY_COMMA)
                                            .append(locVoForSave.getDoubleValue(DeviceApiConstants.REQ_LON))
                                            .append(WzConstants.KEY_SEMICOLON);
                                    if (0 == i) {
                                        sTime = locVoForSave.getLongValue(DeviceApiConstants.KEY_LOC_TIME);
                                    }
                                    if ((trackSize - 1) == i) {
                                        eTime = locVoForSave.getLongValue(DeviceApiConstants.KEY_LOC_TIME);
                                    }
                                    i++;
                                }
                                BizVehicleTrack bvt = new BizVehicleTrack();
                                bvt.setDeviceId(deviceId);
                                bvt.setDeviceType(WzStringUtil.isBlank(deviceType) ? DeviceType.BATTERY.toString() : deviceType);
                                bvt.setStartTime(sTime);
                                bvt.setEndTime(eTime);
                                bvt.setTaskInfo(WzUniqueValUtil.makeUUID());
                                bvt.setLocations(trackStr.toString());
                                // 保存轨迹链
                                bizVehicleTrackService.insertVehicleTrack(bvt);
                                // 清空已保存的轨迹链
                                redisClient.zsetOperations().removeRange(trackKey, 0, -1);
                            }
                            // 插入该次定为数据
                            redisClient.zsetOperations().add(trackKey, trackLocVo, sysTime);
                        }
                    }
                }
                // 记录电池等变化信息
                // 整理电量变化相关信息
                JSONObject powerVo = new JSONObject();
                powerVo.put(DeviceApiConstants.REQ_RSOC, rsoc);
                powerVo.put(DeviceApiConstants.REQ_QUANITY, quanity);
                powerVo.put(DeviceApiConstants.REQ_PS, ps);
                JSONObject lastPowerVo = (JSONObject) redisClient.hashOperations().get(WzConstants.GK_DEVIE_POWER_MAP, devicePk);
                if (null == lastPowerVo) {
                    redisClient.hashOperations().put(WzConstants.GK_DEVIE_POWER_MAP, devicePk, powerVo);
                } else {
                    if (WzStringUtil.isNotBlank(rsoc)) {
                        lastPowerVo.put(DeviceApiConstants.REQ_RSOC, rsoc);
                    }
                    if (WzStringUtil.isNotBlank(quanity)) {
                        lastPowerVo.put(DeviceApiConstants.REQ_QUANITY, quanity);
                    }
                    if (WzStringUtil.isNotBlank(ps)) {
                        lastPowerVo.put(DeviceApiConstants.REQ_PS, ps);
                    }
                    redisClient.hashOperations().put(WzConstants.GK_DEVIE_POWER_MAP, devicePk, lastPowerVo);
                }
                // 保存该设备的基本信息
                // 整理设备基本信息
                JSONObject deviceParamVo = new JSONObject();
                deviceParamVo.put(DeviceApiConstants.REQ_VERSION, version);
                deviceParamVo.put(DeviceApiConstants.REQ_DATE, madeDate);
                deviceParamVo.put(DeviceApiConstants.REQ_PV, pv);
                deviceParamVo.put(DeviceApiConstants.REQ_TV, tv);
                JSONObject lastDeviceParamVo = (JSONObject) redisClient.hashOperations().get(WzConstants.GK_DEVICE_PARAM_MAP, devicePk);
                if (null == lastDeviceParamVo) {
                    redisClient.hashOperations().put(WzConstants.GK_DEVICE_PARAM_MAP, devicePk, deviceParamVo);
                } else {
                    if (WzStringUtil.isNotBlank(version)) {
                        lastDeviceParamVo.put(DeviceApiConstants.REQ_VERSION, version);
                    }
                    if (WzStringUtil.isNotBlank(madeDate)) {
                        lastDeviceParamVo.put(DeviceApiConstants.REQ_DATE, madeDate);
                    }
                    if (WzStringUtil.isNotBlank(pv)) {
                        lastDeviceParamVo.put(DeviceApiConstants.REQ_PV, pv);
                    }
                    if (WzStringUtil.isNotBlank(tv)) {
                        lastDeviceParamVo.put(DeviceApiConstants.REQ_TV, tv);
                    }
                    redisClient.hashOperations().put(WzConstants.GK_DEVICE_PARAM_MAP, devicePk, lastDeviceParamVo);
                }
            } catch (BizException ex) {
                logger.error("接口调用出现异常", ex);
                throw new BizException(RunningResult.BAD_REQUEST.code(), "接口调用异常");
            } catch (Exception ex) {
                logger.error("接口调用出现异常", ex);
                throw new BizException(RunningResult.BAD_REQUEST.code(), "接口调用异常");
            }
        }
    }
}
