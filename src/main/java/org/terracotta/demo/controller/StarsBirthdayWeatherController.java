package org.terracotta.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.terracotta.demo.ResourceCallReport;
import org.terracotta.demo.domain.Actor;
import org.terracotta.demo.domain.WeatherReport;
import org.terracotta.demo.repository.ActorRepository;
import org.terracotta.demo.service.WeatherService;

import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StarsBirthdayWeatherController {

  private static final String MONTREAL = "Montréal, Québec, Canada";
  private static final String PARIS = "Paris, Ile de France, France";
  private static final String MOSCOW = "Moscow, Russia";
  private static final String TOKYO = "Tokyo, Japan";

  // Returns the current thread's list of ResourceCallReports
  public static List<ResourceCallReport> getResourceCallReports() {
    return resourceCallReports.get();
  }
    static final ThreadLocal<List<ResourceCallReport>> resourceCallReports =
      new ThreadLocal<List<ResourceCallReport>>().withInitial(() -> new ArrayList());

  @Autowired
  ActorRepository actorRepository;

  @Autowired
  WeatherService weatherService;

  @PostMapping("/stars")
  public String actorsList(@ModelAttribute Actor actor, Model model) {
    long findStartTime = Clock.systemDefaultZone().millis();
    List<Actor> foundActors = actorRepository.findByLastNameIgnoreCase(actor.getLastName());
    long findEndTime = Clock.systemDefaultZone().millis();
    getResourceCallReports().add(new ResourceCallReport(actor.getLastName(), findEndTime - findStartTime, "ActorRepository.findByLastNameIgnoreCase", ResourceCallReport.ResourceType.DATABASE, "Actor table"));


    if (foundActors.isEmpty()) {
      throw new RuntimeException("No actors found with that name");
    }

    Map<Long, List<WeatherReport>> weatherReports = new HashMap<>();

    foundActors.forEach(currentActor -> {

      List<WeatherReport> weatherReportsForId = new ArrayList<>();
      weatherReportsForId.add(currentActor.getBirthLocation() == null ? new WeatherReport() : weatherService.retrieveWeatherReport(currentActor.getBirthLocation(), currentActor.getBirthDate()));
      weatherReportsForId.add(weatherService.retrieveWeatherReport(MONTREAL, currentActor.getBirthDate()));
      weatherReportsForId.add(weatherService.retrieveWeatherReport(PARIS, currentActor.getBirthDate()));
      weatherReportsForId.add(weatherService.retrieveWeatherReport(MOSCOW, currentActor.getBirthDate()));
      weatherReportsForId.add(weatherService.retrieveWeatherReport(TOKYO, currentActor.getBirthDate()));
      weatherReports.put(currentActor.getId(), weatherReportsForId);

    });

    model.addAttribute("actors", foundActors);
    model.addAttribute("weatherReports", weatherReports);
    model.addAttribute("resourceCallReports", getResourceCallReports());
    model.addAttribute("timeSpentTotal", getResourceCallReports().stream().mapToLong(resourceCallReport -> resourceCallReport.getTimeSpentMillis()).sum());

    resourceCallReports.remove();
    return "starsResult";
  }


  @GetMapping("/stars")
  public String starsForm(Model model) {
    model.addAttribute("actor", new Actor());
    return "stars";
  }


}
