package com.elextec.persist.dao.mybatis;

import com.elextec.persist.model.mybatis.BizOrganization;

public interface BizOrganizationMapperExt extends BizOrganizationMapper {

    /**
     * 根据code查询
     * @param orgCode
     * @return
     */
    public BizOrganization getByCode(String orgCode);

}
