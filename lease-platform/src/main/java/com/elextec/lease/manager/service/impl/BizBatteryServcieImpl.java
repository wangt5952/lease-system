package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.request.BizBatteryParam;
import com.elextec.lease.manager.service.BizBatteryService;
import com.elextec.persist.dao.mybatis.BizBatteryMapperExt;
import com.elextec.persist.dao.mybatis.BizRefVehicleBatteryMapperExt;
import com.elextec.persist.field.enums.RecordStatus;
import com.elextec.persist.model.mybatis.BizBattery;
import com.elextec.persist.model.mybatis.BizBatteryExample;
import com.elextec.persist.model.mybatis.BizRefVehicleBatteryExample;
import com.elextec.persist.model.mybatis.ext.BizBatteryExt;
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
 * 资源管理Service实现类.
 * Created by wangtao on 2018/1/16.
 */
@Service
public class BizBatteryServcieImpl implements BizBatteryService {
    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(BizBatteryServcieImpl.class);

    @Autowired
    private BizBatteryMapperExt bizBatteryMapperExt;

    @Autowired
    private BizRefVehicleBatteryMapperExt bizRefVehicleBatteryMapperExt;

    @Override
    public PageResponse<BizBattery> list(boolean needPaging, PageRequest pr) {
        // 查询总记录数
        int resTotal = 0;
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
            resTotal = pr.getTotal();
        } else {
            BizBatteryExample bizBatteryExample = new BizBatteryExample();
            bizBatteryExample.setDistinct(true);
            resTotal = bizBatteryMapperExt.countByExample(bizBatteryExample);
        }
        // 分页查询
        BizBatteryExample bizBatteryExample = new BizBatteryExample();
        bizBatteryExample.setDistinct(true);
        if (needPaging) {
            bizBatteryExample.setPageBegin(pr.getPageBegin());
            bizBatteryExample.setPageSize(pr.getPageSize());
        }
        List<BizBattery> resLs = bizBatteryMapperExt.selectByExample(bizBatteryExample);
        // 组织并返回结果
        PageResponse<BizBattery> presp = new PageResponse<BizBattery>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(resTotal);
        if (null == resLs) {
            presp.setRows(new ArrayList<BizBattery>());
        } else {
            presp.setRows(resLs);
        }
        return presp;
    }

    @Override
    public PageResponse<BizBatteryExt> listExtByParam(boolean needPaging, BizBatteryParam pr) {
        // 查询总记录数
        int batteryTotal = 0;
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
            batteryTotal = pr.getTotal();
        } else {
            batteryTotal = bizBatteryMapperExt.countExtByParam(pr);
        }
        // 分页查询
        if (needPaging) {
            pr.setPageBegin();
        }
        List<BizBatteryExt> batteryLs = bizBatteryMapperExt.selectExtByParam(pr);
        // 组织并返回结果
        PageResponse<BizBatteryExt> presp = new PageResponse<BizBatteryExt>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(batteryTotal);
        if (null == batteryLs) {
            presp.setRows(new ArrayList<BizBatteryExt>());
        } else {
            presp.setRows(batteryLs);
        }
        return presp;
    }

    @Override
    @Transactional
    public void insertBatterys(List<BizBattery> batteryInfos) {
        int i = 0;
        BizBattery insertVo = null;
        try {
            for (; i < batteryInfos.size(); i++) {
                insertVo = batteryInfos.get(i);
                insertVo.setId(WzUniqueValUtil.makeUUID());
                insertVo.setCreateTime(new Date());
                bizBatteryMapperExt.insertSelective(insertVo);
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录插入时发生错误", ex);
        }
    }

    @Override
    @Transactional
    public void insertBattery(BizBattery batteryInfo) {
        // 电池编号重复提示错误
        BizBatteryExample lnExample = new BizBatteryExample();
        BizBatteryExample.Criteria lnCriteria = lnExample.createCriteria();
        lnCriteria.andBatteryCodeEqualTo(batteryInfo.getBatteryCode());
        int lnCnt = bizBatteryMapperExt.countByExample(lnExample);
        if (0 < lnCnt) {
            throw new BizException(RunningResult.MULTIPLE_RECORD.code(), "电池编号(" + batteryInfo.getBatteryCode() + ")已存在");
        }
        // 保存用户信息
        try {
            batteryInfo.setId(WzUniqueValUtil.makeUUID());
            batteryInfo.setCreateTime(new Date());
            bizBatteryMapperExt.insertSelective(batteryInfo);
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "记录插入时发生错误", ex);
        }
    }

    @Override
    @Transactional
    public void updateBattery(BizBattery batteryInfo) {
        //电池作废需要验证电池是否绑定了车辆
        if(RecordStatus.INVALID.equals(batteryInfo.getBatteryStatus())){
            BizRefVehicleBatteryExample example = new BizRefVehicleBatteryExample();
            BizRefVehicleBatteryExample.Criteria criteria = example.createCriteria();
            criteria.andBatteryIdEqualTo(batteryInfo.getId());
            criteria.andUnbindTimeIsNull();
            int count = bizRefVehicleBatteryMapperExt.countByExample(example);
            if(count >= 1){
                throw new BizException(RunningResult.HAVE_BIND.code(), "电池已绑定车辆,无法作废");
            }
        }
        bizBatteryMapperExt.updateByPrimaryKeySelective(batteryInfo);
    }

    @Override
    @Transactional
    public void deleteBattery(List<String> ids) {
        int i = 0;
        try {
            BizRefVehicleBatteryExample example = new BizRefVehicleBatteryExample();
            BizRefVehicleBatteryExample.Criteria criteria = example.createCriteria();
            criteria.andUnbindTimeIsNull();
            for (; i < ids.size(); i++) {
                //验证电池是否绑定车辆
                criteria.andBatteryIdEqualTo(ids.get(i));
                int count = bizRefVehicleBatteryMapperExt.countByExample(example);
                if(count >= 1){
                    throw new BizException(RunningResult.HAVE_BIND.code(), "第" + i + "条记录删除时发生错误,电池已绑定车辆");
                }
                bizBatteryMapperExt.deleteByPrimaryKey(ids.get(i));
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
        }
    }


    @Override
    public BizBattery getByPrimaryKey(Map<String,Object> param) {
        return bizBatteryMapperExt.getBatteryInfoByBatteryId(param);
    }
}
