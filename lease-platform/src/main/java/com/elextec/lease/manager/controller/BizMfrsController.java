package com.elextec.lease.manager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.service.BizManufacturerService;
import com.elextec.persist.field.enums.MfrsType;
import com.elextec.persist.field.enums.RecordStatus;
import com.elextec.persist.model.mybatis.BizManufacturer;
import com.elextec.persist.model.mybatis.SysResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 制造商管理Controller.
 * Created by wangtao on 2018/1/19.
 */
@RestController
@RequestMapping(path = "/manager/mfrs")
public class BizMfrsController extends BaseController {

    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(BizMfrsController.class);

    @Autowired
    private BizManufacturerService bizManufacturerService;

    /**
     * 根据id查询单个对象
     * @param id 页面传过来的id
     * @return
     */
    @RequestMapping(value = "/getbypk",method = RequestMethod.POST)
    public MessageResponse getByPK(@RequestBody String id){
        try {
            if (id != null) {
                JSONObject jsonObject = JSON.parseObject(id);
                return bizManufacturerService.selectByPrimaryKey(jsonObject.get("id").toString());
            } else {
                throw new BizException(RunningResult.NO_PARAM);
            }
        } catch (Exception e) {
            throw new BizException(RunningResult.PARAM_ANALYZE_ERROR);
        }
    }

    /**
     * 查询制造商.
     * @param paramAndPaging 查询及分页参数JSON
     * @return
     */
    @RequestMapping(value = "/listmfrs",method = RequestMethod.POST)
    public MessageResponse listMfrs(@RequestBody String paramAndPaging) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(paramAndPaging)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            PageRequest pagingParam = null;
            try {
                String paramStr = URLDecoder.decode(paramAndPaging, "utf-8");
                pagingParam = JSON.parseObject(paramStr, PageRequest.class);
                if (null == pagingParam) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            PageResponse<BizManufacturer> resPageResp = bizManufacturerService.paging(true,pagingParam);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, resPageResp);
            return mr;
        }
    }

    /**
     * 批量增加制造商.
     * @param mfrs 制造商信息列表JSON
     * @return
     */
    @RequestMapping(value = "/addmfrs",method = RequestMethod.POST)
    public MessageResponse addUsers(@RequestBody String mfrs) {
        if (WzStringUtil.isBlank(mfrs)) {
            //当参数为空时返回给前台
            return new MessageResponse(RunningResult.NO_PARAM);
        } else {
            List<Map<String,Object>> list = JSON.parseObject(mfrs,new TypeReference<List<Map<String,Object>>>(){});
            List<BizManufacturer> bizManufacturerList = new ArrayList<BizManufacturer>();
            if (list.size() != 0) {
                for (int i = 0; i < list.size(); i++) {
                    BizManufacturer biz = new BizManufacturer();
                    biz.setId(WzUniqueValUtil.makeUUID());
                    biz.setMfrsName(list.get(i).get("mfrsName").toString());
                    biz.setMfrsType(MfrsType.VEHICLE);
                    biz.setMfrsIntroduce(list.get(i).get("mfrsIntroduce").toString());
                    biz.setMfrsAddress(list.get(i).get("mfrsAddress").toString());
                    biz.setMfrsContacts(list.get(i).get("mfrsContacts").toString());
                    biz.setMfrsPhone(list.get(i).get("mfrsPhone").toString());
                    biz.setMfrsStatus(RecordStatus.NORMAL.toString());
                    biz.setCreateUser(list.get(i).get("createUser").toString());
                    biz.setCreateTime(new Date());
                    biz.setUpdateUser(list.get(i).get("updateUser").toString());
                    bizManufacturerList.add(biz);
                }
                return bizManufacturerService.insert(bizManufacturerList);
            } else {
                //前台参数解析错误
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
            }
        }
    }

    /**
     * 修改制造商信息.
     * @param mfrs 制造商信息JSON
     * @return
     */
    @RequestMapping(value = "/modifymfrs",method = RequestMethod.POST)
    public MessageResponse modifyMfrs(@RequestBody String mfrs) {
        try {
            String data = URLDecoder.decode(mfrs, "utf-8");
            if(WzStringUtil.isBlank(mfrs)){
                return new MessageResponse(RunningResult.NO_PARAM);
            } else {
                List<Map<String,Object>> list = JSON.parseObject(data,new TypeReference<List<Map<String,Object>>>(){});
                if (list.size() != 0) {
                    List<BizManufacturer> list1 = new ArrayList<BizManufacturer>();
                    for (int i = 0; i < list.size(); i++) {
                        BizManufacturer biz = new BizManufacturer();
                        biz.setId(list.get(i).get("id").toString());
                        biz.setMfrsName(list.get(i).get("mfrsName").toString());
                        biz.setMfrsType(MfrsType.BATTERY);
                        biz.setMfrsIntroduce(list.get(i).get("mfrsIntroduce").toString());
                        biz.setMfrsAddress(list.get(i).get("mfrsAddress").toString());
                        biz.setMfrsContacts(list.get(i).get("mfrsContacts").toString());
                        biz.setMfrsPhone(list.get(i).get("mfrsPhone").toString());
                        biz.setMfrsStatus(RecordStatus.NORMAL.toString());
                        biz.setCreateUser(list.get(i).get("createUser").toString());
                        biz.setUpdateUser(list.get(i).get("updateUser").toString());
                        list1.add(biz);
                    }
                    return bizManufacturerService.updateByPrimaryKey(list1);
                } else {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new BizException(RunningResult.PARAM_ANALYZE_ERROR);
        }
    }

    /**
     * 批量删除制造商.
     * @param mfrs 待删除的制造商列表JSON
     * @return
     */
    @RequestMapping(value = "/deletemfrs",method = RequestMethod.POST)
    public MessageResponse deleteUsers(@RequestBody String mfrs) {
        if (WzStringUtil.isBlank(mfrs)) {
            return new MessageResponse(RunningResult.NO_PARAM);
        } else {
            List<Map<String,Object>> list = JSON.parseObject(mfrs,new TypeReference<List<Map<String,Object>>>(){});
            if (list.size() != 0) {
                List<String> list1 = new ArrayList<String>();
                for (int i = 0; i < list.size(); i++) {
                    String id = list.get(i).get("id").toString();
                    list1.add(id);
                }
                return bizManufacturerService.deleteByPrimaryKey(list1);
            } else {
                return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
            }
        }
    }
}
