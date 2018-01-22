package com.elextec.framework.common.response;

import com.elextec.framework.BaseModel;
import com.elextec.framework.common.constants.RunningResult;

/**
 * 请求返回对象.
 * Created by wangtao on 2018/1/16.
 */
public class MessageResponse extends BaseModel {
    /** 响应Code. */
    private String code;
    /** 响应消息 */
    private String message;
    /** 响应结果对象. */
    private Object respData;

    /*
     * 构造方法.
     */
    public MessageResponse() {
        this.code = "";
        this.message = "";
        this.respData = "";
    }

    public MessageResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.respData = "";
    }

    public MessageResponse(RunningResult runningResult) {
        this.code = runningResult.code();
        this.message = runningResult.getInfo();
        this.respData = "";
    }

    public MessageResponse(String code, String message, Object respData) {
        this.code = code;
        this.message = message;
        this.respData = respData;
    }

    public MessageResponse(RunningResult runningResult, Object respData) {
        this.code = runningResult.code();
        this.message = runningResult.getInfo();
        this.respData = respData;
    }

    /*
     * Getter 和 Setter 方法.
     */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getRespData() {
        return respData;
    }

    public void setRespData(Object respData) {
        this.respData = respData;
    }
}
