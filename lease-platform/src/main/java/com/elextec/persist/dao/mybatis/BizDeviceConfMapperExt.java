package com.elextec.persist.dao.mybatis;

import java.util.List;

public interface BizDeviceConfMapperExt extends BizDeviceConfMapper {

    /**
     * 查询不重复id
     * @return
     */
    List<String> getDistinctDeviceId();

}
