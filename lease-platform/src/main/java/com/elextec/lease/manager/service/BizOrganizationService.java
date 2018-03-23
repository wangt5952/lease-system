package com.elextec.lease.manager.service;

import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.lease.manager.request.BizOrganizationParam;
import com.elextec.persist.model.mybatis.BizOrganization;
import com.elextec.persist.model.mybatis.BizVehicle;

import java.util.List;
import java.util.Map;

/**
 * 公司组织Service
 * Created by Yangkun on 2018/1/29
 */
public interface BizOrganizationService {

    /**
     * 获得公司组织列表.
     * @param needPaging 是否需要分页
     * @param pr 分页参数
     * @return 公司组织列表
     */
    public PageResponse<BizOrganization> list(boolean needPaging, PageRequest pr);

    /**
     * 获得公司组织列表.
     * @param needPaging 是否需要分页
     * @param pr 带条件的分页参数
     * @return 公司组织列表.
     */
    public PageResponse<BizOrganization> listByParam(boolean needPaging, BizOrganizationParam pr);

    /**
     * 批量插入公司组织.
     * @param orgInfos 公司组织信息
     */
    public void insertBizOrganization(List<BizOrganization> orgInfos);

    /**
     * 插入公司组织资源.
     * @param orgInfo 资源信息
     */
    public void insertBizOrganization(BizOrganization orgInfo);

    /**
     * 修改公司组织信息.
     * @param orgInfo 新的公司组织信息
     */
    public void updateBizOrganization(BizOrganization orgInfo);

    /**
     * 批量删除公司组织.
     * @param ids 待删除的公司ID列表
     */
    public void deleteBizOrganization(List<String> ids);

    /**
     * 根据ID查询公司组织信息
     * @param id 公司组织ID
     * @return 公司组织信息
     */
    public BizOrganization getBizOrganizationByPrimaryKey(String id);

    /**
     * 根据企业id查看该企业下的所有车辆
     * @param sysUserId 用户登录id
     * @param orgId 企业id
     * @return 车辆列表
     */
    public List<BizVehicle> getOrgIdByVehicle(String sysUserId, String orgId);

}
