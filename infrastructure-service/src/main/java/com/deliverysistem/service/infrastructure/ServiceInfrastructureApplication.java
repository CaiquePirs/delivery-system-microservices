package com.deliverysistem.service.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class ServiceInfrastructureApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceInfrastructureApplication.class, args);
	}

}
