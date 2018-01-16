package com.elextec.framework.exceptions;

/**
 * 自定义业务异常.
 * Service及Controller应将Exception包装成BizException后再向上抛出
 * Created by wangtao on 2018/1/16.
 */
public class BizException extends Exception {
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
}
