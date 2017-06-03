package com.bnsantos.weather.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.bnsantos.weather.model.City;

public class WeatherContract {
  public static final String CONTENT_AUTHORITY = "com.bnsantos.weather";
  public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

  public static final String PATH_CITY = "city";

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

    public static void remove(@NonNull final ContentResolver cr, @NonNull final String[] remove){
      cr.delete(WeatherContract.CityEntry.CONTENT_URI, WeatherContract.CityEntry._ID + " in ("+makePlaceholders(remove.length)+")", remove);
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
}
