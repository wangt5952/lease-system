package com.elextec.lease.manager.controller;

import com.alibaba.fastjson.JSON;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.manager.request.BizMfrsParam;
import com.elextec.lease.manager.service.BizManufacturerService;
import com.elextec.persist.field.enums.MfrsType;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.field.enums.RecordStatus;
import com.elextec.persist.model.mybatis.BizManufacturer;
import com.elextec.persist.model.mybatis.SysUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.List;

/**
 * 制造商管理Controller.
 * Created by wangtao on 2018/1/19.
 */
@RestController
@RequestMapping(path = "/manager/mfrs")
public class BizMfrsController extends BaseController {

    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(BizMfrsController.class);

    @Autowired
    private BizManufacturerService bizManufacturerService;

    /**
     * 查询制造商.
     * @param paramAndPaging 查询及分页参数JSON
     * <pre>
     *     {
     *         keyStr:查询关键字（非必填，模糊查询，可填写制造商名称、制造商介绍、制造商地址、联系人、联系电话）,
     *         mfrsType:制造商类型（非必填，包括VEHICLE、BATTERY、PARTS）,
     *         mfrsStatus:制造商状态（非必填，包括NORMAL、FREEZE、INVALID）,
     *         needPaging:是否需要分页（仅为false时不需要分页，其余情况均需要分页）,
     *         currPage:当前页,
     *         pageSize:每页记录数
     *     }
     * </pre>
     * @param request HttpServletRequest
     * @return 查询结果列表
     * <pre>
     *     {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:[
     *             {
     *                 id:ID,
     *                 mfrsName:制造商名称,
     *                 mfrsType:制造商类型（车辆、电池、配件）,
     *                 mfrsIntroduce:制造商介绍,
     *                 mfrsAddress:制造商地址,
     *                 mfrsContacts:联系人（多人用 , 分割）,
     *                 mfrsPhone:联系电话（多个电话用 , 分割）,
     *                 mfrsStatus:制造商状态（正常、冻结、作废）,
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
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public MessageResponse list(@RequestBody String paramAndPaging, HttpServletRequest request) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(paramAndPaging)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
//            PageRequest pagingParam = null;
            BizMfrsParam pagingParam = null;
            try {
                String paramStr = URLDecoder.decode(paramAndPaging, "utf-8");
//                pagingParam = JSON.parseObject(paramStr, PageRequest.class);
                pagingParam = JSON.parseObject(paramStr,BizMfrsParam.class);
                if (null == pagingParam) {
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
                // 仅needPaging标志为false时，不需要分页，其他情况均需要进行分页
                if (WzStringUtil.isNotBlank(pagingParam.getNeedPaging()) && "false".equals(pagingParam.getNeedPaging().toLowerCase())) {
                    pagingParam.setNeedPaging("false");
                } else {
                    if (null == pagingParam.getCurrPage() || null == pagingParam.getPageSize()) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "未获得分页参数");
                    }
                    pagingParam.setNeedPaging("true");
                }
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR.code(), ex.getMessage(), ex);
            }
//            PageResponse<BizManufacturer> mfrsPageResp = bizManufacturerService.list(true, pagingParam);
            PageResponse<BizManufacturer> mfrsPageResp = bizManufacturerService.listByParam(Boolean.valueOf(pagingParam.getNeedPaging()), pagingParam);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, mfrsPageResp);
            return mr;
        }
    }

    /**
     * 批量增加制造商.
     * @param addParam 批量新增参数列表JSON
     * <pre>
     *     [
     *         {
     *             mfrsName:制造商名称,
     *             mfrsType:制造商类型（车辆VEHICLE、电池BATTERY、配件PARTS）
     *             mfrsIntroduce:制造商介绍,
     *             mfrsAddress:制造商地址,
     *             mfrsContacts:联系人（多人用 , 分割）,
     *             mfrsPhone:联系电话（多个电话用 , 分割）,
     *             mfrsStatus:制造商状态（正常NORMAL、冻结FREEZE、作废INVALID）
     *             createUser:创建人,
     *             updateUser:更新人
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
            List<BizManufacturer> mfrsInfos = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                mfrsInfos = JSON.parseArray(paramStr, BizManufacturer.class);
                if (null == mfrsInfos || 0 == mfrsInfos.size()) {
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
                BizManufacturer insMfrsVo = null;
                for (int i = 0; i < mfrsInfos.size(); i++) {
                    insMfrsVo = mfrsInfos.get(i);
                    if (WzStringUtil.isBlank(insMfrsVo.getMfrsName())
                            || null == insMfrsVo.getMfrsType()
                            || null == insMfrsVo.getMfrsStatus()
                            || WzStringUtil.isBlank(insMfrsVo.getCreateUser())
                            || WzStringUtil.isBlank(insMfrsVo.getUpdateUser())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "第" + i + "条记录制造商参数有误");
                    }
                    if (!insMfrsVo.getMfrsType().toString().equals(MfrsType.VEHICLE.toString())
                            && !insMfrsVo.getMfrsType().toString().equals(MfrsType.BATTERY.toString())
                            && !insMfrsVo.getMfrsType().toString().equals(MfrsType.PARTS.toString())) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "第" + i + "条记录制造商类别无效");
                    }
//                    if (!insMfrsVo.getMfrsStatus().toString().equals(RecordStatus.NORMAL.toString())
//                            && !insMfrsVo.getMfrsStatus().toString().equals(RecordStatus.FREEZE.toString())
//                            && !insMfrsVo.getMfrsStatus().toString().equals(RecordStatus.INVALID.toString())) {
//                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "第" + i + "条记录制造商状态无效");
//                    }
                    insMfrsVo.setMfrsStatus(RecordStatus.NORMAL);
                }
                bizManufacturerService.insertBizManufacturers(mfrsInfos);
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
     * 增加制造商信息.
     * @param addParam 批量新增参数JSON
     * <pre>
     *  {
     *              mfrsName:制造商名称,
     *              mfrsType:制造商类型（车辆VEHICLE、电池BATTERY、配件PARTS）
     *              mfrsIntroduce:制造商介绍,
     *              mfrsAddress:制造商地址,
     *              mfrsContacts:联系人（多人用 , 分割）,
     *              mfrsPhone:联系电话（多个电话用 , 分割）,
     *              mfrsStatus:制造商状态（正常NORMAL、冻结FREEZE、作废INVALID）
     *              createUser:创建人,
     *              updateUser:更新人
     *  }
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
            BizManufacturer mfrsInfo = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                mfrsInfo = JSON.parseObject(paramStr, BizManufacturer.class);
                if (null == mfrsInfo) {
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
                if (WzStringUtil.isBlank(mfrsInfo.getMfrsName())
                        || null == mfrsInfo.getMfrsType()
                        || null == mfrsInfo.getMfrsStatus()
                        || WzStringUtil.isBlank(mfrsInfo.getCreateUser())
                        || WzStringUtil.isBlank(mfrsInfo.getUpdateUser())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "制造商参数有误");
                }
                if (!mfrsInfo.getMfrsType().toString().equals(MfrsType.VEHICLE.toString())
                        && !mfrsInfo.getMfrsType().toString().equals(MfrsType.BATTERY.toString())
                        && !mfrsInfo.getMfrsType().toString().equals(MfrsType.PARTS.toString())) {
                    return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的制造商类别");
                }
//                if (!mfrsInfo.getMfrsStatus().toString().equals(RecordStatus.NORMAL.toString())
//                        && !mfrsInfo.getMfrsStatus().toString().equals(RecordStatus.FREEZE.toString())
//                       && !mfrsInfo.getMfrsStatus().toString().equals(RecordStatus.INVALID.toString())) {
//                   return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "无效的制造商状态");
//                }
                mfrsInfo.setMfrsStatus(RecordStatus.NORMAL);
                bizManufacturerService.insertBizManufacturers(mfrsInfo);
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
     * 修改制造商信息.
     * @param modifyParam 修改参数JSON
     * <pre>
     *     {
     *         id:ID,
     *         mfrsName:制造商名称,
     *         mfrsIntroduce:制造商介绍,
     *         mfrsAddress:制造商地址,
     *         mfrsContacts:联系人（多人用 , 分割）,
     *         mfrsPhone:联系电话（多个电话用 , 分割）,
     *         updateUser:更新人
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
            // 参数解析错误报“参数解析错误”
            BizManufacturer mfrs = null;
            try {
                String paramStr = URLDecoder.decode(modifyParam, "utf-8");
                mfrs = JSON.parseObject(paramStr, BizManufacturer.class);
                if (null == mfrs || WzStringUtil.isBlank(mfrs.getUpdateUser())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if (mfrs.getMfrsStatus() != null) {
                    if (!mfrs.getMfrsStatus().toString().equals(RecordStatus.NORMAL.toString())
                            && !mfrs.getMfrsStatus().toString().equals(RecordStatus.INVALID.toString())) {
                        return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(),"无效的制造商状态");
                    }
                }
                if (mfrs.getMfrsType() != null) {
                    if (!mfrs.getMfrsType().toString().equals(MfrsType.BATTERY.toString())
                            && !mfrs.getMfrsType().toString().equals(MfrsType.VEHICLE.toString())
                            && !mfrs.getMfrsType().toString().equals(MfrsType.PARTS.toString())) {
                        return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(),"无效的制造商类别");
                    }
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
                if (WzStringUtil.isBlank(mfrs.getId())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "无法确定需要修改的数据");
                }
                bizManufacturerService.updateBizManufacturer(mfrs);
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
     * 批量删除制造商.
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
            List<String> mfrsIds = null;
            try {
                String paramStr = URLDecoder.decode(deleteParam, "utf-8");
                mfrsIds = JSON.parseArray(paramStr, String.class);
                if (null == mfrsIds || 0 == mfrsIds.size()) {
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
                bizManufacturerService.deleteBizManufacturers(mfrsIds);
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
     * 根据id查询制造商信息.
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
     *         respData:{
     *             id:ID,
     *             mfrsName:制造商名称,
     *             mfrsType:制造商类型（车辆、电池、配件）,
     *             mfrsIntroduce:制造商介绍,
     *             mfrsAddress:制造商地址,
     *             mfrsContacts:联系人（多人用 , 分割）,
     *             mfrsPhone:联系电话（多个电话用 , 分割）,
     *             mfrsStatus:制造商状态（正常、冻结、作废）,
     *             createUser:创建人,
     *             createTime:创建时间,
     *             updateUser:更新人,
     *             updateTime:更新时间
     *         }
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
            List<String> mfrsId = null;
            try {
                String paramStr = URLDecoder.decode(id, "utf-8");
                mfrsId = JSON.parseArray(paramStr, String.class);
                if (null == mfrsId || 0 == mfrsId.size() || WzStringUtil.isBlank(mfrsId.get(0))) {
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
            } catch (BizException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }

            BizManufacturer mfrs = bizManufacturerService.getBizManufacturerByPrimaryKey(mfrsId.get(0));
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, mfrs);
            return mr;
        }
    }
}
