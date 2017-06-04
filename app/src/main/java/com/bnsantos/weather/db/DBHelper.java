package com.bnsantos.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
  private static final int DATABASE_VERSION = 3;
  private static final String DATABASE_NAME = "weather.db";

  public DBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    final String SQL_CREATE_CITY_TABLE = "CREATE TABLE " + WeatherContract.CityEntry.TABLE_NAME + " (" +
        WeatherContract.CityEntry._ID + " INTEGER PRIMARY KEY," +
        WeatherContract.CityEntry.COLUMN_NAME + " TEXT NOT NULL, " +
        WeatherContract.CityEntry.COLUMN_COUNTRY + " TEXT NOT NULL, " +
        WeatherContract.CityEntry.COLUMN_LAT + " REAL NOT NULL, " +
        WeatherContract.CityEntry.COLUMN_LON + " REAL NOT NULL " +
        " );";
    db.execSQL(SQL_CREATE_CITY_TABLE);

    final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherContract.WeatherEntry.TABLE_NAME + " (" +
        WeatherContract.WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        WeatherContract.WeatherEntry.COLUMN_LAT + " REAL NOT NULL, " +
        WeatherContract.WeatherEntry.COLUMN_LON + " REAL NOT NULL, " +
        WeatherContract.WeatherEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
        WeatherContract.WeatherEntry.COLUMN_TEMP + " REAL NOT NULL, " +
        WeatherContract.WeatherEntry.COLUMN_TEMP_MIN + " REAL NOT NULL, " +
        WeatherContract.WeatherEntry.COLUMN_TEMP_MAX + " REAL NOT NULL, " +
        WeatherContract.WeatherEntry.COLUMN_TEMP_UNIT + " TEXT NOT NULL, " +
        WeatherContract.WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, " +
        WeatherContract.WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
        WeatherContract.WeatherEntry.COLUMN_WIND_DEGREES + " REAL NOT NULL, " +
        WeatherContract.WeatherEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
        WeatherContract.WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL, " +
        " UNIQUE (" +
          WeatherContract.WeatherEntry.COLUMN_DATE + ", " +
          WeatherContract.WeatherEntry.COLUMN_LAT + ", " +
          WeatherContract.WeatherEntry.COLUMN_LON +
        ") ON CONFLICT REPLACE);";
    db.execSQL(SQL_CREATE_WEATHER_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.CityEntry.TABLE_NAME);
    db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherEntry.TABLE_NAME);
    onCreate(db);
  }
}
