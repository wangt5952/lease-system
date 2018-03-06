package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.request.RefUserRolesParam;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.request.SysUserParam;
import com.elextec.lease.manager.service.SysUserService;
import com.elextec.lease.model.BizVehicleBatteryParts;
import com.elextec.persist.dao.mybatis.*;
import com.elextec.persist.field.enums.RecordStatus;
import com.elextec.persist.model.mybatis.*;
import com.elextec.persist.model.mybatis.ext.BizBatteryExt;
import com.elextec.persist.model.mybatis.ext.BizPartsExt;
import com.elextec.persist.model.mybatis.ext.SysUserExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

/**
 * 资源管理Service实现类.
 * Created by wangtao on 2018/1/16.
 */
@Service
public class SysUserServcieImpl implements SysUserService {

    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(SysUserServcieImpl.class);

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
        }
        List<SysUserExt> userLs = sysUserMapperExt.selectExtByParam(pr);
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
    @Transactional
    public void insertSysUsers(List<SysUser> usersInfos) {
        int i = 0;
        SysUser insertVo = null;
        try {
            for (; i < usersInfos.size(); i++) {
                insertVo = usersInfos.get(i);
                insertVo.setId(WzUniqueValUtil.makeUUID());
                insertVo.setCreateTime(new Date());
                sysUserMapperExt.insertSelective(insertVo);
            }
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
        if(RecordStatus.INVALID.toString().equals(userInfo.getUserStatus())){
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
            sysUserMapperExt.updateByPrimaryKeySelective(userInfo);
        }
    }

    @Override
    @Transactional
    public void deleteSysUser(List<String> ids) {
        int i = 0;
        try {
            BizRefUserVehicleExample refExample = new BizRefUserVehicleExample();
            BizRefUserVehicleExample.Criteria selectRefCriteria = refExample.createCriteria();
            selectRefCriteria.andBindTimeIsNull();
            SysRefUserRoleExample delExample = new SysRefUserRoleExample();
            SysRefUserRoleExample.Criteria delCriteria = delExample.createCriteria();
            for (; i < ids.size(); i++) {
                //验证用户名下是否有未归还的车辆
                selectRefCriteria.andUserIdEqualTo(ids.get(i));
                int count = bizRefUserVehicleMapperExt.countByExample(refExample);
                if(count >= 1){
                    throw new BizException(RunningResult.HAVE_BIND.code(), "第" + i + "条记录删除时发生错误,用户名下有未归还的车辆");
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
        selectUserCriteria.andUserStatusEqualTo(RecordStatus.NORMAL);
        if(null != orgId){
            selectUserCriteria.andOrgIdEqualTo(orgId);
        }
        List<SysUserExt> user = sysUserMapperExt.selectExtByExample(userExample);
        if(user.size() < 1){
            throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "用户不存在或已冻结、作废或用户不在企业名下");
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
            throw new BizException(RunningResult.BAD_REQUEST.code(), "车辆已被绑定");
        }

        BizRefUserVehicle param = new BizRefUserVehicle();
        param.setUserId(userId);
        param.setVehicleId(vehicleId);
        param.setBindTime(new Date());
        bizRefUserVehicleMapperExt.insert(param);

    }

}
