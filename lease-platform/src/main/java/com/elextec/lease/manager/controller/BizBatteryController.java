package com.elextec.lease.manager.controller;

import com.alibaba.fastjson.JSON;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.manager.request.BizBatteryParam;
import com.elextec.lease.manager.service.BizBatteryService;
import com.elextec.persist.model.mybatis.BizBattery;
import com.elextec.persist.model.mybatis.ext.BizBatteryExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.List;

/**
 * 电池管理Controller.
 * Created by wangtao on 2018/1/19.
 */
@RestController
@RequestMapping(path = "/manager/battery")
public class BizBatteryController extends BaseController {

    /** 日志. */
    private final Logger logger = LoggerFactory.getLogger(BizBatteryController.class);

    @Autowired
    private BizBatteryService bizBatteryService;

    /**
     * 查询电池
     * @param paramAndPaging 查询及分页参数JSON
     * <pre>
     *     {
     *         keyStr:查询关键字（非必填，模糊查询，可填写电池编号、电池货名、电池品牌、电池型号、电池参数、生产商ID、生产商名）,
     *         batteryStatus:电池状态（非必填，包括NORMAL、FREEZE、INVALID）,
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
     *                 batteryCode:电池编号,
     *                 batteryName:电池货名,
     *                 batteryBrand:电池品牌,
     *                 batteryPn:电池型号,
     *                 batteryParameters:电池参数,
     *                 mfrsId:生产商ID,
     *                 batteryStatus:电池状态（正常、冻结、作废）,
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
            BizBatteryParam pagingParam = null;
            try {
                String paramStr = URLDecoder.decode(paramAndPaging, "utf-8");
//                pagingParam = JSON.parseObject(paramStr, PageRequest.class);
                pagingParam = JSON.parseObject(paramStr, BizBatteryParam.class);
                if (null == pagingParam) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                // 仅needPaging标志为false时，不需要分页，其他情况均需要进行分页
                if (WzStringUtil.isNotBlank(pagingParam.getNeedPaging())
                        && "false".equals(pagingParam.getNeedPaging().toLowerCase())) {
                    pagingParam.setNeedPaging("false");
                } else {
                    if (null == pagingParam.getCurrPage() || null == pagingParam.getPageSize()) {
                        return new MessageResponse(RunningResult.PARAM_VERIFY_ERROR.code(), "需要分页参数");
                    }
                    pagingParam.setNeedPaging("true");
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
//            PageResponse<BizBattery> batteryPageResp = bizBatteryService.list(Boolean.valueOf(pagingParam.getNeedPaging()), pagingParam);
            PageResponse<BizBatteryExt> batteryPageResp = bizBatteryService.listExt(Boolean.valueOf(pagingParam.getNeedPaging()), pagingParam);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, batteryPageResp);
            return mr;
        }
    }

    /**
     * 批量增加电池.
     * @param addParam 批量新增参数列表JSON
     * <pre>
     *     [
     *         {
     *              batteryCode:电池编号,
     *              batteryName:电池货名,
     *              batteryBrand:电池品牌,
     *              batteryPn:电池型号,
     *              batteryParameters:电池参数,
     *              mfrsId:生产商ID,
     *              batteryStatus:电池状态（正常、冻结、作废）,
     *              createUser:创建人,
     *              updateUser:更新人
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
            List<BizBattery> batteryInfos = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                batteryInfos = JSON.parseArray(paramStr, BizBattery.class);
                if (null == batteryInfos || 0 == batteryInfos.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizBatteryService.insertBatterys(batteryInfos);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 增加电池.
     * @param addParam 新增参数列表JSON
     * <pre>
     *     {
     *         batteryCode:电池编号,
     *         batteryName:电池货名,
     *         batteryBrand:电池品牌,
     *         batteryPn:电池型号,
     *         batteryPrameters:电池参数,
     *         mfrsId:生产商ID,
     *         batteryStatus:电池状态（正常、冻结、作废）,
     *         createUser:创建人,
     *         updateUser:更新人
     *     }
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
    public MessageResponse addOne(@RequestBody String addParam) {
        // 无参数则报“无参数”
        if (WzStringUtil.isBlank(addParam)) {
            MessageResponse mr = new MessageResponse(RunningResult.NO_PARAM);
            return mr;
        } else {
            // 参数解析错误报“参数解析错误”
            BizBattery batteryInfo = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                batteryInfo = JSON.parseObject(paramStr, BizBattery.class);
                if (null == batteryInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizBatteryService.insertBattery(batteryInfo);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 修改电池信息.
     * @param modifyParam 修改参数JSON
     * <pre>
     *     {
     *         id:ID（必填）,
     *         batteryCode:电池编号,
     *         batteryName:电池货名,
     *         batteryBrand:电池品牌,
     *         batteryPn:电池型号,
     *         batteryParameters:电池参数,
     *         mfrsId:生产商ID,
     *         batteryStatus:电池状态（正常、冻结、作废）,
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
            BizBattery batteryInfo = null;
            try {
                String paramStr = URLDecoder.decode(modifyParam, "utf-8");
                batteryInfo = JSON.parseObject(paramStr, BizBattery.class);
                if (null == batteryInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
                if (WzStringUtil.isBlank(batteryInfo.getId())) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(), "无法确定待修改的记录");
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizBatteryService.updateBattery(batteryInfo);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 批量删除电池.
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
            List<String> batteryIds = null;
            try {
                String paramStr = URLDecoder.decode(deleteParam, "utf-8");
                batteryIds = JSON.parseArray(paramStr, String.class);
                if (null == batteryIds || 0 == batteryIds.size()) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizBatteryService.deleteBattery(batteryIds);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS);
            return mr;
        }
    }

    /**
     * 根据ID获取电池信息.
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
     *             id:ID,
     *             batteryCode:电池编号,
     *             batteryName:电池货名,
     *             batteryBrand:电池品牌,
     *             batteryPn:电池型号,
     *             batteryParameters:电池参数,
     *             mfrsId:生产商ID,
     *             batteryStatus:电池状态（正常、冻结、作废）,
     *             createUser:创建人,
     *             createTime:创建时间,
     *             updateUser:更新人,
     *             updateTime:更新时间
     *         }
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
            List<String> batteryId = null;
            try {
                String paramStr = URLDecoder.decode(id, "utf-8");
                batteryId = JSON.parseArray(paramStr, String.class);
                if (null == batteryId || 0 == batteryId.size() || WzStringUtil.isBlank(batteryId.get(0))) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            BizBattery battery = bizBatteryService.getBatteryByPrimaryKey(batteryId.get(0));
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS,battery);
            return mr;
        }
    }

}
