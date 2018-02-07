package com.elextec.lease.manager.controller;


import com.alibaba.fastjson.JSON;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.manager.request.SysResParam;
import com.elextec.lease.manager.service.SysResourceService;
import com.elextec.lease.model.SysResourcesIcon;
import com.elextec.persist.field.enums.ResourceType;
import com.elextec.persist.field.enums.ShowFlag;
import com.elextec.persist.model.mybatis.SysResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.List;

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
     *         keyStr:查询关键字（非必填，模糊查询，可填写资源Code、资源名）,
     *         resType:资源类别（非必填，包括PLATFORM、ENTERPRISE、INDIVIDUAL）,
     *         showFlg:显示标志（非必填，包括SHOW、HIDDEN）,
     *         needPaging:是否需要分页（仅为false时不需要分页，其余情况均需要分页）,
     *         currPage:当前页（needPaging不为false时必填）,
     *         pageSize:每页记录数（needPaging不为false时必填）
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
     *                 resIcon:资源Icon路径,
     *                 resType:资源类型（目录、菜单、功能或按钮）,
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
//            PageRequest pagingParam = null;
            SysResParam pagingParam = null;
            try {
                String paramStr = URLDecoder.decode(paramAndPaging, "utf-8");
//                pagingParam = JSON.parseObject(paramStr, PageRequest.class);
                pagingParam = JSON.parseObject(paramStr, SysResParam.class);
                if (null == pagingParam) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                // 仅needPaging标志为false时，不需要分页，其他情况均需要进行分页
                if (WzStringUtil.isNotBlank(pagingParam.getNeedPaging())
                        && "false".equals(pagingParam.getNeedPaging().toLowerCase())) {
                    pagingParam.setNeedPaging("false");
                } else {
                    if (null == pagingParam.getCurrPage() || null == pagingParam.getPageSize()) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "未获得分页参数");
                    }
                    pagingParam.setNeedPaging("true");
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
//            PageResponse<SysResources> resPageResp = sysResourceService.list(Boolean.valueOf(pagingParam.getNeedPaging()), pagingParam);
            PageResponse<SysResources> resPageResp = sysResourceService.listByParam(Boolean.valueOf(pagingParam.getNeedPaging()), pagingParam);
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
     *             resIcon:资源Icon路径（非必填）,
     *             resType:资源类型（目录、菜单、功能或按钮）（必填）,
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
                SysResources insResChkVo = null;
                for (int i = 0; i < resInfos.size(); i++) {
                    insResChkVo = resInfos.get(i);
                    if (WzStringUtil.isBlank(insResChkVo.getResCode())
                            || WzStringUtil.isBlank(insResChkVo.getResName())
                            || null == insResChkVo.getResType()
                            || null == insResChkVo.getShowFlag()
                            || null == insResChkVo.getLevel()
                            || null == insResChkVo.getResSort()
                            || null == insResChkVo.getGroupSort()
                            || WzStringUtil.isBlank(insResChkVo.getCreateUser())
                            || WzStringUtil.isBlank(insResChkVo.getUpdateUser())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "第" + i + "条资源信息参数有误");
                    }
                    if (!insResChkVo.getResType().toString().equals(ResourceType.CATALOG.toString())
                            && !insResChkVo.getResType().toString().equals(ResourceType.MENU.toString())
                            && !insResChkVo.getResType().toString().equals(ResourceType.FUNCTION.toString())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "第" + i + "条资源类别无效");
                    }
                    if (!insResChkVo.getShowFlag().toString().equals(ShowFlag.SHOW.toString())
                            && !insResChkVo.getShowFlag().toString().equals(ShowFlag.HIDDEN.toString())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "第" + i + "条资源显示标志无效");
                    }
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
     *             resIcon:资源Icon名称（非必填）,
     *             resType:资源类型（目录、菜单、功能或按钮）（必填）,
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
                        || null == resInfo.getShowFlag()
                        || null == resInfo.getLevel()
                        || null == resInfo.getResSort()
                        || null == resInfo.getGroupSort()
                        || WzStringUtil.isBlank(resInfo.getCreateUser())
                        || WzStringUtil.isBlank(resInfo.getUpdateUser())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "资源参数有误");
                }
                if (!resInfo.getResType().toString().equals(ResourceType.CATALOG.toString())
                        && !resInfo.getResType().toString().equals(ResourceType.MENU.toString())
                        && !resInfo.getResType().toString().equals(ResourceType.FUNCTION.toString())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的资源类别");
                }
                if (!resInfo.getShowFlag().toString().equals(ShowFlag.SHOW.toString())
                        && !resInfo.getShowFlag().toString().equals(ShowFlag.HIDDEN.toString())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的显示标志");
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
     *         resIcon:资源Icon名,
     *         resType:资源类型（目录、菜单、功能或按钮）,
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
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无法确定待修改的记录");
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
     *                 resIcon:资源Icon路径,
     *                 resType:资源类型（目录、菜单、功能或按钮）,
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

    /**
     * 列出资源Icon列表.
     * @return Icon列表
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:[
     *             {
     *                 iconName:icon文件名,
     *                 iconUrl:icon访问URL
     *             },
     *             ......
     *         ]
     *     }
     * </pre>
     */
    @RequestMapping(path = "/listicon")
    public MessageResponse listIcon() {
        List<SysResourcesIcon> icons  = sysResourceService.listSysResourceIcons();
        // 组织返回结果并返回
        MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, icons);
        return mr;
    }
}
