package com.elextec;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.elextec.lease.manager.persist.dao.mybatis"})
public class LeaseManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeaseManagerApplication.class, args);
	}
}
