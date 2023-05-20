package com.silpo.springboot.microservice.moviecatalogservice.resource;

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
    //http://localhost:8081/catalog/resttemplate/user123
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        //get all movie ratings from APIs
        UserRating userRating = restTemplate.getForObject("http://localhost:8083/ratingsdata/users/"+userId, UserRating.class); //calls parameterized constructor

        //for each movie id, call movie info service and get details
        //put all together
        return userRating.getUserRatings().stream().map(rating -> {
            //Using RestTemplate, Spring is deprecating this
            Movie movie = restTemplate.getForObject("http://localhost:8082/movies/"+rating.getMovieId(), Movie.class); //getting the data from movie-info-service

            return new CatalogItem(movie.getName(), "trans - " + userId, rating.getRating());
        }).collect(Collectors.toList());

    }

    @RequestMapping("webclient/{userId}")
    //http://localhost:8081/catalog/webclient/user123
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
