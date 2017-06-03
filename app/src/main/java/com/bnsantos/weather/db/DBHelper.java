package com.bnsantos.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
  private static final int DATABASE_VERSION = 1;
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
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.CityEntry.TABLE_NAME);
    onCreate(db);
  }
}
