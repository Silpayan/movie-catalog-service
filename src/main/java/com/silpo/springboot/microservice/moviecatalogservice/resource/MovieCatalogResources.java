package com.silpo.springboot.microservice.moviecatalogservice.resource;

import com.silpo.springboot.microservice.moviecatalogservice.model.CatalogItem;
import com.silpo.springboot.microservice.moviecatalogservice.model.Movie;
import com.silpo.springboot.microservice.moviecatalogservice.model.Rating;
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

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        //Movie movie = restTemplate.getForObject("http://localhost:8082/movies/1234", Movie.class);
        //get all movie ids
        List<Rating> ratings = Arrays.asList(
                new Rating("7354", "4"),
                new Rating("46rf8", "3")
        );

        //for each movie id, call movie info service and get details
        //put all together
        return ratings.stream().map(rating -> {
            //Using RestTemplate
            //Deprecating this
            Movie movie = restTemplate.getForObject("http://localhost:8082/movies/"+rating.getMovieId(), Movie.class); //getting the data from movie-info-service

            //Using Webclient.Builder
            //RestTemplate suppose to be deprecated in the future,
            //So this should be used it is Reactive or asynchronous
            /*Movie movie = webclientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/movies/"+rating.getMovieId())
                    .retrieve()
                    //empty container will notify once gets data
                    .bodyToMono(Movie.class)
                    //waits until mono is getting the data
                    .block();*/



            return new CatalogItem(movie.getName(), "trans - " + userId, rating.getRating());
        }).collect(Collectors.toList());



        /*return Collections.singletonList(
                new CatalogItem("Transformers", "trans - " + userId, "4"));*/

    }
}
