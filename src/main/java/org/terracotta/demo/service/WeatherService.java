package org.terracotta.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.terracotta.demo.ResourceCallReport;
import org.terracotta.demo.domain.Coordinates;
import org.terracotta.demo.domain.WeatherReport;

import javax.cache.annotation.CacheResult;
import java.net.URLEncoder;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.terracotta.demo.controller.StarsBirthdayWeatherController.getResourceCallReports;

/**
 * Created by Anthony Dahanne on 2016-09-23.
 */


@Service
public class WeatherService {

  @Value("${darkSkyApiKey}")
  private String darkSkyApiKey;

  @Autowired
  private HttpService httpService;

//  @Autowired
//  private RestTemplate restTemplate;

  @Autowired
  private CoordinatesService coordinatesService;

  @Autowired
  private ObjectMapper objectMapper;

  @CacheResult(cacheName = "weatherReports")
  public WeatherReport retrieveWeatherReport(String location, LocalDate date) {
    try {

      long retrieveStartTime = Clock.systemDefaultZone().millis();

      WeatherReport weatherReport;
      Coordinates coordinates = coordinatesService.retrieveCoordinates(location);
      String url = "https://api.darksky.net/forecast/" +
          darkSkyApiKey + "/" +
          coordinates.getLatitude() +
          URLEncoder.encode(",", "UTF-8") +
          coordinates.getLongitude() +
          URLEncoder.encode(",", "UTF-8") +
          date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli() / 1000 + "?units=si";

//    restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());


//      HttpHeaders headers = new HttpHeaders();
//      headers.setContentType(MediaType.APPLICATION_JSON);
//      headers.setAccept(new ArrayList() {{
//        add(MediaType.APPLICATION_JSON);
//      }});
//
//
//      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
//
//      HttpEntity<?> entity = new HttpEntity<>(headers);
//
//      HttpEntity<String> response = restTemplate.exchange(
//          builder.build().encode().toUri(),
//          HttpMethod.GET,
//          entity,
//          String.class);
//
//      String responseAsString = response.getBody();

//      String responseAsString = restTemplate.getForObject(url, String.class);
//      restTemplate.getForObject("http://requestb.in/14anvfh1", Object.class);

      String responseAsString = httpService.sendGet(url);
      JsonNode daily = objectMapper.readTree(responseAsString).findValue("daily");
      if (daily != null) {

        ArrayNode dataNode = (ArrayNode) daily.findValue("data");
        weatherReport = new WeatherReport(
            date, location,
            dataNode.get(0).get("icon").asText(),
            dataNode.get(0).get("summary").asText(),
            dataNode.get(0).get("temperatureMin").asDouble(),
            dataNode.get(0).get("temperatureMax").asDouble());
      } else {
        weatherReport = new WeatherReport();
      }

      long retrieveEndTime = Clock.systemDefaultZone().millis();
      getResourceCallReports().add(new ResourceCallReport(location, retrieveEndTime - retrieveStartTime, this.getClass().getSimpleName() + ".retrieveWeatherReport", ResourceCallReport.ResourceType.WEB_SERVICE, "Darksky Rest API"));
      return weatherReport;

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }


}
