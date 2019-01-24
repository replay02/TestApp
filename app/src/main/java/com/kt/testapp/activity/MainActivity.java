package com.kt.testapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kt.testapp.R;
import com.kt.testapp.utils.MyLog;

public class MainActivity extends AppCompatActivity {

    Button btnStart;
    Button btnStart2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button)findViewById(R.id.btn_start_msg);
        btnStart2 = (Button)findViewById(R.id.btn_start_new_activity);

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
    }

    public void goMessage(String msg) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.setAction(Intent.ACTION_INSERT);
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sendIntent.setType("text/plain");

        // Verify that the intent will resolve to an activity
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
}
