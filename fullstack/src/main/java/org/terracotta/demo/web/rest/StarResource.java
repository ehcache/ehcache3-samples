package org.terracotta.demo.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.terracotta.demo.domain.Actor;
import org.terracotta.demo.repository.ActorRepository;
import org.terracotta.demo.service.ResourceCallService;
import org.terracotta.demo.service.WeatherService;
import org.terracotta.demo.service.dto.StarDTO;
import org.terracotta.demo.service.dto.ResourceCallReport;
import org.terracotta.demo.service.dto.WeatherReport;

import com.google.common.base.Stopwatch;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static java.net.InetAddress.*;

@RestController
@RequestMapping("/api")
public class StarResource {

    private static final String MONTREAL = "Montréal, Québec, Canada";
    private static final String PARIS = "Paris, Ile de France, France";
    private static final String MOSCOW = "Moscow, Russia";
    private static final String TOKYO = "Tokyo, Japan";

    @Inject
    ActorRepository actorRepository;

    @Inject
    WeatherService weatherService;

    @Inject
    ResourceCallService resourceCallService;

    private String hostname;

    @PostConstruct
    public void init() throws UnknownHostException {
        hostname = getLocalHost().getHostName();
    }

    @RequestMapping(value = "/stars/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StarDTO> startDetails(@PathVariable("id") long id) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        Actor foundActor = actorRepository.findOne(id);
        if(foundActor == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        resourceCallService.addCall("ActorRepository.findOne", ResourceCallReport.ResourceType.DATABASE, Long.toString(id), stopwatch.elapsed(TimeUnit.MILLISECONDS));

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
    }
}
