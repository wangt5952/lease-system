package com.elextec.lease.manager.service;

import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.lease.manager.request.BizPartsParam;
import com.elextec.persist.model.mybatis.BizParts;
import com.elextec.persist.model.mybatis.ext.BizPartsExt;

import java.util.List;
import java.util.Map;

/**
 * 接口 配件控制Service.
 * Created by wangtao on 2018/1/16.
 */
public interface BizPartsService {

    /**
     * 获得配件列表.
     * @param needPaging 是否需要分页
     * @param pr 分页参数
     * @return 用户列表
     */
    public PageResponse<BizParts> list(boolean needPaging, PageRequest pr);

    /**
     * 获得配件列表（附带扩展信息）.
     * @param needPaging 是否需要分页
     * @param pr 带条件的分页参数
     * @return 配件列表
     */
    public PageResponse<BizPartsExt> listExtByParam(boolean needPaging, BizPartsParam pr);

    /**
     * 批量插入配件
     * @param partsInfos 配件信息
     */
    public void insertBizParts(List<BizParts> partsInfos);

    /**
     * 插入配件.
     * @param partsInfo 配件信息
     */
    public void insertBizParts(BizParts partsInfo);

    /**
     * 修改用户信息.
     * @param partsInfo 新的配件信息
     */
    public void updateBizParts(BizParts partsInfo);

    /**
     * 批量删除配件.
     * @param ids 待删除的用户ID列表
     */
    public void deleteBizParts(List<String> ids);

    /**
     * 根据ID查询配件信息
     * @param param 配件ID、用户ID或企业ID
     * */
    public BizPartsExt getBizPartsByPrimaryKey(Map<String,Object> param);

    /**
     * 车辆和配件绑定接口
     * @param vehicleId 车辆ID
     * @param partsId 配件ID
     */
    public void bind(String vehicleId,String partsId);

    /**
     * 车辆和配件解绑接口
     * @param vehicleId 车辆ID
     * @param partsId 配件ID
     */
    public void unBind(String vehicleId,String partsId);

}
