package com.bnsantos.weather.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bnsantos.weather.R;

public class CitiesFragment extends Fragment implements View.OnClickListener {

  private CitiesFragmentListener mListener;
  private RecyclerView mRecyclerView;

  public CitiesFragment() { }

  public static CitiesFragment newInstance() {
    return new CitiesFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_cities, container, false);
    mRecyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
    view.findViewById(R.id.fab).setOnClickListener(this);
    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof CitiesFragmentListener) {
      mListener = (CitiesFragmentListener) context;
    } else {
      throw new RuntimeException(context.toString() + " must implement CitiesFragmentListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  @Override
  public void onClick(View v) {
    if (mListener != null) {
      mListener.addCity();
    }
  }

  public interface CitiesFragmentListener {
    void addCity();
  }
}
