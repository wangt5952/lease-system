package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.request.BizOrganizationParam;
import com.elextec.lease.manager.service.BizOrganizationService;
import com.elextec.persist.dao.mybatis.BizOrganizationMapperExt;
import com.elextec.persist.dao.mybatis.BizRefOrgVehicleMapperExt;
import com.elextec.persist.dao.mybatis.SysUserMapperExt;
import com.elextec.persist.field.enums.OrgAndUserType;
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
public class BizOrganizationServiceImpl implements BizOrganizationService {

    /*日志*/
    private final Logger logger = LoggerFactory.getLogger(BizOrganizationServiceImpl.class);

    @Autowired
    private BizOrganizationMapperExt bizOrganizationMapperExt;

    @Autowired
    private SysUserMapperExt sysUserMapperExt;

    @Autowired
    private BizRefOrgVehicleMapperExt bizRefOrgVehicleMapperExt;

    @Override
    public PageResponse<BizOrganization> list(boolean needPaging, PageRequest pr) {
        // 查询总记录数
        int mfrsTotal = 0;
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
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
    public PageResponse<BizOrganization> listByParam(boolean needPaging, BizOrganizationParam pr) {
        // 查询总记录数
        int orgTotal = 0;
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
            orgTotal = pr.getTotal();
        } else {
            orgTotal = bizOrganizationMapperExt.countByParam(pr);
        }
        // 分页查询
        if (needPaging) {
            pr.setPageBegin();
        }
        List<BizOrganization> orgLs = bizOrganizationMapperExt.selectByParam(pr);
        // 组织并返回结果
        PageResponse<BizOrganization> partsesp = new PageResponse<BizOrganization>();
        partsesp.setCurrPage(pr.getCurrPage());
        partsesp.setPageSize(pr.getPageSize());
        partsesp.setTotal(orgTotal);
        if (null == orgLs) {
            partsesp.setRows(new ArrayList<BizOrganization>());
        } else {
            partsesp.setRows(orgLs);
        }
        return partsesp;
    }

    @Override
    @Transactional
    public void insertBizOrganization(List<BizOrganization> orgInfos) {
        int i = 0;
        BizOrganization insertVo = null;
        try {
            for (; i < orgInfos.size(); i++) {
                //平台企业只能创建一个且不能通过接口创建
                if(OrgAndUserType.PLATFORM.toString().equals(orgInfos.get(i).getOrgType().toString())){
                    throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录插入时发生错误,平台企业只能创建一个");
                }
                insertVo = orgInfos.get(i);
                insertVo.setId(WzUniqueValUtil.makeUUID());
                insertVo.setOrgStatus(RecordStatus.NORMAL);
                insertVo.setOrgType(OrgAndUserType.ENTERPRISE);
                insertVo.setCreateTime(new Date());
                bizOrganizationMapperExt.insertSelective(insertVo);
            }
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录插入时发生错误", ex);
        }
    }

    @Override
    public void insertBizOrganization(BizOrganization orgInfo) {
        // 资源code重复提示错误
        BizOrganizationExample lnExample = new BizOrganizationExample();
        BizOrganizationExample.Criteria lnCriteria = lnExample.createCriteria();
        lnCriteria.andOrgCodeEqualTo(orgInfo.getOrgCode());
        int lnCnt = bizOrganizationMapperExt.countByExample(lnExample);
        if (0 < lnCnt) {
            throw new BizException(RunningResult.MULTIPLE_RECORD.code(), "企业code(" + orgInfo.getOrgCode() + ")已存在");
        }
        try {
            orgInfo.setId(WzUniqueValUtil.makeUUID());
            orgInfo.setOrgStatus(RecordStatus.NORMAL);
            orgInfo.setOrgType(OrgAndUserType.ENTERPRISE);
            orgInfo.setCreateTime(new Date());
            bizOrganizationMapperExt.insertSelective(orgInfo);
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "记录插入时发生错误", ex);
        }
    }

    @Override
    @Transactional
    public void updateBizOrganization(BizOrganization orgInfo) {
        //作废操作时，验证企业下面是否有用户和车辆
        if(RecordStatus.INVALID.toString().equals(orgInfo.getOrgStatus().toString())){
            //判定企业下是否有未作废的用户
            SysUserExample userExample = new SysUserExample();
            SysUserExample.Criteria userCriteria = userExample.createCriteria();
            userCriteria.andOrgIdEqualTo(orgInfo.getId());
            userCriteria.andUserStatusEqualTo(RecordStatus.NORMAL);
            int userCot = sysUserMapperExt.countByExample(userExample);
            if(userCot >= 1){
                throw new BizException(RunningResult.HAVE_BIND.code(), "企业名下有绑定的用户,无法作废");
            }
            //判定企业下是否有绑定的车辆
            BizRefOrgVehicleExample refExample = new BizRefOrgVehicleExample();
            BizRefOrgVehicleExample.Criteria refCriteria = refExample.createCriteria();
            refCriteria.andUnbindTimeIsNull();
            refCriteria.andOrgIdEqualTo(orgInfo.getId());
            int refCot = bizRefOrgVehicleMapperExt.countByExample(refExample);
            if(refCot >= 1){
                throw new BizException(RunningResult.HAVE_BIND.code(), "企业名下有绑定的车辆,无法作废");
            }
            //平台企业无法作废
            BizOrganizationExample orgExample = new BizOrganizationExample();
            BizOrganizationExample.Criteria orgCriteria = orgExample.createCriteria();
            orgCriteria.andIdEqualTo(orgInfo.getId());
            List<BizOrganization> org = bizOrganizationMapperExt.selectByExample(orgExample);
            if(org.size() >= 1){
                if(OrgAndUserType.PLATFORM.toString().equals(org.get(0).getOrgStatus().toString())){
                    throw new BizException(RunningResult.DB_ERROR.code(), "平台不能作废");
                }
            }


        }
        bizOrganizationMapperExt.updateByPrimaryKeySelective(orgInfo);
    }

    @Override
    @Transactional
    public void deleteBizOrganization(List<String> ids) {
        int i = 0;
        try {
            for (; i < ids.size(); i++) {
                SysUserExample userExample = new SysUserExample();
                SysUserExample.Criteria userCriteria = userExample.createCriteria();
                userCriteria.andUserStatusEqualTo(RecordStatus.NORMAL);
                BizRefOrgVehicleExample refExample = new BizRefOrgVehicleExample();
                BizRefOrgVehicleExample.Criteria refCriteria = refExample.createCriteria();
                refCriteria.andUnbindTimeIsNull();
                BizOrganizationExample orgExample = new BizOrganizationExample();
                BizOrganizationExample.Criteria orgCriteria = orgExample.createCriteria();
                userCriteria.andOrgIdEqualTo(ids.get(i));
                int userCot = sysUserMapperExt.countByExample(userExample);
                if(userCot >= 1){
                    throw new BizException(RunningResult.HAVE_BIND.code(), "第" + i + "条记录删除时发生错误,企业名下有绑定的用户");
                }
                refCriteria.andOrgIdEqualTo(ids.get(i));
                int refCot = bizRefOrgVehicleMapperExt.countByExample(refExample);
                if(refCot >= 1){
                    throw new BizException(RunningResult.HAVE_BIND.code(), "第" + i + "条记录删除时发生错误,企业名下有绑定的车辆");
                }
                //平台企业无法删除
                orgCriteria.andIdEqualTo(ids.get(i));
                List<BizOrganization> org = bizOrganizationMapperExt.selectByExample(orgExample);
                if(org.size() >= 1){
                    if(OrgAndUserType.PLATFORM.toString().equals(org.get(0).getOrgStatus().toString())){
                        throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误,平台不能作废");
                    }
                }
                bizOrganizationMapperExt.deleteByPrimaryKey(ids.get(i));
            }
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
        }
    }

    @Override
    public BizOrganization getBizOrganizationByPrimaryKey(String id) {
        return bizOrganizationMapperExt.selectByPrimaryKey(id);
    }
}
