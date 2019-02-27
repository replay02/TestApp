package com.kt.testapp.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kt.testapp.R;
import com.kt.testapp.service.MyService;
import com.kt.testapp.service.MyService.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kim-young-hyun on 27/02/2019.
 */

public class CheckServiceActivity extends BaseActivity {
    private Button btnStartService;
    private Button btnStopService;
    private Button btnGetData;
    private TextView tvGoDocs;

    private Intent intent;

    private MyService myService; // 서비스 객체
    private boolean isService = false; // 서비스 중인 확인용

    private ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 서비스와 연결되었을 때 호출되는 메서드
            // 서비스 객체를 전역변수로 저장
            MyBinder mb = (MyBinder) service;
            myService = mb.getService(); // 서비스가 제공하는 메소드 호출하여 서비스쪽 객체를 전달받을수 있음

            Toast.makeText(CheckServiceActivity.this, "서비스가 연결됨",Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName name) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
            Toast.makeText(CheckServiceActivity.this, "서비스 연결이 해제됨",Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_service);

        btnStartService = (Button) findViewById(R.id.btn_1);
        btnStopService = (Button) findViewById(R.id.btn_2);
        btnGetData = (Button) findViewById(R.id.btn_3);

        tvGoDocs = (TextView) findViewById(R.id.doc);

        intent = new Intent(
                CheckServiceActivity.this,
                MyService.class);

        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bindService(intent,
                        conn, // 서비스와 연결에 대한 정의
                        Context.BIND_AUTO_CREATE);

                isService = true;
            }
        });

        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isService) {
                    unbindService(conn); // 서비스 종료
                    isService = false;
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "서비스가 시작되지 않았습니다",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        btnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isService) {
                    Toast.makeText(getApplicationContext(),
                            "서비스중이 아닙니다, 데이터받을수 없음",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                String data = myService.getTime();//서비스쪽 메소드로 값 전달 받아 호출
                Toast.makeText(getApplicationContext(),
                        "받아온 데이터 : " + data,
                        Toast.LENGTH_LONG).show();

            }
        });

        Linkify.TransformFilter mTransform = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher match, String url) {
                return "";
            }
        };
        Pattern pattern1 = Pattern.compile("보러가기");
        Linkify.addLinks(tvGoDocs, pattern1,
                "https://developer.android.com/guide/components/services?hl=ko",null,mTransform);

    }

}
