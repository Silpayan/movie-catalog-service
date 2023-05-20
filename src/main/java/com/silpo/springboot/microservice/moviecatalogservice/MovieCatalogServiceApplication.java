package com.silpo.springboot.microservice.moviecatalogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class MovieCatalogServiceApplication {

	//Rest Template for rest call create a bean out of it
	//Beas in spring are Singleton
	@Bean
	public RestTemplate getRestTemplate(){
		return new RestTemplate();
	}

	//Used in Spring Reactive programing or Asynchronous
	@Bean
	public WebClient.Builder getWebClientBuilder(){
		return WebClient.builder();

	}

	public static void main(String[] args) {
		SpringApplication.run(MovieCatalogServiceApplication.class, args);
	}

}
