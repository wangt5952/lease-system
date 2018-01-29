package com.elextec.lease.manager.controller;

import com.alibaba.fastjson.JSON;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.manager.service.BizOrganizationService;
import com.elextec.persist.model.mybatis.BizOrganization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.List;

/**
 * 企业单位管理Controller.
 * Created by wangtao on 2018/1/19.
 */
@RestController
@RequestMapping(path = "/manager/org")
public class BizOrgController extends BaseController {

    /*日志*/
    private final Logger logger = LoggerFactory.getLogger(BizOrgController.class);

    @Autowired
    private BizOrganizationService bizOrganizationService;

    /**
     * 查询公司组织.
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
     *                 orgCode:组织Code,
     *                 orgName:组织名称,
     *                 orgType:组织类别（平台、企业）,
     *                 orgIntroduce:组织介绍,
     *                 orgAddress:组织地址,
     *                 orgContacts:联系人（多人用 , 分割）,
     *                 orgPhone:联系电话（多个电话用 , 分割）,
     *                 orgBusinessLicences:营业执照号码,
     *                 orgBusinessLicenceFront:营业执照正面照片路径,
     *                 orgBusinessLicenceBack:营业执照背面照片路径,
     *                 orgStatus:组织状态（正常、冻结、作废）,
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
            PageResponse<BizOrganization> orgPageResp = bizOrganizationService.list(true, pagingParam);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, orgPageResp);
            return mr;
        }
    }

    /**
     * 批量增加公司组织.
     * @param addParam 批量新增参数列表JSON
     * <pre>
     *     [
     *         {
     *            org_code:组织Code,
     *            org_name:组织名称,
     *            orgIntroduce:组织介绍,
     *            orgAddress:组织地址,
     *            orgContacts:联系人（多人用 , 分割）,
     *            orgPhone:联系电话（多个电话用 , 分割）,
     *            orgBusinessLicences:营业执照号码,
     *            orgBusinessLicenceFront:营业执照正面照片路径,
     *            orgBusinessLicenceBack:营业执照背面照片路径,
     *            orgStatus:组织状态（正常、冻结、作废）,
     *            createUser:创建人
     *            updateUser:更新人
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
            List<BizOrganization> orgInfos = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                orgInfos = JSON.parseArray(paramStr, BizOrganization.class);
                if (null == orgInfos || orgInfos.size() == 0) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizOrganizationService.insertBizOrganization(orgInfos);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 修改公司组织信息.
     * @param modifyParam 修改参数JSON
     * <pre>
     *     {
     *         id:ID,
     *         orgName:组织名称,
     *         orgType:组织类别（平台、企业）,
     *         orgIntroduce:组织介绍,
     *         orgAddress:组织地址,
     *         orgContacts:联系人（多人用 , 分割）,
     *         orgPhone:联系电话（多个电话用 , 分割）,
     *         orgBusinessLicences:营业执照号码,
     *         orgBusinessLicenceFront:营业执照正面照片路径,
     *         orgBusinessLicenceBack:营业执照背面照片路径,
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
            // 参数解析错误报“参数解析错误
            BizOrganization org = null;
            try {
                String paramStr = URLDecoder.decode(modifyParam, "utf-8");
                org = JSON.parseObject(paramStr, BizOrganization.class);
                if (null == org) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizOrganizationService.updateBizOrganization(org);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 增加公司组织资源.
     * @param addParam 批量新增参数JSON
     * <pre>
     *
     *         {
     *            orgCode:组织Code,
     *            orgName:组织名称,
     *            orgIntroduce:组织介绍,
     *            orgAddress:组织地址,
     *            orgContacts:联系人（多人用 , 分割）,
     *            orgPhone:联系电话（多个电话用 , 分割）,
     *            orgBusinessLicences:营业执照号码,
     *            orgBusinessLicenceFront:营业执照正面照片路径,
     *            orgBusinessLicenceBack:营业执照背面照片路径,
     *            orgStatus:组织状态（正常、冻结、作废）,
     *            createUser:创建人
     *            updateUser:更新人
     *         }
     *
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
            BizOrganization org = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                org = JSON.parseObject(paramStr, BizOrganization.class);
                if (null == org) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizOrganizationService.insertBizOrganization(org);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 批量删除公司组织.
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
            List<String> orgIds = null;
            try {
                String paramStr = URLDecoder.decode(deleteParam, "utf-8");
                orgIds = JSON.parseArray(paramStr, String.class);
                if (null == orgIds || 0 == orgIds.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizOrganizationService.deleteBizOrganization(orgIds);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 根据id查询公司组织信息.
     * @param id 查询ID
     * <pre>
     *     [id]
     * </pre>
     * @return 查询结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:
     *         {
     *                 id:ID,
     *                 orgCode:组织Code,
     *                 orgName:组织名称,
     *                 orgType:组织类别（平台、企业）,
     *                 orgIntroduce:组织介绍,
     *                 orgAddress:组织地址,
     *                 orgContacts:联系人（多人用 , 分割）,
     *                 orgPhone:联系电话（多个电话用 , 分割）,
     *                 orgBusinessLicences:营业执照号码,
     *                 orgBusinessLicenceFront:营业执照正面照片路径,
     *                 orgBusinessLicenceBack:营业执照背面照片路径,
     *                 orgStatus:组织状态（正常、冻结、作废）,
     *                 createUser:创建人,
     *                 createTime:创建时间,
     *                 updateUser:更新人,
     *                 updateTime:更新时间
     *             }
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
            List<String> orgId = null;
            try {
                String paramStr = URLDecoder.decode(id, "utf-8");
                orgId = JSON.parseArray(paramStr, String.class);
                if (null == orgId || 0 == orgId.size() || WzStringUtil.isBlank(orgId.get(0))) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }

            BizOrganization mfrs = bizOrganizationService.getBizOrganizationByPrimaryKey(orgId.get(0));
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, mfrs);
            return mr;
        }
    }

}
