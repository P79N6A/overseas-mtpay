package com.overseas.mtpay.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.overseas.mtpay.R;
import com.overseas.mtpay.app.App;
import com.overseas.mtpay.broadcastreceiver.Alarmreceiver;
import com.overseas.mtpay.db.AppConfigDef;
import com.overseas.mtpay.db.AppConfigHelper;
import com.overseas.mtpay.utils.Calculater;
import com.overseas.mtpay.utils.InputPad;
import com.overseas.mtpay.utils.NewCashTextWatcher;
import com.overseas.mtpay.utils.StringUtil;

public class NewQ2GatheringFragment extends BaseViewFragment {
    private final static String TAG_LOG = "NewGatheringFragment";
    private EditText etAmount;
    private TextView tvShowRMB;
    private TextView tvTips;
    private String exchangeRate;
    private InputPad inputPad;
    private OnConfirmListener onConfirmListener;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != progresser) {
                progresser.showContent();
            }
            if (null != exchangeRate) {
                exchangeRate = AppConfigHelper.getConfig(AppConfigDef.exchangeRate);
            }
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
        }
    };


    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    @Override
    public void initView() {
        setMainView(R.layout.fragment_gathering_for_q2);
        etAmount = mainView.findViewById(R.id.etAmount);
        tvShowRMB = mainView.findViewById(R.id.tvShowRMB);
        tvTips = mainView.findViewById(R.id.tvTips);
        exchangeRate = AppConfigHelper.getConfig(AppConfigDef.exchangeRate);
        tvTips.setText(String.format("Note: 1 CAD = approx.%s CNY", exchangeRate));
        //没有汇率  则一直加载中
//        exchangeRate = "5";
        if (TextUtils.isEmpty(exchangeRate)) {
            progresser.showProgress();
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(Alarmreceiver.ACTION_GET_EXCHANGE));
        }
        initInputPad();
        etAmount.addTextChangedListener(new NewCashTextWatcher(etAmount, new NewCashTextWatcher.EditTextChange() {
            @Override
            public void dataChange(String string) {
                if (etAmount.getText().toString().trim() != null) {
                    String showRMB = String.format("%.2f", Float.parseFloat(Calculater.multiply(etAmount.getText().toString().trim(), exchangeRate)));
                    tvShowRMB.setText(showRMB);
                }
            }
        }));
    }

    private void initInputPad() {
        inputPad = (InputPad) mainView.findViewById(R.id.inputPad);
        etAmount.setSelection(etAmount.getText().length());
        etAmount.setInputType(InputType.TYPE_NULL);
        inputPad.addEditView(etAmount, InputPad.InputType.TYPE_INPUT_MONEY);
        inputPad.setOnConfirmListener(new InputPad.OnComfirmListener() {
            @Override
            public void onConfirm(EditText editText) {
                onSubmit();
            }
        });
    }

    private void onSubmit() {
        if (TextUtils.isEmpty(getAmount())) {
            Toast.makeText(App.getInstance(), App.getInstance().getResources().getString(R.string.payamount_warn), Toast.LENGTH_SHORT).show();
            return;
        }
        if (onConfirmListener != null) {
            onConfirmListener.onComfirm();
        }
    }


    public String getAmount() {
        if (null == etAmount) {
            Log.d(TAG_LOG, "etAmount is null");
            return null;
        }
        String amount = etAmount.getText().toString().trim();
        if (!StringUtil.isSameString(amount, "0.00") && !TextUtils.isEmpty(amount)) {
            Log.d(TAG_LOG, amount + "");
            return amount;
        } else {
            return null;
        }
    }


    public void reset() {
        etAmount.setText("0.00");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
            receiver = null;
        }
    }

    public interface OnConfirmListener {
        void onComfirm();
    }
}