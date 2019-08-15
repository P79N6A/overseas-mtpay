package com.overseas.mtpay.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.common.Response;
import com.overseas.mtpay.R;
import com.overseas.mtpay.app.App;
import com.overseas.mtpay.db.AppConfigDef;
import com.overseas.mtpay.db.AppConfigHelper;
import com.overseas.mtpay.db.Constants;
import com.overseas.mtpay.device.DeviceManager;
import com.overseas.mtpay.http.NetCodeConstants;
import com.overseas.mtpay.http.NetConfig;
import com.overseas.mtpay.http.NetRequest;
import com.overseas.mtpay.ui.base.BaseViewActivity;
import com.overseas.mtpay.ui.fragment.InputPadFragment;
import com.overseas.mtpay.ui.fragment.OnMumberClickListener;
import com.overseas.mtpay.utils.AppConfiger;
import com.overseas.mtpay.utils.MsgTipsDialog;
import com.overseas.mtpay.utils.SharePreferenceUtil;
import com.overseas.mtpay.utils.Tools;
import com.overseas.mtpay.utils.UIHelper;

import java.util.HashMap;
import java.util.Map;


public class InputPassWordActivity extends BaseViewActivity implements OnMumberClickListener {
    private String password = null;
    private EditText etPassword;
    private AppConfiger present;
    private static final String TAG = InputPassWordActivity.class.getSimpleName();
    public static final String ORDER_NO = "order";//交易撤销
    private InputPadFragment inputPadFragment;

    private SharePreferenceUtil spUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        present = new AppConfiger(this);
        setMainView(R.layout.input_password);
        setTitleText(getResources().getString(R.string.input_password_title));
        showTitleBack();
        etPassword = findViewById(R.id.input_password);
        etPassword.setSelection(etPassword.getText().length());

        spUtils = new SharePreferenceUtil(App.getInstance());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        if (DeviceManager.getInstance().getDeviceType() != DeviceManager.DEVICE_TYPE_WIZARHAND_Q1) {
            inputPadFragment = InputPadFragment.newInstance(InputPadFragment.KEYBOARDTYPE_SIMPLE);
            findViewById(R.id.flInputPad).setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().replace(R.id.flInputPad, inputPadFragment).commit();
            inputPadFragment.setOnMumberClickListener(this);
            inputPadFragment.setEditView(etPassword, InputPadFragment.InputType.TYPE_INPUT_NORMAL);
        }

    }

    @Override
    public void onSubmit() {
        handlePasswd();
    }

    private void handlePasswd() {
        password = etPassword.getText().toString();
        Log.e(TAG, password);
        password = password.replace("\n", "").trim();
        checkPassword(password);
    }

    private void checkPassword(String password) {
        progresser.showProgress();
        JSONObject json = new JSONObject();
        json.put("revoke_pwd", Tools.md5(password));
        JSONObject sysParam = new JSONObject();
        sysParam.put("head_shop_id", spUtils.getHeadShopId());
        sysParam.put("shop_id", spUtils.getShopId());
        sysParam.put("oper_id", spUtils.getOperId());
        sysParam.put("oper_name", spUtils.getOperName());
        sysParam.put("sn", App.getInstance().terminalSn());
        sysParam.put("language", AppConfigHelper.getConfig(AppConfigDef.SWITCH_LANGUAGE));
        json.put("sysParam", sysParam);
        NetRequest.getInstance().fakePost(NetConfig.POST_VERIFY_PWD, json, "2", new NetRequest.NetCallback() {
            @Override
            public void onSuccess(String resp) {
                progresser.showContent();
                setResult(RESULT_OK);
                InputPassWordActivity.this.finish();
            }

            @Override
            public void onFailed(String code, String message) {
                progresser.showContent();
                if (NetCodeConstants.MERCHANT_DISABLE_CODE_68.equals(code)) {
                    MsgTipsDialog.toLoginActivity(InputPassWordActivity.this, NetCodeConstants.MERCHANT_DISABLE_MSG);
                } else if (NetCodeConstants.TERMINAL_REGISTER_CODE_53.equals(code)) {
                    MsgTipsDialog.toLoginActivity(InputPassWordActivity.this, NetCodeConstants.TERMINAL_REGISTER_MSG);
                } else {
                    UIHelper.ToastMessage(InputPassWordActivity.this, message, Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_ENTER == keyCode) {
            handlePasswd();
        }
        return super.onKeyUp(keyCode, event);
    }
}
