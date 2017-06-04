package com.bnsantos.weather.model;

import java.util.Date;

public class Weather {
  private final double mLat;
  private final double mLon;
  private final Date mDate;
  private final double mTemp;
  private final double mTempMax;
  private final double mTempMin;
  private final String mTempUnit;
  private final int mHumidity;
  private final double mWindSpeed;
  private final double mWindDegrees;
  private final String mDescription;
  private final int mWeatherId;

  public Weather(double lat, double lon, Date date, double temp, double tempMax, double tempMin, String tempUnit, int humidity, double windSpeed, double windDegrees, String description, int weatherId) {
    mLat = lat;
    mLon = lon;
    mDate = date;
    mTemp = temp;
    mTempMax = tempMax;
    mTempMin = tempMin;
    mTempUnit = tempUnit;
    mHumidity = humidity;
    mWindSpeed = windSpeed;
    mWindDegrees = windDegrees;
    mDescription = description;
    mWeatherId = weatherId;
  }

  public double getLat() {
    return mLat;
  }

  public double getLon() {
    return mLon;
  }

  public Date getDate() {
    return mDate;
  }

  public double getTemp() {
    return mTemp;
  }

  public double getTempMax() {
    return mTempMax;
  }

  public double getTempMin() {
    return mTempMin;
  }

  public String getTempUnit() {
    return mTempUnit;
  }

  public int getHumidity() {
    return mHumidity;
  }

  public double getWindSpeed() {
    return mWindSpeed;
  }

  public double getWindDegrees() {
    return mWindDegrees;
  }

  public String getDescription() {
    return mDescription;
  }

  public int getWeatherId() {
    return mWeatherId;
  }

  @Override
  public String toString() {
    return "Weather{" +
        "mLat=" + mLat +
        ", mLon=" + mLon +
        ", mDate=" + mDate +
        ", mTemp=" + mTemp +
        ", mTempMax=" + mTempMax +
        ", mTempMin=" + mTempMin +
        ", mTempUnit='" + mTempUnit + '\'' +
        ", mHumidity=" + mHumidity +
        ", mWindSpeed=" + mWindSpeed +
        ", mWindDegrees=" + mWindDegrees +
        ", mDescription='" + mDescription + '\'' +
        ", mWeatherId=" + mWeatherId +
        '}';
  }
}
