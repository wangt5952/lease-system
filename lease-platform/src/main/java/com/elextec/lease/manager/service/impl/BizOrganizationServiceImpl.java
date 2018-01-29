package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.service.BizOrganizationService;
import com.elextec.persist.dao.mybatis.BizOrganizationMapperExt;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.field.enums.RecordStatus;
import com.elextec.persist.model.mybatis.BizOrganization;
import com.elextec.persist.model.mybatis.BizOrganizationExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BizOrganizationServiceImpl implements BizOrganizationService {

    /*日志信息*/
    private final Logger logger = LoggerFactory.getLogger(BizOrganizationServiceImpl.class);

    @Autowired
    private BizOrganizationMapperExt bizOrganizationMapperExt;

    @Override
    public PageResponse<BizOrganization> list(boolean needPaging, PageRequest pr) {
        // 查询总记录数
        int mfrsTotal = 0;
        if (0 < pr.getTotal()) {
            mfrsTotal = pr.getTotal();
        } else {
            BizOrganizationExample bizOrganizationExample = new BizOrganizationExample();
            bizOrganizationExample.setDistinct(true);
            mfrsTotal = bizOrganizationMapperExt.countByExample(bizOrganizationExample);
        }
        // 分页查询
        BizOrganizationExample bizOrganizationExample = new BizOrganizationExample();
        bizOrganizationExample.setDistinct(true);
        if (needPaging) {
            bizOrganizationExample.setPageBegin(pr.getPageBegin());
            bizOrganizationExample.setPageSize(pr.getPageSize());
        }
        List<BizOrganization> mfrsLs = bizOrganizationMapperExt.selectByExample(bizOrganizationExample);
        // 组织并返回结果
        PageResponse<BizOrganization> presp = new PageResponse<BizOrganization>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(mfrsTotal);
        if (null == mfrsLs) {
            presp.setRows(new ArrayList<BizOrganization>());
        } else {
            presp.setRows(mfrsLs);
        }
        return presp;
    }

    @Override
    @Transactional
    public void insertBizOrganization(List<BizOrganization> mfrsInfos) {
        int i = 0;
        BizOrganization insertVo = null;
        try {
            for (; i < mfrsInfos.size(); i++) {
                if (bizOrganizationMapperExt.getByCode(mfrsInfos.get(i).getOrgCode()) == null) {
                    insertVo = mfrsInfos.get(i);
                    insertVo.setId(WzUniqueValUtil.makeUUID());
                    insertVo.setOrgStatus(RecordStatus.NORMAL);
                    insertVo.setOrgType(OrgAndUserType.ENTERPRISE);
                    insertVo.setCreateTime(new Date());
                    bizOrganizationMapperExt.insertSelective(insertVo);
                } else {
                    throw new BizException(RunningResult.MULTIPLE_RECORD.code(),"不能插入重复的code");
                }
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录插入时发生错误", ex);
        }
    }

    @Override
    @Transactional
    public void updateBizOrganization(BizOrganization mfrsInfo) {
        bizOrganizationMapperExt.updateByPrimaryKeySelective(mfrsInfo);
    }

    @Override
    @Transactional
    public void deleteBizOrganization(List<String> ids) {
        int i = 0;
        try {
            for (; i < ids.size(); i++) {
                System.out.println(ids.get(i));
                bizOrganizationMapperExt.deleteByPrimaryKey(ids.get(i));
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
        }
    }

    @Override
    public BizOrganization getBizOrganizationByPrimaryKey(String id) {
        return bizOrganizationMapperExt.selectByPrimaryKey(id);
    }
}
