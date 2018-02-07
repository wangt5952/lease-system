package com.elextec.lease.manager.controller;

import com.alibaba.fastjson.JSON;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.common.request.RefUserRolesParam;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.manager.request.SysUserParam;
import com.elextec.lease.manager.service.SysRoleService;
import com.elextec.lease.manager.service.SysUserService;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.field.enums.RealNameAuthFlag;
import com.elextec.persist.field.enums.RecordStatus;
import com.elextec.persist.model.mybatis.SysRole;
import com.elextec.persist.model.mybatis.SysUser;
import com.elextec.persist.model.mybatis.SysUserExample;
import com.elextec.persist.model.mybatis.ext.SysUserExt;
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
 * 用户管理Controller.
 * Created by wangtao on 2018/1/16.
 */
@RestController
@RequestMapping(path = "/manager/user")
public class SysUserController extends BaseController {

    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(SysUserController.class);

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 查询用户.
     * @param paramAndPaging 查询及分页参数JSON
     * <pre>
     *     {
     *         keyStr:查询关键字（非必填，模糊查询，可填写登录名、手机号码、昵称、姓名、身份证号、所属企业Code、所属企业名）,
     *         userType:用户类别（非必填，包括PLATFORM、ENTERPRISE、INDIVIDUAL）,
     *         userStatus:用户状态（非必填，包括NORMAL、FREEZE、INVALID）,
     *         needPaging:是否需要分页（仅为false时不需要分页，其余情况均需要分页）,
     *         currPage:当前页（needPaging不为false时必填）,
     *         pageSize:每页记录数（needPaging不为false时必填）
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
     *                 loginName:用户名,
     *                 userMobile:用户手机号码,
     *                 userType:用户类型（平台、企业或个人）,
     *                 userIcon:用户LOGO路径,
     *                 password:密码,
     *                 nickName:昵称,
     *                 userName:姓名,
     *                 userRealNameAuthFlag:用户实名认证标志（已实名、未实名）,
     *                 userPid:身份证号,
     *                 userIcFront:身份证正面照片路径,
     *                 userIcBack:身份证背面照片路径,
     *                 userIcGroup:用户手举身份证合照路径,
     *                 orgId:所属组织ID,
     *                 orgCode:组织单位Code,
     *                 orgName:组织单位名,
     *                 userStatus:用户状态（正常、冻结、作废）,
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
    public MessageResponse list(@RequestBody String paramAndPaging) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(paramAndPaging)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
//            PageRequest pagingParam = null;
            SysUserParam pagingParam = null;
            try {
                String paramStr = URLDecoder.decode(paramAndPaging, "utf-8");
//                pagingParam = JSON.parseObject(paramStr, PageRequest.class);
                pagingParam = JSON.parseObject(paramStr, SysUserParam.class);
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
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
//            PageResponse<SysUserExt> sysUserPageResp = sysUserService.list(Boolean.valueOf(pagingParam.getNeedPaging()), pagingParam);
            PageResponse<SysUserExt> sysUserPageResp = sysUserService.listExtByParam(Boolean.valueOf(pagingParam.getNeedPaging()), pagingParam);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, sysUserPageResp);
            return mr;
        }
    }

    /**
     * 批量增加用户.
     * @param addParam 批量新增参数列表JSON
     * <pre>
     *     [
     *         {
     *             loginName:用户名（必填）,
     *             userMobile:用户手机号码（必填）,
     *             userType:用户类型（PLATFORM-平台、ENTERPRISE-企业、INDIVIDUAL-个人）（必填）,
     *             userIcon:用户LOGO路径（非必填）,
     *             password:密码（非必填）,
     *             nickName:昵称（非必填）,
     *             userName:姓名（非必填）,
     *             userRealNameAuthFlag:用户实名认证标志（AUTHORIZED-已实名、UNAUTHORIZED-未实名）（必填）,
     *             userPid:身份证号（非必填）,
     *             userIcFront:身份证正面照片路径（非必填）,
     *             userIcBack:身份证背面照片路径（非必填）,
     *             userIcGroup:用户手举身份证合照路径（非必填）,
     *             orgId:所属组织ID（非必填）,
     *             userStatus:用户状态（NORMAL-正常、FREEZE-冻结、INVALID-作废）（必填）,
     *             createUser:创建人（必填）,
     *             updateUser:更新人（必填）
     *         }
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
    @RequestMapping(path = "/add")
    public MessageResponse add(@RequestBody String addParam) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(addParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<SysUser> userInfos = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                userInfos = JSON.parseArray(paramStr, SysUser.class);
                if (null == userInfos || 0 == userInfos.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                SysUser insUserChkVo = null;
                for (int i = 0; i < userInfos.size(); i++) {
                    insUserChkVo = userInfos.get(i);
                    if (WzStringUtil.isBlank(insUserChkVo.getLoginName())
                            || WzStringUtil.isBlank(insUserChkVo.getUserMobile())
                            || null == insUserChkVo.getUserType()
                            || WzStringUtil.isBlank(insUserChkVo.getPassword())
                            || null == insUserChkVo.getUserRealNameAuthFlag()
                            || null == insUserChkVo.getUserStatus()
                            || WzStringUtil.isBlank(insUserChkVo.getCreateUser())
                            || WzStringUtil.isBlank(insUserChkVo.getUpdateUser())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "第" + i + "条记录用户信息参数有误");
                    }
                    if (!insUserChkVo.getUserType().toString().equals(OrgAndUserType.PLATFORM.toString())
                            && !insUserChkVo.getUserType().toString().equals(OrgAndUserType.ENTERPRISE.toString())
                            && !insUserChkVo.getUserType().toString().equals(OrgAndUserType.INDIVIDUAL.toString())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "第" + i + "条记录用户类别无效");
                    }
                    if (!insUserChkVo.getUserRealNameAuthFlag().toString().equals(RealNameAuthFlag.AUTHORIZED.toString())
                            && !insUserChkVo.getUserRealNameAuthFlag().toString().equals(RealNameAuthFlag.UNAUTHORIZED.toString())
                            && !insUserChkVo.getUserRealNameAuthFlag().toString().equals(RealNameAuthFlag.TOAUTHORIZED.toString())
                            && !insUserChkVo.getUserRealNameAuthFlag().toString().equals(RealNameAuthFlag.REJECTAUTHORIZED.toString())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "第" + i + "条记录用户实名认证标志无效");
                    }
                    if (!insUserChkVo.getUserStatus().toString().equals(RecordStatus.NORMAL.toString())
                            && !insUserChkVo.getUserStatus().toString().equals(RecordStatus.FREEZE.toString())
                            && !insUserChkVo.getUserStatus().toString().equals(RecordStatus.INVALID.toString())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "第" + i + "条记录用户状态无效");
                    }
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            sysUserService.insertSysUsers(userInfos);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 增加用户.
     * @param addParam 新增参数列表JSON
     * <pre>
     *     {
     *         loginName:用户名（必填）,
     *         userMobile:用户手机号码（必填）,
     *         userType:用户类型（PLATFORM-平台、ENTERPRISE-企业、INDIVIDUAL-个人）（必填）,
     *         userIcon:用户LOGO路径（非必填）,
     *         password:密码（非必填）,
     *         nickName:昵称（非必填）,
     *         userName:姓名（非必填）,
     *         userRealNameAuthFlag:用户实名认证标志（AUTHORIZED-已实名、UNAUTHORIZED-未实名）（必填）,
     *         userPid:身份证号（非必填）,
     *         userIcFront:身份证正面照片路径（非必填）,
     *         userIcBack:身份证背面照片路径（非必填）,
     *         userIcGroup:用户手举身份证合照路径（非必填）,
     *         orgId:所属组织ID（非必填）,
     *         userStatus:用户状态（NORMAL-正常、FREEZE-冻结、INVALID-作废）（必填）,
     *         createUser:创建人（必填）,
     *         updateUuser:更新人（必填）
     *     }
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
    @RequestMapping(path = "/addone")
    public MessageResponse addOne(@RequestBody String addParam) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(addParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            SysUser userInfo = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                userInfo = JSON.parseObject(paramStr, SysUser.class);
                if (null == userInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if (WzStringUtil.isBlank(userInfo.getLoginName())
                        || WzStringUtil.isBlank(userInfo.getUserMobile())
                        || null == userInfo.getUserType()
                        || WzStringUtil.isBlank(userInfo.getPassword())
                        || null == userInfo.getUserRealNameAuthFlag()
                        || null == userInfo.getUserStatus()
                        || WzStringUtil.isBlank(userInfo.getCreateUser())
                        || WzStringUtil.isBlank(userInfo.getUpdateUser())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "用户参数有误");
                }
                if (!userInfo.getUserType().toString().equals(OrgAndUserType.PLATFORM.toString())
                        && !userInfo.getUserType().toString().equals(OrgAndUserType.ENTERPRISE.toString())
                        && !userInfo.getUserType().toString().equals(OrgAndUserType.INDIVIDUAL.toString())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的用户类别");
                }
                if (!userInfo.getUserRealNameAuthFlag().toString().equals(RealNameAuthFlag.AUTHORIZED.toString())
                        && !userInfo.getUserRealNameAuthFlag().toString().equals(RealNameAuthFlag.UNAUTHORIZED.toString())
                        && !userInfo.getUserRealNameAuthFlag().toString().equals(RealNameAuthFlag.TOAUTHORIZED.toString())
                        && !userInfo.getUserRealNameAuthFlag().toString().equals(RealNameAuthFlag.REJECTAUTHORIZED.toString())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的实名认证标志");
                }
                if (!userInfo.getUserStatus().toString().equals(RecordStatus.NORMAL.toString())
                        && !userInfo.getUserStatus().toString().equals(RecordStatus.FREEZE.toString())
                        && !userInfo.getUserStatus().toString().equals(RecordStatus.INVALID.toString())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的用户状态");
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            sysUserService.insertSysUser(userInfo);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 修改用户信息.
     * @param request HttpServletRequest
     * @param modifyParam 修改参数JSON
     * <pre>
     *     {
     *         id:ID（必填）,
     *         loginName:用户名,
     *         userMobile:用户手机号码,
     *         userType:用户类型（PLATFORM-平台、ENTERPRISE-企业、INDIVIDUAL-个人）,
     *         userIcon:用户LOGO路径,
     *         password:密码,
     *         nickName:昵称,
     *         userName:姓名,
     *         userRealNameAuthFlag:用户实名认证标志（AUTHORIZED-已实名、UNAUTHORIZED-未实名）,
     *         userPid:身份证号,
     *         userIcFront:身份证正面照片路径,
     *         userIcBack:身份证背面照片路径,
     *         userIcGoup:用户手举身份证合照路径,
     *         orgId:所属组织ID,
     *         userStatus:用户状态（NORMAL-正常、FREEZE-冻结、INVALID-作废）,
     *         updateUser:更新人
     *     }
     * </pre>
     * @return 修改结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:{
     *             id:ID,
     *             loginName:登录用户名,
     *             userMobile:手机号码,
     *             userType:用户类型（PLATFORM-平台、ENTERPRISE-企业、INDIVIDUAL-个人）,
     *             userIcon:Icon路径,
     *             nickName:昵称,
     *             userName:名称,
     *             userRealNameAuthFlag:是否已实名认证,
     *             userPid:身份证号,
     *             userIcFront:身份证正面照路径,
     *             userIcBack:身份证背面照路径,
     *             userIcGroup:本人于身份证合照路径,
     *             orgId:所属企业ID,
     *             orgCode:企业Code,
     *             orgName:企业名,
     *             userStatus:用户状态（NORMAL-正常、FREEZE-冻结/维保、INVALID-作废）,
     *             createUser:创建人,
     *             createTime:创建时间,
     *             updateUser:更新人,
     *             updateTime:更新时间
     *         }
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
            SysUser userInfo = null;
            try {
                String paramStr = URLDecoder.decode(modifyParam, "utf-8");
                userInfo = JSON.parseObject(paramStr, SysUser.class);
                if (null == userInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if (WzStringUtil.isBlank(userInfo.getId())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "无法确定需要修改的数据");
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            sysUserService.updateSysUser(userInfo);
            // 更新登录信息
            SysUserExample reListParam  = new SysUserExample();
            SysUserExample.Criteria reListCri = reListParam.createCriteria();
            reListCri.andIdEqualTo(userInfo.getId());
            SysUserExt uExt = sysUserService.getExtById(reListParam);
            resetPcLoginUserInfo(request, uExt);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, uExt);
            return mr;
        }
    }

    /**
     * 批量删除用户.
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
    @RequestMapping(path = "/delete")
    public MessageResponse delete(@RequestBody String deleteParam) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(deleteParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> userIds = null;
            try {
                String paramStr = URLDecoder.decode(deleteParam, "utf-8");
                userIds = JSON.parseArray(paramStr, String.class);
                if (null == userIds || 0 == userIds.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            sysUserService.deleteSysUser(userIds);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 给用户分配角色.
     * @param userAndRoles 用户及角色列表JSON
     * <pre>
     *     {
     *         deleteAllFlg:清空标志（仅为true时有效）,
     *         userId:用户ID,
     *         roleIds:角色ID，多个以逗号隔开，例：角色Id1,角色Id2,角色Id3
     *     }
     * </pre>
     * @return 处理结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(path = "/refuserandroles")
    public MessageResponse refUserAndRoles(@RequestBody String userAndRoles) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(userAndRoles)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            RefUserRolesParam ur = null;
            try {
                String paramStr = URLDecoder.decode(userAndRoles, "utf-8");
                ur = JSON.parseObject(paramStr, RefUserRolesParam.class);
                if (null == ur || WzStringUtil.isBlank(ur.getUserId()) || WzStringUtil.isBlank(ur.getRoleIds())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "参数解析失败或未获得被授权用户");
                }
                if (WzStringUtil.isBlank(ur.getDeleteAllFlg()) || !"true".equals(ur.getDeleteAllFlg().toLowerCase())) {
                    ur.setDeleteAllFlg("false");
                    if (WzStringUtil.isBlank(ur.getRoleIds())) {
                        throw new BizException(RunningResult.PARAM_VERIFY_ERROR.code(), "授权角色不能为空");
                    }
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            sysUserService.refSysUserAndRoles(ur);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }


    /**
     * 根据ID获取用户信息.
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
     *             key_user_info:{
     *                 id:ID,
     *                 loginName:用户名,
     *                 userMobile:用户手机号码,
     *                 userType:用户类型（平台、企业或个人）,
     *                 userIcon:用户LOGO路径,
     *                 password:密码,
     *                 nickName:昵称,
     *                 userName:姓名,
     *                 userRealNameAuthFlag:用户实名认证标志（已实名、未实名）,
     *                 userPid:身份证号,
     *                 userIcFront:身份证正面照片路径,
     *                 userIcBack:身份证背面照片路径,
     *                 userIcGroup:用户手举身份证合照路径,
     *                 orgId:所属组织ID,
     *                 userStatus:用户状态（正常、冻结、作废）,
     *                 createUser:创建人,
     *                 createTime:创建时间,
     *                 updateUser:更新人,
     *                 updateTime:更新时间
     *             },
     *             key_role_info:[
     *                 {
     *                     id:ID,
     *                     roleName:角色名,
     *                     roleIntroduce:角色说明,
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
    public MessageResponse getByPK(@RequestBody String id) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(id)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> userId = null;
            try {
                String paramStr = URLDecoder.decode(id, "utf-8");
                userId = JSON.parseArray(paramStr, String.class);
                if (null == userId || 0 == userId.size() || WzStringUtil.isBlank(userId.get(0))) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            SysUser user = sysUserService.getSysUserByPrimaryKey(userId.get(0));
            List<SysRole> userRoles = sysRoleService.listSysRolesByUserId(userId.get(0));
            // 组织返回结果并返回
            Map<String, Object> respMap = new HashMap<String, Object>();
            respMap.put(WzConstants.KEY_USER_INFO, user);
            respMap.put(WzConstants.KEY_ROLE_INFO, userRoles);
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, respMap);
            return mr;
        }
    }
}
