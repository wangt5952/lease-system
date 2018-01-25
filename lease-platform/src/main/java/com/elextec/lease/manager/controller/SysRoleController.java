package com.elextec.lease.manager.controller;


import com.alibaba.fastjson.JSON;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.request.RefRoleResourceParam;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.manager.service.SysRoleService;
import com.elextec.persist.model.mybatis.SysRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.List;

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

    /**
     * 查询角色.
     * @param paramAndPaging 查询及分页参数JSON
     * @return
     */
    @RequestMapping(path = "/listroles")
    public MessageResponse listRoles(@RequestBody String paramAndPaging) {
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
            PageResponse<SysRole> resPageResp = sysRoleService.list(true, pagingParam);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, resPageResp);
            return mr;
        }
    }

    /**
     * 批量增加角色.
     * @param roles 角色列表JSON
     * @return
     */
    @RequestMapping(path = "/addroles")
    public MessageResponse addRoles(@RequestBody String roles) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(roles)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<SysRole> resInfos = null;
            try {
                String paramStr = URLDecoder.decode(roles, "utf-8");
                resInfos = JSON.parseArray(paramStr, SysRole.class);
                if (null == resInfos) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            sysRoleService.insertSysRoles(resInfos);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 修改角色信息.
     * @param role 角色信息JSON
     * @return
     */
    @RequestMapping(path = "/modifyrole")
    public MessageResponse modifyRole(@RequestBody String role) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(role)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            SysRole resInfo = null;
            try {
                String paramStr = URLDecoder.decode(role, "utf-8");
                resInfo = JSON.parseObject(paramStr, SysRole.class);
                if (null == resInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            sysRoleService.updateSysRole(resInfo);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 批量删除角色.
     * @param roles 待删除的角色列表JSON
     * @return
     */
    @RequestMapping(path = "/deleteroles")
    public MessageResponse deleteRoles(@RequestBody String roles) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(roles)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> resIds = null;
            try {
                String paramStr = URLDecoder.decode(roles, "utf-8");
                resIds = JSON.parseArray(paramStr, String.class);
                if (null == resIds) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            sysRoleService.deleteSysRole(resIds);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 给角色分配资源.
     * @param roleAndResources 角色及资源列表JSON
     * @return
     */
    @RequestMapping(path = "/refroleandresources")
    public MessageResponse refRoleAndResources(@RequestBody String roleAndResources) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(roleAndResources)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            RefRoleResourceParam res = null;
            try {
                String paramStr = URLDecoder.decode(roleAndResources, "utf-8");
                res = JSON.parseObject(paramStr, RefRoleResourceParam.class);
                if (null == res) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            sysRoleService.refRoleAndResource(res);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }
}
