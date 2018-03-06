package org.ehcache.sample.domain;

/**
 * Created by Anthony Dahanne on 2016-09-23.
 */
public class Coordinates {

  private String latitude;
  private String longitude;

  public String getLatitude() {
    return latitude;
  }

  public String getLongitude() {
    return longitude;
  }

  public Coordinates(String latitude, String longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  @Override
  public String toString() {
    return "Coordinates{" +
        "latitude='" + latitude + '\'' +
        ", longitude='" + longitude + '\'' +
        '}';
  }
}
