package com.elextec.lease.manager.service;

import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.persist.model.mybatis.BizOrganization;

import java.util.List;

/**
 * 公司组织Service
 * Created by Yangkun on 2018/1/29
 */
public interface BizOrganizationService {

    /**
     * 获得公司组织列表.
     * @param needPaging 是否需要分页
     * @param pr 分页参数
     * @return 制造商列表
     */
    public PageResponse<BizOrganization> list(boolean needPaging, PageRequest pr);

    /**
     * 批量插入公司组织.
     * @param mfrsInfos 制造商信息
     */
    public void insertBizOrganization(List<BizOrganization> mfrsInfos);

    /**
     * 修改公司组织信息.
     * @param mfrsInfo 新的制造商信息
     */
    public void updateBizOrganization(BizOrganization mfrsInfo);

    /**
     * 批量删除公司组织.
     * @param ids 待删除的制造商ID列表
     */
    public void deleteBizOrganization(List<String> ids);

    /**
     * 根据ID查询公司组织信息
     * @param id 公司组织ID
     * @return 公司组织信息
     */
    public BizOrganization getBizOrganizationByPrimaryKey(String id);

}
