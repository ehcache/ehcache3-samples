package org.terracotta.demo.service.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Created by Anthony Dahanne on 2016-09-23.
 */
public class WeatherReport implements Serializable {
  private LocalDate date;
  private String location;
  private String icon;
  private String summary;
  private double temperatureMin;
  private double temperatureMax;


  public WeatherReport(LocalDate date, String location, String icon, String summary, double temperatureMin, double temperatureMax) {
    this.date = date;
    this.location = location;
    this.icon = icon;
    this.summary = summary;
    this.temperatureMin = temperatureMin;
    this.temperatureMax = temperatureMax;
  }

  public WeatherReport() {

  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public double getTemperatureMin() {
    return temperatureMin;
  }

  public void setTemperatureMin(double temperatureMin) {
    this.temperatureMin = temperatureMin;
  }

  public double getTemperatureMax() {
    return temperatureMax;
  }

  public void setTemperatureMax(double temperatureMax) {
    this.temperatureMax = temperatureMax;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    WeatherReport that = (WeatherReport) o;

    if (Double.compare(that.temperatureMin, temperatureMin) != 0) return false;
    if (Double.compare(that.temperatureMax, temperatureMax) != 0) return false;
    if (date != null ? !date.equals(that.date) : that.date != null) return false;
    if (location != null ? !location.equals(that.location) : that.location != null) return false;
    if (icon != null ? !icon.equals(that.icon) : that.icon != null) return false;
    return summary != null ? summary.equals(that.summary) : that.summary == null;

  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = date != null ? date.hashCode() : 0;
    result = 31 * result + (location != null ? location.hashCode() : 0);
    result = 31 * result + (icon != null ? icon.hashCode() : 0);
    result = 31 * result + (summary != null ? summary.hashCode() : 0);
    temp = Double.doubleToLongBits(temperatureMin);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(temperatureMax);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "WeatherReport{" +
        "date=" + date +
        ", location='" + location + '\'' +
        ", icon='" + icon + '\'' +
        ", summary='" + summary + '\'' +
        ", temperatureMin=" + temperatureMin +
        ", temperatureMax=" + temperatureMax +
        '}';
  }
}
