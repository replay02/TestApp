/*
 * EVCP(Electronic Vehicle Charging Platform) version 1.0
 *
 *  Copyright ⓒ 2016 kt corp. All rights reserved.
 *
 *  This is a proprietary software of kt corp, and you may not use this file except in
 *  compliance with license agreement with kt corp. Any redistribution or use of this
 *  software, with or without modification shall be strictly prohibited without prior written
 *  approval of kt corp, and the copyright notice above does not evidence any actual or
 *  intended publication of such software.
 */

package com.kt.testapp.utils;

import android.util.Log;

import com.kt.testapp.app.TestApplication;


public class MyLog {

    public static final void e(String TAG, String message) {
        if (TestApplication.DEBUG)
            showLog(0,TAG,message);
    }

    public static final void w(String TAG, String message) {
        if (TestApplication.DEBUG)
            showLog(1,TAG,message);
    }

    public static final void i(String TAG, String message) {
        if (TestApplication.DEBUG)
            showLog(2,TAG,message);
    }

    public static final void d(String TAG, String message) {
        if (TestApplication.DEBUG)
            showLog(3,TAG,message);
    }

    public static final void v(String TAG, String message) {
        if (TestApplication.DEBUG)
            showLog(4,TAG,message);
    }

    private static void showLog(int logLevel, String Tag, String message ) {
        int maxLogize = 1000;
        try {
            for(int i = 0; i <= message.length() / maxLogize; i++) {
                int start = i * maxLogize;
                int end = (i+1) * maxLogize;
                end = end > message.length() ? message.length() : end;
                if(logLevel == 0) {
                    Log.e(Tag, message.substring(start, end));
                }
                else if(logLevel == 1) {
                    Log.w(Tag, message.substring(start, end));
                }
                else if(logLevel == 2) {
                    Log.i(Tag, message.substring(start, end));
                }
                else if(logLevel == 3) {
                    Log.d(Tag, message.substring(start, end));
                }
                else if(logLevel == 4) {
                    Log.v(Tag, message.substring(start, end));
                }
            }
        }
        catch (Exception e) {

        }
    }
}