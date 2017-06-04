package com.bnsantos.weather.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bnsantos.weather.R;
import com.bnsantos.weather.db.WeatherContract;
import com.bnsantos.weather.model.City;
import com.bnsantos.weather.ui.CityAdapter;
import com.bnsantos.weather.ui.recyclerview.RecyclerItemClickListener;
import com.bnsantos.weather.ui.recyclerview.SparseItemRemoveAnimator;
import com.bnsantos.weather.ui.recyclerview.SwipeDismiss;

public class CitiesFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>,SwipeDismiss.DismissCallbacks, RecyclerItemClickListener.OnItemClickListener {

  private CitiesFragmentListener mListener;
  private RecyclerView mRecyclerView;
  private CityAdapter mAdapter;
  protected boolean mCanDismiss = true;

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
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    mAdapter = new CityAdapter();
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setItemAnimator(new SparseItemRemoveAnimator());
    mRecyclerView.setOnTouchListener(new SwipeDismiss(mRecyclerView, this));
    mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));
    view.findViewById(R.id.fab).setOnClickListener(this);

    getLoaderManager().initLoader(0, null, this);
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

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    final Uri CONTENT_URI = WeatherContract.BASE_CONTENT_URI.buildUpon().appendPath(WeatherContract.PATH_CITY).build();
    return new CursorLoader(getContext(), CONTENT_URI, null, null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mAdapter.setCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    mAdapter.setCursor(null);
  }

  @Override
  public boolean canDismiss(int position) {
    return mCanDismiss;
  }

  @Override
  public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
    mCanDismiss = false;
    final String[] toRemove = mAdapter.remove(reverseSortedPositions);
    View view = getView();
    if (toRemove != null && view != null) {
      Snackbar.make(view, R.string.removed_cities, Snackbar.LENGTH_SHORT)
          .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
              super.onDismissed(transientBottomBar, event);
              if(event != DISMISS_EVENT_TIMEOUT && event != DISMISS_EVENT_SWIPE && event != DISMISS_EVENT_ACTION) {
                remove(toRemove);
              }
              mCanDismiss = true;
            }
          })
          .setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              mAdapter.notifyDataSetChanged();
            }
          })
          .show();
    }
  }

  protected void remove(String[] remove){
    WeatherContract.remove(getContext().getContentResolver(), remove, WeatherContract.CityEntry.CONTENT_URI);
  }

  @Override
  public void onItemClick(View view, int position) {
    if (mListener != null) {
      mListener.cityClicked(mAdapter.getItem(position));
    }
  }

  public interface CitiesFragmentListener {
    void addCity();
    void cityClicked(City clicked);
  }
}
