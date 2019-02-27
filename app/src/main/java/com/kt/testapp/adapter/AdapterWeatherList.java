package com.kt.testapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kt.testapp.R;
import com.kt.testapp.data.WeatherData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kim-young-hyun on 24/01/2019.
 */



public class AdapterWeatherList extends RecyclerView.Adapter<AdapterWeatherList.ViewHolder> {

    private ArrayList<WeatherData> weatherDatas;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context ctx;

    // data is passed into the constructor
    public AdapterWeatherList(Context context, ArrayList<WeatherData> data) {
        this.mInflater = LayoutInflater.from(context);
        this.weatherDatas = data;
        this.ctx= context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layout_weather_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WeatherData data = weatherDatas.get(position);
        holder.tvStationName.setText(data.getStationName());
        holder.tvP10.setText(data.getPm10Value() + ctx.getString(R.string.pm_unit));
        holder.tvP10grade.setText(data.getPm10gradeString());
        holder.tvP25.setText(data.getPm25Value() + ctx.getString(R.string.pm_unit));
        holder.tvP25grade.setText(data.getPm25gradeString());
        holder.tvDate.setText(data.getDataTime());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return weatherDatas==null?0:weatherDatas.size();
    }

    public void setData(ArrayList<WeatherData> data) {
        this.weatherDatas = data;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvStationName;
        TextView tvP10;
        TextView tvP10grade;
        TextView tvP25;
        TextView tvP25grade;
        TextView tvDate;

        ViewHolder(View itemView) {
            super(itemView);
            tvStationName = itemView.findViewById(R.id.tv_station_name);
            tvP10 = itemView.findViewById(R.id.tv_p10);
            tvP10grade = itemView.findViewById(R.id.tv_p10_grade);
            tvP25 = itemView.findViewById(R.id.tv_p25);
            tvP25grade = itemView.findViewById(R.id.tv_p25_grade);
            tvDate = itemView.findViewById(R.id.tv_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    private WeatherData getItem(int id) {
        return weatherDatas.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}