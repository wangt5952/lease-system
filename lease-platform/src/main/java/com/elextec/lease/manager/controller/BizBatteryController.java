package com.elextec.lease.manager.controller;

import com.alibaba.fastjson.JSON;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.plugins.paging.PageRequest;
import com.elextec.framework.plugins.paging.PageResponse;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.manager.service.BizBatteryService;
import com.elextec.persist.model.mybatis.BizBattery;
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
     *                 battery_code:电池编号,
     *                 battery_name:电池货名,
     *                 battery_brand:电池品牌,
     *                 battery_pn:电池型号,
     *                 battery_parameters:电池参数,
     *                 mfrs_id:生产商ID,
     *                 battery_status:电池状态（正常、冻结、作废）,
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
            PageResponse<BizBattery> resPageResp = bizBatteryService.list(true, pagingParam);
            // 组织返回结果并返回
            MessageResponse mr = new MessageResponse(RunningResult.SUCCESS, resPageResp);
            return mr;
        }
    }

    /**
     * 批量增加电池.
     * @param addParam 批量新增参数列表JSON
     * <pre>
     *     [
     *         {
     *              battery_code:电池编号,
     *              battery_name:电池货名,
     *              battery_brand:电池品牌,
     *              battery_pn:电池型号,
     *              battery_parameters:电池参数,
     *              mfrs_id:生产商ID,
     *              battery_status:电池状态（正常、冻结、作废）,
     *              create_user:创建人,
     *              update_user:更新人
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
            List<BizBattery> resInfos = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                resInfos = JSON.parseArray(paramStr, BizBattery.class);
                if (null == resInfos) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizBatteryService.insertBatterys(resInfos);
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
     *         battery_code:电池编号,
     *         battery_name:电池货名,
     *         battery_brand:电池品牌,
     *         battery_pn:电池型号,
     *         battery_parameters:电池参数,
     *         mfrs_id:生产商ID,
     *         battery_status:电池状态（正常、冻结、作废）,
     *         create_user:创建人,
     *         update_user:更新人
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
            BizBattery resInfo = null;
            try {
                String paramStr = URLDecoder.decode(addParam, "utf-8");
                resInfo = JSON.parseObject(paramStr, BizBattery.class);
                if (null == resInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizBatteryService.insertBattery(resInfo);
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
     *         battery_code:电池编号,
     *         battery_name:电池货名,
     *         battery_brand:电池品牌,
     *         battery_pn:电池型号,
     *         battery_parameters:电池参数,
     *         mfrs_id:生产商ID,
     *         battery_status:电池状态（正常、冻结、作废）,
     *         update_user:更新人
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
            BizBattery resInfo = null;
            try {
                String paramStr = URLDecoder.decode(modifyParam, "utf-8");
                resInfo = JSON.parseObject(paramStr, BizBattery.class);
                if (null == resInfo) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizBatteryService.updateBattery(resInfo);
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
            List<String> resIds = null;
            try {
                String paramStr = URLDecoder.decode(deleteParam, "utf-8");
                resIds = JSON.parseArray(paramStr, String.class);
                if (null == resIds) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR);
                }
            } catch (Exception ex) {
                throw new BizException(RunningResult.PARAM_ANALYZE_ERROR, ex);
            }
            bizBatteryService.deleteBattery(resIds);
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
     *                 id:ID,
     *                 login_name:用户名,
     *                 user_mobile:用户手机号码,
     *                 user_type:用户类型（平台、企业或个人）,
     *                 user_icon:用户LOGO路径,
     *                 password:密码,
     *                 nick_name:昵称,
     *                 user_name:姓名,
     *                 user_real_name_auth_flag:用户实名认证标志（已实名、未实名）,
     *                 user_pid:身份证号,
     *                 user_ic_front:身份证正面照片路径,
     *                 user_ic_back:身份证背面照片路径,
     *                 user_ic_group:用户手举身份证合照路径,
     *                 org_id:所属组织ID,
     *                 user_status:用户状态（正常、冻结、作废）,
     *                 create_user:创建人,
     *                 create_time:创建时间,
     *                 update_user:更新人,
     *                 update_time:更新时间
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
