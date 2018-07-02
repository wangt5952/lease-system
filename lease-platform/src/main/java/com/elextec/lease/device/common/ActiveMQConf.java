package com.elextec.lease.device.common;


import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Destination;

@Configuration
public class ActiveMQConf {
    @Bean
    public Destination sensorDataQueue(){
        return new ActiveMQQueue("xglt_sensor_data_queue");
    }
}
