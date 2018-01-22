package com.elextec.framework;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.common.response.MessageResponse;
import com.elextec.framework.exceptions.BizException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理类.
 * Created by wangtao on 2018/1/19.
 */
@ControllerAdvice
public class WzControllerAdvice {
    /**
     * 全局业务异常处理.
     * @param ex BizException
     * @return 异常消息
     * <pre>
     *     {
     *         code:异常Code，
     *         message:异常消息,
     *         respData:异常堆栈
     *     }
     * </pre>
     */
    @ResponseBody
    @ExceptionHandler(value = BizException.class)
    public MessageResponse errorHandler(BizException ex) {
        MessageResponse mr = new MessageResponse();
        mr.setCode(ex.getInfoCode());
        mr.setMessage(ex.getMessage());
        mr.setRespData(ex.getStackTrace());
        return mr;
    }

    /**
     * 全局异常处理.
     * @param ex Exception
     * @return 异常消息
     * <pre>
     *     {
     *         code:500，
     *         message:异常消息,
     *         respData:异常堆栈
     *     }
     * </pre>
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public MessageResponse errorHandler(Exception ex) {
        MessageResponse mr = new MessageResponse();
        mr.setCode(RunningResult.SERVER_ERROR.code());
        mr.setMessage(ex.getMessage());
        mr.setRespData(ex.getStackTrace());
        return mr;
    }
}
