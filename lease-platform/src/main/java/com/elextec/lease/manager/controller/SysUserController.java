package com.elextec.lease.manager.controller;


import com.alibaba.fastjson.JSON;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.request.RefUserRolesParam;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.manager.service.SysUserService;
import com.elextec.persist.model.mybatis.SysResources;
import com.elextec.persist.model.mybatis.SysUser;
import com.elextec.persist.model.mybatis.SysUserExample;
import org.hibernate.jpa.event.internal.jpa.CallbackRegistryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.List;

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

    /**
     * 查询用户.
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
     *                 login_name:用户名,
     *                 user_mobile:用户手机号码,
     *                 user_type:用户类型（平台、企业或个人）,
     *                 user_icon:用户LOGO路径,
     *                 password:密码,
     *                 nick_name:昵称,
     *                 user_name:姓名,
     *                 user_real_name_auth_flag:用户实名认证标志（已实名、未实名）,
     *                 user_pid:身份证号,
     *                 user_ic_front:身份证正面照片路径,
     *                 user_ic_back:身份证背面照片路径,
     *                 user_ic_group:用户手举身份证合照路径,
     *                 org_id:所属组织ID,
     *                 user_status:用户状态（正常、冻结、作废）,
     *                 create_user:创建人,
     *                 create_time:创建时间,
     *                 update_user:更新人,
     *                 update_time:更新时间
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
            PageResponse<SysUser> resPageResp = sysUserService.list(true, pagingParam);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, resPageResp);
            return mr;
        }
    }

    /**
     * 批量增加用户.
     * @param addParam 批量新增参数列表JSON
     * <pre>
     *     [
     *         {
     *             login_name:用户名（必填）,
     *             user_mobile:用户手机号码（必填）,
     *             user_type:用户类型（PLATFORM-平台、ENTERPRISE-企业、INDIVIDUAL-个人）（必填）,
     *             user_icon:用户LOGO路径（非必填）,
     *             password:密码（非必填）,
     *             nick_name:昵称（非必填）,
     *             user_name:姓名（非必填）,
     *             user_real_name_auth_flag:用户实名认证标志（AUTHORIZED-已实名、UNAUTHORIZED-未实名）（必填）,
     *             user_pid:身份证号（非必填）,
     *             user_ic_front:身份证正面照片路径（非必填）,
     *             user_ic_back:身份证背面照片路径（非必填）,
     *             user_ic_group:用户手举身份证合照路径（非必填）,
     *             org_id:所属组织ID（非必填）,
     *             user_status:用户状态（NORMAL-正常、FREEZE-冻结、INVALID-作废）（必填）,
     *             create_user:创建人（必填）,
     *             update_user:更新人（必填）
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
    @RequestMapping(path = "/batchadd")
    public MessageResponse batchAdd(@RequestBody String addParam) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(addParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<SysUser> resInfos = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                resInfos = JSON.parseArray(paramStr, SysUser.class);
                if (null == resInfos) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            sysUserService.insertSysUsers(resInfos);
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
     *         login_name:用户名（必填）,
     *         user_mobile:用户手机号码（必填）,
     *         user_type:用户类型（PLATFORM-平台、ENTERPRISE-企业、INDIVIDUAL-个人）（必填）,
     *         user_icon:用户LOGO路径（非必填）,
     *         password:密码（非必填）,
     *         nick_name:昵称（非必填）,
     *         user_name:姓名（非必填）,
     *         user_real_name_auth_flag:用户实名认证标志（AUTHORIZED-已实名、UNAUTHORIZED-未实名）（必填）,
     *         user_pid:身份证号（非必填）,
     *         user_ic_front:身份证正面照片路径（非必填）,
     *         user_ic_back:身份证背面照片路径（非必填）,
     *         user_ic_group:用户手举身份证合照路径（非必填）,
     *         org_id:所属组织ID（非必填）,
     *         user_status:用户状态（NORMAL-正常、FREEZE-冻结、INVALID-作废）（必填）,
     *         create_user:创建人（必填）,
     *         update_user:更新人（必填）
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
    @RequestMapping(path = "/add")
    public MessageResponse add(@RequestBody String addParam) {
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
     * @param modifyParam 修改参数JSON
     * <pre>
     *     {
     *         id:ID（必填）,
     *         login_name:用户名,
     *         user_mobile:用户手机号码,
     *         user_type:用户类型（PLATFORM-平台、ENTERPRISE-企业、INDIVIDUAL-个人）,
     *         user_icon:用户LOGO路径,
     *         password:密码,
     *         nick_name:昵称,
     *         user_name:姓名,
     *         user_real_name_auth_flag:用户实名认证标志（AUTHORIZED-已实名、UNAUTHORIZED-未实名）,
     *         user_pid:身份证号,
     *         user_ic_front:身份证正面照片路径,
     *         user_ic_back:身份证背面照片路径,
     *         user_ic_group:用户手举身份证合照路径,
     *         org_id:所属组织ID,
     *         user_status:用户状态（NORMAL-正常、FREEZE-冻结、INVALID-作废）,
     *         update_user:更新人
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
    public MessageResponse modify(@RequestBody String modifyParam) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(modifyParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            SysUser resInfo = null;
            try {
                String paramStr = URLDecoder.decode(modifyParam, "utf-8");
                resInfo = JSON.parseObject(paramStr, SysUser.class);
                if (null == resInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            sysUserService.updateSysUser(resInfo);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
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
            List<String> resIds = null;
            try {
                String paramStr = URLDecoder.decode(deleteParam, "utf-8");
                resIds = JSON.parseArray(paramStr, String.class);
                if (null == resIds) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            sysUserService.deleteSysUser(resIds);
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
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
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
     *                 id:ID,
     *                 login_name:用户名,
     *                 user_mobile:用户手机号码,
     *                 user_type:用户类型（平台、企业或个人）,
     *                 user_icon:用户LOGO路径,
     *                 password:密码,
     *                 nick_name:昵称,
     *                 user_name:姓名,
     *                 user_real_name_auth_flag:用户实名认证标志（已实名、未实名）,
     *                 user_pid:身份证号,
     *                 user_ic_front:身份证正面照片路径,
     *                 user_ic_back:身份证背面照片路径,
     *                 user_ic_group:用户手举身份证合照路径,
     *                 org_id:所属组织ID,
     *                 user_status:用户状态（正常、冻结、作废）,
     *                 create_user:创建人,
     *                 create_time:创建时间,
     *                 update_user:更新人,
     *                 update_time:更新时间
     *             }
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
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS,user);
            return mr;
        }
    }
}
