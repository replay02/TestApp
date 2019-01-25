package com.kt.testapp.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.kt.testapp.R;


public class CustProgressDialog extends ProgressDialog {

    private ProgressBar progress;
    private final String TAG_LOG = CustProgressDialog.class.getSimpleName();


    public static CustProgressDialog getInstance(Context context, boolean cancelable) {
        CustProgressDialog progressDialog = new CustProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(cancelable);
        return progressDialog;
    }

    public CustProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_progress_dialog);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progress = (ProgressBar) findViewById(R.id.pb);
        progress.setMax(100);
    }

    @Override
    public void show() {
        try {
            super.show();

        } catch (Exception e) {
            MyLog.e(TAG_LOG, "Exception : " + e.toString());
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();


        } catch (Exception e) {
            MyLog.e(TAG_LOG, "Exception : " + e.toString());
        }
    }


}
