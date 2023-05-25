


Building Spring MVC steps:
    1. Create a Controller @RestController
    2. Define a Mapping with path
        @RequestMapping("/catalog") - class level
        @RequestMapping("resttemplate/{userId}") - method level
    3. Define RestTemplate - 
        @Autowired
        private RestTemplate restTemplate;
        or WebClient
    4. get the data from another rest call or build the new object, passing the class instance
        restTemplate.getForObject("http://movie-info/movies/"+rating.getMovieId(), Movie.class);
    5. Spring framework will take care of the response in JSON format

Adding Webflux - 
    1. non blocking service call in async mode
            UserRating userRating = webclientBuilder.build()
            .get()
            .uri("<service endpoint)
            .retrieve()
            //empty container will notify once gets data
            .bodyToMono(UserRating.class)
            //waits until mono is getting the data
            .block(); //blocking means synchronous


Adding Eureka for Autodiscovery works as Broker
    1. Add spring-cloud-starter-netflix-eureka-client
    2. Add spring-cloud-starter-netflix-eureka-server
    3. Server should be running in a different port like
            http://localhost:8761/
    4. 

Adding Hystrix to SpringBoot microservice:
    1. Add the Maven spring-cloud-starter-netflix-hystrix dependency
    2. Add @EnableCircuitBreaker to application class
    3. Add @HystrixCommand to methods that need circuit breakers
    4. Configure Hystrix behaviour

Using Hystrix for Bulkhead pattern:
    1. Grouping thread pool for a microservice to restrict propagation of failure -
        Like ship bottom chambers
    2. threadPoolProperties = {
        @HystrixProperty(name = "coreSize", value = "30"),
        @HystrixProperty(name = "maxQueueSize", value = "-1")}

    