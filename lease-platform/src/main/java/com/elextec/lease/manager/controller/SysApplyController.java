package com.elextec.lease.manager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.common.request.RefUserRolesParam;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzEncryptUtil;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.manager.request.SysApplyParam;
import com.elextec.lease.manager.request.SysUserParam;
import com.elextec.lease.manager.service.SysApplyService;
import com.elextec.lease.manager.service.SysRoleService;
import com.elextec.lease.manager.service.SysUserService;
import com.elextec.persist.field.enums.ApplyTypeAndStatus;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.field.enums.RealNameAuthFlag;
import com.elextec.persist.field.enums.RecordStatus;
import com.elextec.persist.model.mybatis.*;
import com.elextec.persist.model.mybatis.ext.SysApplyExt;
import com.elextec.persist.model.mybatis.ext.SysUserExt;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 申请管理Controller.
 * Created by wangtao on 2018/1/16.
 */
@RestController
@RequestMapping(path = "/manager/apply")
public class SysApplyController extends BaseController {

    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(SysApplyController.class);


    @Autowired
    private SysApplyService sysApplyService;


    /**
     * 查询申请
     * @param paramAndPaging 查询及分页参数JSON
     * <pre>
     *     {
     *         keyStr:查询关键字（非必填，模糊查询，可填写登录名、手机号码、昵称、姓名、身份证号、所属企业Code、所属企业名）,
     *         flag:查询类别(仅在操作用户是企业用户的时候生效，"0"为查询待企业审批的所有申请，"1"为查询企业提交给平台的申请)
     *         applyType:申请类别（非必填，暂时只有车辆申请VEHICLEAPPLY）,
     *         applyStatus:申请状态（非必填，包括TOBEAUDITED、AGREE、REJECT）,
     *         needPaging:是否需要分页（仅为false时不需要分页，其余情况均需要分页）,
     *         currPage:当前页（needPaging不为false时必填）,
     *         pageSize:每页记录数（needPaging不为false时必填）
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
     *                 applyTitle:申请标题,
     *                 applyContent:申请内容,
     *                 applyType:申请类型（暂时只有车辆申请）,
     *                 applyStatus:申请状态（同意、驳回、待审批）,
     *                 applyUserId:申请人ID,
     *                 applyUserName:申请人名称,
     *                 applyOrgId:申请企业ID,
     *                 applyOrgName:申请企业名称,
     *                 examineOrgId:审批企业ID,
     *                 examineUserId:审批人ID,
     *                 examineContent:审批内容,
     *                 createTime:创建时间,
     *                 updateTime:更新时间
     *             },
     *             ... ...
     *         ]
     *     }
     * </pre>
     */
    @RequestMapping(path = "/list")
    public MessageResponse list(@RequestBody String paramAndPaging, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(paramAndPaging)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
//            PageRequest pagingParam = null;
            SysApplyParam pagingParam = null;
            try {
                String paramStr = URLDecoder.decode(paramAndPaging, "utf-8");
//                pagingParam = JSON.parseObject(paramStr, PageRequest.class);
                pagingParam = JSON.parseObject(paramStr, SysApplyParam.class);
                if (null == pagingParam) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                // 仅needPaging标志为false时，不需要分页，其他情况均需要进行分页
                if (WzStringUtil.isNotBlank(pagingParam.getNeedPaging())
                        && "false".equals(pagingParam.getNeedPaging().toLowerCase())) {
                    pagingParam.setNeedPaging("false");
                } else {
                    if (null == pagingParam.getCurrPage() || null == pagingParam.getPageSize()) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "未获得分页参数");
                    }
                    pagingParam.setNeedPaging("true");
                }
                SysUser userTemp = getLoginUserInfo(request);
                if(userTemp != null){
                    //根据用户类型添加条件
                    //平台用户需要添加orgId为条件
                    if(OrgAndUserType.INDIVIDUAL.toString().equals(userTemp.getUserType().toString())){
                        pagingParam.setOrgId(userTemp.getOrgId());
                    }
                    //个人用户需要添加userId为条件
                    if(OrgAndUserType.INDIVIDUAL.toString().equals(userTemp.getUserType().toString())){
                        pagingParam.setUserId(userTemp.getId());
                    }
                    //企业用户需要添加orgId为条件
                    if(OrgAndUserType.ENTERPRISE.toString().equals(userTemp.getUserType().toString())){
                        //企业用户分两种查询，查询所有待审批的申请和查询自己提交的申请
                        if(WzStringUtil.isBlank(pagingParam.getFlag())
                                || !"0".equals(pagingParam.getFlag())
                                || !"1".equals(pagingParam.getFlag())){
                            return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "参数为空或不正确");
                        }
                        if("0".equals(pagingParam.getFlag())){
                            pagingParam.setOrgId(userTemp.getOrgId());
                        }
                        if("1".equals(pagingParam.getFlag())){
                            pagingParam.setUserId(userTemp.getId());
                        }
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR.code(), ex.getMessage(), ex);
            }
//            PageResponse<SysUserExt> sysUserPageResp = sysUserService.list(Boolean.valueOf(pagingParam.getNeedPaging()), pagingParam);
            PageResponse<SysApplyExt> sysApplyPageResp = sysApplyService.listExtByParam(Boolean.valueOf(pagingParam.getNeedPaging()), pagingParam);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, sysApplyPageResp);
            return mr;
        }
    }
    /**
     * 增加申请
     * @param addParam 新增参数列表JSON
     * <pre>
     *     {
     *         applyTitle:申请标题,
     *         applyContent:申请内容,
     *         applyType:申请类型（暂时只有车辆申请,非必填）
     *     }
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
    @RequestMapping(path = "/addone")
    public MessageResponse addOne(@RequestBody String addParam, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(addParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            SysApply sysApplyInfo = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                sysApplyInfo = JSON.parseObject(paramStr, SysApply.class);
                if (null == sysApplyInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if (WzStringUtil.isBlank(sysApplyInfo.getApplyTitle())
                        || WzStringUtil.isBlank(sysApplyInfo.getApplyContent())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "参数不能为空");
                }
                SysUser userTemp = getLoginUserInfo(request);
                if(userTemp != null){
                    //平台用户无权执行该操作
                    if(OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                    //个人用户添加默认信息
                    if(OrgAndUserType.INDIVIDUAL.toString().equals(userTemp.getUserType().toString())){
                        sysApplyInfo.setExamineOrgId(userTemp.getOrgId());
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                //添加默认信息
                sysApplyInfo.setApplyOrgId(userTemp.getOrgId());
                sysApplyInfo.setApplyUserId(userTemp.getId());
                sysApplyInfo.setExamineUserId(null);
                sysApplyInfo.setExamineContent(null);
                sysApplyService.insertSysApply(sysApplyInfo,userTemp.getUserType().toString());
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
     * 修改申请信息.
     * @param request HttpServletRequest
     * @param modifyParam 修改参数JSON
     * <pre>
     *     {
     *         id:ID（必填）,
     *         applyTitle:申请标题,
     *         applyContent:申请内容,
     *         applyType:申请类型（暂时只有车辆申请,非必填）
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
    @RequestMapping(path = "/modify")
    public MessageResponse modify(@RequestBody String modifyParam, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(modifyParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            SysApply sysApplyInfo = null;
            try {
                String paramStr = URLDecoder.decode(modifyParam, "utf-8");
                sysApplyInfo = JSON.parseObject(paramStr, SysApply.class);
                SysUser userTemp = getLoginUserInfo(request);
                if(userTemp != null){
                    //平台用户无权执行该操作
                    if(OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                if (WzStringUtil.isBlank(sysApplyInfo.getId())
                        || WzStringUtil.isBlank(sysApplyInfo.getApplyTitle())
                        || WzStringUtil.isBlank(sysApplyInfo.getApplyContent())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "参数不能为空");
                }
                SysApply temp = new SysApply();
                temp.setId(sysApplyInfo.getId());
                temp.setApplyTitle(sysApplyInfo.getApplyTitle());
                temp.setApplyContent(sysApplyInfo.getApplyContent());
                temp.setApplyType(ApplyTypeAndStatus.VEHICLEAPPLY);
                sysApplyService.updateSysApply(temp,userTemp.getId());
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
     * 根据ID获取申请信息.
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
     *         respData:{
     *             key_user_info:{
     *                 id:ID,
     *                 applyTitle:申请标题,
     *                 applyContent:申请内容,
     *                 applyType:申请类型（暂时只有车辆申请）,
     *                 applyStatus:申请状态（同意、驳回、待审批）,
     *                 applyUserId:申请人ID,
     *                 applyUserName:申请人名称,
     *                 applyOrgId:申请企业ID,
     *                 applyOrgName:申请企业名称,
     *                 examineOrgId:审批企业ID,
     *                 examineUserId:审批人ID,
     *                 examineContent:审批内容,
     *                 createTime:创建时间,
     *                 updateTime:更新时间
     *             }
     *         }
     *     }
     * </pre>
     */
    @RequestMapping(path = "/getbypk")
    public MessageResponse getByPK(@RequestBody String id, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(id)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> applyId = null;
            try {
                String paramStr = URLDecoder.decode(id, "utf-8");
                applyId = JSON.parseArray(paramStr, String.class);
                if (null == applyId || 0 == applyId.size() || WzStringUtil.isBlank(applyId.get(0))) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                SysApplyExample applyExample = new SysApplyExample();
                SysApplyExample.Criteria applyCriteria = applyExample.createCriteria();
                applyCriteria.andIdEqualTo(applyId.get(0));

                SysUser userTemp = getLoginUserInfo(request);
                if(userTemp == null){
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                if(OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())){
                    applyCriteria.andExamineOrgIdEqualTo(userTemp.getOrgId());
                }
                if(OrgAndUserType.INDIVIDUAL.toString().equals(userTemp.getUserType().toString())){
                    applyCriteria.andApplyUserIdEqualTo(userTemp.getId());
                }
                if(OrgAndUserType.ENTERPRISE.toString().equals(userTemp.getUserType().toString())) {
                    applyCriteria.andApplyUserIdEqualTo(userTemp.getId());
                    SysApplyExample.Criteria applyCriteria1 = applyExample.or();
                    applyCriteria1.andIdEqualTo(applyId.get(0));
                    applyCriteria1.andExamineOrgIdEqualTo(userTemp.getOrgId());
                }
                SysApply apply = sysApplyService.getExtById(applyExample);
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }

            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 批量删除申请.
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
    @RequestMapping(path = "/delete")
    public MessageResponse delete(@RequestBody String deleteParam, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(deleteParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> applyIds = null;
            try {
                String paramStr = URLDecoder.decode(deleteParam, "utf-8");
                applyIds = JSON.parseArray(paramStr, String.class);
                SysUser userTemp = getLoginUserInfo(request);
                if(userTemp == null){
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                if (null == applyIds || 0 == applyIds.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                sysApplyService.deleteSysApply(applyIds,userTemp.getId());
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
     * 申请审批接口.
     * @param approvalParam 申请ID与申批状态
     * <pre>
     *     {
     *         applyId:申请ID,
     *         flag:申批状态（同意、驳回）
     *     }
     * </pre>
     * @param request HttpServletRequest
     * @return 审批结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(path = "/applyapproval")
    public MessageResponse approval(@RequestBody String approvalParam, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(approvalParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            Map<String,String> param = null;
            try {
                String paramStr = URLDecoder.decode(approvalParam, "utf-8");
                param = JSON.parseObject(paramStr, Map.class);
                if (null == param || 0 == param.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                SysUser userTemp = getLoginUserInfo(request);
                if(userTemp != null){
                    //个人用户无权执行该操作
                    if(OrgAndUserType.INDIVIDUAL.toString().equals(userTemp.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                if (WzStringUtil.isBlank(param.get("applyId")) || WzStringUtil.isBlank(param.get("flag"))) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if(!ApplyTypeAndStatus.AGREE.toString().equals(param.get("flag"))
                        || !ApplyTypeAndStatus.REJECT.toString().equals(param.get("flag"))){
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                sysApplyService.approval(param.get("applyId"),param.get("flag"),userTemp.getOrgId());
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

}
