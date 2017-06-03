package com.bnsantos.weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.bnsantos.weather.model.City;
import com.bnsantos.weather.ui.CitiesFragment;
import com.bnsantos.weather.ui.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements CitiesFragment.CitiesFragmentListener, MapFragment.MapFragmentListener {

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
  public void onCityChosen(City city) {
    Toast.makeText(this, city.toString(), Toast.LENGTH_SHORT).show();
    getSupportFragmentManager()
        .popBackStack();
  }
}
