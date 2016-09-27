package org.terracotta.demo.domain;

/**
 * Created by Anthony Dahanne on 2016-09-23.
 */
public class Coordinates {

  private String latitude;
  private String longitude;

  public String getLatitude() {
    return latitude;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public String getLongitude() {
    return longitude;
  }

  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  public Coordinates(String latitude, String longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Coordinates that = (Coordinates) o;

    if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null) return false;
    return longitude != null ? longitude.equals(that.longitude) : that.longitude == null;

  }

  @Override
  public int hashCode() {
    int result = latitude != null ? latitude.hashCode() : 0;
    result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Coordinates{" +
        "latitude='" + latitude + '\'' +
        ", longitude='" + longitude + '\'' +
        '}';
  }
}
