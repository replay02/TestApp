package com.kt.testapp.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by kim-young-hyun on 20/02/2019.
 */

public class DataBaseScheme {

    public static final class WeatherLocation implements BaseColumns {
        public static final String _TABLENAME = "weather_location";

        public static final String LAT = "lat";
        public static final String LON = "lon";
        public static final String STATION_NAME = "station_name";

        public static final String _DROP = "DROP TABLE IF EXISTS " + _TABLENAME;
        public static final String _CREATE =
                "CREATE TABLE IF NOT EXISTS "+_TABLENAME+"("
                        +_ID + " integer primary key autoincrement, "
                        +STATION_NAME + " text,  "
                        +LAT + " text,  "
                        +LON + " text)";
    }
}
