package org.ehcache.sample.service;

import org.ehcache.sample.config.ApplicationProperties;
import org.ehcache.sample.domain.Coordinates;
import org.ehcache.sample.service.dto.ResourceCallReport;
import org.ehcache.sample.service.dto.ResourceType;
import org.ehcache.sample.service.dto.WeatherReport;
import org.ehcache.sample.service.util.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Stopwatch;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.cache.annotation.CacheResult;

/**
 * Created by Anthony Dahanne on 2016-09-23.
 */
@Service
public class WeatherService {

    private final boolean stubWebServices;

    private final String darkSkyApiKey;

    private final HttpService httpService;

    private final CoordinatesService coordinatesService;

    private final ObjectMapper objectMapper;

    private final ResourceCallService resourceCallService;

    public WeatherService(HttpService httpService, CoordinatesService coordinatesService, ObjectMapper objectMapper, ResourceCallService resourceCallService, ApplicationProperties applicationProperties) {
        this.httpService = httpService;
        this.coordinatesService = coordinatesService;
        this.objectMapper = objectMapper;
        this.resourceCallService = resourceCallService;
        this.darkSkyApiKey = applicationProperties.getDarkSkyApiKey();
        this.stubWebServices = applicationProperties.isStubWebServices();
    }

    @CacheResult(cacheName = "weatherReports")
    public WeatherReport retrieveWeatherReport(String location, LocalDate date) {

        if (stubWebServices) {
            return getFakeWeatherReport(location, date);
        }

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
                return new WeatherReport(date, location, "unknown", "Unknown.", Double.NaN, Double.NaN);
            }
        }
        catch(Exception e) {
            throw new RuntimeException("Can't find weather report for " + location + " for this date : " + date + "\n call to " +
                                       url + " with darkskyapi response was : " + responseAsString, e);
        }
        finally {
            resourceCallService.addCall("Darksky Rest API", ResourceType.WEB_SERVICE, location + " on " + date, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    private static final String[] WEATHERS = {
        "Cloudy",
        "Sunny",
        "Rainy",
        "Snowy"
    };

    private WeatherReport getFakeWeatherReport(String location, LocalDate date) {
        Random rand = ThreadLocalRandom.current();
        try {
            Thread.sleep(500 + rand.nextInt(1000));
        } catch (InterruptedException e) {
            // ignore
        }
        int min = -30 + rand.nextInt(60);
        int max = min + rand.nextInt(15);
        String summary = WEATHERS[rand.nextInt(WEATHERS.length)];
        String icon = summary.toLowerCase(Locale.US);
        return new WeatherReport(date, location, summary, icon, min, max);
    }
}
