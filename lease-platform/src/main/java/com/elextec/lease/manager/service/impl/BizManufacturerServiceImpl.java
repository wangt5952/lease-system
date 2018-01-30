package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.service.BizManufacturerService;
import com.elextec.persist.dao.mybatis.BizManufacturerMapperExt;
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

    @Override
    public PageResponse<BizManufacturer> list(boolean needPaging, PageRequest pr) {
        // 查询总记录数
        int mfrsTotal = 0;
        if (0 < pr.getTotal()) {
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
            mfrsInfo.setMfrsType(MfrsType.VEHICLE);
            mfrsInfo.setMfrsStatus(RecordStatus.NORMAL.getInfo());
            mfrsInfo.setCreateTime(new Date());
            bizManufacturerMapperExt.insertSelective(mfrsInfo);
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "记录插入时发生错误", ex);
        }
    }

    @Override
    @Transactional
    public void updateBizManufacturer(BizManufacturer mfrsInfo) {
        bizManufacturerMapperExt.updateByPrimaryKeySelective(mfrsInfo);
    }

    @Override
    @Transactional
    public void deleteBizManufacturers(List<String> ids) {
        int i = 0;
        try {
            for (; i < ids.size(); i++) {
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

