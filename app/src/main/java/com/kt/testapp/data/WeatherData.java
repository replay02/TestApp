package com.kt.testapp.data;

import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.kt.testapp.R;

import java.io.Serializable;

/**
 * Created by kim-young-hyun on 24/01/2019.
 */

public class WeatherData implements Serializable{

    private String stationName;
    private String dataTime;
    private String pm10Value;
    private String pm25Value;
    private String pm10gradeString;
    private String pm25gradeString;
    private String dmX;
    private String dmY;

    public String getPm10gradeString() {
        return pm10gradeString;
    }

    public void setPm10gradeString(Context ctx, String pm10Grade1H) {

        try {
            int g = Integer.parseInt(pm10Grade1H);

            switch (g) {
                case 1 :
                    pm10gradeString = ctx.getString(R.string.g1);
                    break;
                case 2 :
                    pm10gradeString = ctx.getString(R.string.g2);
                    break;
                case 3 :
                    pm10gradeString = ctx.getString(R.string.g3);
                    break;
                case 4 :
                    pm10gradeString = ctx.getString(R.string.g4);
                    break;
                default:
                    pm10gradeString = ctx.getString(R.string.g_error);
                    break;
            }
        }
        catch (Exception e) {
            pm10gradeString = ctx.getString(R.string.g_error);
        }
    }

    public String getPm25gradeString() {
        return pm25gradeString;
    }

    public void setPm25gradeString(Context ctx, String pm25Grade1H) {

        try {
            int g = Integer.parseInt(pm25Grade1H);

            switch (g) {
                case 1 :
                    pm25gradeString = ctx.getString(R.string.g1);
                    break;
                case 2 :
                    pm25gradeString = ctx.getString(R.string.g2);
                    break;
                case 3 :
                    pm25gradeString = ctx.getString(R.string.g3);
                    break;
                case 4 :
                    pm25gradeString = ctx.getString(R.string.g4);
                    break;
                default:
                    pm25gradeString = ctx.getString(R.string.g_error);
                    break;
            }
        }
        catch (Exception e) {
            pm25gradeString = ctx.getString(R.string.g_error);
        }
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    public String getPm10Value() {
        return pm10Value;
    }

    public void setPm10Value(String pm10Value) {
        this.pm10Value = pm10Value;
    }

    public String getPm25Value() {
        return pm25Value;
    }

    public void setPm25Value(String pm25Value) {
        this.pm25Value = pm25Value;
    }

    public String getDmX() {
        return dmX;
    }

    public void setDmX(String dmX) {
        this.dmX = dmX;
    }

    public String getDmY() {
        return dmY;
    }

    public void setDmY(String dmY) {
        this.dmY = dmY;
    }

    @Override
    public String toString() {
        String json = null;
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            json = ow.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }
}
