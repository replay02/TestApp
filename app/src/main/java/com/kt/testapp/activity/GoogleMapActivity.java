package com.kt.testapp.activity;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.Location;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.kakao.kakaonavi.options.RpOption;
import com.kt.testapp.database.DBHelper;
import com.kt.testapp.R;
import com.kt.testapp.data.WeatherData;
import com.kt.testapp.inf.EventListener;
import com.kt.testapp.utils.GetWeatherAsyncTask;
import com.kt.testapp.utils.MyLog;

/**
 * Created by kim-young-hyun on 22/01/2019.
 */

public class GoogleMapActivity extends BaseActivity implements OnMapReadyCallback {

    private WeatherData weather;

    public static String KEY_WEATHER_OBJ = "weather";

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();

        if (intent != null && intent.getSerializableExtra(KEY_WEATHER_OBJ) != null) {
            weather = (WeatherData) intent.getSerializableExtra(KEY_WEATHER_OBJ);
        }
    }


    @Override
    public void onMapReady(final GoogleMap map) {
        googleMap = map;

        final DBHelper dbHelper = DBHelper.getInstance(GoogleMapActivity.this);


        LatLng latlng = dbHelper.getLatLng(weather.getStationName());
        // db에 위도 경도 저장 된 내역이 있다면 db 값으로 셋팅
        if (latlng != null) {
            try {
                weather.setDmX(String.valueOf(latlng.latitude));
                weather.setDmY(String.valueOf(latlng.longitude));
                setMapMarker(weather);

                Toast.makeText(GoogleMapActivity.this, "load from saved station latlng", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                MyLog.e(LOG_TAG, e.toString());
            }

        } else {
            GetWeatherAsyncTask task = new GetWeatherAsyncTask(this, GetWeatherAsyncTask.GET_LOCATION, new EventListener() {
                @Override
                public void onEvent(Object obj) {
                    MyLog.e(LOG_TAG, "GetWeatherAsyncTask onEvent()'s obj : " + obj.toString());
                    if (obj instanceof WeatherData) {

                        WeatherData data = ((WeatherData) obj);

                        // 측정소의 위도경도 정보를 정상적으로 가져오지 못했을 경우
                        if (data == null || TextUtils.isEmpty(data.getDmY()) || TextUtils.isEmpty(data.getDmX())) {
                            Toast.makeText(GoogleMapActivity.this, "load data from server error", Toast.LENGTH_SHORT).show();
                        } else {
                            setMapMarker((WeatherData) obj);
                            long insertedId = dbHelper.insertStationLatLng(((WeatherData) obj).getStationName(),
                                    ((WeatherData) obj).getDmX(), ((WeatherData) obj).getDmY());

                            Toast.makeText(GoogleMapActivity.this, "load data from server and saved station's latlng data at " + insertedId, Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });
            task.setWeatherData(weather);
            task.execute();
        }
    }

    private void setMapMarker(WeatherData data) {

        LatLng position;
        try {
            position = new LatLng(Double.parseDouble(data.getDmX()), Double.parseDouble(data.getDmY()));
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_location), Toast.LENGTH_SHORT).show();
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


        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                showAlertDialog(marker);
            }
        });

        GoogleMap.InfoWindowAdapter adapter = new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {

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

    private void sendKakaoNavi(String desName, LatLng position) {
        // 목적지 이름과 위경도 좌표를 통한 길안내 목적지 생성
        Location destination = Location.newBuilder(desName, position.longitude, position.latitude).build();
        // 길안내에 사용할 옵션 설정
        NaviOptions options = NaviOptions.newBuilder().setCoordType(CoordType.WGS84)./*setVehicleType(VehicleType.FIRST).*/setRpOption(RpOption.FAST).build();
        KakaoNaviParams.Builder builder = KakaoNaviParams.newBuilder(destination).setNaviOptions(options);
        KakaoNaviService.getInstance().navigate(GoogleMapActivity.this, builder.build());
    }


    private void showAlertDialog(final Marker marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("길안내");
        builder.setMessage("이 위치로 카카오내비를 연동하여 길안내를 하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendKakaoNavi(marker.getTitle(), marker.getPosition());
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
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
