package com.kt.testapp.activity;

import android.support.v7.app.AppCompatActivity;

import com.kt.testapp.utils.CustProgressDialog;

/**
 * Created by kim-young-hyun on 24/01/2019.
 */

public class BaseActivity extends AppCompatActivity {
    protected String LOG_TAG = this.getClass().getSimpleName();
    protected CustProgressDialog pd;
    protected void showProgress() {

        if (pd == null)
            pd = CustProgressDialog.getInstance(this,false);

        if (pd.isShowing()==false) {

            boolean isNotRunning = this.isFinishing();
            if(!isNotRunning)
                pd.show();
        }
    }

    protected void hideProgress() {

        if (pd == null) return ;

        if (pd.isShowing()==true)
            pd.dismiss();
    }

}
