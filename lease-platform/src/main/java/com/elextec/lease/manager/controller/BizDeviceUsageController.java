package com.elextec.lease.manager.controller;

import com.alibaba.fastjson.JSONObject;
import com.elextec.framework.BaseController;
import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.utils.WzStringUtil;
import com.elextec.lease.manager.request.BizDeviceUsageParam;
import com.elextec.lease.manager.service.BizDeviceUsageService;
import com.elextec.persist.field.enums.OrgAndUserType;
import com.elextec.persist.model.mybatis.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@RestController
@RequestMapping(value = "/manager/usage")
public class BizDeviceUsageController extends BaseController {

    @Autowired
    private BizDeviceUsageService bizDeviceUsageService;

    /**
     * 设备统计分页接口
     * <pre>
     *      {
     *          orgId:企业id
     *          deviceId：设备id
     *          startTime：起始时间
     *          endTime：结束时间
     *          needPaging:是否需要分页（仅为false时不需要分页，其余情况均需要分页）,
     *          currPage:当前页（needPaging不为false时必填）,
     *          pageSize:每页记录数（needPaging不为false时必填）
     *      }
     * </pre>
     * @param param
     * @param request
     * @return
     * <pre>
     *      {
     *         code:返回Code,
     *         message:返回消息,
     *         respData:{
     *             currPage:当前页
     *             pageSize:当前页显示数量
     *             row:[
     *                  {
     *                      deviceId:设备id，
     *                      recTime:插入时间
     *                      useDistance:使用时长
     *                      useDuration:使用距离
     *                  }，
     *                  ... ...
     *             ]
     *         }
     *     }
     * </pre>
     */
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public MessageResponse list(@RequestBody String param, HttpServletRequest request) {
        //参数不为空
        if (WzStringUtil.isBlank(param)) {
            return new MessageResponse(RunningResult.NO_PARAM);
        }
        BizDeviceUsageParam bizDeviceUsageParam = null;
        try {
            String paramStr = URLDecoder.decode(param,"utf-8");
            bizDeviceUsageParam = JSONObject.parseObject(paramStr,BizDeviceUsageParam.class);
            //参数解析
            if (bizDeviceUsageParam == null) {
                return new MessageResponse(RunningResult.SUCCESS);
            }
            //判断是否分页
            if (WzStringUtil.isNotBlank(bizDeviceUsageParam.getNeedPaging())
                    && bizDeviceUsageParam.getNeedPaging().toLowerCase().equals("false")) {
                bizDeviceUsageParam.setNeedPaging("false");
            } else {
                if (bizDeviceUsageParam.getPageSize() == null || bizDeviceUsageParam.getCurrPage() == null) {
                    return new MessageResponse(RunningResult.PARAM_ANALYZE_ERROR.code(),"未获得分页参数");
                }
                bizDeviceUsageParam.setNeedPaging("true");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //获取登录对象
        SysUser sysUser = super.getLoginUserInfo(request);
        if (sysUser == null) {
            return new MessageResponse(RunningResult.AUTH_OVER_TIME);
        }
        //判断用户类型
        if (sysUser.getUserType().toString().equals(OrgAndUserType.PLATFORM.toString())) {
            return new MessageResponse(RunningResult.SUCCESS,bizDeviceUsageService.list(Boolean.valueOf(bizDeviceUsageParam.getNeedPaging()), bizDeviceUsageParam));
        } else {
            return new MessageResponse(RunningResult.NO_PERMISSION);
        }
    }

}
