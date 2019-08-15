package com.overseas.mtpay.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.overseas.mtpay.R;
import com.overseas.mtpay.utils.MsgTipsDialog;

/**
 * Created by jack on 2019/8/7.
 */

public class TestBCR extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        intent.getAction();
        Log.d(TestBCR.class.getName(), intent.getAction());
        MsgTipsDialog.toLoginActivity(context, context.getString(R.string.warn_terminal_register));
    }
}
