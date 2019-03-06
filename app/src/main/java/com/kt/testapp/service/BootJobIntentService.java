package com.kt.testapp.service;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

/**
 * Created by kim-young-hyun on 04/03/2019.
 */

public class BootJobIntentService extends JobIntentService {

    public static final int JOB_ID = 0x01;


    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, BootJobIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        startForegroundService(intent);
//        startService(intent);
    }


    @Override
    public boolean isStopped() {
        return super.isStopped();
    }


}
