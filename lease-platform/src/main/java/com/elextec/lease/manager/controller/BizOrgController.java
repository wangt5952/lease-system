package com.elextec.lease.manager.controller;

import com.alibaba.fastjson.JSON;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzEncryptUtil;
import com.elextec.framework.utils.WzFileUtil;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.request.BizOrganizationParam;
import com.elextec.lease.manager.service.BizOrganizationService;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.field.enums.RecordStatus;
import com.elextec.persist.model.mybatis.BizOrganization;
import com.elextec.persist.model.mybatis.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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

    @Value("${localsetting.upload-res-icon-root}")
    private String uploadOrgIconRoot;

    @Value("${localsetting.download-res-icon-prefix}")
    private String downloadOrgIconPrefix;

    /**
     * 查询公司组织.
     * @param paramAndPaging 查询及分页参数JSON
     * <pre>
     *     {
     *         keyStr:查询关键字（非必填，模糊查询，可填写组织Code、组织名称、组织介绍、组织地址、联系人、联系电话、营业执照号码）,
     *         orgType:组织类别（非必填，PLATFORM、ENTERPRISE、INDIVIDUAL）,
     *         orgStatus：组织状态（非必填，NORMAL、FREEZE、INVALID），
     *         needPaging:是否需要分页（仅为false时不需要分页，其余情况均需要分页）,
     *         currPage:当前页,
     *         pageSize:每页记录数
     *     }
     * </pre>
     * @param request HttpServletRequest
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
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public MessageResponse list(@RequestBody String paramAndPaging, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(paramAndPaging)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            //PageRequest pagingParam = null;
            BizOrganizationParam pagingParam = null;
            try {
                String paramStr = URLDecoder.decode(paramAndPaging, "utf-8");
                //pagingParam = JSON.parseObject(paramStr, PageRequest.class);
                pagingParam = JSON.parseObject(paramStr,BizOrganizationParam.class);
                if (null == pagingParam) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                SysUser userTemp = getLoginUserInfo(request);
                if(userTemp != null){
                    //只有平台用户可以操作
                    if(!OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                // 仅needPaging标志为false时，不需要分页，其他情况均需要进行分页
                if (WzStringUtil.isNotBlank(pagingParam.getNeedPaging()) && "false".equals(pagingParam.getNeedPaging().toLowerCase())) {
                    pagingParam.setNeedPaging("false");
                } else {
                    if (null == pagingParam.getCurrPage() || null == pagingParam.getPageSize()) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "未获得分页参数");
                    }
                    pagingParam.setNeedPaging("true");
                }
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR.code(), ex.getMessage(), ex);
            }
            //PageResponse<BizOrganization> orgPageResp = bizOrganizationService.list(true, pagingParam);
            PageResponse<BizOrganization> orgPageResp = bizOrganizationService.listByParam(Boolean.valueOf(pagingParam.getNeedPaging()), pagingParam);
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
     *            orgCode:组织Code,
     *            orgName:组织名称,
     *            orgType:组织类别（平台PLATFORM、企业ENTERPRISE、个人INDIVIDUAL）
     *            orgIntroduce:组织介绍,
     *            orgAddress:组织地址,
     *            orgContacts:联系人（多人用 , 分割）,
     *            orgPhone:联系电话（多个电话用 , 分割）,
     *            orgBusinessLicences:营业执照号码,
     *            orgBusinessLicenceFront:营业执照正面照片路径,
     *            orgBusinessLicenceBack:营业执照背面照片路径,
     *            orgStatus:组织状态（正常NORMAL、冻结FREEZE、作废INVALID）,
     *            createUser:创建人
     *            updateUser:更新人
     *         },
     *         ... ...
     *     ]
     * </pre>
     * @param request HttpServletRequest
     * @return 批量新增结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public MessageResponse add(@RequestBody String addParam, HttpServletRequest request) {
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
                if (null == orgInfos || 0 == orgInfos.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                SysUser userTemp = getLoginUserInfo(request);
                if(userTemp != null){
                    //只有平台用户可以操作
                    if(!OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                BizOrganization insOrgVo = null;
                for (int i = 0; i < orgInfos.size(); i++) {
                    insOrgVo = orgInfos.get(i);
                    if (WzStringUtil.isBlank(insOrgVo.getOrgCode())
                            || WzStringUtil.isBlank(insOrgVo.getOrgName())
                            || null == insOrgVo.getOrgType()
                            || null == insOrgVo.getOrgStatus()
                            || WzStringUtil.isBlank(insOrgVo.getCreateUser())
                            || WzStringUtil.isBlank(insOrgVo.getUpdateUser())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "第" + i + "条记录企业参数有误");
                    }
                    if (!insOrgVo.getOrgType().toString().equals(OrgAndUserType.PLATFORM.toString())
                            && !insOrgVo.getOrgType().toString().equals(OrgAndUserType.ENTERPRISE.toString())
                            && !insOrgVo.getOrgType().toString().equals(OrgAndUserType.INDIVIDUAL.toString())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "第" + i + "条记录企业类别无效");
                    }
                    if (!insOrgVo.getOrgStatus().toString().equals(RecordStatus.NORMAL.toString())
                            && !insOrgVo.getOrgStatus().toString().equals(RecordStatus.FREEZE.toString())
                            && !insOrgVo.getOrgStatus().toString().equals(RecordStatus.INVALID.toString())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "第" + i + "条记录企业状态无效");
                    }
                }
                bizOrganizationService.insertBizOrganization(orgInfos);
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 增加公司组织资源.
     * @param addParam 批量新增参数JSON
     * <pre>
     *     {
     *            orgCode:组织Code,
     *            orgName:组织名称,
     *            orgType:组织类别（平台PLATFORM、企业ENTERPRISE、个人INDIVIDUAL）
     *            orgIntroduce:组织介绍,
     *            orgAddress:组织地址,
     *            orgContacts:联系人（多人用 , 分割）,
     *            orgPhone:联系电话（多个电话用 , 分割）,
     *            orgBusinessLicences:营业执照号码,
     *            orgBusinessLicenceFront:营业执照正面照片路径,
     *            orgBusinessLicenceBack:营业执照背面照片路径,
     *            orgStatus:组织状态（正常NORMAL、冻结FREEZE、作废INVALID）,
     *            createUser:创建人
     *            updateUser:更新人
     *      }
     * </pre>
     * @param request HttpServletRequest
     * @return 新增结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(value = "/addone", method = RequestMethod.POST)
    public MessageResponse addone(@RequestBody String addParam, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(addParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            BizOrganization orgInfo = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                orgInfo = JSON.parseObject(paramStr, BizOrganization.class);
                if (null == orgInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                SysUser userTemp = getLoginUserInfo(request);
                if(userTemp != null){
                    //只有平台用户可以操作
                    if(!OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                if (WzStringUtil.isBlank(orgInfo.getOrgCode())
                        || WzStringUtil.isBlank(orgInfo.getOrgName())
                        || null == orgInfo.getOrgType()
                        || null == orgInfo.getOrgStatus()
                        || WzStringUtil.isBlank(orgInfo.getCreateUser())
                        || WzStringUtil.isBlank(orgInfo.getUpdateUser())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "企业参数有误");
                }
                if (!orgInfo.getOrgType().toString().equals(OrgAndUserType.PLATFORM.toString())
                        && !orgInfo.getOrgType().toString().equals(OrgAndUserType.ENTERPRISE.toString())
                        && !orgInfo.getOrgType().toString().equals(OrgAndUserType.INDIVIDUAL.toString())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的企业类别");
                }
                if (!orgInfo.getOrgStatus().toString().equals(RecordStatus.NORMAL.toString())
                        && !orgInfo.getOrgStatus().toString().equals(RecordStatus.FREEZE.toString())
                        && !orgInfo.getOrgStatus().toString().equals(RecordStatus.INVALID.toString())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的企业状态");
                }
                //上传企业营业执照
                if (WzStringUtil.isNotBlank(orgInfo.getOrgBusinessLicenceFront())) {
                    //把唯一时间作为照片名字
                    String imageName = WzUniqueValUtil.makeUniqueTimes();
                    //去掉BASE64里的空格和回车换成加号
                    String orgBusinessLicenceFront = orgInfo.getOrgBusinessLicenceFront().replace(" ","+");
                    /**
                     * 保存文件.
                     * fileBase64Data 文件内容Base64字符串
                     * saveRoot 保存根目录
                     * saveDir 保存相对目录
                     * fileName 文件名（不带扩展名）
                     * ext 扩展名
                     */
                    WzFileUtil.save(orgBusinessLicenceFront, uploadOrgIconRoot, "", imageName, WzFileUtil.EXT_JPG);
                    orgInfo.setOrgBusinessLicenceFront(imageName + WzFileUtil.EXT_JPG);
                }
                bizOrganizationService.insertBizOrganization(orgInfo);
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
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
     *         orgType:组织类别（平台PLATFORM、企业ENTERPRISE、个人INDIVIDUAL），
     *         orgIntroduce:组织介绍,
     *         orgAddress:组织地址,
     *         orgContacts:联系人（多人用 , 分割）,
     *         orgPhone:联系电话（多个电话用 , 分割）,
     *         orgBusinessLicences:营业执照号码,
     *         orgBusinessLicenceFront:营业执照正面照片路径,
     *         orgBusinessLicenceBack:营业执照背面照片路径,
     *         orgStatus:组织状态（正常NORMAL、冻结FREEZE、作废INVALID）,
     *         updateUser:更新人
     *     }
     * </pre>
     * @param request HttpServletRequest
     * @return 修改结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public MessageResponse modify(@RequestBody String modifyParam, HttpServletRequest request) {
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
                if (null == org
                        || WzStringUtil.isBlank(org.getUpdateUser())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                SysUser userTemp = getLoginUserInfo(request);
                if(userTemp != null){
                    //只有平台用户可以操作
                    if(!OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                if (WzStringUtil.isBlank(org.getId())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "无法确定需要修改的数据");
                }
                //企业营业执照如果不为空空
                if (WzStringUtil.isNotBlank(org.getOrgBusinessLicenceFront())){
                    //获取原企业营业执照
                    BizOrganization bizOrganization = bizOrganizationService.getBizOrganizationByPrimaryKey(org.getId());
                    String orgImg = bizOrganization.getOrgBusinessLicenceFront();
                    if (orgImg != null || !orgImg.equals("")) {
                        //删除库里的营业执照
                        if (WzStringUtil.isNotBlank(orgImg)) {
                            WzFileUtil.deleteFile(uploadOrgIconRoot,orgImg);
                        }
                    }
                    //把唯一时间作为照片名字
                    String orgImgTime = WzUniqueValUtil.makeUniqueTimes();
                    //去掉BASE64里的空格和回车换成加号
                    String orgBusinessLicenceFront = org.getOrgBusinessLicenceFront().replace(" ","+");
                    //保存企业营业执照
                    WzFileUtil.save(orgBusinessLicenceFront, uploadOrgIconRoot, "", orgImgTime, WzFileUtil.EXT_JPG);
                    //把时间和图片格式拼一起，库里只保存照片名
                    org.setOrgBusinessLicenceFront(orgImgTime + WzFileUtil.EXT_JPG);
                }
                bizOrganizationService.updateBizOrganization(org);
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
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
     * @param request HttpServletRequest
     * @return 批量删除结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public MessageResponse delete(@RequestBody String deleteParam, HttpServletRequest request) {
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
                SysUser userTemp = getLoginUserInfo(request);
                if(userTemp != null){
                    //只有平台用户可以操作
                    if(!OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                bizOrganizationService.deleteBizOrganization(orgIds);
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
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
     * @param request HttpServletRequest
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
    @RequestMapping(value = "/getbypk", method = RequestMethod.POST)
    public MessageResponse getByPK(@RequestBody String id, HttpServletRequest request) {
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
                SysUser userTemp = getLoginUserInfo(request);
                if(userTemp != null){
                    //只有平台用户可以操作
                    if(!OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
            } catch (BizException ex) {
                throw ex;
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
