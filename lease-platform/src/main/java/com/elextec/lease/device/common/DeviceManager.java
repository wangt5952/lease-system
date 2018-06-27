package com.elextec.lease.device.common;

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

    /**
     * 及时发送
     * @param destination
     * @param data
     */
    public void send(Destination destination, String data){
        this.jmsMessagingTemplate.convertAndSend(destination,data);
    }

}
