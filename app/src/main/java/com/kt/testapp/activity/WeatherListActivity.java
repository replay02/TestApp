package com.kt.testapp.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kt.testapp.R;
import com.kt.testapp.adapter.AdapterWeatherList;
import com.kt.testapp.data.WeatherData;
import com.kt.testapp.inf.EventListener;

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

public class WeatherListActivity extends BaseActivity {

    private final String API_KEY = "ipIQQAASbqJVNrR%2BryI5oa0a%2B1G0W3JNcaku6UP3ODNFlHHr95tN%2F7%2BlQ8Jr44%2BdtffXOXPJDvkBC7cWGZkvUg%3D%3D";

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
        recyclerView.setAdapter(adapter);

        getAirCondition(API_KEY);
    }


    // AsyncTask를 통해 미세먼지 농도 가져옴
    public void getAirCondition(String apikey) {

        GetWeatherAsyncTask task = new GetWeatherAsyncTask(this,apikey, new EventListener() {
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

    public static class GetWeatherAsyncTask extends AsyncTask<String,Void,ArrayList<WeatherData>> {

        private String result = null;;
        private String apiKey = null;
        private EventListener listener;
        private BaseActivity activity;

        public GetWeatherAsyncTask(BaseActivity activity, String API_KEY, EventListener listener) {
            apiKey = API_KEY;
            this.listener = listener;
            this.activity = activity;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activity.showProgress();
        }

        @Override
        protected ArrayList<WeatherData> doInBackground(String... params) {

            ArrayList<WeatherData> datas = new ArrayList<>();  // return value;

            StringBuilder sb = null;

            try {
                StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty"); /*URL*/
                urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + apiKey); /*Service Key*/
                urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("40", "UTF-8")); /*한 페이지 결과 수*/
                urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
                urlBuilder.append("&" + URLEncoder.encode("sidoName","UTF-8") + "=" + URLEncoder.encode("서울", "UTF-8")); /*시도 이름 (서울, 부산, 대구, 인천, 광주, 대전, 울산, 경기, 강원, 충북, 충남, 전북, 전남, 경북, 경남, 제주, 세종)*/
                urlBuilder.append("&" + URLEncoder.encode("ver","UTF-8") + "=" + URLEncoder.encode("1.3", "UTF-8")); /*버전별 상세 결과 참고문서 참조*/
                URL url = new URL(urlBuilder.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/xml");
                conn.setRequestProperty("Content-type", "application/xml");
                System.out.println("Response code: " + conn.getResponseCode());
                BufferedReader rd;
                if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                conn.disconnect();
            }

            catch (Exception e) {
                e.printStackTrace();
            }
            result = sb==null?"":sb.toString();


            // XML 파싱
            try {
                XmlPullParserFactory factory = null;
                factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);

                XmlPullParser parser = factory.newPullParser() ;

                InputStream inputStream = new ByteArrayInputStream(result.getBytes());
                parser.setInput(inputStream, null) ;


                boolean isItem = false;
                String tagName="";

                int eventType = parser.getEventType() ;

                WeatherData weather = null;

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        // XML 데이터 시작
                    } else if (eventType == XmlPullParser.START_TAG) {


                        tagName = parser.getName();

                        if(tagName.equals("item")) {
                            weather = new WeatherData();
                        }
                        if(tagName.equals("stationName") || tagName.equals("dataTime") ||
                                tagName.equals("pm10Value") || tagName.equals("pm25Value") ||
                                tagName.equals("pm10Grade1h") || tagName.equals("pm25Grade1h")) {
                            isItem = true;
                        }

                    }  else if (eventType == XmlPullParser.TEXT) {
                        if(isItem && tagName.equals("stationName")) {
                            weather.setStationName(parser.getText());
                        }

                        else if(isItem && tagName.equals("dataTime")) {
                            weather.setDataTime(parser.getText());
                        }

                        else if(isItem && tagName.equals("pm10Value")) {
                            weather.setPm10Value(parser.getText());
                        }
                        else if(isItem && tagName.equals("pm25Value")) {
                            weather.setPm25Value(parser.getText());
                        }

                        else if(isItem && tagName.equals("pm10Grade1h")) {
                            weather.setPm10gradeString(activity, parser.getText());
                        }

                        else if(isItem && tagName.equals("pm25Grade1h")) {
                            weather.setPm25gradeString(activity, parser.getText());
                        }

                    } else if (eventType == XmlPullParser.END_TAG) {
                        tagName = parser.getName() ;

                        if(tagName.equals("item")) {
                            datas.add(weather);
                        }

                        if(tagName.equals("stationName") || tagName.equals("dataTime") ||
                                tagName.equals("pm10Value") || tagName.equals("pm25Value") ||
                                tagName.equals("pm10Grade1h") || tagName.equals("pm25Grade1h")) {
                            isItem = false;

                        }
                    }

                    eventType = parser.next();
                }


            } catch (Exception e) {
            }

            return datas;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ArrayList<WeatherData> s) {
            super.onPostExecute(s);
            listener.onEvent(s);
            activity.hideProgress();
        }
    }
}
