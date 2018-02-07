package com.elextec.lease.manager.service;

import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.lease.manager.request.BizMfrsParam;
import com.elextec.persist.model.mybatis.BizManufacturer;

import java.util.List;

/**
 * 接口 制造商管理Service.
 * Created by wangtao on 2018/1/16.
 */
public interface BizManufacturerService {

    /**
     * 获得制造商列表.
     * @param needPaging 是否需要分页
     * @param pr 分页参数
     * @return 制造商列表
     */
    public PageResponse<BizManufacturer> list(boolean needPaging, PageRequest pr);

    /**
     * 获得制造商列表
     * @param needPaging 是否需要分页
     * @param pr 分页参数
     * @return 制造商列表
     */
    public PageResponse<BizManufacturer> listByParam(boolean needPaging, BizMfrsParam pr);

    /**
     * 批量插入制造商.
     * @param mfrsInfos 制造商信息
     */
    public void insertBizManufacturers(List<BizManufacturer> mfrsInfos);

    /**
     * 插入公司组织资源.
     * @param mfrsInfo 资源信息
     */
    public void insertBizManufacturers(BizManufacturer mfrsInfo);

    /**
     * 修改制造商信息.
     * @param mfrsInfo 新的制造商信息
     */
    public void updateBizManufacturer(BizManufacturer mfrsInfo);

    /**
     * 批量删除制造商.
     * @param ids 待删除的制造商ID列表
     */
    public void deleteBizManufacturers(List<String> ids);

    /**
     * 根据ID查询制造商信息
     * @param id 制造商ID
     * @return 制造商信息
     */
    public BizManufacturer getBizManufacturerByPrimaryKey(String id);
}
