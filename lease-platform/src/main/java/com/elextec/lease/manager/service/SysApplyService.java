package com.elextec.lease.manager.service;

import com.elextec.framework.common.request.RefUserRolesParam;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.lease.manager.request.SysApplyParam;
import com.elextec.lease.manager.request.SysUserParam;
import com.elextec.lease.model.BizVehicleBatteryParts;
import com.elextec.persist.model.mybatis.SysApply;
import com.elextec.persist.model.mybatis.SysApplyExample;
import com.elextec.persist.model.mybatis.SysUser;
import com.elextec.persist.model.mybatis.SysUserExample;
import com.elextec.persist.model.mybatis.ext.SysApplyExt;
import com.elextec.persist.model.mybatis.ext.SysUserExt;

import java.util.List;

/**
 * 接口 申请控制Service.
 * Created by wangtao on 2018/1/16.
 */
public interface SysApplyService {

    /**
     * 获得申请列表（附带扩展信息）.
     * @param needPaging 是否需要分页
     * @param pr 带条件的分页参数
     * @return 用户列表
     */
    public PageResponse<SysApplyExt> listExtByParam(boolean needPaging, SysApplyParam pr);


    /**
     * 插入申请.
     * @param applyInfo 申请信息
     */
    public void insertSysApply(SysApply applyInfo,String userType);


    /**
     * 修改申请信息.
     * @param applyInfo 新的用户信息
     */
    public void updateSysApply(SysApply applyInfo,String userId);

    /**
     * 批量删除申请.
     * @param ids 待删除的用户ID列表
     */
    public void deleteSysApply(List<String> ids,String userId);

    /**
     * 根据ID查询申请扩展信息.
     * @param example 查询条件
     * @return 用户扩展信息
     */
    public SysApplyExt getExtById(SysApplyExample example);


    /**
     * 审批申请
     * @param applyId 申请ID
     * @param orgId 企业ID
     */
    public void approval(String applyId,String authFlag,String orgId);

}
