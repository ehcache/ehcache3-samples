package org.terracotta.demo.service.dto;

import org.terracotta.demo.domain.Actor;

import java.util.List;

/**
 * Created by Anthony Dahanne on 2016-09-27.
 */
public class StarDTO {
    private Actor actor;
    private List<WeatherReport> weatherReports;
    private List<ResourceCallReport> resourceCallReports;
    private long totalTimeSpent;
    private String hostname;

    public StarDTO(Actor actor, List<WeatherReport> weatherReports, List<ResourceCallReport> resourceCallReports, long totalTimeSpent, String hostname) {
        this.actor = actor;
        this.weatherReports = weatherReports;
        this.resourceCallReports = resourceCallReports;
        this.totalTimeSpent = totalTimeSpent;
        this.hostname = hostname;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public List<WeatherReport> getWeatherReports() {
        return weatherReports;
    }

    public void setWeatherReports(List<WeatherReport> weatherReports) {
        this.weatherReports = weatherReports;
    }

    public List<ResourceCallReport> getResourceCallReports() {
        return resourceCallReports;
    }

    public void setResourceCallReports(List<ResourceCallReport> resourceCallReports) {
        this.resourceCallReports = resourceCallReports;
    }

    public long getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalTimeSpent(long totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String toString() {
        return "StarDTO{" +
            "actor=" + actor +
            ", weatherReports=" + weatherReports +
            ", resourceCallReports=" + resourceCallReports +
            ", totalTimeSpent=" + totalTimeSpent +
            ", hostname='" + hostname + '\'' +
            '}';
    }
}
