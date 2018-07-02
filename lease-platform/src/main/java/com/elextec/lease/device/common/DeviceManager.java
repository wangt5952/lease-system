package com.elextec.lease.device.common;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Destination;

/**
 * 对ActiveMq进行封装
 */
@Component
public class DeviceManager {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private Destination sensorDataQueue;

    /**
     * 及时发送.
     * @param data 数据
     */
    public void send(String data){
        this.jmsMessagingTemplate.convertAndSend(sensorDataQueue, data);
    }
}
