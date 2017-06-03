package com.bnsantos.weather.model;

public class City {
  private final String mName;
  private final String mCountry;
  private final double mLat;
  private final double mLon;

  public City(String name, String country, double lat, double lon) {
    mName = name;
    mCountry = country;
    mLat = lat;
    mLon = lon;
  }

  public String getName() {
    return mName;
  }

  public String getCountry() {
    return mCountry;
  }

  public double getLat() {
    return mLat;
  }

  public double getLon() {
    return mLon;
  }

  @Override
  public String toString() {
    return "City{" +
        "mName='" + mName + '\'' +
        ", mCountry='" + mCountry + '\'' +
        ", mLat=" + mLat +
        ", mLon=" + mLon +
        '}';
  }
}
