package com.elextec.lease.manager.controller;


import com.elextec.framework.BaseController;
import com.elextec.framework.common.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 资源管理Controller.
 * Created by wangtao on 2018/1/16.
 */
@RestController
@RequestMapping(path = "/manager/res")
public class SysResourceController extends BaseController {
    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(SysResourceController.class);

    /**
     * 查询资源.
     * @param paramAndPaging 查询及分页参数JSON
     * @return
     */
    @RequestMapping(path = "/manager/listresources")
    public MessageResponse listRoles(@RequestBody String paramAndPaging) {
        return null;
    }

    /**
     * 批量增加资源.
     * @param resources 资源列表JSON
     * @return
     */
    @RequestMapping(path = "/manager/addresources")
    public MessageResponse addResources(@RequestBody String resources) {
        return null;
    }

    /**
     * 修改资源信息.
     * @param resource 资源信息JSON
     * @return
     */
    @RequestMapping(path = "/manager/modifyresource")
    public MessageResponse modifyResource(@RequestBody String resource) {
        return null;
    }

    /**
     * 批量删除资源.
     * @param resources 待删除的资源列表JSON
     * @return
     */
    @RequestMapping(path = "/manager/deleteresources")
    public MessageResponse deleteResources(@RequestBody String resources) {
        return null;
    }
}
