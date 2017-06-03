package com.bnsantos.weather.ui;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bnsantos.weather.R;
import com.bnsantos.weather.db.WeatherContract;
import com.bnsantos.weather.model.City;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityHolder> {
  private Cursor mCursor;

  public CityAdapter() { }

  @Override
  public CityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_city, parent, false);
    return new CityHolder(view);
  }

  @Override
  public void onBindViewHolder(CityHolder holder, int position) {
    mCursor.moveToPosition(position);
    int idx_name = mCursor.getColumnIndex(WeatherContract.CityEntry.COLUMN_NAME);
    int idx_country = mCursor.getColumnIndex(WeatherContract.CityEntry.COLUMN_COUNTRY);

    holder.mText.setText(mCursor.getString(idx_name) + "-" + mCursor.getString(idx_country));
  }

  @Override
  public int getItemCount() {
    return mCursor != null ? mCursor.getCount() : 0;
  }

  public void setCursor(Cursor data) {
    mCursor = data;
    notifyDataSetChanged();
  }

  public String[] remove(int[] reverseSortedPositions) {
    if (reverseSortedPositions != null && reverseSortedPositions.length > 0) {

      int idx_id = mCursor.getColumnIndex(WeatherContract.CityEntry._ID);
      String[] ids = new String[reverseSortedPositions.length];

      for (int i = 0; i < reverseSortedPositions.length; i++) {
        mCursor.moveToPosition(reverseSortedPositions[i]);
        ids[i] = mCursor.getString(idx_id);
      }

      return ids;
    }else {
      return null;
    }
  }

  public City getItem(int position) {
    mCursor.moveToPosition(position);
    int idx_name = mCursor.getColumnIndex(WeatherContract.CityEntry.COLUMN_NAME);
    int idx_country = mCursor.getColumnIndex(WeatherContract.CityEntry.COLUMN_COUNTRY);
    int idx_lat = mCursor.getColumnIndex(WeatherContract.CityEntry.COLUMN_LAT);
    int idx_lon = mCursor.getColumnIndex(WeatherContract.CityEntry.COLUMN_LON);
    City city = new City(mCursor.getString(idx_name), mCursor.getString(idx_country), mCursor.getDouble(idx_lat), mCursor.getDouble(idx_lon));
    return city;
  }

  class CityHolder extends RecyclerView.ViewHolder {
    final TextView mText;

    public CityHolder(View itemView) {
      super(itemView);
      mText = (TextView) itemView.findViewById(R.id.text);
    }
  }
}
