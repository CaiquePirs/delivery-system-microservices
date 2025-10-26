package com.deliverysistem.eureka_server_infra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerInfraApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerInfraApplication.class, args);
	}

}
