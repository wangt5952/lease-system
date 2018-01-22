package com.elextec.lease.manager.controller;


import com.elextec.framework.BaseController;
import com.elextec.framework.common.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理Controller.
 * Created by wangtao on 2018/1/16.
 */
@RestController
@RequestMapping(path = "/manager/user")
public class SysUserController extends BaseController {

    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(SysUserController.class);

    /**
     * 查询用户.
     * @param paramAndPaging 查询及分页参数JSON
     * @return
     */
    @RequestMapping(path = "/listusers")
    public MessageResponse listUsers(@RequestBody String paramAndPaging) {
        return null;
    }

    /**
     * 批量增加用户.
     * @param users 用户信息列表JSON
     * @return
     */
    @RequestMapping(path = "/addusers")
    public MessageResponse addUsers(@RequestBody String users) {
        return null;
    }

    /**
     * 修改用户信息.
     * @param user 用户信息JSON
     * @return
     */
    @RequestMapping(path = "/modifyuser")
    public MessageResponse modifyUser(@RequestBody String user) {
        return null;
    }

    /**
     * 批量删除用户.
     * @param users 待删除的用户列表JSON
     * @return
     */
    @RequestMapping(path = "/deleteusers")
    public MessageResponse deleteUsers(@RequestBody String users) {
        return null;
    }

    /**
     * 给用户分配角色.
     * @param userAndRoles 用户及角色列表JSON
     * @return
     */
    @RequestMapping(path = "/refuserandroles")
    public MessageResponse refUserAndRoles(@RequestBody String userAndRoles) {
        return null;
    }
}
