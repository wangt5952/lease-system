package com.elextec.lease.manager.controller;

import com.elextec.framework.BaseController;
import com.elextec.lease.manager.service.SysResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 车辆管理Controller.
 * Created by wangtao on 2018/1/19.
 */
public class BizVehicleController extends BaseController {
    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(BizVehicleController.class);

    @Autowired
    private SysResourceService sysResourceService;
}
