package com.bnsantos.weather.model;

import android.os.Parcel;
import android.os.Parcelable;

public class City implements Parcelable {
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


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.mName);
    dest.writeString(this.mCountry);
    dest.writeDouble(this.mLat);
    dest.writeDouble(this.mLon);
  }

  protected City(Parcel in) {
    this.mName = in.readString();
    this.mCountry = in.readString();
    this.mLat = in.readDouble();
    this.mLon = in.readDouble();
  }

  public static final Parcelable.Creator<City> CREATOR = new Parcelable.Creator<City>() {
    @Override
    public City createFromParcel(Parcel source) {
      return new City(source);
    }

    @Override
    public City[] newArray(int size) {
      return new City[size];
    }
  };
}
