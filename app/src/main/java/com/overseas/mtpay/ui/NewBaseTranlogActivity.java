package com.overseas.mtpay.ui;

import android.content.Intent;
import android.os.Bundle;

import com.overseas.mtpay.R;
import com.overseas.mtpay.base.ProgressLayout;
import com.overseas.mtpay.ui.base.BaseViewActivity;

/**
 * Created by wu on 16/3/27.
 */
public class NewBaseTranlogActivity extends BaseViewActivity {

    protected ProgressLayout progresser;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_tranlog_detail_new);
        progresser = (ProgressLayout) findViewById(R.id.progress);
        progresser.showContent();
    }
//
//    protected void requestFocus(){
//        RelativeLayout focuslayout = (RelativeLayout) findViewById(R.id.rlRequestFocusLayout);
//        focuslayout.requestFocus();
//    }

    public void startNewActivity(Class<?> cls) {
        startActivity(new Intent(this, cls));
        finish();
    }
}
