package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.request.RefUserRolesParam;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzFileUtil;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.request.BizVehicleParam;
import com.elextec.lease.manager.request.SysUserParam;
import com.elextec.lease.manager.service.SysUserService;
import com.elextec.lease.model.BizVehicleBatteryParts;
import com.elextec.lease.model.SysResourcesIcon;
import com.elextec.lease.model.SysUserIcon;
import com.elextec.persist.dao.mybatis.*;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.field.enums.RealNameAuthFlag;
import com.elextec.persist.field.enums.RecordStatus;
import com.elextec.persist.model.mybatis.*;
import com.elextec.persist.model.mybatis.ext.BizBatteryExt;
import com.elextec.persist.model.mybatis.ext.BizPartsExt;
import com.elextec.persist.model.mybatis.ext.BizVehicleExt;
import com.elextec.persist.model.mybatis.ext.SysUserExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.util.*;

/**
 * 资源管理Service实现类.
 * Created by wangtao on 2018/1/16.
 */
@Service
public class SysUserServcieImpl implements SysUserService {

    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(SysUserServcieImpl.class);

    @Value("${localsetting.upload-user-icon-root}")
    private String  uploadUserIconRoot;

    @Value("${localsetting.download-user-icon-prefix}")
    private String downloadUserIconPrefix;

    @Autowired
    private SysUserMapperExt sysUserMapperExt;

    @Autowired
    private BizVehicleMapperExt bizVehicleMapperExt;

    @Autowired
    private BizBatteryMapperExt bizBatteryMapperExt;

    @Autowired
    private BizPartsMapperExt bizPartsMapperExt;

    @Autowired
    private BizRefUserVehicleMapperExt bizRefUserVehicleMapperExt;

    @Autowired
    private BizRefOrgVehicleMapperExt bizRefOrgVehicleMapperExt;

    @Autowired
    private SysRefUserRoleMapperExt sysRefUserRoleMapperExt;

    @Autowired
    private BizOrganizationMapperExt bizOrganizationMapperExt;

    @Override
    public PageResponse<SysUserExt> list(boolean needPaging, SysUserParam pr) {
        // 查询总记录数
        int userTotal = 0;
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
            userTotal = pr.getTotal();
        } else {
            SysUserExample sysUserCountExample = new SysUserExample();
            sysUserCountExample.setDistinct(true);
            userTotal = sysUserMapperExt.countByExample(sysUserCountExample);
        }
        // 分页查询
        SysUserExample sysUserLsExample = new SysUserExample();
        sysUserLsExample.setDistinct(true);
        if (needPaging) {
            sysUserLsExample.setPageBegin(pr.getPageBegin());
            sysUserLsExample.setPageSize(pr.getPageSize());
        }
        List<SysUserExt> userLs = sysUserMapperExt.selectExtByExample(sysUserLsExample);
        // 组织并返回结果
        PageResponse<SysUserExt> presp = new PageResponse<SysUserExt>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(userTotal);
        if (null == userLs) {
            presp.setRows(new ArrayList<SysUserExt>());
        } else {
            presp.setRows(userLs);
        }
        return presp;
    }

    @Override
    public PageResponse<SysUserExt> listExtByParam(boolean needPaging, SysUserParam pr) {
        // 查询总记录数
        int userTotal = 0;
        if (null != pr.getTotal() && 0 < pr.getTotal()) {
            userTotal = pr.getTotal();
        } else {
            userTotal = sysUserMapperExt.countExtByParam(pr);
        }
        // 分页查询
        if (needPaging) {
            pr.setPageBegin();
            pr.setNeedPaging("true");
        }else{
            pr.setNeedPaging("false");
        }
        List<SysUserExt> userLs = sysUserMapperExt.selectExtByParam(pr);
        // 组织并返回结果
        PageResponse<SysUserExt> presp = new PageResponse<SysUserExt>();
        if(needPaging){
            presp.setCurrPage(pr.getCurrPage());
            presp.setPageSize(pr.getPageSize());
        }
        presp.setTotal(userTotal);
        if (null == userLs) {
            presp.setRows(new ArrayList<SysUserExt>());
        } else {
            presp.setRows(userLs);
        }
        return presp;
    }

    @Override
    @Transactional
    public void insertSysUsers(List<SysUser> usersInfos) {
        int i = 0;
        SysUser insertVo = null;
        try {
            for (; i < usersInfos.size(); i++) {
                //验证企业是否存在（状态为正常）
                if(WzStringUtil.isNotBlank(usersInfos.get(i).getOrgId())){
                    BizOrganizationExample orgExample = new BizOrganizationExample();
                    BizOrganizationExample.Criteria orgCriteria = orgExample.createCriteria();
                    orgCriteria.andIdEqualTo(usersInfos.get(i).getOrgId());
                    orgCriteria.andOrgStatusEqualTo(RecordStatus.NORMAL);
                    List<BizOrganization> org = bizOrganizationMapperExt.selectByExample(orgExample);
                    if(org.size() < 1){
                        throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录插入时发生错误,用户相关企业不存在或已作废");
                    }
                    //验证企业类型与用户类型是否一致
                    if(OrgAndUserType.PLATFORM.toString().equals(org.get(0).getOrgType().toString())
                            && OrgAndUserType.ENTERPRISE.toString().equals(usersInfos.get(i).getUserType().toString())){
                        throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录插入时发生错误,平台下不能创建企业用户");
                    }
                    if(OrgAndUserType.ENTERPRISE.toString().equals(org.get(0).getOrgType().toString())
                            && OrgAndUserType.PLATFORM.toString().equals(usersInfos.get(i).getUserType().toString())){
                        throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录插入时发生错误,普通企业下不能创建平台用户");
                    }

                }
                insertVo = usersInfos.get(i);
                insertVo.setId(WzUniqueValUtil.makeUUID());
                insertVo.setCreateTime(new Date());
                sysUserMapperExt.insertSelective(insertVo);
            }
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录插入时发生错误", ex);
        }
    }

    @Override
    @Transactional
    public void insertSysUser(SysUser userInfo) {
        // 用户名重复提示错误
        SysUserExample lnExample = new SysUserExample();
        SysUserExample.Criteria lnCriteria = lnExample.createCriteria();
        lnCriteria.andLoginNameEqualTo(userInfo.getLoginName());
        int lnCnt = sysUserMapperExt.countByExample(lnExample);
        if (0 < lnCnt) {
            throw new BizException(RunningResult.MULTIPLE_RECORD.code(), "用户名(" + userInfo.getLoginName() + ")已存在");
        }
        // 手机号码重复提示错误
        SysUserExample mobileExample = new SysUserExample();
        SysUserExample.Criteria mobileCriteria = mobileExample.createCriteria();
        mobileCriteria.andUserMobileEqualTo(userInfo.getUserMobile());
        int mobileCnt = sysUserMapperExt.countByExample(mobileExample);
        if (0 < mobileCnt) {
            throw new BizException(RunningResult.MULTIPLE_RECORD.code(), "手机号码(" + userInfo.getUserMobile() + ")已存在");
        }
        //验证企业是否存在（状态为正常）
        if(WzStringUtil.isNotBlank(userInfo.getOrgId())){
            BizOrganizationExample orgExample = new BizOrganizationExample();
            BizOrganizationExample.Criteria orgCriteria = orgExample.createCriteria();
            orgCriteria.andIdEqualTo(userInfo.getOrgId());
            orgCriteria.andOrgStatusEqualTo(RecordStatus.NORMAL);
            List<BizOrganization> org = bizOrganizationMapperExt.selectByExample(orgExample);
            if(org.size() < 1){
                throw new BizException(RunningResult.DB_ERROR.code(), "用户相关企业不存在或已作废");
            }
            //验证企业类型与用户类型是否一致
            if(OrgAndUserType.PLATFORM.toString().equals(org.get(0).getOrgType().toString())
                    && OrgAndUserType.ENTERPRISE.toString().equals(userInfo.getUserType().toString())){
                throw new BizException(RunningResult.DB_ERROR.code(), "平台下不能创建企业用户");
            }
            if(OrgAndUserType.ENTERPRISE.toString().equals(org.get(0).getOrgType().toString())
                    && OrgAndUserType.PLATFORM.toString().equals(userInfo.getUserType().toString())){
                throw new BizException(RunningResult.DB_ERROR.code(), "普通企业下不能创建平台用户");
            }

        }
        // 保存用户信息
        try {
            userInfo.setId(WzUniqueValUtil.makeUUID());
            userInfo.setCreateTime(new Date());
            sysUserMapperExt.insertSelective(userInfo);
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "记录插入时发生错误", ex);
        }
    }

    @Override
    @Transactional
    public void updateSysUser(SysUser userInfo) {
        //如果是用户作废，需要验证用户是否有在绑的车辆和删除所有角色关联
        if(null != userInfo.getUserStatus() && RecordStatus.INVALID.toString().equals(userInfo.getUserStatus().toString())){
            //判断用户是否为admin
            SysUser userTemp = sysUserMapperExt.selectByPrimaryKey(userInfo.getId());
            //admin作为基本用户无法删除
            if("admin".equals(userTemp.getLoginName())){
                throw new BizException(RunningResult.HAVE_BIND.code(), "系统管理员无法作废");
            }
            //验证用户名下是否有未归还的车辆
            BizRefUserVehicleExample refExample = new BizRefUserVehicleExample();
            BizRefUserVehicleExample.Criteria selectRefCriteria = refExample.createCriteria();
            selectRefCriteria.andBindTimeIsNull();
            selectRefCriteria.andUserIdEqualTo(userInfo.getId());
            int count = bizRefUserVehicleMapperExt.countByExample(refExample);
            if(count >= 1){
                throw new BizException(RunningResult.HAVE_BIND.code(), "用户名下有未归还的车辆,无法作废");
            }
            sysUserMapperExt.updateByPrimaryKeySelective(userInfo);
            SysRefUserRoleExample delExample = new SysRefUserRoleExample();
            SysRefUserRoleExample.Criteria delCriteria = delExample.createCriteria();
            //删除用户和角色的所有关联
            delCriteria.andUserIdEqualTo(userInfo.getId());
            sysRefUserRoleMapperExt.deleteByExample(delExample);
        }else{
            //判断用户是否为admin
            SysUser userTemp = sysUserMapperExt.selectByPrimaryKey(userInfo.getId());
            //admin作为基本用户无法修改他的归属企业和用户类型
            if("admin".equals(userTemp.getLoginName())
                    && !OrgAndUserType.PLATFORM.toString().equals(userInfo.getUserType().toString())){
//                userInfo.setOrgId(null);
//                //用户类型无法修改，清空上传数据
//                userInfo.setUserType(null);
                throw new BizException(RunningResult.DB_ERROR.code(), "系统管理员不能修改用户类型");
            }
//            if(OrgAndUserType.INDIVIDUAL.toString().equals(userTemp.getUserType().toString())){
//                throw new BizException(RunningResult.DB_ERROR.code(), "不能修改个人用户信息");
//            }
            //验证企业是否存在（状态为正常）
            if(WzStringUtil.isNotBlank(userInfo.getOrgId())){
                BizOrganizationExample orgExample = new BizOrganizationExample();
                BizOrganizationExample.Criteria orgCriteria = orgExample.createCriteria();
                orgCriteria.andIdEqualTo(userInfo.getOrgId());
                orgCriteria.andOrgStatusEqualTo(RecordStatus.NORMAL);
                List<BizOrganization> org = bizOrganizationMapperExt.selectByExample(orgExample);
                if(org.size() < 1){
                    throw new BizException(RunningResult.DB_ERROR.code(), "用户相关企业不存在或已作废");
                }
                if("admin".equals(userTemp.getLoginName())
                        && !userTemp.getOrgId().equals(userInfo.getOrgId())){
                    throw new BizException(RunningResult.DB_ERROR.code(), "系统管理员不能修改所属企业");
                }
                //验证企业类型与用户类型是否一致
                if(OrgAndUserType.PLATFORM.toString().equals(org.get(0).getOrgType().toString())
                        && OrgAndUserType.ENTERPRISE.toString().equals(userInfo.getUserType().toString())){
                    throw new BizException(RunningResult.DB_ERROR.code(), "平台下不能创建企业用户");
                }
                if(OrgAndUserType.ENTERPRISE.toString().equals(org.get(0).getOrgType().toString())
                        && OrgAndUserType.PLATFORM.toString().equals(userInfo.getUserType().toString())){
                    throw new BizException(RunningResult.DB_ERROR.code(), "普通企业下不能创建平台用户");
                }
            }
            sysUserMapperExt.updateByPrimaryKeySelective(userInfo);
        }
    }

    @Override
    @Transactional
    public void deleteSysUser(List<String> ids) {
        int i = 0;
        try {
            for (; i < ids.size(); i++) {
                //判断用户是否为admin
                SysUser userTemp = sysUserMapperExt.selectByPrimaryKey(ids.get(i));
                //admin作为基本用户无法删除
                if("admin".equals(userTemp.getLoginName())){
                    throw new BizException(RunningResult.HAVE_BIND.code(), "系统管理员无法作废");
                }
                BizRefUserVehicleExample refExample = new BizRefUserVehicleExample();
                BizRefUserVehicleExample.Criteria selectRefCriteria = refExample.createCriteria();
                selectRefCriteria.andBindTimeIsNull();
                SysRefUserRoleExample delExample = new SysRefUserRoleExample();
                SysRefUserRoleExample.Criteria delCriteria = delExample.createCriteria();
                SysUserExample sysUserExample = new SysUserExample();
                SysUserExample.Criteria sysUserCriteria = sysUserExample.createCriteria();
                sysUserCriteria.andLoginNameEqualTo("admin");
                //验证用户名下是否有未归还的车辆
                selectRefCriteria.andUserIdEqualTo(ids.get(i));
                int count = bizRefUserVehicleMapperExt.countByExample(refExample);
                if(count >= 1){
                    throw new BizException(RunningResult.HAVE_BIND.code(), "第" + i + "条记录删除时发生错误,用户名下有未归还的车辆");
                }
                //基本用户admin不能删除
                sysUserCriteria.andIdEqualTo(ids.get(i));
                int sysCot = sysUserMapperExt.countByExample(sysUserExample);
                if(sysCot >= 1){
                    throw new BizException(RunningResult.HAVE_BIND.code(), "第" + i + "条记录删除时发生错误,admin不能删除");
                }
                sysUserMapperExt.deleteByPrimaryKey(ids.get(i));
                //删除用户和角色的所有关联
                delCriteria.andUserIdEqualTo(ids.get(i));
                sysRefUserRoleMapperExt.deleteByExample(delExample);

            }
        } catch (BizException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
        }
    }

    @Override
    public void updatePassword(SysUser user) {
        sysUserMapperExt.updateByPrimaryKeySelective(user);
    }

    @Override
    @Transactional
    public void refSysUserAndRoles(RefUserRolesParam params){
        int i = 0;
        String userId = params.getUserId();
        if ("true".equals(params.getDeleteAllFlg().toLowerCase())) {
            sysUserMapperExt.deleteUserAndRoles(userId);
        } else {
            String[] rolesIds = params.getRoleIds().split(",");
            SysRefUserRoleKey sysRefUserRoleKey = new SysRefUserRoleKey();
            if(rolesIds.length > 0){
                try{
                    //删除用户原来的ROLE
                    sysUserMapperExt.deleteUserAndRoles(userId);
                    for (; i < rolesIds.length; i++) {
                        sysRefUserRoleKey.setUserId(userId);
                        sysRefUserRoleKey.setRoleId(rolesIds[i]);
                        sysUserMapperExt.refUserAndRoles(sysRefUserRoleKey);
                    }
                }catch(Exception ex){
                    throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
                }
            }
        }
    }

    @Override
    public SysUser getSysUserByPrimaryKey(String id) {
        SysUser data = sysUserMapperExt.selectByPrimaryKey(id);
        if (null == data) {
            throw new BizException(RunningResult.NO_USER);
        }
        return data;
    }

    @Override
    public SysUserExt getExtById(SysUserExample example) {
        List<SysUserExt> datas = sysUserMapperExt.selectExtByExample(example);
        if (null == datas || 0 == datas.size()) {
            throw new BizException(RunningResult.NO_USER);
        }
        if (1 != datas.size()) {
            throw new BizException(RunningResult.MULTIPLE_RECORD.code(), "查询到重复用户信息");
        }
        return datas.get(0);
    }

    @Override
    public List<BizVehicleBatteryParts> getVehiclePartsById(String userId) {
        List<BizVehicleBatteryParts> datas = bizVehicleMapperExt.getVehicleInfoByUserId(userId);
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("flag",true);
        if(datas.size() > 0){
            for(int i=0;i<datas.size();i++){
                //根据车辆ID获取电池信息
                param.put("id",datas.get(i).getId());
                List<BizBatteryExt> batteryDatas = bizBatteryMapperExt.getBatteryInfoByVehicleId(param);
                datas.get(i).setBizBatteries(batteryDatas);
                //根据车辆ID获取配件信息
                List<BizPartsExt> partsDatas = bizPartsMapperExt.getById(param);
                datas.get(i).setBizPartss(partsDatas);
            }
        }
        return datas;
    }

    @Override
    public void unBind(String userId, String vehicleId,String orgId) {
        //判定用户是否在操作用的企业名下
        SysUserExample userExample = new SysUserExample();
        SysUserExample.Criteria selectUserCriteria = userExample.createCriteria();
        selectUserCriteria.andIdEqualTo(userId);
        if(null != orgId){
            selectUserCriteria.andOrgIdEqualTo(orgId);
        }
        int userCount = sysUserMapperExt.countByExample(userExample);
        if(userCount<1){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "用户不存在或用户不在企业名下");
        }

        BizRefUserVehicle param = new BizRefUserVehicle();
        param.setUnbindTime(new Date());
        BizRefUserVehicleExample refExample = new BizRefUserVehicleExample();
        BizRefUserVehicleExample.Criteria selectRefCriteria = refExample.createCriteria();
        selectRefCriteria.andUserIdEqualTo(userId);
        selectRefCriteria.andVehicleIdEqualTo(vehicleId);
        selectRefCriteria.andBindTimeIsNotNull();
        selectRefCriteria.andUnbindTimeIsNull();
        int temp = bizRefUserVehicleMapperExt.updateByExampleSelective(param,refExample);
        if(temp < 1){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "车辆未被绑定或未与该用户绑定");
        }
    }

    @Override
    public void bind(String userId, String vehicleId,String orgId) {

        //判定用户是否存在或已作废
        SysUserExample userExample = new SysUserExample();
        SysUserExample.Criteria selectUserCriteria = userExample.createCriteria();
        selectUserCriteria.andIdEqualTo(userId);
        //平台用户与企业用户下面不可以绑定车辆
        selectUserCriteria.andUserTypeEqualTo(OrgAndUserType.INDIVIDUAL);
        selectUserCriteria.andUserStatusEqualTo(RecordStatus.NORMAL);
        if(null != orgId){
            selectUserCriteria.andOrgIdEqualTo(orgId);
        }
        List<SysUserExt> user = sysUserMapperExt.selectExtByExample(userExample);
        if(user.size() < 1){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "用户不存在或已作废");
        }

        //判定车辆是否存在或已作废
        BizVehicleExample vehicleExample = new BizVehicleExample();
        BizVehicleExample.Criteria selectVehicleCriteria = vehicleExample.createCriteria();
        selectVehicleCriteria.andIdEqualTo(vehicleId);
        selectVehicleCriteria.andVehicleStatusEqualTo(RecordStatus.NORMAL);
        int vehicleCount = bizVehicleMapperExt.countByExample(vehicleExample);
        if(vehicleCount < 1){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "车辆不存在或已冻结、作废");
        }

        //判定车辆是否属于操作用户的企业名下
        BizRefOrgVehicleExample orgVehicleExample = new BizRefOrgVehicleExample();
        BizRefOrgVehicleExample.Criteria selectOrgVehicleExample = orgVehicleExample.createCriteria();
        if(null != orgId){
            selectOrgVehicleExample.andOrgIdEqualTo(orgId);
        }
        selectOrgVehicleExample.andUnbindTimeIsNull();
        selectOrgVehicleExample.andVehicleIdEqualTo(vehicleId);
        List<BizRefOrgVehicle> orgVehicle = bizRefOrgVehicleMapperExt.selectByExample(orgVehicleExample);
        if(orgVehicle.size() < 1){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "车辆与操作用户不属于同一企业");
        }

        //判定车辆是否与分配用户企业相同
        if(!orgVehicle.get(0).getOrgId().equals(user.get(0).getOrgId())){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "车辆与分配用户不属于同一企业");
        }

        //校验车辆是否已经被绑定
        BizRefUserVehicleExample refExample = new BizRefUserVehicleExample();
        BizRefUserVehicleExample.Criteria selectRefCriteria = refExample.createCriteria();
        selectRefCriteria.andVehicleIdEqualTo(vehicleId);
        selectRefCriteria.andBindTimeIsNotNull();
        selectRefCriteria.andUnbindTimeIsNull();
        int refCount = bizRefUserVehicleMapperExt.countByExample(refExample);
        if(refCount >= 1){
            throw new BizException(RunningResult.HAVE_BIND.code(), "车辆已被绑定");
        }

        BizRefUserVehicle param = new BizRefUserVehicle();
        param.setUserId(userId);
        param.setVehicleId(vehicleId);
        param.setBindTime(new Date());
        bizRefUserVehicleMapperExt.insert(param);

    }

    @Override
    @Transactional
    public void batchBind(int count, String orgId,String userOrgId) {
        //验证企业是否存在（状态为正常）
        BizOrganizationExample orgExample = new BizOrganizationExample();
        BizOrganizationExample.Criteria orgCriteria = orgExample.createCriteria();
        orgCriteria.andIdEqualTo(orgId);
        orgCriteria.andOrgStatusEqualTo(RecordStatus.NORMAL);
        orgCriteria.andOrgTypeEqualTo(OrgAndUserType.ENTERPRISE);
        int orgCot = bizOrganizationMapperExt.countByExample(orgExample);
        if(orgCot < 1){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "企业不存在或已作废");
        }
        //查询平台下未分配出去的车辆
//        BizRefOrgVehicleExample refExample = new BizRefOrgVehicleExample();
//        BizRefOrgVehicleExample.Criteria refCriteria = refExample.createCriteria();
//        refCriteria.andOrgIdEqualTo(userOrgId);
//        refCriteria.andUnbindTimeIsNull();
        BizVehicleParam paramMap = new BizVehicleParam();
        paramMap.setOrgId(userOrgId);
        paramMap.setVehicleStatus(RecordStatus.NORMAL.toString());
        List<BizVehicleExt> vehicles = bizVehicleMapperExt.selectExtByParam(paramMap);
        if(vehicles.size() < count){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "库存车辆数量不足");
        }
        //将指定数量的车辆分配到指定企业
        for(int i=0;i<count;i++){
            //解除原有绑定关系
            BizRefOrgVehicleExample delExample = new BizRefOrgVehicleExample();
            BizRefOrgVehicleExample.Criteria delCriteria = delExample.createCriteria();
            delCriteria.andOrgIdEqualTo(userOrgId);
            delCriteria.andVehicleIdEqualTo(vehicles.get(i).getId());
            delCriteria.andUnbindTimeIsNull();
            BizRefOrgVehicle delTemp = new BizRefOrgVehicle();
            delTemp.setUnbindTime(new Date());

            bizRefOrgVehicleMapperExt.updateByExampleSelective(delTemp,delExample);
            //绑定新关系
            BizRefOrgVehicle addTemp = new BizRefOrgVehicle();
            addTemp.setOrgId(orgId);
            addTemp.setVehicleId(vehicles.get(i).getId());
            addTemp.setBindTime(new Date());
            bizRefOrgVehicleMapperExt.insertSelective(addTemp);
        }
    }

    @Override
    @Transactional
    public void batchUnbind(int count, String orgId) {
        //验证企业是否存在（状态为正常）
        BizOrganizationExample orgExample = new BizOrganizationExample();
        BizOrganizationExample.Criteria orgCriteria = orgExample.createCriteria();
        orgCriteria.andIdEqualTo(orgId);
        orgCriteria.andOrgStatusEqualTo(RecordStatus.NORMAL);
        orgCriteria.andOrgTypeEqualTo(OrgAndUserType.ENTERPRISE);
        int orgCot = bizOrganizationMapperExt.countByExample(orgExample);
        if(orgCot < 1){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "企业不存在或已作废");
        }
        //查询企业下未分配出去的车辆
//        BizRefOrgVehicleExample refExample = new BizRefOrgVehicleExample();
//        BizRefOrgVehicleExample.Criteria refCriteria = refExample.createCriteria();
//        refCriteria.andOrgIdEqualTo(orgId);
//        refCriteria.andUnbindTimeIsNull();
//        List<BizRefOrgVehicle> list = bizRefOrgVehicleMapperExt.selectByExample(refExample);
//        BizVehicleParam paramMap = new BizVehicleParam();
//        paramMap.setOrgId(orgId);
//        paramMap.setVehicleStatus(RecordStatus.NORMAL.toString());
//        List<BizVehicleExt> vehicles = bizVehicleMapperExt.selectExtByParam(paramMap);
//        if(vehicles.size() < count){
//            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "库存车辆数量不足");
//        }
        BizVehicleParam param = new BizVehicleParam();
        param.setOrgId(orgId);
        param.setVehicleStatus(RecordStatus.NORMAL.toString());
        param.setIsBind("UNBIND");
        List<BizVehicleExt> list = bizVehicleMapperExt.selectExtUnbindExtByParams(param);
        if(list.size() < count){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "库存车辆数量不足");
        }

        //查询平台企业ID
        BizOrganizationExample plExample = new BizOrganizationExample();
        BizOrganizationExample.Criteria plCriteria = plExample.createCriteria();
        plCriteria.andOrgTypeEqualTo(OrgAndUserType.PLATFORM);
        List<BizOrganization> plOrg = bizOrganizationMapperExt.selectByExample(plExample);
        if(plOrg.size() != 1){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "平台信息异常用,平台不存在或存在多个");
        }
        //将指定数量的车辆归还给平台
        for(int i=0;i<count;i++){
            //解除原有绑定关系
//            BizRefOrgVehicleExample delExample = new BizRefOrgVehicleExample();
//            BizRefOrgVehicleExample.Criteria delCriteria = delExample.createCriteria();
//            delCriteria.andOrgIdEqualTo(orgId);
//            delCriteria.andVehicleIdEqualTo(vehicles.get(i).getId());
//            delCriteria.andUnbindTimeIsNull();
//            BizRefOrgVehicle delTemp = new BizRefOrgVehicle();
//            delTemp.setUnbindTime(new Date());
//            bizRefOrgVehicleMapperExt.updateByExampleSelective(delTemp,delExample);
            BizRefOrgVehicleExample delExample = new BizRefOrgVehicleExample();
            BizRefOrgVehicleExample.Criteria delCriteria = delExample.createCriteria();
            delCriteria.andOrgIdEqualTo(orgId);
            delCriteria.andVehicleIdEqualTo(list.get(i).getId());
            delCriteria.andUnbindTimeIsNull();
            BizRefOrgVehicle delTemp = new BizRefOrgVehicle();
            delTemp.setUnbindTime(new Date());
            bizRefOrgVehicleMapperExt.updateByExampleSelective(delTemp,delExample);
            //绑定新关系
//            BizRefOrgVehicle addTemp = new BizRefOrgVehicle();
//            addTemp.setOrgId(plOrg.get(0).getId());
//            addTemp.setVehicleId(vehicles.get(i).getId());
//            addTemp.setBindTime(new Date());
//            bizRefOrgVehicleMapperExt.insertSelective(addTemp);
            BizRefOrgVehicle addTemp = new BizRefOrgVehicle();
            addTemp.setOrgId(plOrg.get(0).getId());
            addTemp.setVehicleId(list.get(i).getId());
            addTemp.setBindTime(new Date());
            bizRefOrgVehicleMapperExt.insertSelective(addTemp);
        }
    }

    @Override
    public SysUser getByMobile(String mobile) {
        SysUserExample example = new SysUserExample();
        SysUserExample.Criteria selectMobile = example.createCriteria();
        selectMobile.andUserMobileEqualTo(mobile);
        List<SysUser> user = sysUserMapperExt.selectByExample(example);
        if(user.size() < 1){
            throw new BizException(RunningResult.NO_USER.code(),"用户未注册");
        }
        return user.get(0);
    }

    @Override
    @Transactional
    public void modifyInformation(SysUser user) {
        sysUserMapperExt.updateByPrimaryKeySelective(user);
    }

    @Override
    public void approval(String userId,String authFlag,String orgId) {
        SysUserExample example = new SysUserExample();
        SysUserExample.Criteria selectMobile = example.createCriteria();
        selectMobile.andIdEqualTo(userId);
        selectMobile.andOrgIdEqualTo(orgId);
        selectMobile.andUserStatusEqualTo(RecordStatus.NORMAL);
        selectMobile.andUserTypeEqualTo(OrgAndUserType.INDIVIDUAL);
        List<SysUser> user = sysUserMapperExt.selectByExample(example);
        if(user.size() != 1){
            throw new BizException(RunningResult.NO_USER.code(),"用户不存在或已作废");
        }
        if(RealNameAuthFlag.AUTHORIZED.toString().equals(user.get(0).getUserRealNameAuthFlag().toString())){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "用户不能重复认证");
        }
        SysUser temp = new SysUser();
        temp.setId(userId);
        temp.setUserRealNameAuthFlag(RealNameAuthFlag.valueOf(authFlag));
        sysUserMapperExt.updateByPrimaryKeySelective(temp);
    }

    @Override
    public SysUser getUserByVehicle(String vehicleId,String userId,String orgId) {
        //个人
        if (WzStringUtil.isNotBlank(userId) && WzStringUtil.isBlank(orgId)) {
            BizRefUserVehicleExample bizRefUserVehicleExample = new BizRefUserVehicleExample();
            BizRefUserVehicleExample.Criteria criteria = bizRefUserVehicleExample.createCriteria();
            criteria.andUserIdEqualTo(userId);
            criteria.andVehicleIdEqualTo(vehicleId);
            criteria.andUnbindTimeIsNull();
            if (bizRefUserVehicleMapperExt.countByExample(bizRefUserVehicleExample) == 0) {
                throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(),"无权查看该车信息");
            }
            return sysUserMapperExt.getUserByVehicle(vehicleId);
        }
        //企业
        if (WzStringUtil.isBlank(userId) && WzStringUtil.isNotBlank(orgId)) {
            BizRefOrgVehicleExample bizRefOrgVehicleExample = new BizRefOrgVehicleExample();
            BizRefOrgVehicleExample.Criteria criteria = bizRefOrgVehicleExample.createCriteria();
            criteria.andOrgIdEqualTo(orgId);
            criteria.andVehicleIdEqualTo(vehicleId);
            criteria.andUnbindTimeIsNull();
            if (bizRefOrgVehicleMapperExt.countByExample(bizRefOrgVehicleExample) == 0) {
                throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(),"该车辆不属于以所属的企业");
            }
            return sysUserMapperExt.getUserByVehicle(vehicleId);
        }
        //平台
        if (WzStringUtil.isBlank(userId) && WzStringUtil.isBlank(orgId)) {
            return sysUserMapperExt.getUserByVehicle(vehicleId);
        } else {
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR);
        }
    }

    @Override
    public List<SysUserIcon> listSysUserIcons(){
        if (WzStringUtil.isBlank(uploadUserIconRoot)) {
            throw new BizException(RunningResult.NO_DIRECTORY);
        }
        if (WzStringUtil.isBlank(downloadUserIconPrefix)) {
            throw new BizException(RunningResult.NOT_FOUND);
        }
        File iconDir = new File(uploadUserIconRoot);
        if (iconDir.exists() && iconDir.isDirectory()) {
            File[] iconFiles = iconDir.listFiles();
            List<SysUserIcon> iconLs = new ArrayList<SysUserIcon>();
            SysUserIcon iconVo = null;
            for (int i = 0; i < iconFiles.length; i++) {
                iconVo = new SysUserIcon();
                iconVo.setIconName(iconFiles[i].getName());
                //iconVo.setIconUrl(WzFileUtil.makeRequestUrl(uploadUserIconRoot, "", iconFiles[i].getName()));
                iconLs.add(iconVo);
            }
            if (0 == iconLs.size()) {
                throw new BizException(RunningResult.NOT_FOUND.code(), "未获得ICON文件");
            }
            return iconLs;
        } else {
            throw new BizException(RunningResult.NO_DIRECTORY);
        }
    }

}
