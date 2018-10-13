package org.ehcache.sample.web.rest;

import org.ehcache.sample.domain.Actor;
import org.ehcache.sample.repository.ActorRepository;
import org.ehcache.sample.service.ActorService;
import org.ehcache.sample.service.ResourceCallService;
import org.ehcache.sample.service.WeatherService;
import org.ehcache.sample.service.dto.ResourceType;
import org.ehcache.sample.service.dto.StarDTO;
import org.ehcache.sample.service.dto.WeatherReport;
import org.ehcache.sample.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Stopwatch;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import static java.net.InetAddress.*;

@RestController
@RequestMapping("/api")
public class StarResource {

    private static final String MONTREAL = "Montréal, Québec, Canada";
    private static final String PARIS = "Paris, Ile de France, France";
    private static final String MOSCOW = "Moscow, Russia";
    private static final String TOKYO = "Tokyo, Japan";

    private final Logger log = LoggerFactory.getLogger(StarResource.class);

    private final ActorService actorService;

    private final WeatherService weatherService;

    private final ResourceCallService resourceCallService;

    private String hostname;

    public StarResource(ActorService actorService, WeatherService weatherService, ResourceCallService resourceCallService) {
        this.actorService = actorService;
        this.weatherService = weatherService;
        this.resourceCallService = resourceCallService;
    }

    @PostConstruct
    public void init() throws UnknownHostException {
        hostname = getLocalHost().getHostName();
    }

    /**
     * GET  /stars : get all the stars.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of stars in body
     */
    @GetMapping("/stars")
    @Timed
    public ResponseEntity<List<Actor>> getAllStars(Pageable pageable) {
        log.debug("REST request to get a page of Stars");
        Page<Actor> page = actorService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/stars");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/stars/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StarDTO> starDetails(@PathVariable("id") long id) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        return actorService.findOne(id)
            .map(foundActor -> {

                resourceCallService.addCall("ActorRepository.findOne", ResourceType.DATABASE, Long.toString(id), stopwatch.elapsed(TimeUnit.MILLISECONDS));

                List<WeatherReport> weatherReports = new ArrayList<>(5);
                weatherReports.add(foundActor.getBirthLocation() == null ? new WeatherReport() : weatherService.retrieveWeatherReport(foundActor.getBirthLocation(), foundActor.getBirthDate()));
                weatherReports.add(weatherService.retrieveWeatherReport(MONTREAL, foundActor.getBirthDate()));
                weatherReports.add(weatherService.retrieveWeatherReport(PARIS, foundActor.getBirthDate()));
                weatherReports.add(weatherService.retrieveWeatherReport(MOSCOW, foundActor.getBirthDate()));
                weatherReports.add(weatherService.retrieveWeatherReport(TOKYO, foundActor.getBirthDate()));

                long sum = resourceCallService.currentElapsed();

                StarDTO actorAndWeatherAndCallReports =
                    new StarDTO(foundActor, weatherReports, resourceCallService.getReports(), sum, hostname);

                return new ResponseEntity<>(actorAndWeatherAndCallReports, HttpStatus.OK);
            })
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
