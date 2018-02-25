package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.request.BizVehicleParam;
import com.elextec.lease.manager.request.VehicleBatteryParam;
import com.elextec.lease.manager.service.BizVehicleService;
import com.elextec.persist.dao.mybatis.BizBatteryMapperExt;
import com.elextec.persist.dao.mybatis.BizRefVehicleBatteryMapperExt;
import com.elextec.persist.dao.mybatis.BizVehicleMapperExt;
import com.elextec.persist.model.mybatis.*;
import com.elextec.persist.model.mybatis.ext.BizVehicleExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 车辆管理Service实现类.
 * Created by wangtao on 2018/1/16.
 */
@Service
public class BizVehicleServcieImpl implements BizVehicleService {
    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(BizVehicleServcieImpl.class);

    @Autowired
    private BizVehicleMapperExt bizVehicleMapperExt;

    @Autowired
    private BizBatteryMapperExt bizBatteryMapperExt;

    @Autowired
    private BizRefVehicleBatteryMapperExt bizRefVehicleBatteryMapperExt;


    @Override
    public PageResponse<BizVehicle> list(boolean needPaging, PageRequest pr) {
        // 查询总记录数
        int resTotal = 0;
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
            resTotal = pr.getTotal();
        } else {
            BizVehicleExample bizVehicleCountExample = new BizVehicleExample();
            bizVehicleCountExample.setDistinct(true);
            resTotal = bizVehicleMapperExt.countByExample(bizVehicleCountExample);
        }
        // 分页查询
        BizVehicleExample bizVehicleExample = new BizVehicleExample();
        bizVehicleExample.setDistinct(true);
        if (needPaging) {
            bizVehicleExample.setPageBegin(pr.getPageBegin());
            bizVehicleExample.setPageSize(pr.getPageSize());
        }
        List<BizVehicle> resLs = bizVehicleMapperExt.selectByExample(bizVehicleExample);
        // 组织并返回结果
        PageResponse<BizVehicle> presp = new PageResponse<BizVehicle>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(resTotal);
        if (null == resLs) {
            presp.setRows(new ArrayList<BizVehicle>());
        } else {
            presp.setRows(resLs);
        }
        return presp;
    }

    @Override
    public PageResponse<BizVehicleExt> listExtByParam(boolean needPaging, BizVehicleParam pr) {
        // 查询总记录数
        int vehicleTotal = 0;
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
            vehicleTotal = pr.getTotal();
        } else {
            vehicleTotal = bizVehicleMapperExt.countExtByParam(pr);
        }
        // 分页查询
        if (needPaging) {
            pr.setPageBegin();
        }
        List<BizVehicleExt> vehicleLs = bizVehicleMapperExt.selectExtByParam(pr);
        // 组织并返回结果
        PageResponse<BizVehicleExt> presp = new PageResponse<BizVehicleExt>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(vehicleTotal);
        if (null == vehicleLs) {
            presp.setRows(new ArrayList<BizVehicleExt>());
        } else {
            presp.setRows(vehicleLs);
        }
        return presp;
    }

    @Override
    public List<BizVehicleExt> listByLocation(long lng, long lat, int radius) {
        return null;
    }

    @Override
    @Transactional
    public void insertVehicles(List<VehicleBatteryParam> vehicleInfos) {
        int i = 0;
        BizVehicle insertVehicleVo = null;
        BizBattery insertBatteryVo = null;
        BizRefVehicleBattery temp = new BizRefVehicleBattery();
        try {
            for (; i < vehicleInfos.size(); i++) {
                //新车与新电池信息配对
                if("0".equals(vehicleInfos.get(i).getFlag())){
                    insertVehicleVo = vehicleInfos.get(i).getBizVehicleInfo();
                    insertBatteryVo = vehicleInfos.get(i).getBatteryInfo();
                    String vehicleId = WzUniqueValUtil.makeUUID();
                    String batteryId = WzUniqueValUtil.makeUUID();
                    //插入新车信息
                    insertVehicleVo.setId(vehicleId);
                    insertVehicleVo.setCreateTime(new Date());
                    bizVehicleMapperExt.insertSelective(insertVehicleVo);
                    //插入新电池信息
                    insertBatteryVo.setId(batteryId);
                    insertBatteryVo.setCreateTime(new Date());
                    bizBatteryMapperExt.insertSelective(insertBatteryVo);
                    //插入新车与新电池的MAP信息
                    temp.setVehicleId(vehicleId);
                    temp.setBatteryId(batteryId);
                    temp.setBindTime(new Date());
                    bizRefVehicleBatteryMapperExt.insertSelective(temp);
                }
                //新车与旧电池配对
                else if("1".equals(vehicleInfos.get(i).getFlag()))
                {
                    insertVehicleVo = vehicleInfos.get(i).getBizVehicleInfo();
                    String vehicleId = WzUniqueValUtil.makeUUID();
                    //插入新车信息
                    insertVehicleVo.setId(vehicleId);
                    insertVehicleVo.setCreateTime(new Date());
                    bizVehicleMapperExt.insertSelective(insertVehicleVo);
                    //插入新车与旧电池MAP信息
                    temp.setVehicleId(vehicleId);
                    temp.setBatteryId(vehicleInfos.get(i).getBatteryInfo().getId());
                    temp.setBindTime(new Date());
                    bizRefVehicleBatteryMapperExt.insertSelective(temp);
                }
                //只有新车信息
                else if("2".equals(vehicleInfos.get(i).getFlag()))
                {
                    insertVehicleVo = vehicleInfos.get(i).getBizVehicleInfo();
                    //插入新车信息
                    insertVehicleVo.setId(WzUniqueValUtil.makeUUID());
                    insertVehicleVo.setCreateTime(new Date());
                    bizVehicleMapperExt.insertSelective(insertVehicleVo);
                }
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录插入时发生错误", ex);
        }

    }

    @Override
    public void insertVehicle(VehicleBatteryParam vehicleInfo) {
        // 车辆编号重复提示错误
        BizVehicleExample lnExample = new BizVehicleExample();
        BizVehicleExample.Criteria lnCriteria = lnExample.createCriteria();
        lnCriteria.andVehicleCodeEqualTo(vehicleInfo.getBizVehicleInfo().getVehicleCode());
        int lnCnt = bizVehicleMapperExt.countByExample(lnExample);
        if (0 < lnCnt) {
            throw new BizException(RunningResult.MULTIPLE_RECORD.code(), "车辆编号(" + vehicleInfo.getBizVehicleInfo().getVehicleCode() + ")已存在");
        }
        // 电池编号重复提示错误
        BizBatteryExample brExample = new BizBatteryExample();
        BizBatteryExample.Criteria brCriteria = brExample.createCriteria();
        brCriteria.andBatteryCodeEqualTo(vehicleInfo.getBatteryInfo().getBatteryCode());
        int brCnt = bizBatteryMapperExt.countByExample(brExample);
        if (0 < brCnt) {
            throw new BizException(RunningResult.MULTIPLE_RECORD.code(), "电池编号(" + vehicleInfo.getBizVehicleInfo().getVehicleCode() + ")已存在");
        }
        BizVehicle insertVehicleVo = null;
        BizBattery insertBatteryVo = null;
        BizRefVehicleBattery temp = new BizRefVehicleBattery();
        try {
            //新车与新电池信息配对
            if("0".equals(vehicleInfo.getFlag())){
                insertVehicleVo = vehicleInfo.getBizVehicleInfo();
                insertBatteryVo = vehicleInfo.getBatteryInfo();
                String vehicleId = WzUniqueValUtil.makeUUID();
                String batteryId = WzUniqueValUtil.makeUUID();
                //插入新车信息
                insertVehicleVo.setId(vehicleId);
                insertVehicleVo.setCreateTime(new Date());
                bizVehicleMapperExt.insertSelective(insertVehicleVo);
                //插入新电池信息
                insertBatteryVo.setId(batteryId);
                insertBatteryVo.setCreateTime(new Date());
                bizBatteryMapperExt.insertSelective(insertBatteryVo);
                //插入新车与新电池的MAP信息
                temp.setVehicleId(vehicleId);
                temp.setBatteryId(batteryId);
                temp.setBindTime(new Date());
                bizRefVehicleBatteryMapperExt.insertSelective(temp);
            }
            //新车与旧电池配对
            else if("1".equals(vehicleInfo.getFlag()))
            {
                insertVehicleVo = vehicleInfo.getBizVehicleInfo();
                String vehicleId = WzUniqueValUtil.makeUUID();
                //插入新车信息
                insertVehicleVo.setId(vehicleId);
                insertVehicleVo.setCreateTime(new Date());
                bizVehicleMapperExt.insertSelective(insertVehicleVo);
                //插入新车与旧电池MAP信息
                temp.setVehicleId(vehicleId);
                temp.setBatteryId(vehicleInfo.getBatteryInfo().getId());
                temp.setBindTime(new Date());
                bizRefVehicleBatteryMapperExt.insertSelective(temp);
            }
            //只有新车信息
            else if("2".equals(vehicleInfo.getFlag()))
            {
                insertVehicleVo = vehicleInfo.getBizVehicleInfo();
                //插入新车信息
                insertVehicleVo.setId(WzUniqueValUtil.makeUUID());
                insertVehicleVo.setCreateTime(new Date());
                bizVehicleMapperExt.insertSelective(insertVehicleVo);
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "记录插入时发生错误", ex);
        }
    }

    @Override
    @Transactional
    public void updateVehicle(BizVehicle vehicle) {
        bizVehicleMapperExt.updateByPrimaryKeySelective(vehicle);
    }

    @Override
    @Transactional
    public void deleteVehicles(List<String> ids) {
        int i = 0;
        try {
            for (; i < ids.size(); i++) {
                bizVehicleMapperExt.deleteByPrimaryKey(ids.get(i));
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
        }
    }

    @Override
    public List<Map<String, Object>> getByPrimaryKey(Map<String, Object> param) {
        return bizVehicleMapperExt.getVehicleInfoById(param);
    }
}
