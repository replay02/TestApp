package com.kt.testapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;
import com.kt.testapp.utils.MyLog;

/**
 * Created by kim-young-hyun on 20/02/2019.
 */

public class DBHelper extends SQLiteOpenHelper {

    private final String TAG_LOG = DBHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "mytable.db";
    public static final int DATABASE_VERSION = 1;

    private static Context ctx;
    private static DBHelper dbHelper = null;
    private static SQLiteDatabase db = null;


    public DBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DataBaseScheme.WeatherLocation._CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL(DataBaseScheme.WeatherLocation._DROP);
            onCreate(db);
        }
    }


    // singleton helper객체 리턴
    public static synchronized final DBHelper getInstance(Context context) {
        ctx = context;

        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
            try {
                db = dbHelper.getWritableDatabase();
            } catch (SQLiteException se) {
                se.printStackTrace();
            }
        }
        return dbHelper;
    }


    // stationName과 위도,경도 좌표를 db에 넣음(stationName이 key 역할을 하므로 중복된 데이터 잇을 경우 삭제)
    public long insertStationLatLng(String stationName, String lat, String lon){
        ContentValues values = new ContentValues();
        values.put(DataBaseScheme.WeatherLocation.LAT, lat);
        values.put(DataBaseScheme.WeatherLocation.LON, lon);
        values.put(DataBaseScheme.WeatherLocation.STATION_NAME, stationName);

        long id = -1;
        db.beginTransaction();
        try {

            String table = DataBaseScheme.WeatherLocation._TABLENAME;

            // station name 에 해당하는 값이 있다면 먼저 지움
            String whereClause = DataBaseScheme.WeatherLocation.STATION_NAME + "=?";
            long deleted_id = db.delete(table, whereClause, new String[]{stationName});

            // value insert
            id = db.insert(table, null, values);
			MyLog.d(TAG_LOG, "deleted id : " + deleted_id);
            MyLog.d(TAG_LOG, "inserted id : " + id);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return id;
    }


    // stationName을 키값으로 위도경도 좌표를 만들어 리턴
    public LatLng getLatLng(String stationName) {

        LatLng retVal = null;
        String lat = "";
        String lng = "";
        String table = DataBaseScheme.WeatherLocation._TABLENAME;
        Cursor c;
        c = db.rawQuery(
                "select * "
                        + " from " + table
                        + " where " + DataBaseScheme.WeatherLocation.STATION_NAME + " = ?"
                , new String[]{stationName});

        if(c != null && c.getCount() > 0) {
            c.moveToFirst();
            lat = c.getString(c.getColumnIndex(DataBaseScheme.WeatherLocation.LAT));
            lng = c.getString(c.getColumnIndex(DataBaseScheme.WeatherLocation.LON));
        }

        try {
            retVal = new LatLng(Double.valueOf(lat),Double.valueOf(lng));
        }
        catch (Exception e) {

        }

        c.close();
        return retVal;
    }

}
