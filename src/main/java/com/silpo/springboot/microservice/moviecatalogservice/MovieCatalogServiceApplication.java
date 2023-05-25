package com.silpo.springboot.microservice.moviecatalogservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
@EnableHystrixDashboard
public class MovieCatalogServiceApplication {

	//https://github.com/koushikkothagal/spring-boot-microservices-workshop/

	//Rest Template for rest call create a bean out of it
	//Beas in spring are Singleton
	@Bean
	@LoadBalanced
	public RestTemplate getRestTemplate(){
		//return new RestTemplate(); //First leaning course 1

		/*
		Microservice level 2 : Fault tolerance and resilience
		If a micro-service call taking too much time, this thread is stuck
		as a cascading effect other thread also may starve
		solution -
			1. Increase the resource, like PermFinder
			2. Setting time out for the request, below is an example 3 sec time out for the RestTemplate
		 */
		/*HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setConnectTimeout(3000);  //setting time out of 3000 ms == 3 sec
		return new RestTemplate(clientHttpRequestFactory);*/
		return new RestTemplate();

	}

	//@Autowired
	//private DiscoveryClient discoveryClient; //use getInstance and port, etc. then can do adv load balancing

	//Used in Spring Reactive programing or Asynchronous
	@Bean
	public WebClient.Builder getWebClientBuilder(){
		return WebClient.builder();

	}

	public static void main(String[] args) {
		SpringApplication.run(MovieCatalogServiceApplication.class, args);
	}

}
