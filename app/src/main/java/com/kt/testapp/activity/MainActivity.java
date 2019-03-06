package com.kt.testapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kt.testapp.R;

public class MainActivity extends AppCompatActivity {

    Button btnStart;
    Button btnStart2;
    Button btnStart3;
    Button btnStart4;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button)findViewById(R.id.btn_start_msg);
        btnStart2 = (Button)findViewById(R.id.btn_start_new_activity);
        btnStart3 = (Button)findViewById(R.id.btn_image_grid);
        btnStart4 = (Button)findViewById(R.id.btn_start_check_service);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMessage("메시지 보내기 입니다");
            }
        });

        btnStart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNewActivity();
            }
        });
        btnStart3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goImageGridActivity();
            }
        });

        btnStart4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goCheckServiceActivity();
            }
        });

    }



    public void goMessage(String msg) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sendIntent.setType("text/plain");

        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent);
        }
        else {
            Toast.makeText(this,"해당하는 App이 존재하지 않습니다",Toast.LENGTH_LONG).show();
        }
    }

    public void goNewActivity() {
        Intent newIntent = new Intent(this, WeatherListActivity.class);
        startActivity(newIntent);
    }

    public void goImageGridActivity() {
        Intent newIntent = new Intent(this, GridImageActivity.class);
        startActivity(newIntent);
    }

    public void goCheckServiceActivity() {
        Intent newIntent = new Intent(this, CheckServiceActivity.class);
        startActivity(newIntent);
    }

}
