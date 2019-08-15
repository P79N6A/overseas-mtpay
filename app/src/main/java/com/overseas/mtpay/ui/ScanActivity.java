package com.overseas.mtpay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.journeyapps.barcodescanner.Capture;
import com.journeyapps.barcodescanner.CaptureManager;
import com.overseas.mtpay.R;
import com.overseas.mtpay.ui.base.BaseViewActivity;
import com.overseas.mtpay.utils.Configs;
import com.overseas.mtpay.utils.widget.CommonToastUtil;


public class ScanActivity extends BaseViewActivity {

    private Capture capture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.activity_scan, null);
//        initView(view);
        initScan(savedInstanceState, view);
        setContentView(view);
    }

    private void initView(View view) {
//        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != capture) {
            capture.onResume();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (null != capture) {
            capture.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != capture) {
            capture.onDestroy();
        }
    }


    private void initScan(Bundle savedInstanceState, View view) {
        capture = new Capture(this);
        capture.onCreate(savedInstanceState, view);
        capture.setListener(new CaptureManager.CaptureListener() {
            @Override
            public void onCaptureCancel() {
                capture.onPause();
                CommonToastUtil.showMsgAbove(ScanActivity.this, CommonToastUtil.LEVEL_WARN,"解码失败，请重新扫描");
                capture.resumeDelay(2000);
            }

            @Override
            public void onCaptureResult(Intent intent, String result) {
                capture.onPause();
                onGetResult(result);
            }
        });
    }

    private void onGetResult(String result) {
        Intent intent = getIntent();
        intent.putExtra(Configs.CODE,result);
        setResult(RESULT_OK,intent);
        finish();
    }


}
