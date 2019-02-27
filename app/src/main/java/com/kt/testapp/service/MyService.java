package com.kt.testapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by kim-young-hyun on 27/02/2019.
 */

public class MyService extends Service {

    IBinder mBinder = new MyBinder();

    private int cnt = 0;

    private boolean isCon = false;

    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        isCon = true;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 (화면단 Activity 사이에서)
        // 통신(데이터를 주고받을) 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isCon) {
                    try {
                        Thread.sleep(1000);
                        cnt++;

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 호출될 때마다 실행
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isCon = false;

        // 서비스가 종료될 때 실행
    }

    public String getTime() { // 임의 랜덤값을 리턴하는 메서드
        return "서비스시작한지 " + cnt + "초가 지났습니다.";
    }


    public class MyBinder extends Binder {
        public MyService getService() { // 서비스 객체를 리턴
            return MyService.this;
        }
    }

}
