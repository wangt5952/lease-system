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
import com.elextec.persist.model.mybatis.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
     * 查询制造商.
     * @param paramAndPaging 查询及分页参数JSON
     * <pre>
     *     {
     *         currPage:当前页,
     *         pageSize:每页记录数
     *     }
     * </pre>
     * @return 查询结果列表
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:[
     *             {
     *                 id:ID,
     *                 mfrsName:制造商名称,
     *                 mfrsType:制造商类型（车辆、电池、配件）,
     *                 mfrsIntroduce:制造商介绍,
     *                 mfrsAddress:制造商地址,
     *                 mfrsContacts:联系人（多人用 , 分割）,
     *                 mfrsPhone:联系电话（多个电话用 , 分割）,
     *                 mfrsStatus:制造商状态（正常、冻结、作废）,
     *                 createUser:创建人,
     *                 createTime:创建时间,
     *                 updateUser:更新人,
     *                 updateTime:更新时间
     *             },
     *             ... ...
     *         ]
     *     }
     * </pre>
     */
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public MessageResponse list(@RequestBody String paramAndPaging) {
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
            PageResponse<BizManufacturer> mfrsPageResp = bizManufacturerService.list(true, pagingParam);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, mfrsPageResp);
            return mr;
        }
    }

    /**
     * 批量增加制造商.
     * @param addParam 批量新增参数列表JSON
     * <pre>
     *     [
     *         {
     *             mfrsName:制造商名称,
     *             mfrsIntroduce:制造商介绍,
     *             mfrsAddress:制造商地址,
     *             mfrsContacts:联系人（多人用 , 分割）,
     *             mfrsPhone:联系电话（多个电话用 , 分割）,
     *             createUser:创建人,
     *             updateUser:更新人
     *         },
     *         ... ...
     *     ]
     * </pre>
     * @return 批量新增结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public MessageResponse add(@RequestBody String addParam) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(addParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<BizManufacturer> mfrsInfos = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                mfrsInfos = JSON.parseArray(paramStr, BizManufacturer.class);
                if (null == mfrsInfos || 0 == mfrsInfos.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizManufacturerService.insertBizManufacturers(mfrsInfos);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 增加制造商信息.
     * @param addParam 批量新增参数JSON
     * <pre>
     *  {
     *             mfrsName:制造商名称,
     *             mfrsIntroduce:制造商介绍,
     *             mfrsAddress:制造商地址,
     *             mfrsContacts:联系人（多人用 , 分割）,
     *             mfrsPhone:联系电话（多个电话用 , 分割）,
     *             createUser:创建人,
     *             updateUser:更新人
     *  }
     * </pre>
     * @return 新增结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(value = "/addone",method = RequestMethod.POST)
    public MessageResponse addone(@RequestBody String addParam) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(addParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            BizManufacturer mfrs = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                mfrs = JSON.parseObject(paramStr, BizManufacturer.class);
                if (null == mfrs) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizManufacturerService.insertBizManufacturers(mfrs);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 修改制造商信息.
     * @param modifyParam 修改参数JSON
     * <pre>
     *     {
     *         id:ID,
     *         mfrsName:制造商名称,
     *         mfrsIntroduce:制造商介绍,
     *         mfrsAddress:制造商地址,
     *         mfrsContacts:联系人（多人用 , 分割）,
     *         mfrsPhone:联系电话（多个电话用 , 分割）,
     *         updateUser:更新人
     *     }
     * </pre>
     * @return 修改结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(value = "/modify",method = RequestMethod.POST)
    public MessageResponse modify(@RequestBody String modifyParam) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(modifyParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            BizManufacturer mfrs = null;
            try {
                String paramStr = URLDecoder.decode(modifyParam, "utf-8");
                mfrs = JSON.parseObject(paramStr, BizManufacturer.class);
                if (null == mfrs) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizManufacturerService.updateBizManufacturer(mfrs);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 批量删除制造商.
     * @param deleteParam 删除ID列表JSON
     * <pre>
     *     [ID1,ID2,......]
     * </pre>
     * @return 批量删除结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public MessageResponse delete(@RequestBody String deleteParam) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(deleteParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> mfrsIds = null;
            try {
                String paramStr = URLDecoder.decode(deleteParam, "utf-8");
                mfrsIds = JSON.parseArray(paramStr, String.class);
                if (null == mfrsIds || 0 == mfrsIds.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizManufacturerService.deleteBizManufacturers(mfrsIds);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 根据id查询制造商信息.
     * @param id 查询ID
     * <pre>
     *     [id]
     * </pre>
     * @return 查询结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:{
     *             id:ID,
     *             mfrsName:制造商名称,
     *             mfrsType:制造商类型（车辆、电池、配件）,
     *             mfrsIntroduce:制造商介绍,
     *             mfrsAddress:制造商地址,
     *             mfrsContacts:联系人（多人用 , 分割）,
     *             mfrsPhone:联系电话（多个电话用 , 分割）,
     *             mfrsStatus:制造商状态（正常、冻结、作废）,
     *             createUser:创建人,
     *             createTime:创建时间,
     *             updateUser:更新人,
     *             updateTime:更新时间
     *         }
     *     }
     * </pre>
     */
    @RequestMapping(value = "/getbypk",method = RequestMethod.POST)
    public MessageResponse getByPK(@RequestBody String id) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(id)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> mfrsId = null;
            try {
                String paramStr = URLDecoder.decode(id, "utf-8");
                mfrsId = JSON.parseArray(paramStr, String.class);
                if (null == mfrsId || 0 == mfrsId.size() || WzStringUtil.isBlank(mfrsId.get(0))) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }

            BizManufacturer mfrs = bizManufacturerService.getBizManufacturerByPrimaryKey(mfrsId.get(0));
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, mfrs);
            return mr;
        }
    }
}
