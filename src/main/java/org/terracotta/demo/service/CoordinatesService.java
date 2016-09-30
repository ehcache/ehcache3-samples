package org.terracotta.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.terracotta.demo.domain.Coordinates;
import org.terracotta.demo.service.dto.ResourceCallReport;
import org.terracotta.demo.service.util.HttpUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * Created by Anthony Dahanne on 2016-09-23.
 */
@Service
public class CoordinatesService {

    @Value("${demo.googleApiKey}")
    private String googleAPiKey;

    @Inject
    private HttpService httpService;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private ResourceCallService resourceCallService;

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
            resourceCallService.addCall("Google Geocode Rest API", ResourceCallReport.ResourceType.WEB_SERVICE, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }

    }

}
