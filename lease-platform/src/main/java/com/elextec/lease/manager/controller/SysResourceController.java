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
import com.elextec.persist.field.enums.DeviceType;
import com.elextec.persist.field.enums.ResourceType;
import com.elextec.persist.model.mybatis.SysResources;
import com.elextec.persist.model.mybatis.ext.SysUserExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
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
     *                 resCode:资源编码,
     *                 resName:资源名,
     *                 resType:资源类型（目录、菜单、页面、功能或按钮）,
     *                 resUrl:资源请求URL,
     *                 groupSort:分组排序,
     *                 resSort:组内排序,
     *                 showFlag:显示标志（显示、不显示）,
     *                 parent:上级资源（Root为空）,
     *                 level:级别,
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
            PageResponse<SysResources> resPageResp = sysResourceService.list(true, pagingParam);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, resPageResp);
            return mr;
        }
    }

    /**
     * 批量增加资源.
     * @param addParam 批量新增参数JSON
     * <pre>
     *     [
     *         {
     *             resCode:资源编码（必填）,
     *             resName:资源名（必填）,
     *             resType:资源类型（目录、菜单、页面、功能或按钮）（必填）,
     *             resUrl:资源请求URL（非必填）,
     *             groupSort:分组排序（必填）,
     *             resSort:组内排序（必填）,
     *             showFlag:显示标志（显示、不显示）（必填）,
     *             parent:上级资源（Root为空）（非必填）,
     *             level:级别（必填）,
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
            List<SysResources> resInfos = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                resInfos = JSON.parseArray(paramStr, SysResources.class);
                if (null == resInfos || 0 == resInfos.size()) {
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
     * 增加资源.
     * @param addParam 批量新增参数JSON
     * <pre>
     *
     *         {
     *             resCode:资源编码（必填）,
     *             resName:资源名（必填）,
     *             resType:资源类型（目录、菜单、页面、功能或按钮）（必填）,
     *             resUrl:资源请求URL（非必填）,
     *             groupSort:分组排序（必填）,
     *             resSort:组内排序（必填）,
     *             showFlag:显示标志（显示、不显示）（必填）,
     *             parent:上级资源（Root为空）（非必填）,
     *             level:级别（必填）,
     *             createUser:创建人（必填）,
     *             updateUser:更新人（必填）
     *         }
     *
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
    public MessageResponse addone(@RequestBody String addParam) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(addParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            SysResources resInfo = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                resInfo = JSON.parseObject(paramStr, SysResources.class);
                if (null == resInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if (WzStringUtil.isBlank(resInfo.getResCode())
                        || WzStringUtil.isBlank(resInfo.getResName())
                        || null == resInfo.getResType()
                        || null == resInfo.getLevel()
                        || null == resInfo.getResSort()
                        || null == resInfo.getGroupSort()
                        || WzStringUtil.isBlank(resInfo.getCreateUser())
                        || WzStringUtil.isBlank(resInfo.getUpdateUser())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "资源参数有误");
                }
                if (!resInfo.getResType().toString().equals(ResourceType.CATALOG.toString())
                        && !resInfo.getResType().toString().equals(ResourceType.MENU.toString())
                        && !resInfo.getResType().toString().equals(ResourceType.PAGE.toString())
                        && !resInfo.getResType().toString().equals(ResourceType.FUNCTION.toString())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "无效的资源类别");
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            sysResourceService.insertSysResource(resInfo);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }


    /**
     * 修改资源信息.
     * @param modifyParam 修改参数JSON
     * <pre>
     *     {
     *         id:ID,
     *         resCode:资源编码,
     *         resName:资源名,
     *         resType:资源类型（目录、菜单、页面、功能或按钮）,
     *         resUrl:资源请求URL,
     *         groupSort:分组排序,
     *         resSort:组内排序,
     *         showFlag:显示标志（显示、不显示）,
     *         parent:上级资源（Root为空）,
     *         level:级别,
     *         updateUser:更新人
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
            SysResources resInfo = null;
            try {
                String paramStr = URLDecoder.decode(modifyParam, "utf-8");
                resInfo = JSON.parseObject(paramStr, SysResources.class);
                if (null == resInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if (WzStringUtil.isBlank(resInfo.getId())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "无法确定待修改的记录");
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
                if (null == resIds || 0 == resIds.size()) {
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

    /**
     * 根据ID获取资源信息.
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
     *                 resCode:资源编码,
     *                 resName:资源名,
     *                 resType:资源类型（目录、菜单、页面、功能或按钮）,
     *                 resUrl:资源请求URL,
     *                 resSort:组内排序,
     *                 groupSort:分组排序,
     *                 showFlag:显示标志（显示、不显示）,
     *                 parent:上级资源（Root为空）,
     *                 level:级别,
     *                 createUser:创建人,
     *                 createTime:创建时间,
     *                 updateUser:更新人,
     *                 updateTime:更新时间
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
            List<String> resId = null;
            try {
                String paramStr = URLDecoder.decode(id, "utf-8");
                resId = JSON.parseArray(paramStr, String.class);
                if (null == resId || 0 == resId.size() || WzStringUtil.isBlank(resId.get(0))) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }

            SysResources res = sysResourceService.getSysResourceByPrimaryKey(resId.get(0));
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, res);
            return mr;
        }
    }
}
