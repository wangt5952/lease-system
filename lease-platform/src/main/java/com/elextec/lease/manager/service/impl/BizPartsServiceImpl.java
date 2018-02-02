package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.service.BizPartsService;
import com.elextec.persist.dao.mybatis.BizPartsMapperExt;
import com.elextec.persist.field.enums.MfrsType;
import com.elextec.persist.field.enums.RecordStatus;
import com.elextec.persist.model.mybatis.BizParts;
import com.elextec.persist.model.mybatis.BizPartsExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BizPartsServiceImpl implements BizPartsService {

    /*日志*/
    private final Logger logger = LoggerFactory.getLogger(BizPartsServiceImpl.class);

    @Autowired
    private BizPartsMapperExt bizPartsMapperExt;

    @Override
    public PageResponse<BizParts> list(boolean needPaging, PageRequest pr) {
        // 查询总记录数
        int mfrsTotal = 0;
        if (0 < pr.getTotal()) {
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
    @Transactional
    public void insertBizParts(List<BizParts> partsInfos) {
        int i = 0;
        BizParts insertVo = null;
        try {
            for (; i < partsInfos.size(); i++) {
                insertVo = partsInfos.get(i);
                insertVo.setId(WzUniqueValUtil.makeUUID());
                insertVo.setPartsStatus(RecordStatus.NORMAL);
                insertVo.setPartsType(MfrsType.VEHICLE);
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
        try {
            partsInfo.setId(WzUniqueValUtil.makeUUID());
            partsInfo.setPartsStatus(RecordStatus.NORMAL);
            partsInfo.setPartsType(MfrsType.VEHICLE);
            partsInfo.setCreateTime(new Date());
            bizPartsMapperExt.insertSelective(partsInfo);
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "记录插入时发生错误", ex);
        }
    }

    @Override
    @Transactional
    public void updateBizParts(BizParts partsInfo) {
        bizPartsMapperExt.updateByPrimaryKeySelective(partsInfo);
    }

    @Override
    @Transactional
    public void deleteBizParts(List<String> ids) {
        int i = 0;
        try {
            for (; i < ids.size(); i++) {
                System.out.println(ids.get(i));
                bizPartsMapperExt.deleteByPrimaryKey(ids.get(i));
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
        }
    }

    @Override
    public BizParts getBizPartsByPrimaryKey(String id) {
        return bizPartsMapperExt.selectByPrimaryKey(id);
    }

}
