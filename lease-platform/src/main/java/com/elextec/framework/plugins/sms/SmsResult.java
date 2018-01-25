package com.elextec.framework.plugins.sms;

import com.elextec.framework.BaseModel;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 短信发送结果.
 * Created by wangtao on 2018/1/24.
 */
@XmlRootElement(name = "sms")
public class SmsResult extends BaseModel {
    /** 返回码及状态. */
    private SmsStatusAndMessage statusAndMessage;

    /*
     * Getter 和 Setter 方法.
     */
    @XmlElement(name = "mt")
    public SmsStatusAndMessage getStatusAndMessage() {
        return statusAndMessage;
    }

    public void setStatusAndMessage(SmsStatusAndMessage statusAndMessage) {
        this.statusAndMessage = statusAndMessage;
    }
}
