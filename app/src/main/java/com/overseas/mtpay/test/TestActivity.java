package com.overseas.mtpay.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.overseas.mtpay.R;
import com.overseas.mtpay.utils.MsgTipsDialog;


/**
 * Created by jack on 2019/8/7.
 */

public class TestActivity extends Activity implements View.OnClickListener {

    Button btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        btn = findViewById(R.id.btn_ok);
        btn.setOnClickListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                showMsgDialog();
                break;
            default:
                break;
        }
    }

    private void showMsgDialog() {
        MsgTipsDialog.toLoginActivity(this, getString(R.string.warn_merchant_disable));
    }


    private void showScreenParam() {
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        int sw = this.getResources().getConfiguration().smallestScreenWidthDp;
        log("屏幕分辨率:" + width + "*" + height + ",dpi:" + dm.densityDpi + ",sw:" + sw);
    }

    private void log(String str) {
        Log.d(TestActivity.class.getName(), str);
    }
}
