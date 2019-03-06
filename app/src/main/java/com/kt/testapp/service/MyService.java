package com.kt.testapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.kt.testapp.R;
import com.kt.testapp.activity.MainActivity;
import com.kt.testapp.utils.MyLog;

public class MyService extends Service {

    public IBinder mBinder = new MyBinder();

    private int cnt = 0;

    private boolean isCon = false;

    private MediaPlayer mp; // 음악 재생을 위한 객체

    public RemoteViews remoteViews;

    public int musicPos;

    public BroadcastReceiver reciver;

    public NotificationCompat.Builder builder26;
    public Notification.Builder builder;
    public NotificationManager notificationManager;

    public final int NOTI_ID = 0x11;
    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        isCon = true;

        reciver = new MusicButtonListener();

        IntentFilter theFilter = new IntentFilter();
        theFilter.addAction(MusicButtonListener.ACTIION_MYMUSIC);

        registerReceiver(reciver, theFilter);
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
    public int onStartCommand(Intent intent, int flags, int startId) {  // 서비스가 호출될 때마다 실행
        startForegroundService();

        mp = MediaPlayer.create(this, R.raw.test);
        mp.setLooping(false); // 반복재생
        mp.start(); // 노래 시작

        return super.onStartCommand(intent, flags, startId);
    }


    private void startForegroundService() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "testapp_service_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);

            builder26 = new NotificationCompat.Builder(this, CHANNEL_ID);

            remoteViews = new RemoteViews(getPackageName(),R.layout.notification_foreground);


            Intent startIntent = new Intent(MusicButtonListener.ACTIION_MYMUSIC);
            startIntent.putExtra("type",1);

            Intent stopIntent = new Intent(MusicButtonListener.ACTIION_MYMUSIC);
            stopIntent.putExtra("type",3);


            PendingIntent start = PendingIntent.getBroadcast(this, 0,
                    startIntent, 0);

            PendingIntent stop = PendingIntent.getBroadcast(this, 1,
                    stopIntent, 0);


            remoteViews.setOnClickPendingIntent(R.id.btn_start, start);
            remoteViews.setOnClickPendingIntent(R.id.btn_stop, stop);


            builder26.setSmallIcon(R.mipmap.ic_launcher)
                    .setContent(remoteViews)
                    .setContentIntent(pendingIntent);

            startForeground(NOTI_ID, builder26.build());

        } else {
            builder = new Notification.Builder(this);

            remoteViews = new RemoteViews(getPackageName(),R.layout.notification_foreground);

            Notification notification = builder.build();
            notification.contentView = remoteViews;
            notification.flags |= Notification.FLAG_ONGOING_EVENT;

            notification.icon = R.mipmap.ic_launcher;

            notification.contentIntent = pendingIntent;

            startForeground(NOTI_ID, builder.build());
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mp != null) {
            mp.stop();
            mp.release();
        }

        if(reciver != null) {

            unregisterReceiver(reciver);
            reciver = null;
        }
        isCon = false;
    }



    public String getTime() { // 임의 랜덤값을 리턴하는 메서드
        return "서비스 bind 이후 " + cnt + "초가 지났습니다.";
    }


    public class MyBinder extends Binder {
        public MyService getService() { // 서비스 객체를 리턴
            return MyService.this;
        }
    }


    public class MusicButtonListener extends BroadcastReceiver {

        public static final String ACTIION_MYMUSIC = "com.kt.testapp.intent.MYMUSIC";

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent != null && intent.getIntExtra("type",0) != 0) {

                switch (intent.getIntExtra("type",0)) {
                    case 1:  //start or pause
                        if(mp.isPlaying()) {
                            remoteViews.setImageViewResource(R.id.btn_start, R.drawable.ic_stat_play_arrow);
                            musicPos = mp.getCurrentPosition();
                            mp.pause();
                        }
                        else {
                            remoteViews.setImageViewResource(R.id.btn_start, R.drawable.ic_stat_pause);
                            mp.seekTo(musicPos);
                            mp.start();
                        }

                        if (Build.VERSION.SDK_INT >= 26) {
                            notificationManager.notify(NOTI_ID, builder26.build());
                        }
                        else {
                            notificationManager.notify(NOTI_ID, builder.build());
                        }

                        break;
                    case 3:  //stop
                        if(mp != null) {
                            mp.stop(); // 멈춤
                            mp.release(); // 자원 해제
                            stopForeground(true);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

}
