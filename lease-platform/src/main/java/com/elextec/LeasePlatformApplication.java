package com.elextec;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.jms.ConnectionFactory;

@SpringBootApplication
@MapperScan(basePackages = {"com.elextec.persist.dao.mybatis"})
@EnableTransactionManagement
@ServletComponentScan
@EnableJms
@ComponentScan
@EnableAutoConfiguration
@EnableScheduling
@Configuration
public class LeasePlatformApplication {
        public static void main(String[] args) {SpringApplication.run(LeasePlatformApplication.class, args);}
}
