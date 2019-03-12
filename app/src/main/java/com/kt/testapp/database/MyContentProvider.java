package com.kt.testapp.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by kim-young-hyun on 12/03/2019.
 */

public class MyContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.kt.testapp.MyContentProvider";

    public static final String BASE_PATH = DataBaseScheme.WeatherLocation._TABLENAME;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final String DATAS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.kt.testapp.weather_location";
    public static final String DATA_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.kt.testapp.weather_location";

    public static final int GET_ALL = 0; //
    public static final int GET_ONE = 1; //
//    public static final int GET_ONE2 = 2; //

    private UriMatcher uriMatcher = buildUriMatcher();

    private SQLiteDatabase db;

    private static UriMatcher buildUriMatcher()
    {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Uris to match, and the code to return when matched
        matcher.addURI(AUTHORITY, BASE_PATH, GET_ALL); // all
        matcher.addURI(AUTHORITY, BASE_PATH + "/*", GET_ONE); // specific data by string ID
//        matcher.addURI(AUTHORITY, BASE_PATH + "/#", GET_ONE2); // specific data by numeric ID
        return matcher;
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        DBHelper mySQLiteOpenHelper = new DBHelper(context);
        db = mySQLiteOpenHelper.getWritableDatabase();
        return (db == null) ? false : true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        switch (uriMatcher.match(uri)) {
            case GET_ALL:
                Cursor c = db.query(DataBaseScheme.WeatherLocation._TABLENAME, projection, selection, selectionArgs, null, null, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            case GET_ONE:
                String name = uri.getLastPathSegment();
                c = db.rawQuery("SELECT * FROM weather_location WHERE station_name = '" + name + "'", null);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case GET_ALL:
                return DATAS_MIME_TYPE; // list
            case GET_ONE:
                return DATA_MIME_TYPE; // single item
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri muri = null;
        long id = db.insert(DataBaseScheme.WeatherLocation._TABLENAME, null, values);
        if (id > 0) {
            muri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(muri, null,false);
        } else {
            //삽입 실패
        }
        return muri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int id = db.delete(DataBaseScheme.WeatherLocation._TABLENAME, selection, selectionArgs);
        if (id > 0) {
            Uri muri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(muri, null);
        } else {
            //삭제 실패했을 경우
        }
        return id;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int id = db.update(DataBaseScheme.WeatherLocation._TABLENAME, values, selection, selectionArgs);
        if (id > 0) {
            Uri muri  = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(muri, null);
        } else {
            //업데이트 실패
        }
        return id;
    }
}
