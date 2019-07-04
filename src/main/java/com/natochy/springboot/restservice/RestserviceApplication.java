// bootstrapped using https://start.spring.io
// https://www.springboottutorial.com/creating-rest-service-with-spring-boot
package com.natochy.springboot.restservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.natochy.springboot")
public class RestserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestserviceApplication.class, args);
	}

}
