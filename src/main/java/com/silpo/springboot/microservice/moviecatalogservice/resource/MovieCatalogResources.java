package com.silpo.springboot.microservice.moviecatalogservice.resource;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.silpo.springboot.microservice.moviecatalogservice.model.CatalogItem;
import com.silpo.springboot.microservice.moviecatalogservice.model.Movie;
import com.silpo.springboot.microservice.moviecatalogservice.model.Rating;
import com.silpo.springboot.microservice.moviecatalogservice.model.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResources {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webclientBuilder;

    @RequestMapping("resttemplate/{userId}")
    @HystrixCommand(fallbackMethod = "getFallbackCatalog",
            threadPoolKey = "getProductThreadPool",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "50"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "1000"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "10"),
                    @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "20000"),
                    @HystrixProperty(name = "metrics.rollingPercentile.timeInMilliseconds", value = "20000"),
                    @HystrixProperty(name = "metrics.healthSnapshot.intervalInMilliseconds", value = "5000"),
                    @HystrixProperty(name = "fallback.isolation.semaphore.maxConcurrentRequests", value = "100")
            },
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "30"),
                    @HystrixProperty(name = "maxQueueSize", value = "-1"),
            })
    //http://localhost:8081/catalog/resttemplate/user123
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        //get all movie ratings from APIs
        //calls parameterized constructor
        //UserRating userRating = restTemplate.getForObject("http://localhost:8083/ratingsdata/users/"+userId, UserRating.class);

        //using Eureka, application mapping name "rating-data" taken from application.properties of the app
        //works only with Eureka app running and @LoadBalanced in RestTemplate Bean.
        //this urls is cached so not calling Eureka for each call
        UserRating userRating = restTemplate.getForObject("http://rating-data/ratingsdata/users/"+userId, UserRating.class);


        //for each movie id, call movie info service and get details
        //put all together
        return userRating.getUserRatings().stream().map(rating -> {
            //Using RestTemplate, Spring is deprecating this
            //getting the data from movie-info-service
            //Movie movie = restTemplate.getForObject("http://localhost:8082/movies/"+rating.getMovieId(), Movie.class);

            //using Eureka, application mapping name "movie-info" taken from application.properties of the app
            //works only with Eureka app running and @LoadBalanced in RestTemplate Bean.
            //getting the data from movie-info-service
            Movie movie = restTemplate.getForObject("http://movie-info/movies/"+rating.getMovieId(), Movie.class);


            return new CatalogItem(movie.getName(), "trans - " + userId, rating.getRating());
        }).collect(Collectors.toList());

    }

    public List<CatalogItem> getFallbackCatalog(@PathVariable("userId") String userId) {
            return Arrays.asList(
                    new CatalogItem("No Movie", "", "0"));
    }

    @RequestMapping("webclient/{userId}")
    //http://localhost:8081/catalog/webclient/user123
    @HystrixCommand(fallbackMethod = "getFallbackCatalog")
    public List<CatalogItem> getCatalogUsingWebClient(@PathVariable("userId") String userId) {

        //get all movie ratings from APIs using WebClient
        UserRating userRating = webclientBuilder.build()
                .get()
                .uri("http://localhost:8083/ratingsdata/users/"+ userId)
                .retrieve()
                //empty container will notify once gets data
                .bodyToMono(UserRating.class)
                //waits until mono is getting the data
                .block();

        //for each movie id, call movie info service and get details
        //put all together
        return userRating.getUserRatings().stream().map(rating -> {
            //Using Webclient.Builder
            //RestTemplate suppose to be deprecated in the future,
            //So this should be used it is Reactive or asynchronous
            Movie movie = webclientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/movies/"+rating.getMovieId())
                    .retrieve()
                    //empty container will notify once gets data
                    .bodyToMono(Movie.class)
                    //waits until mono is getting the data
                    .block();

            return new CatalogItem(movie.getName(), "trans - " + userId, rating.getRating());
        }).collect(Collectors.toList());

    }

    @RequestMapping("hardcoded/{userId}")
    //http://localhost:8081/catalog/hardcoded/user123
    public List<CatalogItem> getCatalogHC(@PathVariable("userId") String userId) {

        //get all movie ratings hardcoded
        List<Rating> ratings = Arrays.asList(
                new Rating("7354", "4"),
                new Rating("46rf8", "3")
        );

        //for each movie id, call movie info service and get details
        //put all together
        return Collections.singletonList(
                new CatalogItem("Transformers", "trans - " + userId, "4"));

    }
}
