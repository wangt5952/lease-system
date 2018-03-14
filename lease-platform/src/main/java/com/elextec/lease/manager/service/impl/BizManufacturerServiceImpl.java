package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.request.BizMfrsParam;
import com.elextec.lease.manager.service.BizManufacturerService;
import com.elextec.persist.dao.mybatis.BizBatteryMapperExt;
import com.elextec.persist.dao.mybatis.BizManufacturerMapperExt;
import com.elextec.persist.dao.mybatis.BizPartsMapperExt;
import com.elextec.persist.dao.mybatis.BizVehicleMapperExt;
import com.elextec.persist.field.enums.MfrsType;
import com.elextec.persist.field.enums.RecordStatus;
import com.elextec.persist.model.mybatis.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BizManufacturerServiceImpl implements BizManufacturerService {

    /*日志信息*/
    private final Logger logger = LoggerFactory.getLogger(BizManufacturerServiceImpl.class);

    @Autowired
    private BizManufacturerMapperExt bizManufacturerMapperExt;

    @Autowired
    private BizVehicleMapperExt bizVehicleMapperExt;

    @Autowired
    private BizBatteryMapperExt bizBatteryMapperExt;

    @Autowired
    private BizPartsMapperExt bizPartsMapperExt;

    @Override
    public PageResponse<BizManufacturer> list(boolean needPaging, PageRequest pr) {
        // 查询总记录数
        int mfrsTotal = 0;
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
            mfrsTotal = pr.getTotal();
        } else {
            BizManufacturerExample bizManufacturerCountExample = new BizManufacturerExample();
            bizManufacturerCountExample.setDistinct(true);
            mfrsTotal = bizManufacturerMapperExt.countByExample(bizManufacturerCountExample);
        }
        // 分页查询
        BizManufacturerExample bizManufacturerExample = new BizManufacturerExample();
        bizManufacturerExample.setDistinct(true);
        if (needPaging) {
            bizManufacturerExample.setPageBegin(pr.getPageBegin());
            bizManufacturerExample.setPageSize(pr.getPageSize());
        }
        List<BizManufacturer> mfrsLs = bizManufacturerMapperExt.selectByExample(bizManufacturerExample);
        // 组织并返回结果
        PageResponse<BizManufacturer> presp = new PageResponse<BizManufacturer>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(mfrsTotal);
        if (null == mfrsLs) {
            presp.setRows(new ArrayList<BizManufacturer>());
        } else {
            presp.setRows(mfrsLs);
        }
        return presp;
    }

    @Override
    public PageResponse<BizManufacturer> listByParam(boolean needPaging, BizMfrsParam pr) {
        // 查询总记录数
        int mfrsTotal = 0;
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
            mfrsTotal = pr.getTotal();
        } else {
            mfrsTotal = bizManufacturerMapperExt.countByParam(pr);
        }
        // 分页查询
        if (needPaging) {
            pr.setPageBegin();
        }
        List<BizManufacturer> mfrsLs = bizManufacturerMapperExt.selectByParam(pr);
        // 组织并返回结果
        PageResponse<BizManufacturer> presp = new PageResponse<BizManufacturer>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(mfrsTotal);
        if (null == mfrsLs) {
            presp.setRows(new ArrayList<BizManufacturer>());
        } else {
            presp.setRows(mfrsLs);
        }
        return presp;
    }

    @Override
    @Transactional
    public void insertBizManufacturers(List<BizManufacturer> mfrsInfos) {
        int i = 0;
        BizManufacturer insertVo = null;
        try {
            for (; i < mfrsInfos.size(); i++) {
                insertVo = mfrsInfos.get(i);
                insertVo.setId(WzUniqueValUtil.makeUUID());
                insertVo.setCreateTime(new Date());
                bizManufacturerMapperExt.insertSelective(insertVo);
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录插入时发生错误", ex);
        }
    }

    @Override
    public void insertBizManufacturers(BizManufacturer mfrsInfo) {
        // 资源code重复提示错误
        BizManufacturerExample lnExample = new BizManufacturerExample();
        BizManufacturerExample.Criteria lnCriteria = lnExample.createCriteria();
        lnCriteria.andMfrsNameEqualTo(mfrsInfo.getMfrsName());
        int lnCnt = bizManufacturerMapperExt.countByExample(lnExample);
        if (0 < lnCnt) {
            throw new BizException(RunningResult.MULTIPLE_RECORD.code(), "制造商名称(" + mfrsInfo.getMfrsName() + ")已存在");
        }
        try {
            mfrsInfo.setId(WzUniqueValUtil.makeUUID());
            mfrsInfo.setCreateTime(new Date());
            bizManufacturerMapperExt.insertSelective(mfrsInfo);
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "记录插入时发生错误", ex);
        }
    }

    @Override
    @Transactional
    public void updateBizManufacturer(BizManufacturer mfrsInfo) {
        //制造商作废，判定是否绑定了设备
        if(RecordStatus.INVALID.toString().equals(mfrsInfo.getMfrsStatus().toString())){
            //验证是否有车辆绑定
            BizVehicleExample vehicleExample = new BizVehicleExample();
            BizVehicleExample.Criteria vehicleCriteria = vehicleExample.createCriteria();
            vehicleCriteria.andMfrsIdEqualTo(mfrsInfo.getId());
            vehicleCriteria.andVehicleStatusEqualTo(RecordStatus.NORMAL);
            int vehicleCot = bizVehicleMapperExt.countByExample(vehicleExample);
            if(vehicleCot >= 1){
                throw new BizException(RunningResult.HAVE_BIND.code(), "制造商有车辆绑定,无法作废");
            }
            //验证是否有电池绑定
            BizBatteryExample batteryExample = new BizBatteryExample();
            BizBatteryExample.Criteria batteryCriteria = batteryExample.createCriteria();
            batteryCriteria.andMfrsIdEqualTo(mfrsInfo.getId());
            batteryCriteria.andBatteryStatusEqualTo(RecordStatus.NORMAL);
            int batteryCot = bizBatteryMapperExt.countByExample(batteryExample);
            if(batteryCot >= 1){
                throw new BizException(RunningResult.HAVE_BIND.code(), "制造商有电池绑定,无法作废");
            }
            //验证是否有配件绑定
            BizPartsExample partsExample = new BizPartsExample();
            BizPartsExample.Criteria partsCriteria = partsExample.createCriteria();
            partsCriteria.andMfrsIdEqualTo(mfrsInfo.getId());
            partsCriteria.andPartsStatusEqualTo(RecordStatus.NORMAL);
            int partsCot = bizPartsMapperExt.countByExample(partsExample);
            if(partsCot >= 1){
                throw new BizException(RunningResult.HAVE_BIND.code(), "制造商有配件绑定,无法作废");
            }
        }
        bizManufacturerMapperExt.updateByPrimaryKeySelective(mfrsInfo);
    }

    @Override
    @Transactional
    public void deleteBizManufacturers(List<String> ids) {
        int i = 0;
        try {
            for (; i < ids.size(); i++) {
                BizVehicleExample vehicleExample = new BizVehicleExample();
                BizVehicleExample.Criteria vehicleCriteria = vehicleExample.createCriteria();
                vehicleCriteria.andVehicleStatusEqualTo(RecordStatus.NORMAL);
                BizBatteryExample batteryExample = new BizBatteryExample();
                BizBatteryExample.Criteria batteryCriteria = batteryExample.createCriteria();
                batteryCriteria.andBatteryStatusEqualTo(RecordStatus.NORMAL);
                BizPartsExample partsExample = new BizPartsExample();
                BizPartsExample.Criteria partsCriteria = partsExample.createCriteria();
                partsCriteria.andPartsStatusEqualTo(RecordStatus.NORMAL);
                //验证是否有车辆绑定
                vehicleCriteria.andMfrsIdEqualTo(ids.get(i));
                int vehicleCot = bizVehicleMapperExt.countByExample(vehicleExample);
                if(vehicleCot >= 1){
                    throw new BizException(RunningResult.HAVE_BIND.code(), "第" + i + "条记录删除时发生错误,制造商有车辆绑定");
                }
                //验证是否有电池绑定
                batteryCriteria.andMfrsIdEqualTo(ids.get(i));
                int batteryCot = bizBatteryMapperExt.countByExample(batteryExample);
                if(batteryCot >= 1){
                    throw new BizException(RunningResult.HAVE_BIND.code(), "第" + i + "条记录删除时发生错误,制造商有电池绑定");
                }
                //验证是否有配件绑定
                partsCriteria.andMfrsIdEqualTo(ids.get(i));
                int partsCot = bizPartsMapperExt.countByExample(partsExample);
                if(partsCot >= 1){
                    throw new BizException(RunningResult.HAVE_BIND.code(), "第" + i + "条记录删除时发生错误,制造商有配件绑定");
                }
                bizManufacturerMapperExt.deleteByPrimaryKey(ids.get(i));
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
        }
    }

    @Override
    public BizManufacturer getBizManufacturerByPrimaryKey(String id) {
        return bizManufacturerMapperExt.selectByPrimaryKey(id);
    }

}

