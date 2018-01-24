package com.elextec.lease.manager.controller;


import com.alibaba.fastjson.JSON;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.constants.WzConstants;
import com.elextec.framework.common.request.LoginParam;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.framework.utils.WzUniqueValUtil;
import com.elextec.lease.manager.service.SysResourceService;
import com.elextec.persist.model.mybatis.SysResources;
import com.elextec.persist.model.mybatis.ext.SysUserExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 资源管理Controller.
 * Created by wangtao on 2018/1/16.
 */
@RestController
@RequestMapping(path = "/manager/res")
public class SysResourceController extends BaseController {
    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(SysResourceController.class);

    @Autowired
    private SysResourceService sysResourceService;

    /**
     * 查询资源.
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
     *                 res_code:资源编码,
     *                 res_name:资源名,
     *                 res_type:资源类型（目录、菜单、页面、功能或按钮）,
     *                 res_url:资源请求URL,
     *                 res_sort:排序,
     *                 show_flag:显示标志（显示、不显示）,
     *                 parent:上级资源（Root为空）,
     *                 level:级别,
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
    @RequestMapping(path = "/manager/listresources")
    public MessageResponse listRoles(@RequestBody String paramAndPaging) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(paramAndPaging)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            PageRequest pagingParam = null;
            try {
                pagingParam = JSON.parseObject(paramAndPaging, PageRequest.class);
                if (null == pagingParam) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            PageResponse<SysResources> resPageResp = sysResourceService.list(true, pagingParam);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, resPageResp);
            return mr;
        }
    }

    /**
     * 批量增加资源.
     * @param resources 资源列表JSON
     * <pre>
     *     [
     *         {
     *             res_code:资源编码,
     *             res_name:资源名,
     *             res_type:资源类型（目录、菜单、页面、功能或按钮）,
     *             res_url:资源请求URL,
     *             res_sort:排序,
     *             show_flag:显示标志（显示、不显示）,
     *             parent:上级资源（Root为空）,
     *             level:级别,
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
    @RequestMapping(path = "/manager/addresources")
    public MessageResponse addResources(@RequestBody String resources) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(resources)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<SysResources> resInfos = null;
            try {
                resInfos = JSON.parseArray(resources, SysResources.class);
                if (null == resInfos) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            sysResourceService.insertSysResources(resInfos);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 修改资源信息.
     * @param resource 资源信息JSON
     * <pre>
     *     {
     *         id:ID,
     *         res_code:资源编码,
     *         res_name:资源名,
     *         res_type:资源类型（目录、菜单、页面、功能或按钮）,
     *         res_url:资源请求URL,
     *         res_sort:排序,
     *         show_flag:显示标志（显示、不显示）,
     *         parent:上级资源（Root为空）,
     *         level:级别,
     *         update_user:更新人
     *     }
     * </pre>
     * @return
     */
    @RequestMapping(path = "/manager/modifyresource")
    public MessageResponse modifyResource(@RequestBody String resource) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(resource)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            SysResources resInfo = null;
            try {
                resInfo = JSON.parseObject(resource, SysResources.class);
                if (null == resInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            sysResourceService.updateSysResources(resInfo);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 批量删除资源.
     * @param resources 待删除的资源列表JSON
     * @return
     */
    @RequestMapping(path = "/manager/deleteresources")
    public MessageResponse deleteResources(@RequestBody String resources) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(resources)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> resIds = null;
            try {
                resIds = JSON.parseArray(resources, String.class);
                if (null == resIds) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            sysResourceService.deleteSysResources(resIds);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }
}
