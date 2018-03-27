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
    public void deleteSysApply(List<String> ids,String userId) {
        int i = 0;
        try {
            for (; i < ids.size(); i++) {
                SysApplyExample example = new SysApplyExample();
                SysApplyExample.Criteria criteria = example.createCriteria();
                criteria.andIdEqualTo(ids.get(i));
                criteria.andApplyUserIdEqualTo(userId);
                int count = sysApplyMapperExt.countByExample(example);
                if(count != 1){
                    throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误,你没有权限删除ID为["+ids.get(i)+"]的申请");
                }
                sysApplyMapperExt.deleteByExample(example);
            }
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
        }

    }

    @Override
    public SysApplyExt getExtById(SysApplyExample example) {
        List<SysApplyExt> apply = sysApplyMapperExt.selectExtByExample(example);
        if (null == apply || 0 == apply.size()) {
            throw new BizException(RunningResult.NO_USER);
        }
        if (1 != apply.size()) {
            throw new BizException(RunningResult.MULTIPLE_RECORD.code(), "查询到重复申请信息");
        }
        return apply.get(0);
    }

    @Override
    public void approval(String applyId,String authFlag,String orgId) {
        SysApplyExample example = new SysApplyExample();
        SysApplyExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(applyId);
        criteria.andExamineOrgIdEqualTo(orgId);
        List<SysApply> apply = sysApplyMapperExt.selectByExample(example);
        if(apply.size() != 1){
            throw new BizException(RunningResult.PARAM_ANALYZE_ERROR.code(),"申请不存在");
        }
        if(ApplyTypeAndStatus.REJECT.toString().equals(apply.get(0).getApplyStatus().toString())
                || ApplyTypeAndStatus.AGREE.toString().equals(apply.get(0).getApplyStatus().toString())){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "申请不能重复审核");
        }
        SysApply temp = new SysApply();
        temp.setId(applyId);
        temp.setApplyStatus(ApplyTypeAndStatus.valueOf(authFlag));
        sysApplyMapperExt.updateByPrimaryKeySelective(temp);
    }
}
