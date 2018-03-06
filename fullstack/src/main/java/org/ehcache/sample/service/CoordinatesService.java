package org.ehcache.sample.service;

import org.ehcache.sample.domain.Coordinates;
import org.ehcache.sample.service.dto.ResourceCallReport;
import org.ehcache.sample.service.dto.ResourceType;
import org.ehcache.sample.service.util.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * Created by Anthony Dahanne on 2016-09-23.
 */
@Service
public class CoordinatesService {

    @Value("${application.googleApiKey}")
    private String googleAPiKey;

    private final HttpService httpService;

    private final ObjectMapper objectMapper;

    private final ResourceCallService resourceCallService;

    public CoordinatesService(HttpService httpService, ObjectMapper objectMapper, ResourceCallService resourceCallService) {
        this.httpService = httpService;
        this.objectMapper = objectMapper;
        this.resourceCallService = resourceCallService;
    }

    public Coordinates retrieveCoordinates(String location) {

        Stopwatch stopwatch = Stopwatch.createStarted();

        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                     HttpUtil.utf8Encode(location) +
                     "&key=" + googleAPiKey;

        String responseAsString = "Not received";
        try {
            responseAsString = httpService.sendGet(url);
            JsonNode locationNode = objectMapper.readTree(responseAsString).findValue("location");
            if(locationNode != null) {
                String latitude = locationNode.get("lat").asText();
                String longitude = locationNode.get("lng").asText();

                return new Coordinates(latitude, longitude);
            }
            else {
                return new Coordinates("0", "0");
            }
        }
        catch(Exception e) {
            throw new RuntimeException("Can't find coordinates for " + location + "\n call to " + url + " with google response : " + responseAsString, e);
        }
        finally {
            resourceCallService.addCall("Google Geocode Rest API", ResourceType.WEB_SERVICE, location, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }

    }

}
