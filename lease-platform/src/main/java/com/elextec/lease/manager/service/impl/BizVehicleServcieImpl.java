package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.request.BizVehicleParam;
import com.elextec.lease.manager.request.VehicleBatteryParam;
import com.elextec.lease.manager.service.BizVehicleService;
import com.elextec.lease.model.BizVehicleBatteryParts;
import com.elextec.persist.dao.mybatis.*;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.field.enums.RecordStatus;
import com.elextec.persist.model.mybatis.*;
import com.elextec.persist.model.mybatis.ext.BizBatteryExt;
import com.elextec.persist.model.mybatis.ext.BizPartsExt;
import com.elextec.persist.model.mybatis.ext.BizVehicleExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

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

    @Autowired
    private BizPartsMapperExt bizPartsMapperExt;

    @Autowired
    private BizRefUserVehicleMapperExt bizRefUserVehicleMapperExt;

    @Autowired
    private BizRefVehiclePartsMapperExt bizRefVehiclePartsMapperExt;

    @Autowired
    private BizRefOrgVehicleMapperExt bizRefOrgVehicleMapperExt;

    @Autowired
    private BizManufacturerMapperExt bizManufacturerMapperExt;

    @Autowired
    private BizOrganizationMapperExt bizOrganizationMapperExt;


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
            //新建车辆默认归属到平台下
            BizOrganizationExample bizOrganizationExample = new BizOrganizationExample();
            BizOrganizationExample.Criteria bizOrgCriteria = bizOrganizationExample.createCriteria();
            bizOrgCriteria.andOrgTypeEqualTo(OrgAndUserType.PLATFORM);
            List<BizOrganization> org = bizOrganizationMapperExt.selectByExample(bizOrganizationExample);
            if(org.size() != 1){
                throw new BizException(RunningResult.DB_ERROR.code(), "记录插入时发生错误,平台企业不存在或存在多个");
            }
            BizRefOrgVehicle ref = new BizRefOrgVehicle();
            ref.setOrgId(org.get(0).getId());
            for (; i < vehicleInfos.size(); i++) {
                //校验车辆制造商是否存在（状态为正常）
                if(WzStringUtil.isNotBlank(vehicleInfos.get(i).getBizVehicleInfo().getMfrsId())){
                    BizManufacturerExample manuExample = new BizManufacturerExample();
                    BizManufacturerExample.Criteria manuCriteria = manuExample.createCriteria();
                    manuCriteria.andIdEqualTo(vehicleInfos.get(i).getBizVehicleInfo().getMfrsId());
                    manuCriteria.andMfrsStatusEqualTo(RecordStatus.NORMAL);
                    int manuCot = bizManufacturerMapperExt.countByExample(manuExample);
                    if(manuCot < 1){
                        throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "第" + i + "条记录插入时发生错误,车辆对应的制造商不存在或已作废");
                    }
                }
                //新车与新电池信息配对
                if("0".equals(vehicleInfos.get(i).getFlag())){
                    //校验电池制造商是否存在（状态为正常）
                    if(WzStringUtil.isNotBlank(vehicleInfos.get(i).getBatteryInfo().getMfrsId())){
                        BizManufacturerExample manuExample = new BizManufacturerExample();
                        BizManufacturerExample.Criteria manuCriteria = manuExample.createCriteria();
                        manuCriteria.andIdEqualTo(vehicleInfos.get(i).getBatteryInfo().getMfrsId());
                        manuCriteria.andMfrsStatusEqualTo(RecordStatus.NORMAL);
                        int manuCot = bizManufacturerMapperExt.countByExample(manuExample);
                        if(manuCot < 1){
                            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "电池对应的制造商不存在或已作废");
                        }
                    }
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
                //新建车辆默认绑定在平台下面
                ref.setVehicleId(vehicleInfos.get(i).getBizVehicleInfo().getId());
                ref.setBindTime(new Date());
                bizRefOrgVehicleMapperExt.insertSelective(ref);
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
        //校验车辆制造商是否存在（状态为正常）
        if(WzStringUtil.isNotBlank(vehicleInfo.getBizVehicleInfo().getMfrsId())){
            BizManufacturerExample manuExample = new BizManufacturerExample();
            BizManufacturerExample.Criteria manuCriteria = manuExample.createCriteria();
            manuCriteria.andIdEqualTo(vehicleInfo.getBizVehicleInfo().getMfrsId());
            manuCriteria.andMfrsStatusEqualTo(RecordStatus.NORMAL);
            int manuCot = bizManufacturerMapperExt.countByExample(manuExample);
            if(manuCot < 1){
                throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "车辆对应的制造商不存在或已作废");
            }
        }
        if("0".equals(vehicleInfo.getFlag())){
            // 电池编号重复提示错误
            BizBatteryExample brExample = new BizBatteryExample();
            BizBatteryExample.Criteria brCriteria = brExample.createCriteria();
            brCriteria.andBatteryCodeEqualTo(vehicleInfo.getBatteryInfo().getBatteryCode());
            int brCnt = bizBatteryMapperExt.countByExample(brExample);
            if (0 < brCnt) {
                throw new BizException(RunningResult.MULTIPLE_RECORD.code(), "电池编号(" + vehicleInfo.getBizVehicleInfo().getVehicleCode() + ")已存在");
            }
            //校验电池制造商是否存在（状态为正常）
            if(WzStringUtil.isNotBlank(vehicleInfo.getBatteryInfo().getMfrsId())){
                BizManufacturerExample manuExample = new BizManufacturerExample();
                BizManufacturerExample.Criteria manuCriteria = manuExample.createCriteria();
                manuCriteria.andIdEqualTo(vehicleInfo.getBatteryInfo().getMfrsId());
                manuCriteria.andMfrsStatusEqualTo(RecordStatus.NORMAL);
                int manuCot = bizManufacturerMapperExt.countByExample(manuExample);
                if(manuCot < 1){
                    throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "电池对应的制造商不存在或已作废");
                }
            }
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
            //新建车辆默认归属到平台下
            BizOrganizationExample bizOrganizationExample = new BizOrganizationExample();
            BizOrganizationExample.Criteria bizOrgCriteria = bizOrganizationExample.createCriteria();
            bizOrgCriteria.andOrgTypeEqualTo(OrgAndUserType.PLATFORM);
            List<BizOrganization> org = bizOrganizationMapperExt.selectByExample(bizOrganizationExample);
            if(org.size() == 1){
                BizRefOrgVehicle ref = new BizRefOrgVehicle();
                ref.setOrgId(org.get(0).getId());
                ref.setVehicleId(vehicleInfo.getBizVehicleInfo().getId());
                ref.setBindTime(new Date());
                bizRefOrgVehicleMapperExt.insertSelective(ref);
            }else{
                throw new BizException(RunningResult.DB_ERROR.code(), "记录插入时发生错误,平台企业不存在或存在多个");
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "记录插入时发生错误", ex);
        }
    }

    @Override
    @Transactional
    public void updateVehicle(BizVehicle vehicle) {
        //如果车辆做报废的话，需要判定车辆是否已绑定用户并将已绑定的电池与配件全部解绑
        if(RecordStatus.INVALID.toString().equals(vehicle.getVehicleStatus())){
            BizRefUserVehicleExample example = new BizRefUserVehicleExample();
            BizRefUserVehicleExample.Criteria criteria = example.createCriteria();
            criteria.andUnbindTimeIsNull();
            criteria.andVehicleIdEqualTo(vehicle.getId());
            int count = bizRefUserVehicleMapperExt.countByExample(example);
            if(count >= 1){
                throw new BizException(RunningResult.HAVE_BIND.code(), "车辆已绑定用户,无法作废");
            }
            //验证车辆是否有企业绑定(平台除外)
            BizOrganizationExample organizationExample = new BizOrganizationExample();
            BizOrganizationExample.Criteria organizationCriteria = organizationExample.createCriteria();
            organizationCriteria.andOrgTypeEqualTo(OrgAndUserType.PLATFORM);
            List<BizOrganization> org = bizOrganizationMapperExt.selectByExample(organizationExample);
            if(org.size() != 1){
                throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "平台信息有误");
            }

            BizRefOrgVehicleExample orgExample = new BizRefOrgVehicleExample();
            BizRefOrgVehicleExample.Criteria orgCriteria = orgExample.createCriteria();
            orgCriteria.andUnbindTimeIsNull();
            orgCriteria.andVehicleIdEqualTo(vehicle.getId());
            //查询时需要将平台企业ID排除在外
            orgCriteria.andOrgIdNotEqualTo(org.get(0).getId());
            int orgCot = bizRefOrgVehicleMapperExt.countByExample(orgExample);
            if(orgCot >= 1){
                throw new BizException(RunningResult.HAVE_BIND.code(), "车辆已绑定企业,无法作废");
            }
            bizVehicleMapperExt.updateByPrimaryKeySelective(vehicle);
            //解除所有电池绑定关系
            BizRefVehicleBatteryExample delBatteryExample = new BizRefVehicleBatteryExample();
            BizRefVehicleBatteryExample.Criteria delBatteryCriteria = delBatteryExample.createCriteria();
            delBatteryCriteria.andUnbindTimeIsNull();
            delBatteryCriteria.andVehicleIdEqualTo(vehicle.getId());
            BizRefVehicleBattery batteryBif = new BizRefVehicleBattery();
            batteryBif.setUnbindTime(new Date());
            bizRefVehicleBatteryMapperExt.updateByExample(batteryBif,delBatteryExample);
            //解除所有配件绑定关系
            BizRefVehiclePartsExample delPartsExample = new BizRefVehiclePartsExample();
            BizRefVehiclePartsExample.Criteria delPartsCriteria = delPartsExample.createCriteria();
            delPartsCriteria.andUnbindTimeIsNull();
            delPartsCriteria.andVehicleIdEqualTo(vehicle.getId());
            BizRefVehicleParts partsBif = new BizRefVehicleParts();
            partsBif.setUnbindTime(new Date());
            bizRefVehiclePartsMapperExt.updateByExample(partsBif,delPartsExample);
            //解除平台与车辆绑定关系
            BizRefOrgVehicleExample delOrgExample = new BizRefOrgVehicleExample();
            BizRefOrgVehicleExample.Criteria delOrgCriteria = delOrgExample.createCriteria();
            delOrgCriteria.andVehicleIdEqualTo(vehicle.getId());
            delOrgCriteria.andUnbindTimeIsNull();
            BizRefOrgVehicle orgRef = new BizRefOrgVehicle();
            orgRef.setUnbindTime(new Date());
            bizRefOrgVehicleMapperExt.updateByExample(orgRef,delOrgExample);

        }else{
            //校验车辆制造商是否存在（状态为正常）
            if(WzStringUtil.isNotBlank(vehicle.getMfrsId())){
                BizManufacturerExample manuExample = new BizManufacturerExample();
                BizManufacturerExample.Criteria manuCriteria = manuExample.createCriteria();
                manuCriteria.andIdEqualTo(vehicle.getMfrsId());
                manuCriteria.andMfrsStatusEqualTo(RecordStatus.NORMAL);
                int manuCot = bizManufacturerMapperExt.countByExample(manuExample);
                if(manuCot < 1){
                    throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "车辆对应的制造商不存在或已作废");
                }
            }
            bizVehicleMapperExt.updateByPrimaryKeySelective(vehicle);
        }
    }

    @Override
    @Transactional
    public void deleteVehicles(List<String> ids) {
        int i = 0;
        try {
            BizOrganizationExample organizationExample = new BizOrganizationExample();
            BizOrganizationExample.Criteria organizationCriteria = organizationExample.createCriteria();
            organizationCriteria.andOrgTypeEqualTo(OrgAndUserType.PLATFORM);
            List<BizOrganization> org = bizOrganizationMapperExt.selectByExample(organizationExample);
            if(org.size() != 1){
                throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "平台信息有误");
            }

            for (; i < ids.size(); i++) {
                BizRefUserVehicleExample example = new BizRefUserVehicleExample();
                BizRefUserVehicleExample.Criteria criteria = example.createCriteria();
                criteria.andUnbindTimeIsNull();
                BizRefVehicleBatteryExample delBatteryExample = new BizRefVehicleBatteryExample();
                BizRefVehicleBatteryExample.Criteria delBatteryCriteria = delBatteryExample.createCriteria();
                BizRefVehiclePartsExample delPartsExample = new BizRefVehiclePartsExample();
                BizRefVehiclePartsExample.Criteria delPartsCriteria = delPartsExample.createCriteria();
                delPartsCriteria.andUnbindTimeIsNull();
                delBatteryCriteria.andUnbindTimeIsNull();
                BizRefOrgVehicleExample orgExample = new BizRefOrgVehicleExample();
                BizRefOrgVehicleExample.Criteria orgCriteria = orgExample.createCriteria();
                orgCriteria.andUnbindTimeIsNull();
                BizRefVehicleBattery batteryBif = new BizRefVehicleBattery();
                batteryBif.setUnbindTime(new Date());
                BizRefVehicleParts partsBif = new BizRefVehicleParts();
                partsBif.setUnbindTime(new Date());
                criteria.andVehicleIdEqualTo(ids.get(i));
                int count = bizRefUserVehicleMapperExt.countByExample(example);
                if(count >= 1){
                    throw new BizException(RunningResult.HAVE_BIND.code(), "第" + i + "条记录删除时发生错误,车辆已绑定用户");
                }
                orgCriteria.andVehicleIdEqualTo(ids.get(i));
                //查询绑定企业时需要将平台企业除外
                orgCriteria.andOrgIdNotEqualTo(org.get(0).getId());
                int orgCot = bizRefOrgVehicleMapperExt.countByExample(orgExample);

                if(orgCot != 1){
                    throw new BizException(RunningResult.HAVE_BIND.code(), "第" + i + "条记录删除时发生错误,车辆已绑定企业");
                }
                bizVehicleMapperExt.deleteByPrimaryKey(ids.get(i));
                //解除所有电池绑定关系
                delBatteryCriteria.andVehicleIdEqualTo(ids.get(i));
                bizRefVehicleBatteryMapperExt.updateByExample(batteryBif,delBatteryExample);
                //解除所有配件绑定关系
                delPartsCriteria.andVehicleIdEqualTo(ids.get(i));
                bizRefVehiclePartsMapperExt.updateByExample(partsBif,delPartsExample);
                //解除平台与车辆绑定关系
                BizRefOrgVehicleExample delOrgExample = new BizRefOrgVehicleExample();
                BizRefOrgVehicleExample.Criteria delOrgCriteria = delOrgExample.createCriteria();
                delOrgCriteria.andVehicleIdEqualTo(ids.get(i));
                delOrgCriteria.andUnbindTimeIsNull();
                BizRefOrgVehicle orgRef = new BizRefOrgVehicle();
                orgRef.setUnbindTime(new Date());
                bizRefOrgVehicleMapperExt.updateByExample(orgRef,delOrgExample);
            }
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
        }
    }

    @Override
    public List<Map<String, Object>> getByPrimaryKey(Map<String,Object> param, Boolean isUsed) {
        param.put("flag", isUsed);
        return bizVehicleMapperExt.getVehicleInfoById(param);
    }

    @Override
    public List<BizVehicleBatteryParts> getByUserId(String id) {
        return bizVehicleMapperExt.getVehicleInfoByUserId(id);
    }

    @Override
    public void unBind(String vehicleId,String batteryId) {
        BizRefVehicleBattery param = new BizRefVehicleBattery();
        param.setUnbindTime(new Date());
        BizRefVehicleBatteryExample refExample = new BizRefVehicleBatteryExample();
        BizRefVehicleBatteryExample.Criteria selectRefCriteria = refExample.createCriteria();
        selectRefCriteria.andVehicleIdEqualTo(vehicleId);
        selectRefCriteria.andBatteryIdEqualTo(batteryId);
        selectRefCriteria.andBindTimeIsNotNull();
        selectRefCriteria.andUnbindTimeIsNull();
        int temp = bizRefVehicleBatteryMapperExt.updateByExampleSelective(param,refExample);
        if(temp < 1){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "电池未被绑定或未与该车辆绑定");
        }
    }

    @Override
    public void bind(String vehicleId,String batteryId) {

        //判定车辆是否存在或已作废
        BizVehicleExample vehicleExample = new BizVehicleExample();
        BizVehicleExample.Criteria selectVehicleCriteria = vehicleExample.createCriteria();
        selectVehicleCriteria.andIdEqualTo(vehicleId);
        selectVehicleCriteria.andVehicleStatusEqualTo(RecordStatus.NORMAL);
        int vehicleCount = bizVehicleMapperExt.countByExample(vehicleExample);
        if(vehicleCount < 1){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "车辆不存在或已冻结、作废");
        }

        //判定电池是否存在或已作废
        BizBatteryExample batteryExample = new BizBatteryExample();
        BizBatteryExample.Criteria selectUserCriteria = batteryExample.createCriteria();
        selectUserCriteria.andIdEqualTo(batteryId);
        selectUserCriteria.andBatteryStatusEqualTo(RecordStatus.NORMAL);
        int userCount = bizBatteryMapperExt.countByExample(batteryExample);
        if(userCount<1){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "电池不存在或已冻结、作废");
        }

        //校验车辆是否已经绑定电池
        BizRefVehicleBatteryExample refVehicleExample = new BizRefVehicleBatteryExample();
        BizRefVehicleBatteryExample.Criteria selectVehicleRefCriteria = refVehicleExample.createCriteria();
        selectVehicleRefCriteria.andVehicleIdEqualTo(vehicleId);
        selectVehicleRefCriteria.andBindTimeIsNotNull();
        selectVehicleRefCriteria.andUnbindTimeIsNull();
        int refVehicleCount = bizRefVehicleBatteryMapperExt.countByExample(refVehicleExample);
        if(refVehicleCount >= 1){
            throw new BizException(RunningResult.BAD_REQUEST.code(), "车辆已经绑定了电池");
        }

        //校验电池是否已经被绑定
        BizRefVehicleBatteryExample refBatteryExample = new BizRefVehicleBatteryExample();
        BizRefVehicleBatteryExample.Criteria selectBatteryRefCriteria = refBatteryExample.createCriteria();
        selectBatteryRefCriteria.andBatteryIdEqualTo(batteryId);
        selectBatteryRefCriteria.andBindTimeIsNotNull();
        selectBatteryRefCriteria.andUnbindTimeIsNull();
        int refBatteryCount = bizRefVehicleBatteryMapperExt.countByExample(refBatteryExample);
        if(refBatteryCount >= 1){
            throw new BizException(RunningResult.BAD_REQUEST.code(), "电池已被绑定");
        }

        BizRefVehicleBattery param = new BizRefVehicleBattery();
        param.setVehicleId(vehicleId);
        param.setBatteryId(batteryId);
        param.setBindTime(new Date());
        bizRefVehicleBatteryMapperExt.insert(param);
    }

    @Override
    public List<Map<String, Object>> listByBatteryCode(Map<String,Object> param) {

        return bizVehicleMapperExt.selectExtByBatteryCodes(param);
    }

    @Override
    public BizVehicleBatteryParts queryBatteryInfoByVehicleId(Map<String,Object> paramMap, Boolean isUsed) {
        BizVehicleBatteryParts vehicle = bizVehicleMapperExt.getVehicleInfoByVehicleId(paramMap);
        Map<String,Object> param = new HashMap<String,Object>();
        if(vehicle != null){
            param.put("flag",isUsed);
            param.put("id",vehicle.getId());
            List<BizBatteryExt> batteryDatas = bizBatteryMapperExt.getBatteryInfoByVehicleId(param);
            vehicle.setBizBatteries(batteryDatas);
        }else{
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "车辆不存在");
        }
        return vehicle;
    }

    @Override
    public List<BizPartsExt> getBizPartsByVehicle(Map<String,Object> param) {
        return bizPartsMapperExt.getById(param);
    }

    @Override
    public int getOrgBindVehicle(String orgId) {
        //验证企业是否存
        BizOrganizationExample bizOrganizationExample = new BizOrganizationExample();
        BizOrganizationExample.Criteria bizOrgCriteria = bizOrganizationExample.createCriteria();
        bizOrgCriteria.andOrgStatusEqualTo(RecordStatus.NORMAL);
        bizOrgCriteria.andIdEqualTo(orgId);
        int orgCot = bizOrganizationMapperExt.countByExample(bizOrganizationExample);
        if(orgCot != 1){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "企业不存在或已作废");
        }
        BizRefOrgVehicleExample example = new BizRefOrgVehicleExample();
        BizRefOrgVehicleExample.Criteria criteria = example.createCriteria();
        criteria.andOrgIdEqualTo(orgId);
        criteria.andUnbindTimeIsNull();
        return bizRefOrgVehicleMapperExt.countByExample(example);
    }
}
