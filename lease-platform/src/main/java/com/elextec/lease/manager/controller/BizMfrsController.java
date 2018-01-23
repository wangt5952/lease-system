package com.elextec.lease.manager.controller;

import com.elextec.framework.BaseController;
import com.elextec.framework.common.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 制造商管理Controller.
 * Created by wangtao on 2018/1/19.
 */
@RestController
@RequestMapping(path = "/manager/mfrs")
public class BizMfrsController extends BaseController {
    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(BizMfrsController.class);

    /**
     * 查询制造商.
     * @param paramAndPaging 查询及分页参数JSON
     * @return
     */
    @RequestMapping(path = "/listmfrs")
    public MessageResponse listMfrs(@RequestBody String paramAndPaging) {
        return null;
    }

    /**
     * 批量增加制造商.
     * @param mfrs 制造商信息列表JSON
     * @return
     */
    @RequestMapping(path = "/addmfrs")
    public MessageResponse addUsers(@RequestBody String mfrs) {
        return null;
    }

    /**
     * 修改制造商信息.
     * @param mfrs 制造商信息JSON
     * @return
     */
    @RequestMapping(path = "/modifymfrs")
    public MessageResponse modifyMfrs(@RequestBody String mfrs) {
        return null;
    }

    /**
     * 批量删除制造商.
     * @param mfrs 待删除的制造商列表JSON
     * @return
     */
    @RequestMapping(path = "/deletemfrs")
    public MessageResponse deleteUsers(@RequestBody String mfrs) {
        return null;
    }
}
