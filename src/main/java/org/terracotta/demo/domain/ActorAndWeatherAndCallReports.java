package org.terracotta.demo.domain;

import org.terracotta.demo.ResourceCallReport;

import java.util.List;

/**
 * Created by Anthony Dahanne on 2016-09-27.
 */
public class ActorAndWeatherAndCallReports {
    private Actor actor;
    private List<WeatherReport> weatherReports;
    private List<ResourceCallReport> resourceCallReports;
    private long totalTimeSpent;

    public ActorAndWeatherAndCallReports(Actor actor, List<WeatherReport> weatherReports, List<ResourceCallReport> resourceCallReports, long totalTimeSpent) {
        this.actor = actor;
        this.weatherReports = weatherReports;
        this.resourceCallReports = resourceCallReports;
        this.totalTimeSpent = totalTimeSpent;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActorAndWeatherAndCallReports that = (ActorAndWeatherAndCallReports) o;

        if (totalTimeSpent != that.totalTimeSpent) return false;
        if (actor != null ? !actor.equals(that.actor) : that.actor != null) return false;
        if (weatherReports != null ? !weatherReports.equals(that.weatherReports) : that.weatherReports != null)
            return false;
        return resourceCallReports != null ? resourceCallReports.equals(that.resourceCallReports) : that.resourceCallReports == null;

    }

    @Override
    public int hashCode() {
        int result = actor != null ? actor.hashCode() : 0;
        result = 31 * result + (weatherReports != null ? weatherReports.hashCode() : 0);
        result = 31 * result + (resourceCallReports != null ? resourceCallReports.hashCode() : 0);
        result = 31 * result + (int) (totalTimeSpent ^ (totalTimeSpent >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ActorAndWeatherAndCallReports{" +
            "actor=" + actor +
            ", weatherReports=" + weatherReports +
            ", resourceCallReports=" + resourceCallReports +
            ", totalTimeSpent=" + totalTimeSpent +
            '}';
    }
}
