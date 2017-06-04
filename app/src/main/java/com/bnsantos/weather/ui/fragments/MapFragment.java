package com.bnsantos.weather.ui.fragments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bnsantos.weather.R;
import com.bnsantos.weather.model.City;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
  private static final String TAG = MapFragment.class.getSimpleName();
  private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
  private MapFragmentListener mListener;
  private MapView mMapView;
  private GoogleMap mGoogleMap;
  private Marker mMarker;

  private double mLat;
  private double mLon;
  private String mCity;
  private String mCountry;
  private MenuItem mConfirm;
  protected Geocoder mGeocoder;

  private Handler mGeoCoderHandler = new  Handler() {
    @Override
    public void handleMessage(Message message) {
      switch (message.what) {
        case 1:
          Bundle bundle = message.getData();
          Address address = bundle.getParcelable("address");

          if (address != null && mGoogleMap != null) {
            mCity = address.getLocality();
            mCountry = address.getCountryCode();
            mLat = address.getLatitude();
            mLon = address.getLongitude();

            mConfirm.setVisible(true);
            if (mMarker != null) {
              mMarker.setPosition(new LatLng(mLat, mLon));
            } else {
              mMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mLat, mLon)).draggable(true));
            }
          }
          break;
        default:
          Toast.makeText(getContext(), R.string.no_city_found, Toast.LENGTH_SHORT).show();
      }
    }
  };

  public MapFragment() { }

  public static MapFragment newInstance() {
    return new MapFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    mGeocoder = new Geocoder(getContext(), Locale.getDefault());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_map, container, false);

    Bundle mapViewBundle = null;
    if (savedInstanceState != null) {
      mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
    }
    mMapView = (MapView) view.findViewById(R.id.map);
    mMapView.onCreate(mapViewBundle);
    mMapView.getMapAsync(this);

    return view;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
    if (mapViewBundle == null) {
      mapViewBundle = new Bundle();
      outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
    }

    mMapView.onSaveInstanceState(mapViewBundle);
  }

  @Override
  public void onResume() {
    super.onResume();
    mMapView.onResume();
  }

  @Override
  public void onStart() {
    super.onStart();
    mMapView.onStart();
  }

  @Override
  public void onStop() {
    super.onStop();
    mMapView.onStop();
  }

  @Override
  public void onPause() {
    mMapView.onPause();
    super.onPause();
  }

  @Override
  public void onDestroy() {
    mMapView.onDestroy();
    super.onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mMapView.onLowMemory();
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    UiSettings uiSettings = googleMap.getUiSettings();
    uiSettings.setCompassEnabled(false);
    uiSettings.setMyLocationButtonEnabled(false);
    uiSettings.setTiltGesturesEnabled(false);

    googleMap.setOnMapLongClickListener(this);
    mGoogleMap = googleMap;

    Toast.makeText(getContext(), R.string.choose_city_instructions, Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onMapLongClick(LatLng latLng) {
    Toast.makeText(getContext(), R.string.looking_for_cities, Toast.LENGTH_SHORT).show();
    getAddressFromLocation(latLng, mGeocoder, mGeoCoderHandler);
  }

  public static void getAddressFromLocation(final LatLng location, final Geocoder geocoder, final Handler handler) {
    Thread thread = new Thread() {
      @Override public void run() {
        Address result = null;
        try {
          List<Address> list = geocoder.getFromLocation(location.latitude, location.longitude, 1);
          if (list != null && list.size() > 0) {
            result = list.get(0);
          }
        } catch (IOException e) {
          Log.e(TAG, "Impossible to connect to Geocoder", e);
        } finally {
          Message msg = Message.obtain();
          msg.setTarget(handler);
          if (result != null) {
            msg.what = 1;
            Bundle bundle = new Bundle();
            bundle.putParcelable("address", result);
            msg.setData(bundle);
          } else
            msg.what = 0;
          msg.sendToTarget();
        }
      }
    };
    thread.start();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.map, menu);
    mConfirm = menu.findItem(R.id.confirm);
    mConfirm.setVisible(false);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.confirm) {
      if (mListener != null) {
        mListener.onCityChosen(new City(mCity, mCountry, mLat, mLon));
      }
      return true;
    }else {
      return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof MapFragmentListener) {
      mListener = (MapFragmentListener) context;
    } else {
      throw new RuntimeException(context.toString() + " must implement MapFragmentListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  public interface MapFragmentListener {
    void onCityChosen(City city);
  }
}
