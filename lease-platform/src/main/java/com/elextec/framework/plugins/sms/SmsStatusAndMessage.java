package com.elextec.framework.plugins.sms;

import javax.xml.bind.annotation.XmlElement;

/**
 * 短信发送结果消息.
 * Created by wangtao on 2018/1/24.
 */
public class SmsStatusAndMessage {
    /** 状态值. */
    private String status;
    /** 消息id. */
    private String msgid;

    /*
     * 构造方法.
     */
    public SmsStatusAndMessage() {
    }

    public SmsStatusAndMessage(String status, String msgid) {
        this.status = status;
        this.msgid = msgid;
    }

    /*
     * Getter 和 Setter 方法.
     */
    @XmlElement(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @XmlElement(name = "msgid")
    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }
}
