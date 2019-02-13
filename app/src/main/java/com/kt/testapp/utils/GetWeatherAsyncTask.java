package com.kt.testapp.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.kt.testapp.activity.BaseActivity;
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
import java.util.WeakHashMap;

/**
 * Created by kim-young-hyun on 12/02/2019.
 */

public class GetWeatherAsyncTask extends AsyncTask<String,Void,Object> {

    private final String API_KEY = "ipIQQAASbqJVNrR%2BryI5oa0a%2B1G0W3JNcaku6UP3ODNFlHHr95tN%2F7%2BlQ8Jr44%2BdtffXOXPJDvkBC7cWGZkvUg%3D%3D";

    private String result = null;;
    private String api = "";
    private EventListener listener;
    private BaseActivity activity;
    private WeatherData baseData;

    public final static String GET_WEATHER = "/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty";
    public final static String GET_LOCATION = "/MsrstnInfoInqireSvc/getMsrstnList";

    public GetWeatherAsyncTask(BaseActivity activity, String api, EventListener listener) {
        this.api = api;
        this.listener = listener;
        this.activity = activity;
    }


    public void setWeatherData(WeatherData data) {
        this.baseData = data;
    }

    private Object parseData(String result) {
        ArrayList<WeatherData> datas = new ArrayList<>();  // GET_WEATHER return value;
//        WeatherData data = new WeatherData();  // GET_LOCATION return value;

        // XML 파싱
        try {
            XmlPullParserFactory factory = null;
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser parser = factory.newPullParser() ;

            InputStream inputStream = new ByteArrayInputStream(result.getBytes());
            parser.setInput(inputStream, null) ;



            String tagName="";

            int eventType = parser.getEventType() ;


            if(GET_WEATHER.equals(api)) {
                boolean isItem = false;

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

                return datas;
            }

            else if(GET_LOCATION.equals(api)) {

                boolean isLocationItem = false;

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        // XML 데이터 시작
                    } else if (eventType == XmlPullParser.START_TAG) {


                        tagName = parser.getName();

                        if(tagName.equals("dmX") || tagName.equals("dmY")) {
                            isLocationItem = true;
                        }

                    }  else if (eventType == XmlPullParser.TEXT) {
                        if(isLocationItem && tagName.equals("dmX")) {
                            baseData.setDmX(parser.getText());
                        }

                        else if(isLocationItem && tagName.equals("dmY")) {
                            baseData.setDmY(parser.getText());
                        }

                    } else if (eventType == XmlPullParser.END_TAG) {
                        tagName = parser.getName() ;
                        if(tagName.equals("dmX") || tagName.equals("dmY")) {
                            isLocationItem = false;
                        }
                    }

                    eventType = parser.next();
                }
                return baseData;
            }

            else {

            }

        } catch (Exception e) {
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.showProgress();
    }

    @Override
    protected Object doInBackground(String... params) {

        StringBuilder sb = null;

        try {
            StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest" + api); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + API_KEY); /*Service Key*/


            if(GET_WEATHER.equals(api)) {
                urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("40", "UTF-8")); /*한 페이지 결과 수*/
                urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
                urlBuilder.append("&" + URLEncoder.encode("sidoName","UTF-8") + "=" + URLEncoder.encode("서울", "UTF-8")); /*시도 이름 (서울, 부산, 대구, 인천, 광주, 대전, 울산, 경기, 강원, 충북, 충남, 전북, 전남, 경북, 경남, 제주, 세종)*/
                urlBuilder.append("&" + URLEncoder.encode("ver","UTF-8") + "=" + URLEncoder.encode("1.3", "UTF-8")); /*버전별 상세 결과 참고문서 참조*/
            }

            else if(GET_LOCATION.equals(api)) {
                urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*한 페이지 결과 수*/
                urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
                urlBuilder.append("&" + URLEncoder.encode("addr","UTF-8") + "=" + URLEncoder.encode("서울", "UTF-8")); /*시도 이름 (서울, 부산, 대구, 인천, 광주, 대전, 울산, 경기, 강원, 충북, 충남, 전북, 전남, 경북, 경남, 제주, 세종)*/
                urlBuilder.append("&" + URLEncoder.encode("stationName","UTF-8") + "=" + URLEncoder.encode(baseData.getStationName(), "UTF-8")); /*시도 이름 (서울, 부산, 대구, 인천, 광주, 대전, 울산, 경기, 강원, 충북, 충남, 전북, 전남, 경북, 경남, 제주, 세종)*/

            }
            else {

            }

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

        Object retVal = parseData(result);

        return retVal;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Object s) {
        super.onPostExecute(s);
        listener.onEvent(s);
        activity.hideProgress();
    }
}