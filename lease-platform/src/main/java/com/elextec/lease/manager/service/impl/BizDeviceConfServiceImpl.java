package com.elextec.lease.manager.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.plugins.redis.RedisClient;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.device.common.DeviceApiConstants;
import com.elextec.lease.manager.request.BizBatteryParam;
import com.elextec.lease.manager.request.BizDeviceConfParam;
import com.elextec.lease.manager.service.BizDeviceConfService;
import com.elextec.persist.dao.mybatis.BizDeviceConfMapperExt;
import com.elextec.persist.field.enums.DeviceType;
import com.elextec.persist.model.mybatis.BizDeviceConf;
import com.elextec.persist.model.mybatis.BizDeviceConfExample;
import com.elextec.persist.model.mybatis.BizDeviceConfKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 设备参数设置管理Service实现类.
 * Created by wangtao on 2018/1/16.
 */
@Service
public class BizDeviceConfServiceImpl implements BizDeviceConfService {

    @Autowired
    private BizDeviceConfMapperExt bizDeviceConfMapperExt;

    @Autowired
    private RedisClient redisClient;

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
    public PageResponse<Map<String,Object>> lists(boolean needPaging, BizDeviceConfParam pr) {
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
        //取得设备信息
        List<BizDeviceConf> devLs = bizDeviceConfMapperExt.selectByExample(devLsExample);
        //获取设备电量和定位
        Map<String,Object> map;
        JSONObject electric = null;//电量
        JSONObject location = null;//定位
        List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
        for (BizDeviceConf bizDeviceConf:devLs) {
            map = new HashMap<String,Object>();
            map.put("perSet",bizDeviceConf.getPerSet());//请求间隔时间
            map.put("reset",bizDeviceConf.getReset());//硬件复位标志
            map.put("request",bizDeviceConf.getRequest());//主动请求数据标志
            map.put("deviceId",bizDeviceConf.getDeviceId());//设备ID,
            map.put("deviceType",bizDeviceConf.getDeviceType());//设备类别
            electric =  (JSONObject)redisClient.hashOperations().get(WzConstants.GK_DEVIE_POWER_MAP, bizDeviceConf.getDeviceId() + WzConstants.KEY_SPLIT + DeviceType.BATTERY.toString());
            if (electric != null) {
                //取到设备的电量就传进map
                map.put(DeviceApiConstants.REQ_RSOC, electric.getString(DeviceApiConstants.REQ_RSOC));
            } else {
                //如果设备电量为空也是传进map
                map.put(DeviceApiConstants.REQ_RSOC,"");
            }
            //获取当前设备的定位
            location = (JSONObject) redisClient.hashOperations().get(WzConstants.GK_DEVICE_LOC_MAP,bizDeviceConf.getDeviceId() + WzConstants.KEY_SPLIT + bizDeviceConf.getDeviceType());
            if (location != null) {
                map.put(DeviceApiConstants.REQ_LON,location.getString(DeviceApiConstants.REQ_LON));//获取经度
                map.put(DeviceApiConstants.REQ_LAT,location.getString(DeviceApiConstants.REQ_LAT));//获取纬度
            } else {
                map.put(DeviceApiConstants.REQ_LON,"");
                map.put(DeviceApiConstants.REQ_LAT,"");
            }
            mapList.add(map);
        }
        // 组织并返回结果
        PageResponse<Map<String,Object>> presp = new PageResponse<Map<String,Object>>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(devTotal);
        if (null == mapList) {
            presp.setRows(new ArrayList<Map<String,Object>>());
        } else {
            presp.setRows(mapList);
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
    public Map<String,Object> getElectricByDevice(String deviceId) {
        JSONObject jsonObject =  (JSONObject)redisClient.hashOperations().get(WzConstants.GK_DEVIE_POWER_MAP, deviceId + WzConstants.KEY_SPLIT + DeviceType.BATTERY.toString());
        Map<String,Object> map = new HashMap<String,Object>();
        if (jsonObject != null) {
            map.put(DeviceApiConstants.REQ_RSOC,jsonObject.getString(DeviceApiConstants.REQ_RSOC));
        } else {
            map.put(DeviceApiConstants.REQ_RSOC,"");
        }
        return map;
    }

    @Override
    public Map<String, Object> getLocationByDevice(BizDeviceConfKey bizDeviceConfKey) {
        JSONObject jsonObject = (JSONObject) redisClient.hashOperations().get(WzConstants.GK_DEVICE_LOC_MAP,bizDeviceConfKey.getDeviceId() + WzConstants.KEY_SPLIT + bizDeviceConfKey.getDeviceType());
        Map<String,Object> map = new HashMap<String,Object>();
        if (jsonObject != null) {
            map.put(DeviceApiConstants.REQ_LON,jsonObject.getString(DeviceApiConstants.REQ_LON));//经度
            map.put(DeviceApiConstants.REQ_LAT,jsonObject.getString(DeviceApiConstants.REQ_LAT));//纬度
        } else {
            map.put(DeviceApiConstants.REQ_LON,"");//经度
            map.put(DeviceApiConstants.REQ_LAT,"");//纬度
        }
        return map;
    }
}
