package com.elextec.persist.dao.mybatis;

import com.elextec.lease.manager.request.BizOrganizationParam;
import com.elextec.persist.model.mybatis.BizOrganization;

import java.util.List;

public interface BizOrganizationMapperExt extends BizOrganizationMapper {

    /**
     * 根据code查询对象
     * @param orgCode
     * @return
     */
    public BizOrganization getByCode(String orgCode);

    /**
     * 查询配件扩展信息列表
     * @param bizOrganizationParam 查询条件
     * @return 配件扩展信息列表
     */
    public List<BizOrganization> selectByParam(BizOrganizationParam bizOrganizationParam);

    /**
     * 查询配件扩展信息记录数
     * @param bizOrganizationParam 查询条件
     * @return 配件扩展信息记录数
     */
    public int countByParam(BizOrganizationParam bizOrganizationParam);

}
