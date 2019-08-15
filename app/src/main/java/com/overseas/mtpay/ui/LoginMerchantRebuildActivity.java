package com.overseas.mtpay.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.log.Logger;
import com.overseas.mtpay.R;
import com.overseas.mtpay.app.App;
import com.overseas.mtpay.bean.LoginInitResp;
import com.overseas.mtpay.bean.LoginTerminalResp;
import com.overseas.mtpay.bean.SysTipsResp;
import com.overseas.mtpay.broadcastreceiver.Alarmreceiver;
import com.overseas.mtpay.db.AppConfigDef;
import com.overseas.mtpay.db.AppConfigHelper;
import com.overseas.mtpay.db.Constants;
import com.overseas.mtpay.device.DeviceManager;
import com.overseas.mtpay.http.NetConfig;
import com.overseas.mtpay.http.NetRequest;
import com.overseas.mtpay.ui.base.BaseViewActivity;
import com.overseas.mtpay.utils.CommonDigistKeyListener;
import com.overseas.mtpay.utils.Configs;
import com.overseas.mtpay.utils.PreferenceHelper;
import com.overseas.mtpay.utils.SharePreferenceUtil;
import com.overseas.mtpay.utils.Tools;
import com.overseas.mtpay.utils.UIHelper;
import com.overseas.mtpay.utils.XEditText;
import com.overseas.mtpay.utils.widget.CommonToastUtil;


/**
 * 商户终端体系改造后登陆,界面改造后
 */
public class LoginMerchantRebuildActivity extends BaseViewActivity implements OnEditorActionListener, XEditText.DrawableRightListener {
    /**
     * 密码最大长度
     */
    private int REQUEST_CODE_SCAN = 112;
    private static final int MAX_PWD_LENGTH = 18;
//    protected LoginPresenter2 loginpresenter;

    private EditText et_operater, etMerchantId;
    private XEditText et_password;
    private TextView tvMerchantId;
    private CheckBox cb_remember_psw;
    private TextView tvForgetPwd;
    private ImageView ivLoginClose;
    private boolean isShowPassword = false;

    //由于登录前无法获取数据库信息，从Sp中读取相关数据
    private PreferenceHelper spHelper;
    public static final String ACTION_GET_EXCHANGE = "action_get_exchange";
    /**
     * Map
     * key:商户名 value:mid
     */
    private String lastLoginId, lastPasswd, lastLoginMid;
    private boolean isRemember;
    SharePreferenceUtil spUtils;
    private TextView tvVersion;

    /**
     * 重新进入login界面，清除之前打开的所有界面
     *
     * @param context
     */
    public static void reloadLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginMerchantRebuildActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spUtils = new SharePreferenceUtil(LoginMerchantRebuildActivity.this);
        spHelper = new PreferenceHelper(this, AppConfigDef.SP_loginedInfo);
        initView();
//        if (BuildConfig.DEBUG) {
//            et_operater.setText("0005");
//            et_password.setText("111111");
//        }
        showVersion();
    }

    private void showVersion() {
        tvVersion.setText("Version:" + getVersionName(this));
    }

    private String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packInfo == null) {
            return "0";
        }
        return TextUtils.isEmpty(packInfo.versionName) ? "0" : packInfo.versionName;
    }

    private void initView() {
        setTitleText("");
        setMainView(R.layout.activity_login_new_ui);
        setTitleRightImage(R.drawable.ic_init);
        tvVersion = (TextView) findViewById(R.id.tvVersion);
        cb_remember_psw = (CheckBox) findViewById(R.id.cb_remember_psw);
        cb_remember_psw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_remember_psw.setTextColor(0xff0074e1);
                    spHelper.putBoolean(AppConfigDef.SP_isRemember, true);
                } else {
                    cb_remember_psw.setTextColor(0xff888888);
                    spHelper.putBoolean(AppConfigDef.SP_isRemember, false);
                }
            }
        });
        isRemember = spHelper.getBoolean(AppConfigDef.SP_isRemember, false);
        cb_remember_psw.setChecked(isRemember);
        cb_remember_psw.setTextColor(isRemember ? 0xff0074e1 : 0xff888888);
        setOnClickListenerById(R.id.btn_login, this);
//        setOnClickListenerById(R.id.btnSwitchLanguage, this);

        initUserEt();
    }

    @Override
    protected void onTitleRightClicked() {
        super.onTitleRightClicked();
//        CommonToastUtil.showMsgAbove(this, CommonToastUtil.LEVEL_WARN, "初始化");
        //终端扫描初始化
        doScan();
    }

    private void doScan() {
        Intent i = new Intent(this, ScanActivity.class);
        startActivityForResult(i, REQUEST_CODE_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            String noStr = data.getStringExtra(Configs.CODE);
            if (!TextUtils.isEmpty(noStr)) {
//                CommonToastUtil.showMsgAbove(this, CommonToastUtil.LEVEL_WARN, Configs.CODE + ":" + noStr);
                netInitInfo(noStr);
            }
        }
    }

    private void netInitInfo(String noStr) {
        progresser.showProgress();
        JSONObject json = new JSONObject();
        json.put("qr_code", noStr);
        json.put("sn", App.getInstance().terminalSn());
        json.put("language", AppConfigHelper.getConfig(AppConfigDef.SWITCH_LANGUAGE));
        NetRequest.getInstance().fakePost(NetConfig.POST_LOGIN_INIT_INFO, json, "0", new NetRequest.NetCallback() {
            @Override
            public void onSuccess(String resp) {
                progresser.showContent();
                LoginInitResp resp1 = JSONObject.parseObject(resp, LoginInitResp.class);
                spUtils.setShopId(resp1.getResult().getShop_id());
                spUtils.setHeadShopId(resp1.getResult().getHead_shop_id());
                spUtils.setRSA(resp1.getResult().getPublic_key());
                spUtils.setMerchantAddr(resp1.getResult().getMerchant_addr());
                spUtils.setMerchantName(resp1.getResult().getMerchant_name());
                spUtils.setMerchantTel(resp1.getResult().getMerchant_tel());
                AppConfigHelper.setConfig(AppConfigDef.RsaKey, resp1.getResult().getPublic_key());
                CommonToastUtil.showMsgAbove(LoginMerchantRebuildActivity.this, CommonToastUtil.LEVEL_SUCCESS, getResources().getString(R.string.Successful_activation));
                Logger.d("ShopId: " + spUtils.getShopId());
                Logger.d("RsaKey: " + AppConfigHelper.getConfig(AppConfigDef.RsaKey));
            }

            @Override
            public void onFailed(String code, String message) {
                progresser.showContent();
                CommonToastUtil.showMsgAbove(LoginMerchantRebuildActivity.this, CommonToastUtil.LEVEL_WARN, message);
                Logger.d("Login onFailed: " + code + "--------" + message);
            }
        });
    }

    private void initUserEt() {
        et_password = (XEditText) findViewById(R.id.et_password);
        et_password.setDrawableRightListener(this);
        et_password.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                findViewById(R.id.llPwdInput).setPressed(hasFocus);
            }
        });
        et_operater = (EditText) findViewById(R.id.et_operator_account);
        et_operater.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                findViewById(R.id.llUserNameInput).setPressed(hasFocus);
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_operater.getWindowToken(), 0); //强制隐藏键盘
                }
            }
        });
        //MO不弹出软键盘
        if (DeviceManager.getInstance().getDeviceType() == DeviceManager.DEVICE_TYPE_WIZARHAND_M0 || DeviceManager.getInstance().getDeviceType() == DeviceManager.DEVICE_TYPE_WIZARHAND_Q1) {
            etMerchantId.setInputType(InputType.TYPE_NULL);
            et_operater.setInputType(InputType.TYPE_NULL);
        }
        et_password.setOnEditorActionListener(this);
        et_password.setKeyListener(new CommonDigistKeyListener(false, R.string.english_only_can_input));
        et_operater.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                et_operater.setSelection(et_operater.getText().toString().length());
            }
        });
        if (isRemember) {
            String name = lastLoginId;
            String pwd = lastPasswd;
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) {
                et_operater.setText(name);
                if (DeviceManager.getInstance().getDeviceType() == DeviceManager.DEVICE_TYPE_WIZARHAND_M0 || DeviceManager.getInstance().getDeviceType() == DeviceManager.DEVICE_TYPE_WIZARHAND_Q1) {
                    et_password.setText(pwd);
                } else {
                    et_password.setText(pwd);
                }
            }
        }
        if (spHelper.getBoolean(AppConfigDef.SP_isRemember, false)) {
            et_password.setText(spHelper.getString(AppConfigDef.SP_lastPasswd, ""));
            et_operater.setText(spHelper.getString(AppConfigDef.SP_lastLoginId, ""));
        }
        et_password.setSelection(et_password.getText().length());
        et_operater.setSelection(et_operater.getText().length());
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_login:
                final String usename = et_operater.getText().toString();
                String password = et_password.getText().toString();
                UIHelper.hideSoftInput(this);
                if (TextUtils.isEmpty(usename)) {
                    CommonToastUtil.showMsgAbove(this, CommonToastUtil.LEVEL_WARN, getResources().getString(R.string.Enter_username));
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    CommonToastUtil.showMsgAbove(this, CommonToastUtil.LEVEL_WARN, getResources().getString(R.string.Enter_password));
                    return;
                }
                if (TextUtils.isEmpty(spUtils.getRSA())) {
                    CommonToastUtil.showMsgAbove(this, CommonToastUtil.LEVEL_WARN, getResources().getString(R.string.Please_init));
                    return;
                }
                doLogin(usename, password);
                break;

        }
    }

    private void doLogin(String usename, String password) {
        progresser.showProgress();
        if (TextUtils.isEmpty(AppConfigHelper.getConfig(AppConfigDef.RsaKey))) {
            AppConfigHelper.setConfig(AppConfigDef.RsaKey, spUtils.getRSA().trim());
        }
        JSONObject json = new JSONObject();
        json.put("login_name", usename);
        json.put("passwd", Tools.md5(password));
        json.put("terminal_ver", getVersionName(this));
        JSONObject sysParam = new JSONObject();
        sysParam.put("head_shop_id", spUtils.getHeadShopId());
        sysParam.put("shop_id", spUtils.getShopId());
        sysParam.put("sn", App.getInstance().terminalSn());
        sysParam.put("language", AppConfigHelper.getConfig(AppConfigDef.SWITCH_LANGUAGE));
        json.put("sysParam", sysParam);
//        String data = json.toJSONString();
        NetRequest.getInstance().fakePost(NetConfig.POST_LOGIN, json, "1", new NetRequest.NetCallback() {
            @Override
            public void onSuccess(String resp) {
                progresser.showContent();
                LoginTerminalResp resp1 = JSONObject.parseObject(resp, LoginTerminalResp.class);

                JSONObject jsonObject = JSONObject.parseObject(resp1.getResult().getTips_percentage());
                if (null != jsonObject) {
                    AppConfigHelper.setConfig(AppConfigDef.tipsPercentage, resp1.getResult().getTips_percentage());
                    AppConfigHelper.setConfig(AppConfigDef.percentP1, jsonObject.getString("p1"));
                    AppConfigHelper.setConfig(AppConfigDef.percentP2, jsonObject.getString("p2"));
                    AppConfigHelper.setConfig(AppConfigDef.percentP3, jsonObject.getString("p3"));
                }
                AppConfigHelper.setConfig(AppConfigDef.collectTips, resp1.getResult().getCollect_tips());//是否启用小费  ON启用 OFF禁用
                AppConfigHelper.setConfig(AppConfigDef.tipsCustomAllow, resp1.getResult().getTips_custom_allow());//是否允许顾客输入小费金额  T允许  F不允许
                AppConfigHelper.setConfig(AppConfigDef.tipsTerminalAllow, resp1.getResult().getTips_terminal_allow());//是否允许客户端输入小费金额  T允许  F不允许
                AppConfigHelper.setConfig(AppConfigDef.AesKey, resp1.getResult().getAes_secret());
                AppConfigHelper.setConfig(AppConfigDef.tipsPercentageAllow, resp1.getResult().getTips_percentage_allow());//是否允许手动设置百分比，T允许，F不允许
                AppConfigHelper.setConfig(AppConfigDef.merchantName, resp1.getResult().getMerchant_name());// 商户名称
                Logger.d("AesKey: " + AppConfigHelper.getConfig(AppConfigDef.AesKey));
                AppConfigHelper.setConfig(AppConfigDef.isLogin, Constants.TRUE);
                Logger.d("isLogin: " + AppConfigHelper.getConfig(AppConfigDef.isLogin));
                spUtils.setOperId(resp1.getResult().getOper_id());
                spUtils.setOpeName(resp1.getResult().getOper_name());
                AppConfigHelper.setConfig(AppConfigDef.authFlag, resp1.getResult().getAdmin_flag());
                updateTip();
//                sendBroadcast();
                spHelper.putString(AppConfigDef.SP_lastLoginMid, lastLoginMid);
                if (spHelper.getBoolean(AppConfigDef.SP_isRemember, false)) {
                    spHelper.putString(AppConfigDef.SP_lastLoginId, et_operater.getText().toString());
                    spHelper.putString(AppConfigDef.SP_lastPasswd, et_password.getText().toString());
                }

//                startNewActivity(NewMainActivity.class);
            }

            @Override
            public void onFailed(String code, String message) {
                progresser.showContent();
                CommonToastUtil.showMsgAbove(LoginMerchantRebuildActivity.this, CommonToastUtil.LEVEL_WARN, message);
                Logger.d("Login onFailed: " + code + "--------" + message);
            }
        });
    }

    private void updateTip() {
        JSONObject json = new JSONObject();
        JSONObject sysParam = new JSONObject();
        sysParam.put("head_shop_id", spUtils.getHeadShopId());
        sysParam.put("shop_id", spUtils.getShopId());
        sysParam.put("sn", App.getInstance().terminalSn());
        sysParam.put("language", AppConfigHelper.getConfig(AppConfigDef.SWITCH_LANGUAGE));
        json.put("sysParam", sysParam);
//        String data = json.toJSONString();
        NetRequest.getInstance().fakePost(NetConfig.POST_SYS_QUERY_TIPS, json, "2", new NetRequest.NetCallback() {
            @Override
            public void onSuccess(String resp) {
                SysTipsResp resp1 = JSONObject.parseObject(resp, SysTipsResp.class);
                if (null != resp1.getResult()) {
                    String exchangeRate = resp1.getResult().getRate();
                    if (!TextUtils.isEmpty(exchangeRate)) {
                        AppConfigHelper.setConfig(AppConfigDef.exchangeRate, exchangeRate);
                        Intent intent1 = new Intent();
                        intent1.setAction(ACTION_GET_EXCHANGE);
                        LocalBroadcastManager.getInstance(LoginMerchantRebuildActivity.this).sendBroadcast(intent1);
                        startNewActivity(NewMainActivity.class);
                    }
                }
            }

            @Override
            public void onFailed(String code, String message) {
                Toast.makeText(LoginMerchantRebuildActivity.this, message, Toast.LENGTH_SHORT).show();
                Logger.d("Login onFailed: " + code + "--------" + message);
            }
        });
    }

    private void sendBroadcast() {
        Intent intent = new Intent(LoginMerchantRebuildActivity.this, Alarmreceiver.class);
        intent.setAction(Constants.UPDATE);
        PendingIntent sender = PendingIntent.getBroadcast(LoginMerchantRebuildActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm.cancel(sender);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 24 * 60 * 60 * 1000, sender);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.et_password && actionId == EditorInfo.IME_ACTION_GO) {
            findViewById(R.id.btn_login).performClick();
            return true;
        }
        return false;
    }

    @Override
    public void onDrawableRightClick(View view) {
        isShowPassword = !isShowPassword;
        String tag = "";
        if (et_password.getText().toString() != null) {
            tag = et_password.getText().toString();
//        } else if (!TextUtils.isEmpty(et_password.getText().toString().trim())){
//            et_password.setTag(et_password.getText().toString().trim());
//            tag = et_password.getTag().toString();
        }
        if (isShowPassword) {
            et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            if (!TextUtils.isEmpty(tag)) {
                et_password.setText(tag);
            }
            et_password.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.icon_show_pw), null);
        } else {
            et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
//            if (!TextUtils.isEmpty(tag)) {
//                et_password.setText(StringUtilUI.copyChar(tag.length(), "*"));
//            }
            et_password.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.icon_hide_pw), null);
        }
    }
}
