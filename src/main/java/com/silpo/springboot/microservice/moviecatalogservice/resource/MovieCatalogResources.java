package com.silpo.springboot.microservice.moviecatalogservice.resource;

import com.silpo.springboot.microservice.moviecatalogservice.model.CatalogItem;
import com.silpo.springboot.microservice.moviecatalogservice.model.Movie;
import com.silpo.springboot.microservice.moviecatalogservice.model.Rating;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResources {

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        //Rest Template for rest call
        RestTemplate restTemplate = new RestTemplate();

        //Movie movie = restTemplate.getForObject("http://localhost:8082/movies/1234", Movie.class);
        //get all movie ids
        List<Rating> ratings = Arrays.asList(
                new Rating("7354", "4"),
            new Rating("46rf8", "3")
        );

        //for each movie id, call movie info service and get details
        //put all together
        return ratings.stream().map(rating -> {
            Movie movie = restTemplate.getForObject("http://localhost:8082/movies/"+rating.getMovieId(), Movie.class);
            return new CatalogItem(movie.getName(), "trans - " + userId, rating.getRating());
        }).collect(Collectors.toList());



        /*return Collections.singletonList(
                new CatalogItem("Transformers", "trans - " + userId, "4"));*/

    }
}
