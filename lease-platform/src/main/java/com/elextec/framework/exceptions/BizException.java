package com.elextec.framework.exceptions;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.utils.WzStringUtil;

/**
 * 自定义业务异常.
 * Service及Controller应将Exception包装成BizException后再向上抛出
 * Created by wangtao on 2018/1/16.
 */
public class BizException extends RuntimeException {
    private String infoCode;

    public BizException() {
        super();
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String code, String message) {
        super(message);
        this.infoCode = code;
    }

    public BizException(String code, String message, Throwable cause) {
        super(message, cause);
        this.infoCode = code;
    }

    public BizException(RunningResult runningResult) {
        super(runningResult.getInfo());
        this.infoCode = runningResult.code();
    }

    public BizException(RunningResult runningResult, Throwable cause) {
        super(runningResult.getInfo(), cause);
        this.infoCode = runningResult.code();
    }

    public String getInfoCode() {
        if (WzStringUtil.isBlank(infoCode)) {
            return "";
        } else {
            return infoCode;
        }
    }
}
