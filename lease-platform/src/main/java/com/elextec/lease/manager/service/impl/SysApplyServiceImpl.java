package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.request.SysApplyParam;
import com.elextec.lease.manager.service.SysApplyService;
import com.elextec.persist.dao.mybatis.BizOrganizationMapperExt;
import com.elextec.persist.dao.mybatis.SysApplyMapperExt;
import com.elextec.persist.dao.mybatis.SysUserMapperExt;
import com.elextec.persist.field.enums.ApplyTypeAndStatus;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.model.mybatis.BizOrganization;
import com.elextec.persist.model.mybatis.BizOrganizationExample;
import com.elextec.persist.model.mybatis.SysApply;
import com.elextec.persist.model.mybatis.SysApplyExample;
import com.elextec.persist.model.mybatis.ext.SysApplyExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SysApplyServiceImpl implements SysApplyService {

    @Autowired
    private SysApplyMapperExt sysApplyMapperExt;

    @Autowired
    private BizOrganizationMapperExt bizOrganizationMapperExt;

    @Autowired
    private SysUserMapperExt sysUserMapperExt;

    @Override
    public PageResponse<SysApplyExt> listExtByParam(boolean needPaging, SysApplyParam pr) {
        // 查询总记录数
        int appleTotal = 0;
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
            appleTotal = pr.getTotal();
        } else {
            appleTotal = sysApplyMapperExt.countExtByParam(pr);
        }
        // 分页查询
        if (needPaging) {
            pr.setPageBegin();
        }
        List<SysApplyExt> userLs = sysApplyMapperExt.selectExtByParam(pr);
        // 组织并返回结果
        PageResponse<SysApplyExt> presp = new PageResponse<SysApplyExt>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(appleTotal);
        if (null == userLs) {
            presp.setRows(new ArrayList<SysApplyExt>());
        } else {
            presp.setRows(userLs);
        }
        return presp;
    }

    @Override
    public void insertSysApply(SysApply applyInfo,String userType) {
        if(OrgAndUserType.ENTERPRISE.toString().equals(userType)){
            //企业用户提交的申请需要提交给平台用户
            BizOrganizationExample orgExample = new BizOrganizationExample();
            BizOrganizationExample.Criteria orgCriteria = orgExample.createCriteria();
            orgCriteria.andOrgTypeEqualTo(OrgAndUserType.PLATFORM);
            List<BizOrganization> org = bizOrganizationMapperExt.selectByExample(orgExample);
            if(org.size() != 1){
                throw new BizException(RunningResult.DB_ERROR.code(), "平台信息有误");
            }
            applyInfo.setExamineOrgId(org.get(0).getId());
        }
        applyInfo.setApplyType(ApplyTypeAndStatus.VEHICLEAPPLY);
        applyInfo.setApplyStatus(ApplyTypeAndStatus.TOBEAUDITED);
        applyInfo.setId(WzUniqueValUtil.makeUUID());
        applyInfo.setCreateTime(new Date());
        sysApplyMapperExt.insertSelective(applyInfo);
    }

    @Override
    public void updateSysApply(SysApply applyInfo,String userId) {
        //查询申请的原信息
        SysApply sysApply = sysApplyMapperExt.selectByPrimaryKey(applyInfo.getId());
        if(sysApply == null){
            throw new BizException(RunningResult.DB_ERROR.code(), "申请信息不存在");
        }
        if(ApplyTypeAndStatus.AGREE.toString().equals(sysApply.getApplyStatus().toString())
                || ApplyTypeAndStatus.REJECT.toString().equals(sysApply.getApplyStatus().toString())){
            throw new BizException(RunningResult.DB_ERROR.code(), "申请已处理，无法修改");
        }
        if(!userId.equals(sysApply.getApplyUserId())){
            throw new BizException(RunningResult.DB_ERROR.code(), "无权修改他人的申请");
        }
        sysApplyMapperExt.updateByPrimaryKeySelective(applyInfo);
    }

    @Override
    public void deleteSysApply(List<String> ids) {

    }

    @Override
    public SysApplyExt getExtById(SysApplyExample example) {
//        SysApplyExt apply = sysApplyMapperExt.selectExtByExample()
        return null;
    }
}
