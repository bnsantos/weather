package com.bnsantos.weather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bnsantos.weather.db.WeatherContract;
import com.bnsantos.weather.model.City;
import com.bnsantos.weather.service.WeatherService;
import com.bnsantos.weather.ui.fragments.CitiesFragment;
import com.bnsantos.weather.ui.fragments.ForecastFragment;
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

    Intent intent = new Intent(this, WeatherService.class);
    intent.putExtra("city", clicked);
    startService(intent);

    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.content, ForecastFragment.newInstance(clicked))
        .addToBackStack("Map")
        .commit();
  }

  @Override
  public void onCityChosen(City city) {
    Toast.makeText(this, city.toString(), Toast.LENGTH_SHORT).show();

    WeatherContract.CityEntry.insert(getContentResolver(), city);

    getSupportFragmentManager()
        .popBackStack();
  }
}
