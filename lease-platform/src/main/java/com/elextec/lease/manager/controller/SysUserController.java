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
     * @param paramAndPaging 分页参数JSON
     * <pre>
     *     {
     *         currPage:当前页,
     *         pageSize:每页记录数
     *     }
     * </pre>
     * @return 资源列表
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
    @RequestMapping(path = "/listusers")
    public MessageResponse listUsers(@RequestBody String paramAndPaging) {
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
     * @param users 用户列表JSON
     * <pre>
     *     [
     *         {
     *             login_name:用户名,
     *             user_mobile:用户手机号码,
     *             user_type:用户类型（平台、企业或个人）,
     *             user_icon:用户LOGO路径,
     *             password:密码,
     *             nick_name:昵称,
     *             user_name:姓名,
     *             user_real_name_auth_flag:用户实名认证标志（已实名、未实名）,
     *             user_pid:身份证号,
     *             user_ic_front:身份证正面照片路径,
     *             user_ic_back:身份证背面照片路径,
     *             user_ic_group:用户手举身份证合照路径,
     *             org_id:所属组织ID,
     *             user_status:用户状态（正常、冻结、作废）,
     *             create_user:创建人,
     *             update_user:更新人
     *         }
     *     ]
     * </pre>
     * @return 批量新增结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:
     *     }
     * </pre>
     */
    @RequestMapping(path = "/addusers")
    public MessageResponse addUsers(@RequestBody String users) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(users)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<SysUser> resInfos = null;
            try {
                String paramStr = URLDecoder.decode(users, "utf-8");
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
     * 修改用户信息.
     * @param user 用户信息JSON
     * <pre>
     *     {
     *         id:ID,
     *         login_name:用户名,
     *         user_mobile:用户手机号码,
     *         user_type:用户类型（平台、企业或个人）,
     *         user_icon:用户LOGO路径,
     *         password:密码,
     *         nick_name:昵称,
     *         user_name:姓名,
     *         user_real_name_auth_flag:用户实名认证标志（已实名、未实名）,
     *         user_pid:身份证号,
     *         user_ic_front:身份证正面照片路径,
     *         user_ic_back:身份证背面照片路径,
     *         user_ic_group:用户手举身份证合照路径,
     *         org_id:所属组织ID,
     *         user_status:用户状态（正常、冻结、作废）,
     *         update_user:更新人
     *     }
     * </pre>
     * @return
     */
    @RequestMapping(path = "/modifyuser")
    public MessageResponse modifyUser(@RequestBody String user) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(user)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            SysUser resInfo = null;
            try {
                String paramStr = URLDecoder.decode(user, "utf-8");
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
     * @param users 待删除的用户列表JSON
     * @return
     */
    @RequestMapping(path = "/deleteusers")
    public MessageResponse deleteUsers(@RequestBody String users) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(users)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> resIds = null;
            try {
                String paramStr = URLDecoder.decode(users, "utf-8");
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
     *         roleIds:角色ID，多个以逗号隔开，例：角色1，角色2，角色3
     *     }
     * </pre>
     * @return
     */
    @RequestMapping(path = "/refuserandroles")
    public MessageResponse refUserAndRoles(@RequestBody String userAndRoles) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(userAndRoles)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            RefUserRolesParam res = null;
            try {
                String paramStr = URLDecoder.decode(userAndRoles, "utf-8");
                res = JSON.parseObject(paramStr, RefUserRolesParam.class);
                if (null == res) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            sysUserService.refUserAndRoles(res);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }
}
