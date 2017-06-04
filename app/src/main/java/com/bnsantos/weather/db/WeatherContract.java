package com.bnsantos.weather.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.format.Time;

import com.bnsantos.weather.model.City;
import com.bnsantos.weather.model.Weather;

import java.util.Date;

public class WeatherContract {
  public static final String CONTENT_AUTHORITY = "com.bnsantos.weather";
  public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

  public static final String PATH_CITY = "city";
  public static final String PATH_WEATHER = "weather";

  public static final class CityEntry implements BaseColumns {
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CITY).build();
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CITY;
    public static final String TABLE_NAME = "city";

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LON = "lon";

    public static Uri buildLocationUri(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public static void insert(@NonNull final ContentResolver contentResolver, @NonNull final City city){
      ContentValues values = new ContentValues();
      values.put(WeatherContract.CityEntry.COLUMN_NAME, city.getName());
      values.put(WeatherContract.CityEntry.COLUMN_COUNTRY, city.getCountry());
      values.put(WeatherContract.CityEntry.COLUMN_LAT, city.getLat());
      values.put(WeatherContract.CityEntry.COLUMN_LON, city.getLon());

      contentResolver.insert(WeatherContract.CityEntry.CONTENT_URI, values);
    }
  }

  public static final class WeatherEntry implements BaseColumns {
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
    public static final String TABLE_NAME = "weather";

    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LON = "lon";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TEMP = "temp";
    public static final String COLUMN_TEMP_MAX = "temp_max";
    public static final String COLUMN_TEMP_MIN = "temp_min";
    public static final String COLUMN_TEMP_UNIT = "temp_unit";
    public static final String COLUMN_HUMIDITY = "humidity";
    public static final String COLUMN_WIND_SPEED = "wind_speed";
    public static final String COLUMN_WIND_DEGREES = "wind_degrees";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_WEATHER_ID = "weather_id";

    public static Uri buildWeatherUri(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public static Uri buildWeatherLocationDateUri(final double lat, final double lon, final Date date){
      return CONTENT_URI.buildUpon()
          .appendQueryParameter(COLUMN_LAT, Double.toString(lat))
          .appendQueryParameter(COLUMN_LON, Double.toString(lon))
          .appendQueryParameter(COLUMN_DATE, normalizeDate(date.getTime()))
          .build();
    }

    public static void insert(@NonNull final ContentResolver cr, @NonNull final Weather weather){
      ContentValues values = new ContentValues();
      values.put(COLUMN_LAT, weather.getLat());
      values.put(COLUMN_LON, weather.getLon());
      values.put(COLUMN_DATE, weather.getDate().getTime());
      values.put(COLUMN_TEMP, weather.getTemp());
      values.put(COLUMN_TEMP_MAX, weather.getTempMax());
      values.put(COLUMN_TEMP_MIN, weather.getTempMin());
      values.put(COLUMN_TEMP_UNIT, weather.getTempUnit());
      values.put(COLUMN_HUMIDITY, weather.getHumidity());
      values.put(COLUMN_WIND_SPEED, weather.getWindSpeed());
      values.put(COLUMN_WIND_DEGREES, weather.getWindDegrees());
      values.put(COLUMN_DESCRIPTION, weather.getDescription());
      values.put(COLUMN_WEATHER_ID, weather.getWeatherId());

      cr.insert(WeatherContract.WeatherEntry.CONTENT_URI, values);
    }

    public static Weather parse(@NonNull final Cursor data){
      int latIdx = data.getColumnIndex(COLUMN_LAT);
      int lonIdx = data.getColumnIndex(COLUMN_LON);
      int dateIdx = data.getColumnIndex(COLUMN_DATE);
      int tempIdx = data.getColumnIndex(COLUMN_TEMP);
      int tempMaxIdx = data.getColumnIndex(COLUMN_TEMP_MAX);
      int tempMinIdx = data.getColumnIndex(COLUMN_TEMP_MIN);
      int tempUnitIdx = data.getColumnIndex(COLUMN_TEMP_UNIT);
      int humidityIdx = data.getColumnIndex(COLUMN_HUMIDITY);
      int windIdx = data.getColumnIndex(COLUMN_WIND_SPEED);
      int windDegreeIdx = data.getColumnIndex(COLUMN_WIND_DEGREES);
      int descIdx = data.getColumnIndex(COLUMN_DESCRIPTION);
      int weatherIdx = data.getColumnIndex(COLUMN_WEATHER_ID);

      return new Weather(
          data.getDouble(latIdx),
          data.getDouble(lonIdx),
          new Date(data.getLong(dateIdx)),
          data.getDouble(tempIdx),
          data.getDouble(tempMaxIdx),
          data.getDouble(tempMinIdx),
          data.getString(tempUnitIdx),
          data.getInt(humidityIdx),
          data.getDouble(windIdx),
          data.getDouble(windDegreeIdx),
          data.getString(descIdx),
          data.getInt(weatherIdx)
      );
    }

  }

  // To make it easy to query for the exact date, we normalize all dates that go into
  // the database to the start of the the Julian day at UTC.
  public static String normalizeDate(long startDate) {
    // normalize the start date to the beginning of the (UTC) day
    Time time = new Time();
    time.set(startDate);
    int julianDay = Time.getJulianDay(startDate, time.gmtoff);
    return Long.toString(time.setJulianDay(julianDay));
  }

  public static void remove(@NonNull final ContentResolver cr, @NonNull final String[] remove, Uri contentUri){
    cr.delete(contentUri, WeatherContract.CityEntry._ID + " in ("+makePlaceholders(remove.length)+")", remove);
  }

  private static String makePlaceholders(int len) {
    if (len < 1) {
      // It will lead to an invalid query anyway ..
      throw new RuntimeException("No placeholders");
    } else {
      StringBuilder sb = new StringBuilder(len * 2 - 1);
      sb.append("?");
      for (int i = 1; i < len; i++) {
        sb.append(",?");
      }
      return sb.toString();
    }
  }

}
