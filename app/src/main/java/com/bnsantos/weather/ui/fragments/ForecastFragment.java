package com.bnsantos.weather.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bnsantos.weather.R;
import com.bnsantos.weather.db.WeatherContract;
import com.bnsantos.weather.model.City;
import com.bnsantos.weather.model.Weather;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
  private static final String TAG = ForecastFragment.class.getSimpleName();
  private static final String ARG_CITY = "city";
  private City mCity;

  private TextView mAddress;
  private ImageView mIcon;
  private TextView mDay;
  private TextView mDate;
  private TextView mDescription;
  private TextView mTemp;
  private TextView mTempMax;
  private TextView mTempMin;
  private TextView mHumidity;
  private TextView mWind;

  private int mUnit;

  public ForecastFragment() {}

  public static ForecastFragment newInstance(@NonNull final City city) {
    ForecastFragment fragment = new ForecastFragment();
    Bundle args = new Bundle();
    args.putParcelable(ARG_CITY, city);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mUnit = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE).getInt("unit", 0);
    if (getArguments() != null) {
      mCity = getArguments().getParcelable(ARG_CITY);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_forecast, container, false);
    mAddress = (TextView) view.findViewById(R.id.address);
    mIcon = (ImageView) view.findViewById(R.id.detailIcon);
    mDay = (TextView) view.findViewById(R.id.day);
    mDate = (TextView) view.findViewById(R.id.date);
    mDescription = (TextView) view.findViewById(R.id.forecast);
    mTemp = (TextView) view.findViewById(R.id.temp);
    mTempMax = (TextView) view.findViewById(R.id.tempMax);
    mTempMin = (TextView) view.findViewById(R.id.tempMin);
    mHumidity = (TextView) view.findViewById(R.id.humidity);
    mWind = (TextView) view.findViewById(R.id.wind);
    return view;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mAddress.setText(mCity.getName() + " - " + mCity.getCountry());
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    getLoaderManager().initLoader(0, null, this);
    super.onActivityCreated(savedInstanceState);
  }


  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(ARG_CITY, mCity);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(getActivity(),
        WeatherContract.WeatherEntry.buildWeatherLocationDateUri(mCity.getLat(), mCity.getLon(), Calendar.getInstance().getTime()),
        null,
        WeatherContract.WeatherEntry.COLUMN_LAT + " = ? AND " + WeatherContract.WeatherEntry.COLUMN_LON + " = ?",
        new String[] { Double.toString(mCity.getLat()),  Double.toString(mCity.getLon())},
        WeatherContract.WeatherEntry.COLUMN_DATE + " DESC"
    );
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    if (data != null && data.moveToFirst()) {
      Weather weather = WeatherContract.WeatherEntry.parse(data);

      Log.i(TAG, weather.toString());

      mIcon.setImageResource(getArtResourceForWeatherCondition(weather.getWeatherId()));

      long time = weather.getDate().getTime();
      mDay.setText(getDayName(getActivity(), time));
      mDate.setText(getFormattedMonthDay(time));

      mDescription.setText(weather.getDescription());
      mTemp.setText(formatTemperature(getContext(), mUnit, weather.getTemp()));
      mTempMax.setText(formatTemperature(getContext(), mUnit, weather.getTempMax()));
      mTempMin.setText(formatTemperature(getContext(), mUnit, weather.getTempMin()));
      mHumidity.setText(getString(R.string.format_humidity, weather.getHumidity()));
      mWind.setText(getFormattedWind(getContext(), mUnit, weather.getWindSpeed(), weather.getWindDegrees()));
    }else {
      Toast.makeText(getContext(), "Nao achou", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) { }

  public static String formatTemperature(Context context, int unit, double temperature) {
    if (unit != 0) {
      temperature = (temperature * 1.8) + 32;
    }

    return String.format(context.getString(R.string.format_temperature), temperature);
  }

  public static String getFormattedWind(Context context, int unit, double windSpeed, double degrees) {
    int windFormat;
    if (unit == 0) { //Metric
      windFormat = R.string.format_wind_kmh;
    } else {
      windFormat = R.string.format_wind_mph;
      windSpeed = .621371192237334f * windSpeed;
    }

    String direction = "Unknown";
    if (degrees >= 337.5 || degrees < 22.5) {
      direction = "N";
    } else if (degrees >= 22.5 && degrees < 67.5) {
      direction = "NE";
    } else if (degrees >= 67.5 && degrees < 112.5) {
      direction = "E";
    } else if (degrees >= 112.5 && degrees < 157.5) {
      direction = "SE";
    } else if (degrees >= 157.5 && degrees < 202.5) {
      direction = "S";
    } else if (degrees >= 202.5 && degrees < 247.5) {
      direction = "SW";
    } else if (degrees >= 247.5 && degrees < 292.5) {
      direction = "W";
    } else if (degrees >= 292.5 && degrees < 337.5) {
      direction = "NW";
    }
    return String.format(context.getString(windFormat), windSpeed, direction);
  }

  /**
   * Given a day, returns just the name to use for that day.
   * E.g "today", "tomorrow", "wednesday".
   *
   * @param context Context to use for resource localization
   * @param dateInMillis The date in milliseconds
   * @return
   */
  public static String getDayName(Context context, long dateInMillis) {
    Time t = new Time();
    t.setToNow();
    int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
    int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
    if (julianDay == currentJulianDay) {
      return context.getString(R.string.today);
    } else if ( julianDay == currentJulianDay +1 ) {
      return context.getString(R.string.tomorrow);
    } else {
      Time time = new Time();
      time.setToNow();
      // Otherwise, the format is just the day of the week (e.g "Wednesday".
      SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
      return dayFormat.format(dateInMillis);
    }
  }

  /**
   * Converts db date format to the format "Month day", e.g "June 24".
   * @param dateInMillis The db formatted date string, expected to be of the form specified in Utility.DATE_FORMAT
   * @return The day in the form of a string formatted "December 6"
   */
  public static String getFormattedMonthDay(long dateInMillis) {
    Time time = new Time();
    time.setToNow();
    SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
    String monthDayString = monthDayFormat.format(dateInMillis);
    return monthDayString;
  }

  /**
   * Helper method to provide the art resource id according to the weather condition id returned
   * by the OpenWeatherMap call.
   * @param weatherId from OpenWeatherMap API response
   * @return resource id for the corresponding icon. -1 if no relation is found.
   */
  public static int getArtResourceForWeatherCondition(int weatherId) {
    // Based on weather code data found at:
    // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
    if (weatherId >= 200 && weatherId <= 232) {
      return R.drawable.art_storm;
    } else if (weatherId >= 300 && weatherId <= 321) {
      return R.drawable.art_light_rain;
    } else if (weatherId >= 500 && weatherId <= 504) {
      return R.drawable.art_rain;
    } else if (weatherId == 511) {
      return R.drawable.art_snow;
    } else if (weatherId >= 520 && weatherId <= 531) {
      return R.drawable.art_rain;
    } else if (weatherId >= 600 && weatherId <= 622) {
      return R.drawable.art_snow;
    } else if (weatherId >= 701 && weatherId <= 761) {
      return R.drawable.art_fog;
    } else if (weatherId == 761 || weatherId == 781) {
      return R.drawable.art_storm;
    } else if (weatherId == 800) {
      return R.drawable.art_clear;
    } else if (weatherId == 801) {
      return R.drawable.art_light_clouds;
    } else if (weatherId >= 802 && weatherId <= 804) {
      return R.drawable.art_clouds;
    }
    return -1;
  }
}
