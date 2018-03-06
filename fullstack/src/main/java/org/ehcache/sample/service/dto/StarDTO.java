package org.ehcache.sample.service.dto;

import org.ehcache.sample.domain.Actor;

import com.google.common.base.MoreObjects;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by Anthony Dahanne on 2016-09-27.
 */
public class StarDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String birthLocation;

    private List<WeatherReport> weatherReports;
    private List<ResourceCallReport> resourceCallReports;
    private long totalTimeSpent;
    private String hostname;

    public StarDTO(Actor actor, List<WeatherReport> weatherReports, List<ResourceCallReport> resourceCallReports, long totalTimeSpent, String hostname) {
        this.id = actor.getId();
        this.firstName = actor.getFirstName();
        this.lastName = actor.getLastName();
        this.birthDate = actor.getBirthDate();
        this.birthLocation = actor.getBirthLocation();
        this.weatherReports = weatherReports;
        this.resourceCallReports = resourceCallReports;
        this.totalTimeSpent = totalTimeSpent;
        this.hostname = hostname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthLocation() {
        return birthLocation;
    }

    public void setBirthLocation(String birthLocation) {
        this.birthLocation = birthLocation;
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
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("firstName", firstName)
            .add("lastName", lastName)
            .add("birthDate", birthDate)
            .add("birthLocation", birthLocation)
            .add("weatherReports", weatherReports)
            .add("resourceCallReports", resourceCallReports)
            .add("totalTimeSpent", totalTimeSpent)
            .add("hostname", hostname)
            .toString();
    }
}
