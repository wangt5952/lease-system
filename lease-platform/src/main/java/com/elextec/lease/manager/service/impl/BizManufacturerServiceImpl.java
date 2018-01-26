package com.elextec.lease.manager.service.impl;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.lease.manager.service.BizManufacturerService;
import com.elextec.persist.dao.mybatis.BizManufacturerMapperExt;
import com.elextec.persist.model.mybatis.BizManufacturer;
import com.elextec.persist.model.mybatis.BizManufacturerExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class BizManufacturerServiceImpl implements BizManufacturerService {

    /*日志信息*/
    private final Logger logger = LoggerFactory.getLogger(BizManufacturerServiceImpl.class);

    @Autowired
    private BizManufacturerMapperExt bizManufacturerMapperExt;

    @Override
    @Transactional
    public MessageResponse insert(List<BizManufacturer> list) throws BizException{
        try {
            for (BizManufacturer biz : list) {
                if (bizManufacturerMapperExt.getByName(biz.getMfrsName()) == null) {
                    bizManufacturerMapperExt.insert(biz);
                } else {
                    return new MessageResponse(RunningResult.UNAUTHORIZED,"制造商不能重复");
                }
            }
            return new MessageResponse(RunningResult.SUCCESS);
        } catch (Exception e) {
            throw new BizException(RunningResult.DB_ERROR);
        }
    }

    @Override
    public MessageResponse deleteByPrimaryKey(List<String> list) throws BizException{
        try {
            for (int i = 0; i < list.size(); i++) {
                bizManufacturerMapperExt.deleteByPrimaryKey(list.get(i));
            }
            return new MessageResponse(RunningResult.SUCCESS);
        } catch (Exception e) {
            throw new BizException(RunningResult.DB_ERROR);
        }
    }

    @Override
    public MessageResponse updateByPrimaryKey(List<BizManufacturer> list) throws BizException{
        try {
            for (BizManufacturer biz: list) {
                if ( bizManufacturerMapperExt.getByName(biz.getMfrsName()) == null || bizManufacturerMapperExt.getByName(biz.getMfrsName()).getId().equals(biz.getId())){
                    bizManufacturerMapperExt.updateByPrimaryKey(biz);
                } else {
                    return new MessageResponse(RunningResult.UNAUTHORIZED,"已有该制造商，不能修改相同名称");
                }
            }
            return new MessageResponse(RunningResult.SUCCESS);
        } catch (Exception e) {
            throw new BizException(RunningResult.DB_ERROR);
        }
    }

    @Override
    public PageResponse<BizManufacturer> paging(boolean needPaging, PageRequest pr) {
        // 查询总记录数
        int resTotal = 0;
        if (pr.getTotal() > 0) {
            resTotal = pr.getTotal();
        } else {
            BizManufacturerExample bizManufacturerExample = new BizManufacturerExample();
            bizManufacturerExample.setDistinct(true);
            resTotal = bizManufacturerMapperExt.countByExample(bizManufacturerExample);
        }
        // 分页查询
        BizManufacturerExample bizManufacturerExample = new BizManufacturerExample();
        bizManufacturerExample.setDistinct(true);
        if (needPaging) {
            bizManufacturerExample.setPageBegin(pr.getPageBegin());
            bizManufacturerExample.setPageSize(pr.getPageSize());
        }
        List<BizManufacturer> resLs = bizManufacturerMapperExt.selectByExample(bizManufacturerExample);
        // 组织并返回结果
        PageResponse<BizManufacturer> presp = new PageResponse<BizManufacturer>();
        presp.setCurrPage(pr.getCurrPage());
        presp.setPageSize(pr.getPageSize());
        presp.setTotal(resTotal);
        if (null == resLs) {
            presp.setRows(new ArrayList<BizManufacturer>());
        } else {
            presp.setRows(resLs);
        }
        return presp;
    }

    @Override
    public MessageResponse selectByPrimaryKey(String id) {
        if (id != null) {
            System.err.println(id);
            BizManufacturer biz = bizManufacturerMapperExt.selectByPrimaryKey(id);
            System.err.println(biz);
            return new MessageResponse(RunningResult.SUCCESS,biz);
        } else {
            throw new BizException(RunningResult.NO_PARAM);
        }
    }


}

