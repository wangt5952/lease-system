package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.lease.manager.service.BizDeviceConfService;
import com.elextec.persist.dao.mybatis.BizDeviceConfMapperExt;
import com.elextec.persist.model.mybatis.BizDeviceConf;
import com.elextec.persist.model.mybatis.BizDeviceConfExample;
import com.elextec.persist.model.mybatis.BizDeviceConfKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备参数设置管理Service实现类.
 * Created by wangtao on 2018/1/16.
 */
@Service
public class BizDeviceConfServiceImpl implements BizDeviceConfService {
    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(BizDeviceConfServiceImpl.class);

    @Autowired
    private BizDeviceConfMapperExt bizDeviceConfMapperExt;

    @Override
    public PageResponse<BizDeviceConf> list(boolean needPaging, PageRequest pr) {
        // 查询总记录数
        int devTotal = 0;
        if (0 < pr.getTotal()) {
            devTotal = pr.getTotal();
        } else {
            BizDeviceConfExample devCountExample = new BizDeviceConfExample();
            devCountExample.setDistinct(true);
            devTotal = bizDeviceConfMapperExt.countByExample(devCountExample);
        }
        // 分页查询
        BizDeviceConfExample devLsExample = new BizDeviceConfExample();
        devLsExample.setDistinct(true);
        if (needPaging) {
            devLsExample.setPageBegin(pr.getPageBegin());
            devLsExample.setPageSize(pr.getPageSize());
        }
        List<BizDeviceConf> devLs = bizDeviceConfMapperExt.selectByExample(devLsExample);
        // 组织并返回结果
        PageResponse<BizDeviceConf> presp = new PageResponse<BizDeviceConf>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(devTotal);
        if (null == devLs) {
            presp.setRows(new ArrayList<BizDeviceConf>());
        } else {
            presp.setRows(devLs);
        }
        return presp;
    }

    @Override
    public void insertBizDeviceConfs(List<BizDeviceConf> deviceConfs) {
        int i = 0;
        BizDeviceConf insertVo = null;
        try {
            for (; i < deviceConfs.size(); i++) {
                insertVo = deviceConfs.get(i);
                bizDeviceConfMapperExt.insertSelective(insertVo);
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录插入时发生错误", ex);
        }
    }

    @Override
    public void insertBizDeviceConf(BizDeviceConf deviceConf) {
        // 设备重复提示错误
        BizDeviceConfExample devExample = new BizDeviceConfExample();
        BizDeviceConfExample.Criteria devCriteria = devExample.createCriteria();
        devCriteria.andDeviceIdEqualTo(deviceConf.getDeviceId());
        devCriteria.andDeviceTypeEqualTo(deviceConf.getDeviceType());
        int devCnt = bizDeviceConfMapperExt.countByExample(devExample);
        if (0 < devCnt) {
            throw new BizException(RunningResult.MULTIPLE_RECORD.code(), "该设备已存在");
        }
        // 保存用户信息
        try {
            bizDeviceConfMapperExt.insertSelective(deviceConf);
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "记录插入时发生错误", ex);
        }
    }

    @Override
    public void updateBizDeviceConf(BizDeviceConf deviceConf) {
        bizDeviceConfMapperExt.updateByPrimaryKeySelective(deviceConf);
    }

    @Override
    public void deleteBizDeviceConfs(List<BizDeviceConfKey> deviceConfKeys) {
        int i = 0;
        try {
            for (; i < deviceConfKeys.size(); i++) {
                bizDeviceConfMapperExt.deleteByPrimaryKey(deviceConfKeys.get(i));
            }
        } catch (Exception ex) {
            throw new BizException(RunningResult.DB_ERROR.code(), "第" + i + "条记录删除时发生错误", ex);
        }
    }

    @Override
    public BizDeviceConf getBizDeviceConfByPrimaryKey(BizDeviceConfKey key) {
        return bizDeviceConfMapperExt.selectByPrimaryKey(key);
    }
}
