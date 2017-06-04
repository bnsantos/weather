package com.bnsantos.weather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bnsantos.weather.db.WeatherContract;
import com.bnsantos.weather.model.City;
import com.bnsantos.weather.service.WeatherService;
import com.bnsantos.weather.ui.fragments.CitiesFragment;
import com.bnsantos.weather.ui.fragments.ForecastFragment;
import com.bnsantos.weather.ui.fragments.HelpFragment;
import com.bnsantos.weather.ui.fragments.MapFragment;

public class MainActivity extends AppCompatActivity implements CitiesFragment.CitiesFragmentListener, MapFragment.MapFragmentListener {
  private static final String TAG = MainActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.content, CitiesFragment.newInstance())
        .commit();
  }

  @Override
  public void addCity() {
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.content, MapFragment.newInstance())
        .addToBackStack("Map")
        .commit();
  }

  @Override
  public void cityClicked(City clicked) {
    Log.i(TAG, clicked.toString());

    fetchCityWeather(clicked);

    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.content, ForecastFragment.newInstance(clicked))
        .addToBackStack("Forecast")
        .commit();
  }

  @Override
  public void onCityChosen(City city) {
    fetchCityWeather(city);
    WeatherContract.CityEntry.insert(getContentResolver(), city);
    getSupportFragmentManager()
        .popBackStack();
  }

  private void fetchCityWeather(City clicked) {
    Intent intent = new Intent(this, WeatherService.class);
    intent.putExtra("city", clicked);
    startService(intent);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.help:
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content, HelpFragment.newInstance())
            .addToBackStack("Help")
            .commit();
        return true;
      case R.id.settings:
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content, SettingsFragment.newInstance())
            .addToBackStack("Settings")
            .commit();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
