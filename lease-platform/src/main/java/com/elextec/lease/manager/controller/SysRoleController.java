package com.elextec.lease.manager.controller;


import com.alibaba.fastjson.JSON;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.common.request.RefRoleResourceParam;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.manager.request.SysRoleParam;
import com.elextec.lease.manager.service.SysResourceService;
import com.elextec.lease.manager.service.SysRoleService;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.model.mybatis.SysResources;
import com.elextec.persist.model.mybatis.SysRole;
import com.elextec.persist.model.mybatis.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理Controller.
 * Created by wangtao on 2018/1/16.
 */
@RestController
@RequestMapping(path = "/manager/role")
public class SysRoleController extends BaseController {
    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(SysRoleController.class);

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysResourceService sysResourceService;

    /**
     * 查询角色.
     * @param paramAndPaging 查询及分页参数JSON
     * <pre>
     *     {
     *         keyStr:查询关键字（非必填，模糊查询，可填写角色名）,
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
     *                 roleName:角色名,
     *                 roleIntroduce:角色说明,
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
    @RequestMapping(path = "/list")
    public MessageResponse list(@RequestBody String paramAndPaging, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(paramAndPaging)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
//            PageRequest pagingParam = null;
            SysRoleParam pagingParam = null;
            try {
                String paramStr = URLDecoder.decode(paramAndPaging, "utf-8");
//                pagingParam = JSON.parseObject(paramStr, PageRequest.class);
                pagingParam = JSON.parseObject(paramStr, SysRoleParam.class);
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
                if (WzStringUtil.isNotBlank(pagingParam.getNeedPaging())
                        && "false".equals(pagingParam.getNeedPaging().toLowerCase())) {
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
            PageResponse<SysRole> rolePageResp = sysRoleService.listByParam(Boolean.valueOf(pagingParam.getNeedPaging()), pagingParam);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, rolePageResp);
            return mr;
        }
    }

    /**
     * 批量增加角色.
     * @param addParam 批量新增参数列表JSON
     * <pre>
     *     [
     *         {
     *             roleName:角色名,
     *             roleIntroduce:角色说明,
     *             createUser:创建人,
     *             updateUser:更新人
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
     *         respData:
     *     }
     * </pre>
     */
    @RequestMapping(path = "/add")
    public MessageResponse add(@RequestBody String addParam, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(addParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<SysRole> roleInfos = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                roleInfos = JSON.parseArray(paramStr, SysRole.class);
                if (null == roleInfos || 0 == roleInfos.size()) {
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
                SysRole insRoleChkVo = null;
                for (int i = 0; i < roleInfos.size(); i++) {
                    insRoleChkVo = roleInfos.get(i);
                    if (WzStringUtil.isBlank(insRoleChkVo.getRoleName())
                            || WzStringUtil.isBlank(insRoleChkVo.getCreateUser())
                            || WzStringUtil.isBlank(insRoleChkVo.getUpdateUser())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "第" + i + "条记录角色参数有误");
                    }
                }
                sysRoleService.insertSysRoles(roleInfos);
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
     * 增加角色.
     * @param addParam 批量新增参数列表JSON
     * <pre>
     *
     *         {
     *             roleName:角色名,
     *             roleIntroduce:角色说明,
     *             createUser:创建人,
     *             updateUser:更新人
     *         }
     *
     * </pre>
     * @param request HttpServletRequest
     * @return 新增结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:
     *     }
     * </pre>
     */
    @RequestMapping(path = "/addone")
    public MessageResponse addone(@RequestBody String addParam, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(addParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            SysRole roleInfo = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                roleInfo = JSON.parseObject(paramStr, SysRole.class);
                if (null == roleInfo) {
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
                if (WzStringUtil.isBlank(roleInfo.getRoleName())
                        || WzStringUtil.isBlank(roleInfo.getCreateUser())
                        || WzStringUtil.isBlank(roleInfo.getUpdateUser())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "角色参数有误");
                }
                sysRoleService.insertSysRole(roleInfo);
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
     * 修改角色信息.
     * @param modifyParam 修改参数JSON
     * <pre>
     *     {
     *         id:ID,
     *         roleName:角色名,
     *         roleIntroduce:角色说明,
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
    @RequestMapping(path = "/modify")
    public MessageResponse modify(@RequestBody String modifyParam, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(modifyParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            SysRole roleInfo = null;
            try {
                String paramStr = URLDecoder.decode(modifyParam, "utf-8");
                roleInfo = JSON.parseObject(paramStr, SysRole.class);
                if (null == roleInfo
                        || WzStringUtil.isBlank(roleInfo.getUpdateUser())) {
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
                if (WzStringUtil.isBlank(roleInfo.getId())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无法确定待修改的记录");
                }
                sysRoleService.updateSysRole(roleInfo);
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
     * 批量删除角色.
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
            List<String> roleIds = null;
            try {
                String paramStr = URLDecoder.decode(deleteParam, "utf-8");
                roleIds = JSON.parseArray(paramStr, String.class);
                if (null == roleIds) {
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
                sysRoleService.deleteSysRole(roleIds);
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
     * 给角色分配资源.
     * @param roleAndResources 角色及资源列表JSON
     * <pre>
     *     {
     *         deleteAllFlg:清空标志（仅为true时有效）,
     *         roleId:角色ID,
     *         resourceIds:资源ID，多个以逗号隔开，例：资源Id1,资源Id2,资源Id3
     *     }
     * </pre>
     * @param request HttpServletRequest
     * @return 处理结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(path = "/refroleandresources")
    public MessageResponse refRoleAndResources(@RequestBody String roleAndResources, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(roleAndResources)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            RefRoleResourceParam rr = null;
            try {
                String paramStr = URLDecoder.decode(roleAndResources, "utf-8");
                rr = JSON.parseObject(paramStr, RefRoleResourceParam.class);
                if (null == rr || WzStringUtil.isBlank(rr.getRoleId())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "参数解析失败或未获得被授权角色");
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
                if ((WzStringUtil.isBlank(rr.getDeleteAllFlg()) || !"true".equals(rr.getDeleteAllFlg().toLowerCase()))) {
                    rr.setDeleteAllFlg("false");
                    if (WzStringUtil.isBlank(rr.getResourceIds())) {
                        throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "授权资源不能为空");
                    }
                }
                sysRoleService.refSysRoleAndResource(rr);
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
     * 根据ID获取角色信息.
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
     *             key_role_info:{
     *                 id:ID,
     *                 roleName:角色名,
     *                 roleIntroduce:角色说明,
     *                 createUser:创建人,
     *                 createTime:创建时间,
     *                 updateUser:更新人,
     *                 updateTime:更新时间
     *             },
     *             key_res_info:[
     *                 {
     *                     id:ID,
     *                     resCode:资源编码,
     *                     resName:资源名,
     *                     resType:资源类型（目录、菜单、页面、功能或按钮）,
     *                     resUrl:资源请求URL,
     *                     resSort:组内排序,
     *                     groupSort:分组排序,
     *                     showFlag:显示标志（显示、不显示）,
     *                     parent:上级资源（Root为空）,
     *                     level:级别,
     *                     createUser:创建人,
     *                     createTime:创建时间,
     *                     updateUser:更新人,
     *                     updateTime:更新时间
     *                 },
     *                 ......
     *             ]
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
            List<String> roleId = null;
            try {
                String paramStr = URLDecoder.decode(id, "utf-8");
                roleId = JSON.parseArray(paramStr, String.class);
                if (null == roleId || 0 == roleId.size() || WzStringUtil.isBlank(roleId.get(0))) {
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
            SysRole role = sysRoleService.getSysRoleByPrimaryKey(roleId.get(0));
            List<SysResources> roleResources = sysResourceService.listSysResourcesByRoleId(roleId.get(0));
            Map<String, Object> respMap = new HashMap<String, Object>();
            respMap.put(WzConstants.KEY_ROLE_INFO, role);
            respMap.put(WzConstants.KEY_RES_INFO, roleResources);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, respMap);
            return mr;
        }
    }
}
