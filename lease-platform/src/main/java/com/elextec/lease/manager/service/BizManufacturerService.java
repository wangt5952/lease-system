package com.elextec.lease.manager.service;

import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.persist.model.mybatis.BizManufacturer;

import java.util.List;
import java.util.Map;

public interface BizManufacturerService {

    /**
     * 添加制造商信息
     * @param list
     * @return
     */
    public MessageResponse insert(List<BizManufacturer> list);

    /**
     * 删除制造商信息
     * @param list
     * @return
     */
    public MessageResponse deleteByPrimaryKey(List<String> list);

    /**
     * 修改对象信息
     * @param list
     * @return
     */
    public MessageResponse updateByPrimaryKey(List<BizManufacturer> list);

    /**
     * 分页
     * @param needPaging 是否分页
     * @return
     */
    public PageResponse<BizManufacturer> paging(boolean needPaging, PageRequest pr);

    /**
     * 根据id查询对象
     * @param id
     * @return
     */
    public MessageResponse selectByPrimaryKey(String id);

}
