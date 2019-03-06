package com.kt.testapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.kt.testapp.service.BootJobIntentService;
import com.kt.testapp.service.MyService;

/**
 * Created by kim-young-hyun on 04/03/2019.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent i = new Intent(context, MyService.class);


            BootJobIntentService.enqueueWork(context, i);

            Toast.makeText(context,"부팅완료", Toast.LENGTH_LONG).show();
        }
    }






}
