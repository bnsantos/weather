package com.bnsantos.weather.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class WeatherProvider extends ContentProvider {
  private static final UriMatcher sUriMatcher = buildUriMatcher();

  static final int CITY = 200;

  private DBHelper mDBHelper;

  @Override
  public boolean onCreate() {
    mDBHelper = new DBHelper(getContext());
    return false;
  }

  @Nullable
  @Override
  public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
    Cursor retCursor;
    switch (sUriMatcher.match(uri)) {
      case CITY:
        retCursor = mDBHelper.getReadableDatabase().query(
            WeatherContract.CityEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        );
        break;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    retCursor.setNotificationUri(getContext().getContentResolver(), uri);
    return retCursor;
  }

  @Nullable
  @Override
  public String getType(@NonNull Uri uri) {
    switch (sUriMatcher.match(uri)) {
      case CITY:
        return WeatherContract.CityEntry.CONTENT_TYPE;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
  }

  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
    final SQLiteDatabase db = mDBHelper.getWritableDatabase();
    Uri returnUri;

    switch (sUriMatcher.match(uri)) {
      case CITY:
        long _id = db.insert(WeatherContract.CityEntry.TABLE_NAME, null, values);
        if ( _id > 0 )
          returnUri = WeatherContract.CityEntry.buildLocationUri(_id);
        else
          throw new android.database.SQLException("Failed to insert row into " + uri);
        break;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return returnUri;
  }

  @Override
  public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
    final SQLiteDatabase db = mDBHelper.getWritableDatabase();
    int rowsDeleted;
    // this makes delete all rows return the number of rows deleted
    if ( null == selection ) selection = "1";
    switch (sUriMatcher.match(uri)) {
      case CITY:
        rowsDeleted = db.delete(WeatherContract.CityEntry.TABLE_NAME, selection, selectionArgs);
        break;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    if (rowsDeleted != 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }
    return rowsDeleted;
  }

  @Override
  public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
    final SQLiteDatabase db = mDBHelper.getWritableDatabase();
    int rowsUpdated;

    switch (sUriMatcher.match(uri)) {
      case CITY:
        rowsUpdated = db.update(WeatherContract.CityEntry.TABLE_NAME, values, selection, selectionArgs);
        break;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    if (rowsUpdated != 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }
    return rowsUpdated;
  }

  @Override
  public void shutdown() {
    mDBHelper.close();
    super.shutdown();
  }

  static UriMatcher buildUriMatcher() {
    final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    final String authority = WeatherContract.CONTENT_AUTHORITY;

    // For each type of URI you want to add, create a corresponding code.
    matcher.addURI(authority, WeatherContract.PATH_CITY, CITY);
    return matcher;
  }
}
