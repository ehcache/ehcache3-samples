package org.terracotta.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.terracotta.demo.ResourceCallReport;
import org.terracotta.demo.domain.Coordinates;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.Clock;

import static org.terracotta.demo.controller.StarsBirthdayWeatherController.getResourceCallReports;

/**
 * Created by Anthony Dahanne on 2016-09-23.
 */
@Service
public class CoordinatesService {

  @Value("${googleApiKey}")
  private String googleAPiKey;

//  @Autowired
//  private RestTemplate restTemplate;

  @Autowired
  private HttpService httpService;

  @Autowired
  private ObjectMapper objectMapper;

  public Coordinates retrieveCoordinates(String location) throws IOException {
    long retrieveStartTime = Clock.systemDefaultZone().millis();

    String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
        URLEncoder.encode(location, "UTF-8") +
        "&key=" + googleAPiKey;

      String responseAsString = "";
      try {
      responseAsString = httpService.sendGet(url);
      JsonNode locationNode = objectMapper.readTree(responseAsString).findValue("location");
      String latitude = locationNode.get("lat").asText();
      String longitude = locationNode.get("lng").asText();

      long retrieveEndTime = Clock.systemDefaultZone().millis();
      getResourceCallReports().add(new ResourceCallReport(location, retrieveEndTime - retrieveStartTime, this.getClass().getSimpleName() + ".retrieveCoordinates", ResourceCallReport.ResourceType.WEB_SERVICE, "Google Geocode Rest API"));

      return new Coordinates(latitude, longitude);
    } catch (Exception e) {
      throw new RuntimeException("Can't find coordinates for " + location + "\n" +
          "google response was : " + responseAsString, e);
    }

  }

}
