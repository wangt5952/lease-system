package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.request.BizPartsParam;
import com.elextec.lease.manager.service.BizPartsService;
import com.elextec.persist.dao.mybatis.BizManufacturerMapperExt;
import com.elextec.persist.dao.mybatis.BizPartsMapperExt;
import com.elextec.persist.dao.mybatis.BizRefVehiclePartsMapperExt;
import com.elextec.persist.dao.mybatis.BizVehicleMapperExt;
import com.elextec.persist.field.enums.RecordStatus;
import com.elextec.persist.model.mybatis.*;
import com.elextec.persist.model.mybatis.ext.BizPartsExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BizPartsServiceImpl implements BizPartsService {

    /*日志*/
    private final Logger logger = LoggerFactory.getLogger(BizPartsServiceImpl.class);

    @Autowired
    private BizPartsMapperExt bizPartsMapperExt;

    @Autowired
    private BizVehicleMapperExt bizVehicleMapperExt;

    @Autowired
    private BizRefVehiclePartsMapperExt bizRefVehiclePartsMapperExt;

    @Autowired
    private BizManufacturerMapperExt bizManufacturerMapperExt;

    @Override
    public PageResponse<BizParts> list(boolean needPaging, PageRequest pr) {
        // 查询总记录数
        int mfrsTotal = 0;
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
            mfrsTotal = pr.getTotal();
        } else {
            BizPartsExample bizPartsExample = new BizPartsExample();
            bizPartsExample.setDistinct(true);
            mfrsTotal = bizPartsMapperExt.countByExample(bizPartsExample);
        }
        // 分页查询
        BizPartsExample bizPartsExample = new BizPartsExample();
        bizPartsExample.setDistinct(true);
        if (needPaging) {
            bizPartsExample.setPageBegin(pr.getPageBegin());
            bizPartsExample.setPageSize(pr.getPageSize());
        }
        List<BizParts> bizPartsList = bizPartsMapperExt.selectByExample(bizPartsExample);
        // 组织并返回结果
        PageResponse<BizParts> presp = new PageResponse<BizParts>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(mfrsTotal);
        if (null == bizPartsList) {
            presp.setRows(new ArrayList<BizParts>());
        } else {
            presp.setRows(bizPartsList);
        }
        return presp;
    }

    @Override
    public PageResponse<BizPartsExt> listExtByParam(boolean needPaging, BizPartsParam pr) {
        // 查询总记录数
        int partsTotal = 0;
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
            partsTotal = pr.getTotal();
        } else {
            partsTotal = bizPartsMapperExt.countExtByParam(pr);
        }
        // 分页查询
        if (needPaging) {
            pr.setPageBegin();
        }
        List<BizPartsExt> partsLs = bizPartsMapperExt.selectExtByParam(pr);
        // 组织并返回结果
        PageResponse<BizPartsExt> partsesp = new PageResponse<BizPartsExt>();
        partsesp.setCurrPage(pr.getCurrPage());
        partsesp.setPageSize(pr.getPageSize());
        partsesp.setTotal(partsTotal);
        if (null == partsLs) {
            partsesp.setRows(new ArrayList<BizPartsExt>());
        } else {
            partsesp.setRows(partsLs);
        }
        return partsesp;
    }

    @Override
    @Transactional
    public void insertBizParts(List<BizParts> partsInfos) {
        int i = 0;
        BizParts insertVo = null;
        try {
            for (; i < partsInfos.size(); i++) {
                //校验制造商是否存在（状态为正常）
                if(WzStringUtil.isNotBlank(partsInfos.get(i).getMfrsId())){
                    BizManufacturerExample manuExample = new BizManufacturerExample();
                    BizManufacturerExample.Criteria manuCriteria = manuExample.createCriteria();
                    manuCriteria.andIdEqualTo(partsInfos.get(i).getMfrsId());
                    manuCriteria.andMfrsStatusEqualTo(RecordStatus.NORMAL);
                    int manuCot = bizManufacturerMapperExt.countByExample(manuExample);
                    if(manuCot < 1){
                        throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "配件对应的制造商不存在或已作废");
                    }
                }
                insertVo = partsInfos.get(i);
                insertVo.setId(WzUniqueValUtil.makeUUID());
                insertVo.setCreateTime(new Date());
                bizPartsMapperExt.insertSelective(insertVo);
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录插入时发生错误", ex);
        }
    }

    @Override
    public void insertBizParts(BizParts partsInfo) {
        // 资源code重复提示错误
        BizPartsExample bizPartsExample = new BizPartsExample();
        BizPartsExample.Criteria lnCriteria = bizPartsExample.createCriteria();
        lnCriteria.andPartsCodeEqualTo(partsInfo.getPartsCode());
        int lnCnt = bizPartsMapperExt.countByExample(bizPartsExample);
        if (0 < lnCnt) {
            throw new BizException(RunningResult.MULTIPLE_RECORD.code(), "资源code(" + partsInfo.getPartsCode() + ")已存在");
        }
        //校验制造商是否存在（状态为正常）
        if(WzStringUtil.isNotBlank(partsInfo.getMfrsId())){
            BizManufacturerExample manuExample = new BizManufacturerExample();
            BizManufacturerExample.Criteria manuCriteria = manuExample.createCriteria();
            manuCriteria.andIdEqualTo(partsInfo.getMfrsId());
            manuCriteria.andMfrsStatusEqualTo(RecordStatus.NORMAL);
            int manuCot = bizManufacturerMapperExt.countByExample(manuExample);
            if(manuCot < 1){
                throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "配件对应的制造商不存在或已作废");
            }
        }
        try {
            partsInfo.setId(WzUniqueValUtil.makeUUID());
            partsInfo.setCreateTime(new Date());
            bizPartsMapperExt.insertSelective(partsInfo);
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "记录插入时发生错误", ex);
        }
    }

    @Override
    @Transactional
    public void updateBizParts(BizParts partsInfo) {
        //判定配件是否绑定了车辆
        if(RecordStatus.INVALID.toString().equals(partsInfo.getPartsStatus())){
            BizRefVehiclePartsExample example = new BizRefVehiclePartsExample();
            BizRefVehiclePartsExample.Criteria criteria = example.createCriteria();
            criteria.andUnbindTimeIsNull();
            criteria.andPartsIdEqualTo(partsInfo.getId());
            int count = bizRefVehiclePartsMapperExt.countByExample(example);
            if(count >= 1){
                throw new BizException(RunningResult.HAVE_BIND.code(), "配件已绑定车辆,无法作废");
            }
        }
        //校验制造商是否存在（状态为正常）
        if(WzStringUtil.isNotBlank(partsInfo.getMfrsId())){
            BizManufacturerExample manuExample = new BizManufacturerExample();
            BizManufacturerExample.Criteria manuCriteria = manuExample.createCriteria();
            manuCriteria.andIdEqualTo(partsInfo.getMfrsId());
            manuCriteria.andMfrsStatusEqualTo(RecordStatus.NORMAL);
            int manuCot = bizManufacturerMapperExt.countByExample(manuExample);
            if(manuCot < 1){
                throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "配件对应的制造商不存在或已作废");
            }
        }
        bizPartsMapperExt.updateByPrimaryKeySelective(partsInfo);
    }

    @Override
    @Transactional
    public void deleteBizParts(List<String> ids) {
        int i = 0;
        try {
            BizRefVehiclePartsExample example = new BizRefVehiclePartsExample();
            BizRefVehiclePartsExample.Criteria criteria = example.createCriteria();
            criteria.andUnbindTimeIsNull();
            for (; i < ids.size(); i++) {
                criteria.andPartsIdEqualTo(ids.get(i));
                //判定配件是否绑定了车辆
                int count = bizRefVehiclePartsMapperExt.countByExample(example);
                if(count >= 1){
                    throw new BizException(RunningResult.HAVE_BIND.code(), "第" + i + "条记录删除时发生错误,配件已绑定车辆");
                }
                bizPartsMapperExt.deleteByPrimaryKey(ids.get(i));
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
        }
    }

    @Override
    public BizPartsExt getBizPartsByPrimaryKey(Map<String,Object> param) {
        return bizPartsMapperExt.getPartInfoByPartId(param);
    }

    @Override
    public void bind(String vehicleId, String partsId) {
        //判定车辆是否存在或作废
        BizVehicleExample vehicleExample = new BizVehicleExample();
        BizVehicleExample.Criteria selectVehicleCriteria = vehicleExample.createCriteria();
        selectVehicleCriteria.andIdEqualTo(vehicleId);
        selectVehicleCriteria.andVehicleStatusEqualTo(RecordStatus.NORMAL);
        int vehicleCount = bizVehicleMapperExt.countByExample(vehicleExample);
        if(vehicleCount < 1){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "车辆不存在或已冻结、作废");
        }


        //判定配件是否存在或作废
        BizPartsExample bizPartsExample = new BizPartsExample();
        BizPartsExample.Criteria selectPartsCriteria = bizPartsExample.createCriteria();
        selectPartsCriteria.andIdEqualTo(partsId);
        selectPartsCriteria.andPartsStatusEqualTo(RecordStatus.NORMAL);
        int partsCount = bizPartsMapperExt.countByExample(bizPartsExample);
        if (partsCount <1) {
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(),"配件不存在或已冻结、作废");
        }

        //判定车辆和配件是否已经绑定
        BizRefVehiclePartsExample bizRefVehiclePartsExample = new BizRefVehiclePartsExample();
        BizRefVehiclePartsExample.Criteria selectRefCriteria = bizRefVehiclePartsExample.createCriteria();
        selectRefCriteria.andVehicleIdEqualTo(vehicleId);
        selectRefCriteria.andPartsIdEqualTo(partsId);
        selectRefCriteria.andBindTimeIsNotNull();
        selectRefCriteria.andUnbindTimeIsNull();
        int refCount = bizRefVehiclePartsMapperExt.countByExample(bizRefVehiclePartsExample);
        if (refCount >= 1) {
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(),"配件已绑定");
        }

        //如果车辆存在，配件存在，且未绑定就存入数据库
        BizRefVehicleParts bizRefVehicleParts = new BizRefVehicleParts();
        bizRefVehicleParts.setVehicleId(vehicleId);
        bizRefVehicleParts.setPartsId(partsId);
        bizRefVehicleParts.setBindTime(new Date());
        bizRefVehiclePartsMapperExt.insert(bizRefVehicleParts);
    }

    @Override
    public void unBind(String vehicleId, String partsId) {
        BizRefVehicleParts bizRefVehicleParts = new BizRefVehicleParts();
        bizRefVehicleParts.setUnbindTime(new Date());//给定解绑时间

        //判断车辆和配件是否绑定
        BizRefVehiclePartsExample bizRefVehiclePartsExample = new BizRefVehiclePartsExample();
        BizRefVehiclePartsExample.Criteria selectRef = bizRefVehiclePartsExample.createCriteria();
        selectRef.andVehicleIdEqualTo(vehicleId);
        selectRef.andPartsIdEqualTo(partsId);
        selectRef.andBindTimeIsNotNull();
        selectRef.andUnbindTimeIsNull();
        int refCount = bizRefVehiclePartsMapperExt.updateByExampleSelective(bizRefVehicleParts,bizRefVehiclePartsExample);
        if (refCount < 1) {
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(),"配件未绑定或者未与该车辆绑定");
        }
    }

}
