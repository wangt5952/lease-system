package com.elextec.lease.manager.controller;


import com.elextec.framework.BaseController;
import com.elextec.framework.common.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色管理Controller.
 * Created by wangtao on 2018/1/16.
 */
@RestController
@RequestMapping(path = "/manager/role")
public class SysRoleController extends BaseController {
    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(SysRoleController.class);

    /**
     * 查询角色.
     * @param paramAndPaging 查询及分页参数JSON
     * @return
     */
    @RequestMapping(path = "/listroles")
    public MessageResponse listRoles(@RequestBody String paramAndPaging) {
        return null;
    }

    /**
     * 批量增加角色.
     * @param roles 角色列表JSON
     * @return
     */
    @RequestMapping(path = "/addroles")
    public MessageResponse addRoles(@RequestBody String roles) {
        return null;
    }

    /**
     * 修改角色信息.
     * @param role 角色信息JSON
     * @return
     */
    @RequestMapping(path = "/modifyrole")
    public MessageResponse modifyRole(@RequestBody String role) {
        return null;
    }

    /**
     * 批量删除角色.
     * @param roles 待删除的角色列表JSON
     * @return
     */
    @RequestMapping(path = "/deleteroles")
    public MessageResponse deleteRoles(@RequestBody String roles) {
        return null;
    }

    /**
     * 给角色分配资源.
     * @param roleAndResources 角色及资源列表JSON
     * @return
     */
    @RequestMapping(path = "/refroleandresources")
    public MessageResponse refRoleAndResources(@RequestBody String roleAndResources) {
        return null;
    }
}
