package com.elextec.lease.manager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.manager.request.BizPartsParam;
import com.elextec.lease.manager.service.BizPartsService;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.field.enums.PartsType;
import com.elextec.persist.field.enums.RecordStatus;
import com.elextec.persist.model.mybatis.BizParts;
import com.elextec.persist.model.mybatis.SysUser;
import com.elextec.persist.model.mybatis.ext.BizPartsExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配件管理Controller.
 * Created by wangtao on 2018/1/19.
 */
@RestController
@RequestMapping(value = "/manager/parts")
public class
BizPartsController extends BaseController {

    /*日志*/
    private final Logger logger = LoggerFactory.getLogger(BizPartsController.class);

    @Autowired
    private BizPartsService bizPartsService;

    /**
     * 查询配件信息.
     * @param request 请求
     * @param paramAndPaging 查询及分页参数JSON
     * <pre>
     *     {
     *         keyStr:查询关键字(非必填，模糊查询，可填写配件编码、配件货名、配件品牌、配件型号、配件参数、生产商ID、生产商名称)，
     *         partsType:配件类别（非必填，SEATS，FRAME，HANDLEBAR，BELL，TYRE，PEDAL，DASHBOARD）,
     *         partsStatus：配件状态（非必填，NORMAL、FREEZE、INVALID），
     *         needPaging:是否需要分页（仅为false时不需要分页，其余情况均需要分页）,
     *         currPage:当前页,
     *         pageSize:每页记录数,
     *         isBind:绑定状态（BIND-已绑定，UNBIND-未绑定）.
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
     *                 partsCode:配件编码,
     *                 partsName:配件货名,
     *                 partsBrand:配件品牌,
     *                 partsPn:配件型号,
     *                 partsType:配件类别（车座、车架、车把、车铃、轮胎、脚蹬、仪表盘）,
     *                 partsParameters:配件参数,
     *                 mfrsId:生产商ID,
     *                 mfrsName:生产商名,
     *                 partsStatus:配件状态（正常、冻结、作废）,
     *                 createUser:创建人,
     *                 createTime:创建时间,
     *                 updateUser:更新人,
     *                 updateTime:更新时间
     *                 vehicleId:被绑定车辆ID
     *             },
     *             ... ...
     *         ]
     *     }
     * </pre>
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public MessageResponse list(@RequestBody String paramAndPaging, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(paramAndPaging)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
//            PageRequest pagingParam = null;
            BizPartsParam bizPartsParam = null;
            try {
                String paramStr = URLDecoder.decode(paramAndPaging, "utf-8");
//                pagingParam = JSON.parseObject(paramStr, PageRequest.class);
                bizPartsParam = JSON.parseObject(paramStr,BizPartsParam.class);
                if (null == bizPartsParam) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                //仅needPaging标志为false时，不需要分页，其他情况均需要进行分页
                if (WzStringUtil.isNotBlank(bizPartsParam.getNeedPaging()) && "false".equals(bizPartsParam.getNeedPaging().toLowerCase())) {
                    bizPartsParam.setNeedPaging("false");
                } else {
                    if (null == bizPartsParam.getCurrPage() || null == bizPartsParam.getPageSize()) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "需要分页参数");
                    }
                    bizPartsParam.setNeedPaging("true");
                }
                SysUser userTemp = getLoginUserInfo(request);
                if(userTemp != null){
                    //根据用户类型添加条件
                    //个人用户需要添加userId为条件
                    if(OrgAndUserType.INDIVIDUAL.toString().equals(userTemp.getUserType().toString())){
                        bizPartsParam.setUserId(userTemp.getId());
                        bizPartsParam.setIsBind("BIND");
                    }
                    //企业用户需要添加orgId为条件
                    if(OrgAndUserType.ENTERPRISE.toString().equals(userTemp.getUserType().toString())){
                        bizPartsParam.setOrgId(userTemp.getOrgId());
                        bizPartsParam.setIsBind("BIND");
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME.code(),"登录信息已失效");
                }
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR.code(), ex.getMessage(), ex);
            }
            //PageResponse<BizParts> orgPageResp = bizPartsService.list(true, bizPartsParam);
            PageResponse<BizPartsExt> orgPageResp = bizPartsService.listExtByParam(Boolean.valueOf(bizPartsParam.getNeedPaging()), bizPartsParam);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, orgPageResp);
            return mr;
        }
    }

    /**
     * 批量增加配件信息.
     * @param addParam 批量新增参数列表JSON
     * <pre>
     *     [
     *         {
     *            partsCode:配件编码,
     *            partsName:配件货名,
     *            partsBrand:配件品牌,
     *            partsPn:配件型号,
     *            partsType:配件类型（SEATS、FRAME、HANDLEBAR、BELL、TYRE、PEDAL、DASHBOARD）
     *            partsParameters:配件参数,
     *            mfrsId:生产商ID,
     *            partsStatus:配件状态（NORMAL、FREEZE、INVALID）
     *            createUser:创建人,
     *            updateUser:更新人
     *         },
     *         ... ...
     *     ]
     * </pre>
     * @param request HttpServletRequest
     * @return 批量新增结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public MessageResponse add(@RequestBody String addParam, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(addParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<BizParts> bizPartsInfos = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                bizPartsInfos = JSON.parseArray(paramStr, BizParts.class);
                if (null == bizPartsInfos || 0 == bizPartsInfos.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                SysUser userTemp = getLoginUserInfo(request);
                if(userTemp != null){
                    //只有平台用户可以操作
                    if(!OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                BizParts insResChkVo = null;
                for (int i = 0; i < bizPartsInfos.size(); i++) {
                    insResChkVo = bizPartsInfos.get(i);
                    if (WzStringUtil.isBlank(insResChkVo.getPartsCode())
                            || null == insResChkVo.getPartsType()
                            || null == insResChkVo.getPartsStatus()
                            || WzStringUtil.isBlank(insResChkVo.getCreateUser())
                            || WzStringUtil.isBlank(insResChkVo.getUpdateUser())) {
                        return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "配件信息参数有误");
                    }
                    if (!insResChkVo.getPartsType().toString().equals(PartsType.BELL.toString())
                            && !insResChkVo.getPartsType().toString().equals(PartsType.DASHBOARD.toString())
                            && !insResChkVo.getPartsType().toString().equals(PartsType.FRAME.toString())
                            && !insResChkVo.getPartsType().toString().equals(PartsType.HANDLEBAR.toString())
                            && !insResChkVo.getPartsType().toString().equals(PartsType.PEDAL.toString())
                            && !insResChkVo.getPartsType().toString().equals(PartsType.SEATS.toString())
                            && !insResChkVo.getPartsType().toString().equals(PartsType.TYRE.toString())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的配件类型");
                    }
                    if (!insResChkVo.getPartsStatus().toString().equals(RecordStatus.FREEZE.toString())
                            && !insResChkVo.getPartsStatus().toString().equals(RecordStatus.INVALID.toString())
                            && !insResChkVo.getPartsStatus().toString().equals(RecordStatus.NORMAL.toString())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的配件状态");
                    }
                }
                bizPartsService.insertBizParts(bizPartsInfos);
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 增加配件资源.
     * @param addParam 批量新增参数JSON
     * <pre>
     *
     *      {
     *         partsCode:配件编码,
     *         partsName:配件货名,
     *         partsBrand:配件品牌,
     *         partsPn:配件型号,
     *         partsType:配件类型（SEATS、FRAME、HANDLEBAR、BELL、TYRE、PEDAL、DASHBOARD）
     *         partsParameters:配件参数,
     *         mfrsId:生产商ID,
     *         partsStatus:配件状态（NORMAL、FREEZE、INVALID）
     *         createUser:创建人,
     *         updateUser:更新人
     *      }
     *
     * </pre>
     * @param request HttpServletRequest
     * @return 新增结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(value = "/addone", method = RequestMethod.POST)
    public MessageResponse addone(@RequestBody String addParam, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(addParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            BizParts bizParts = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                bizParts = JSON.parseObject(paramStr, BizParts.class);
                if (null == bizParts) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                SysUser userTemp = getLoginUserInfo(request);
                if(userTemp != null){
                    //只有平台用户可以操作
                    if(!OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                if (WzStringUtil.isBlank(bizParts.getPartsCode())
                        || null == bizParts.getPartsType()
                        || null == bizParts.getPartsStatus()
                        || WzStringUtil.isBlank(bizParts.getCreateUser())
                        || WzStringUtil.isBlank(bizParts.getUpdateUser())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "配件信息参数有误");
                }
                if (!bizParts.getPartsType().toString().equals(PartsType.BELL.toString())
                        && !bizParts.getPartsType().toString().equals(PartsType.DASHBOARD.toString())
                        && !bizParts.getPartsType().toString().equals(PartsType.FRAME.toString())
                        && !bizParts.getPartsType().toString().equals(PartsType.HANDLEBAR.toString())
                        && !bizParts.getPartsType().toString().equals(PartsType.PEDAL.toString())
                        && !bizParts.getPartsType().toString().equals(PartsType.SEATS.toString())
                        && !bizParts.getPartsType().toString().equals(PartsType.TYRE.toString())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的配件类型");
                }
                if (!bizParts.getPartsStatus().toString().equals(RecordStatus.FREEZE.toString())
                        && !bizParts.getPartsStatus().toString().equals(RecordStatus.INVALID.toString())
                        && !bizParts.getPartsStatus().toString().equals(RecordStatus.NORMAL.toString())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的配件状态");
                }
                bizPartsService.insertBizParts(bizParts);
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 修改配件信息.
     * @param modifyParam 修改参数JSON
     * <pre>
     *     {
     *         id:ID,
     *         partsName:配件货名,
     *         partsBrand:配件品牌,
     *         partsPn:配件型号,
     *         partsParameters:配件参数,
     *         updateUser:更新人,
     *     }
     * </pre>
     * @param request HttpServletRequest
     * @return 修改结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public MessageResponse modify(@RequestBody String modifyParam, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(modifyParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误
            BizParts bizParts = null;
            try {
                String paramStr = URLDecoder.decode(modifyParam, "utf-8");
                bizParts = JSON.parseObject(paramStr, BizParts.class);
                if (null == bizParts
                        || WzStringUtil.isBlank(bizParts.getUpdateUser())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                SysUser userTemp = getLoginUserInfo(request);
                if(userTemp != null){
                    //只有平台用户可以操作
                    if(!OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                if (WzStringUtil.isBlank(bizParts.getId())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "无法确定待修改的记录");
                }
                if(null != bizParts.getPartsStatus()){
                    if (!bizParts.getPartsStatus().toString().equals(RecordStatus.FREEZE.toString())
                            && !bizParts.getPartsStatus().toString().equals(RecordStatus.INVALID.toString())
                            && !bizParts.getPartsStatus().toString().equals(RecordStatus.NORMAL.toString())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的配件状态");
                    }
                }
                if(null != bizParts.getPartsType()){
                    if (!bizParts.getPartsType().toString().equals(PartsType.BELL.toString())
                            && !bizParts.getPartsType().toString().equals(PartsType.DASHBOARD.toString())
                            && !bizParts.getPartsType().toString().equals(PartsType.FRAME.toString())
                            && !bizParts.getPartsType().toString().equals(PartsType.HANDLEBAR.toString())
                            && !bizParts.getPartsType().toString().equals(PartsType.PEDAL.toString())
                            && !bizParts.getPartsType().toString().equals(PartsType.SEATS.toString())
                            && !bizParts.getPartsType().toString().equals(PartsType.TYRE.toString())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的配件类型");
                    }
                }
                bizPartsService.updateBizParts(bizParts);
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 批量删除配件信息.
     * @param deleteParam 删除ID列表JSON
     * <pre>
     *     [ID1,ID2,......]
     * </pre>
     * @param request HttpServletRequest
     * @return 批量删除结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public MessageResponse delete(@RequestBody String deleteParam, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(deleteParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> bizPartsIds = null;
            try {
                String paramStr = URLDecoder.decode(deleteParam, "utf-8");
                bizPartsIds = JSON.parseArray(paramStr, String.class);
                if (null == bizPartsIds || 0 == bizPartsIds.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                SysUser userTemp = getLoginUserInfo(request);
                if(userTemp != null){
                    //只有平台用户可以操作
                    if(!OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                bizPartsService.deleteBizParts(bizPartsIds);
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 根据id查询配件信息.
     * @param id 查询ID
     * <pre>
     *     [id]
     * </pre>
     * @param request HttpServletRequest
     * @return 查询结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:
     *         {
     *                 id:ID,
     *                 partsCode:配件编码,
     *                 partsName:配件货名,
     *                 partsBrand:配件品牌,
     *                 partsPn:配件型号,
     *                 partsType:配件类别（）,
     *                 partsParameters:配件参数,
     *                 mfrsId:生产商ID,
     *                 partsStatus:配件状态（正常、冻结、作废）,
     *                 createUser:创建人,
     *                 createTime:创建时间,
     *                 updateUser:更新人,
     *                 updateTime:更新时间
     *             }
     *     }
     * </pre>
     */
    @RequestMapping(value = "/getbypk", method = RequestMethod.POST)
    public MessageResponse getByPK(@RequestBody String id, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(id)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            List<String> bizPartsId = null;
            try {
                String paramStr = URLDecoder.decode(id, "utf-8");
                bizPartsId = JSON.parseArray(paramStr, String.class);
                if (null == bizPartsId || 0 == bizPartsId.size() || WzStringUtil.isBlank(bizPartsId.get(0))) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            SysUser userTemp = getLoginUserInfo(request);
            Map<String,Object> paramTemp = new HashMap<String,Object>();
            paramTemp.put("id",bizPartsId.get(0));
            if(userTemp == null){
                return new MessageResponse(RunningResult.AUTH_OVER_TIME);
            }
            if(OrgAndUserType.ENTERPRISE.toString().equals(userTemp.getUserType().toString())){
                paramTemp.put("orgId",userTemp.getOrgId());
            }
            if(OrgAndUserType.INDIVIDUAL.toString().equals(userTemp.getUserType().toString())){
                paramTemp.put("userId",userTemp.getId());
            }
            BizParts bizParts = bizPartsService.getBizPartsByPrimaryKey(paramTemp);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, bizParts);
            return mr;
        }
    }

    /**
     * 车辆和配件绑定接口
     * @param vehicleIdAndPartsId 车辆id和配件id
     * <pre>
     *     {
     *         vehicleId:车辆ID,
     *         partsId:配件ID
     *     }
     * </pre>
     * @param request HttpServletRequest
     * @return 查询结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(value = "/partBind", method = RequestMethod.POST)
    public MessageResponse partBind(@RequestBody String vehicleIdAndPartsId, HttpServletRequest request){
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(vehicleIdAndPartsId)){
            return new MessageResponse(RunningResult.NO_PARAM);
        } else {
            // 参数解析错误报“参数解析错误”
            Map<String,String> map = null;
            try {
                String paramStr = URLDecoder.decode(vehicleIdAndPartsId,"utf-8");
                map = JSONObject.parseObject(paramStr,Map.class);
                if (map == null || map.size() == 0) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                SysUser userTemp = getLoginUserInfo(request);
                if(userTemp != null){
                    //只有平台用户可以操作
                    if(!OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                if (WzStringUtil.isBlank(map.get("vehicleId")) || WzStringUtil.isBlank(map.get("partsId"))) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(),"查询参数不能为空");
                }
                bizPartsService.bind(map.get("vehicleId"),map.get("partsId"));
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            return new MessageResponse(RunningResult.SUCCESS);
        }
    }

    /**
     * 车辆和配件解绑接口
     * @param vehicleIdAndPartsId 车辆ID和配件ID
     * <pre>
     *      {
     *          vehicleId:车辆ID，
     *          partsId:配件ID
     *      }
     * </pre>
     * @param request HttpServletRequest
     * @return 查询结果
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:""
     *     }
     * </pre>
     */
    @RequestMapping(value = "/partsUnBind", method = RequestMethod.POST)
    public MessageResponse partsUnBind(@RequestBody String vehicleIdAndPartsId, HttpServletRequest request){
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(vehicleIdAndPartsId)) {
            return new MessageResponse(RunningResult.NO_PARAM);
        } else {
            // 参数解析错误报“参数解析错误”
            Map<String,String> map = null;
            try {
                String paramStr = URLDecoder.decode(vehicleIdAndPartsId, "utf-8");
                map = JSON.parseObject(paramStr, Map.class);
                if (map == null || map.size() == 0) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                SysUser userTemp = getLoginUserInfo(request);
                if(userTemp != null){
                    //只有平台用户可以操作
                    if(!OrgAndUserType.PLATFORM.toString().equals(userTemp.getUserType().toString())){
                        return new MessageResponse(RunningResult.NO_FUNCTION_PERMISSION);
                    }
                }else{
                    return new MessageResponse(RunningResult.AUTH_OVER_TIME);
                }
                if (WzStringUtil.isBlank(map.get("vehicleId")) || WzStringUtil.isBlank(map.get("partsId"))) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "解绑参数不能为空");
                }
                bizPartsService.unBind(map.get("vehicleId"), map.get("partsId"));
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }

            // 组织返回结果并返回
            return new MessageResponse(RunningResult.SUCCESS);
        }
        
    }

}
