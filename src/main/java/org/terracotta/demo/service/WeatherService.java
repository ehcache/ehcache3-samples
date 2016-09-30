package org.terracotta.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.terracotta.demo.domain.Coordinates;
import org.terracotta.demo.service.dto.ResourceCallReport;
import org.terracotta.demo.service.dto.WeatherReport;
import org.terracotta.demo.service.util.HttpUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Stopwatch;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import javax.cache.annotation.CacheResult;
import javax.inject.Inject;

/**
 * Created by Anthony Dahanne on 2016-09-23.
 */
@Service
public class WeatherService {

    @Value("${demo.darkSkyApiKey}")
    private String darkSkyApiKey;

    @Inject
    private HttpService httpService;

    @Inject
    private CoordinatesService coordinatesService;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private ResourceCallService resourceCallService;

    @CacheResult(cacheName = "weatherReports")
    public WeatherReport retrieveWeatherReport(String location, LocalDate date) {

        // resource call report gathered in the retrieveCoordinates service
        Coordinates coordinates = coordinatesService.retrieveCoordinates(location);

        Stopwatch stopwatch = Stopwatch.createStarted();

        String url = "https://api.darksky.net/forecast/" +
                     darkSkyApiKey + "/" +
                     coordinates.getLatitude() +
                     HttpUtil.utf8Encode(",") +
                     coordinates.getLongitude() +
                     HttpUtil.utf8Encode(",") +
                     date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli() / 1000 + "?units=si";

        String responseAsString = "Never received";
        try {
            responseAsString = httpService.sendGet(url);
            JsonNode daily = objectMapper.readTree(responseAsString).findValue("daily");

            if(daily != null) {

                ArrayNode dataNode = (ArrayNode) daily.findValue("data");
                return new WeatherReport(
                    date, location,
                    dataNode.get(0).get("icon").asText(),
                    dataNode.get(0).get("summary").asText(),
                    dataNode.get(0).get("temperatureMin").asDouble(),
                    dataNode.get(0).get("temperatureMax").asDouble());
            }
            else {
                return new WeatherReport();
            }
        }
        catch(Exception e) {
            throw new RuntimeException("Can't find weather report for " + location + " for this date : " + date + "\n call to " +
                                       url + " with darkskyapi response was : " + responseAsString, e);
        }
        finally {
            resourceCallService.addCall("Darksky Rest API", ResourceCallReport.ResourceType.WEB_SERVICE, location + " on " + date, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

}
