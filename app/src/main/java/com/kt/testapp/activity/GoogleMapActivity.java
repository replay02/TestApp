package com.kt.testapp.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kt.testapp.R;
import com.kt.testapp.adapter.AdapterWeatherList;
import com.kt.testapp.data.WeatherData;
import com.kt.testapp.inf.EventListener;
import com.kt.testapp.utils.GetWeatherAsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by kim-young-hyun on 22/01/2019.
 */

public class GoogleMapActivity extends BaseActivity implements OnMapReadyCallback {

//    private String locationName;
//    private String snippet;

    private WeatherData weather;

//    public static String KEY_NAME = "location_name";
//    public static String KEY_SNIPPET = "location_name";
    public static String KEY_WHEATHER_OBJ = "weather";

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();

        if(intent != null && intent.getSerializableExtra(KEY_WHEATHER_OBJ) != null) {
//            locationName = intent.getBu(KEY_NAME);
//            snippet = intent.getStringExtra(KEY_SNIPPET);
            weather = (WeatherData) intent.getSerializableExtra(KEY_WHEATHER_OBJ);
        }
    }


    @Override
    public void onMapReady(final GoogleMap map) {
        googleMap = map;
        GetWeatherAsyncTask task = new GetWeatherAsyncTask(this,GetWeatherAsyncTask.GET_LOCATION, new EventListener() {
            @Override
            public void onEvent(Object obj) {
                Log.e(LOG_TAG,"GetWeatherAsyncTask onEvent()'s obj : " + obj.toString());
                if(obj instanceof WeatherData) {
                    setMapMarker((WeatherData)obj);
                }
            }
        });
        task.setWeatherData(weather);
        task.execute();

    }

    private void setMapMarker(WeatherData data) {

        LatLng position;
        try {
            position = new LatLng(Double.parseDouble(data.getDmX()),Double.parseDouble(data.getDmY()));
        }
        catch (Exception e) {
            Toast.makeText(this,getString(R.string.error_location),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.56, 126.97)));  // 서울로 이동
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            return;
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.title(data.getStationName());
        markerOptions.snippet(makeSnippetString(data));
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        GoogleMap.InfoWindowAdapter adapter = new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(GoogleMapActivity.this);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(GoogleMapActivity.this);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(GoogleMapActivity.this);
                snippet.setTextColor(Color.GRAY);
                snippet.setGravity(Gravity.CENTER);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        };


        googleMap.setInfoWindowAdapter(adapter);
    }


    private String makeSnippetString(WeatherData data) {
        String retVal = getString(R.string.time) + data.getDataTime() + "\n\n" +
                getString(R.string.num_pm10) + data.getPm10Value() + getString(R.string.pm_unit) + "\n" +
                getString(R.string.num_pm25) + data.getPm10gradeString() + "\n\n" +
                getString(R.string.grade_pm10) + data.getPm25Value() + getString(R.string.pm_unit) + "\n" +
                getString(R.string.grade_pm25) + data.getPm25gradeString();
        return retVal;
    }
}
