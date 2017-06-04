package com.bnsantos.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;


public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
  private RadioButton mMetric;
  private RadioButton mImperial;
  private SharedPreferences mPreferences;

  public SettingsFragment() { }

  public static SettingsFragment newInstance() {
    SettingsFragment fragment = new SettingsFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mPreferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_settings, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mMetric = (RadioButton) view.findViewById(R.id.metric);
    mImperial = (RadioButton) view.findViewById(R.id.imperial);

    int unit = mPreferences.getInt("unit", 0);

    if (unit == 0) {
      mMetric.setChecked(true);
    }else {
      mImperial.setChecked(true);
    }

    mMetric.setOnCheckedChangeListener(this);
    mImperial.setOnCheckedChangeListener(this);
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    SharedPreferences.Editor edit = mPreferences.edit();
    if (isChecked) {
      if (buttonView.getId() == R.id.metric) {
        edit.putInt("unit", 0);
      }else {
        edit.putInt("unit", 1);
      }
    }

    edit.apply();
  }
}
