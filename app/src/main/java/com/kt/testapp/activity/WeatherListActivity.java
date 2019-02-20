package com.kt.testapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kt.testapp.R;
import com.kt.testapp.adapter.AdapterWeatherList;
import com.kt.testapp.data.WeatherData;
import com.kt.testapp.inf.EventListener;
import com.kt.testapp.utils.GetWeatherAsyncTask;

import java.util.ArrayList;

/**
 * Created by kim-young-hyun on 22/01/2019.
 */

public class WeatherListActivity extends BaseActivity {

    private ArrayList<WeatherData> data = new ArrayList<>();
    private AdapterWeatherList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // 리스트뷰 형태 RecyclerView set
        RecyclerView recyclerView = findViewById(R.id.rvWeather);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        // 구분선 추가
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(this,new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        // adapter set
        adapter = new AdapterWeatherList(this, data);
        adapter.setClickListener(new AdapterWeatherList.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent newIntent = new Intent(WeatherListActivity.this, GoogleMapActivity.class);
                newIntent.putExtra(GoogleMapActivity.KEY_WEATHER_OBJ,data.get(position));
                startActivity(newIntent);
            }
        });
        recyclerView.setAdapter(adapter);

        getAirCondition();
    }


    // AsyncTask를 통해 미세먼지 농도 가져옴
    public void getAirCondition() {

        GetWeatherAsyncTask task = new GetWeatherAsyncTask(this,GetWeatherAsyncTask.GET_WEATHER, new EventListener() {
            @Override
            public void onEvent(Object obj) {
                if(obj instanceof ArrayList) {
                    data = (ArrayList<WeatherData>)obj;
                    adapter.setData(data);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        task.execute();
    }


}
