package com.bnsantos.weather.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bnsantos.weather.BuildConfig;
import com.bnsantos.weather.db.WeatherContract;
import com.bnsantos.weather.model.City;
import com.bnsantos.weather.model.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class WeatherService extends IntentService {
  private static final String TAG = WeatherService.class.getSimpleName();

  public WeatherService() {
    super(WeatherService.class.getSimpleName());
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;

    if (intent != null) {
      City city = intent.getParcelableExtra("city");

      try {
        Uri builtUri = generateUri(city);

        URL url = new URL(builtUri.toString());

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();
        if (inputStream == null) {
          // Nothing to do.
          return;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
          buffer.append(line).append("\n");
        }

        if (buffer.length() == 0) {
          // Stream was empty.  No point in parsing.
          return;
        }
        Weather weather = parseResponse(buffer.toString(), city);
        WeatherContract.WeatherEntry.insert(getContentResolver(), weather);
        Log.i(TAG, weather.toString());

      } catch (IOException | JSONException e) {
        e.printStackTrace();
      } finally {
        if (urlConnection != null) {
          urlConnection.disconnect();
        }
        if (reader != null) {
          try {
            reader.close();
          } catch (final IOException e) {
            Log.e(TAG, "Error closing stream", e);
          }
        }
      }
    }
  }

  @NonNull
  private Weather parseResponse(String forecastJsonStr, City city) throws JSONException {
    JSONObject forecastJson = new JSONObject(forecastJsonStr);

    JSONObject main = forecastJson.getJSONObject("main");
    JSONObject wind = forecastJson.getJSONObject("wind");
    JSONArray weatherArray = forecastJson.getJSONArray("weather");
    JSONObject weatherObj = weatherArray.getJSONObject(0);

    return new Weather(
        city.getLat(),
        city.getLon(),
        new Date(forecastJson.getLong("dt")*1000),
        main.getDouble("temp"),
        main.getDouble("temp_max"),
        main.getDouble("temp_min"),
        "metric",
        main.getInt("humidity"),
        wind.getDouble("speed"),
        wind.getDouble("deg"),
        weatherObj.getString("description"),
        weatherObj.getInt("id")
    );
  }

  private Uri generateUri(City city) {
    final String FORMAT_PARAM = "mode";
    final String UNITS_PARAM = "units";
    final String LAT_PARAM = "lat";
    final String LON_PARAM = "lon";
    final String APPID_PARAM = "APPID";

    return Uri.parse(BuildConfig.OPEN_WEATHER_BASE_URL).buildUpon()
        .appendQueryParameter(LAT_PARAM, Double.toString(city.getLat()))
        .appendQueryParameter(LON_PARAM, Double.toString(city.getLon()))
        .appendQueryParameter(FORMAT_PARAM, "json")
        .appendQueryParameter(UNITS_PARAM, "metric")
        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
        .build();
  }
}
