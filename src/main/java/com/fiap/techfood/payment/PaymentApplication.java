package com.fiap.techfood.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class PaymentApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(PaymentApplication.class, args);
	}

}
