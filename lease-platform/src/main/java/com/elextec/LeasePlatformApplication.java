package com.elextec;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan(basePackages = {"com.elextec.persist.dao.mybatis"})
@EnableTransactionManagement
@ServletComponentScan
public class LeasePlatformApplication {

        public static void main(String[] args) { SpringApplication.run(LeasePlatformApplication.class, args); }
}
