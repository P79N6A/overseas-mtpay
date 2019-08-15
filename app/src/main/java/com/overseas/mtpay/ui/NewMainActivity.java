package com.overseas.mtpay.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.overseas.mtpay.R;
import com.overseas.mtpay.app.App;
import com.overseas.mtpay.bean.entity.ItemBean;
import com.overseas.mtpay.db.AppConfigDef;
import com.overseas.mtpay.db.AppConfigHelper;
import com.overseas.mtpay.db.AppStateDef;
import com.overseas.mtpay.db.AppStateManager;
import com.overseas.mtpay.db.Constants;
import com.overseas.mtpay.ui.base.NewBaseMainActivity;
import com.overseas.mtpay.ui.fragment.NewQ2GatheringFragment;
import com.overseas.mtpay.ui.fragment.ReceivablesFragment;
import com.overseas.mtpay.utils.Calculater;
import com.overseas.mtpay.utils.ImageLoadApp;
import com.overseas.mtpay.utils.ItemDataUtils;
import com.overseas.mtpay.utils.SharePreferenceUtil;
import com.overseas.mtpay.utils.widget.CommonToastUtil;
import com.overseas.mtpay.utils.widget.RoundAngleImageView;
import com.overseas.mtpay.device.DeviceManager;


public class NewMainActivity extends NewBaseMainActivity implements OnClickListener, ReceivablesFragment.PayBtnClickListener, ReceivablesFragment.OnSaveListener {

    private static final String LOG_TAG = NewMainActivity.class.getSimpleName();
    private static final String EXTAR_RETURN = "isReturn";
    private ListView lvType;
    private DrawerLayout dl;
    private ActionBarDrawerToggle drawerToggle;
    private int TICKET_USE = 0;
    private int TRANSACTION_RECORD = 1;
    private int TICKET_USE_RECORD = 2;
    private int TODAY_RECORD = 3;
    private int SETTING = 4;
    private int TIP_SETTING = 5;
    private int EXIT = 6;
    private int TESTWEXIN = 7;
    private int MODIFY_SECURE_PASSWORD = 8;
    private static long holdtime = 0L;
    private static long backholdtime = 0L;
    private final static String TAG_GATHERING_FRAGMENT_Q2 = "gathering_fragment2";
    private NewQ2GatheringFragment newQ2GatheringFragment;
    private final static String TAG_RECEIVABLES_FRAGMENT = "receivables_fragment";
    private ReceivablesFragment receivablesFragment;
    private int PAY_REQUEST_CODE = 100;
    private int SET_REQUEST_CODE = 101;
    private int TIP_SETTING_CODE = 102;
    private String payAmounts, tipAmount;
    private int amount;

    private boolean bankcardProgressing = false;
    private boolean bankCardComfirmCard = false;
    private SharePreferenceUtil spUtils;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, NewMainActivity.class);
    }

    public static Intent getReturnIntent(Context context) {
        Intent intent = new Intent(context, NewMainActivity.class);
        intent.putExtra(EXTAR_RETURN, true);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spUtils = new SharePreferenceUtil(App.getInstance());
        initView();
        initData();
        requestFocus();
    }

    @Override
    protected void onPause() {
        clearToast();
        super.onPause();
    }

    @Override
    protected void onStop() {
        clearToast();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        clearToast();
        super.onDestroy();
    }

    private void initView() {
        setTitle();
        dl = (DrawerLayout) findViewById(R.id.dl);
        lvType = (ListView) findViewById(R.id.lvType);
        RoundAngleImageView roundAngleImageView = (RoundAngleImageView) findViewById(R.id.ivMerchantLogo);
        TextView tvMerchantName = (TextView) findViewById(R.id.tvMerchantName);
        TextView tvOpreator = (TextView) findViewById(R.id.tvOpreator);
//        String url = MerchantLogoHelper.getMerchantLogoUrl();
        String url = "";
        showImage(url, roundAngleImageView);
        tvOpreator.setText(getResources().getString(R.string.employee) + ":" + spUtils.getOperName());
//        tvOpreator.setText(getResources().getString(R.string.employee) + ":" + AppConfigHelper.getConfig(AppConfigDef.operatorTrueName, getResources().getString(R.string.employee) + "00"));
        if (TextUtils.isEmpty(url)) {
            roundAngleImageView.setBackgroundResource(R.drawable.logo);
        }
        tvMerchantName.setText(AppConfigHelper.getConfig(AppConfigDef.merchantName));
        drawerToggle = new ActionBarDrawerToggle(this, dl, (Toolbar) findViewById(R.id.toolbarOwner), R.string.drawer_open, R.string.drawer_close);
        drawerToggle.syncState();
        dl.addDrawerListener(drawerToggle);
        setMainFragment();
    }

    private void setTitle() {
        Toolbar toolbarOwner = (Toolbar) findViewById(R.id.toolbarOwner);
        if (toolbarOwner != null) {
            toolbarOwner.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbarOwner);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        TextView tvTitleOwner = (TextView) findViewById(R.id.tvTitleOwner);
        if (tvTitleOwner != null) {
            tvTitleOwner.setText(getString(R.string.sale));
            tvTitleOwner.setVisibility(View.VISIBLE);
        }
    }

    public String getAmount() {
        String amount = "";
        if (DeviceManager.getInstance().getDeviceType() == DeviceManager.DEVICE_TYPE_WIZARHAND_Q1) {
        } else {
            amount = newQ2GatheringFragment.getAmount();
        }
        return amount;
    }


    private void setMainFragment() {
//        if (DeviceManager.getInstance().getDeviceType() == DeviceManager.DEVICE_TYPE_WIZARHAND_Q1) {
//        } else {
        newQ2GatheringFragment = new NewQ2GatheringFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.flContent, newQ2GatheringFragment, TAG_GATHERING_FRAGMENT_Q2).commit();
        newQ2GatheringFragment.setOnConfirmListener(new NewQ2GatheringFragment.OnConfirmListener() {
            @Override
            public void onComfirm() {
                goQ2Pay();
            }
        });
//        }
//        receivablesFragment = new ReceivablesFragment();
//        getSupportFragmentManager().beginTransaction().add(R.id.flContent, receivablesFragment, TAG_RECEIVABLES_FRAGMENT).commit();
    }

    private void showImage(String url, ImageView imageView) {
        if (!TextUtils.isEmpty(url)) {
            ImageLoader.getInstance().displayImage(url, imageView, ImageLoadApp.getOptions());
            imageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - backholdtime > 2000 && receivablesFragment == null) {
                backholdtime = System.currentTimeMillis();
                CommonToastUtil.showMsgBelow(this, CommonToastUtil.LEVEL_INFO, getResources().getString(R.string.exit_info));
                return true;
            } else if (null != receivablesFragment) {
                receivablesFragment = null;
                getSupportFragmentManager().beginTransaction().replace(R.id.flContent, newQ2GatheringFragment, TAG_GATHERING_FRAGMENT_Q2).commit();
                return false;
            } else {
                AppStateManager.clearCache();
                AppConfigHelper.clearCache();
                AppStateManager.setState(AppStateDef.isLogin, Constants.FALSE);
                NewMainActivity.this.finish();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initData() {
        lvType.setAdapter(new QuickAdapter<ItemBean>(NewMainActivity.this, R.layout.item_left_layout, ItemDataUtils.getItemBeans(NewMainActivity.this)) {
            @Override
            protected void convert(BaseAdapterHelper helper, final ItemBean item) {
                helper.setImageResource(R.id.item_img, item.getImg())
                        .setText(R.id.item_tv, item.getTitle());
                helper.getView(R.id.llLferItem).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TRANSACTION_RECORD == item.getRealValue()) {//交易记录
                            //TODO
                            startNewActivity(NewTranlogActivity.class);
                        } else if (TICKET_USE_RECORD == item.getRealValue()) {//卡券核销记录
                            Toast.makeText(NewMainActivity.this, "暂不支持卡券核销", Toast.LENGTH_SHORT).show();
                        } else if (TODAY_RECORD == item.getRealValue()) {//今日汇总
                            startNewActivity(NewDailySumActivityPlus.class);
                        } else if (SETTING == item.getRealValue()) {//设置
                            startActivity(new Intent(NewMainActivity.this, NewSettingActivity.class));
                            NewMainActivity.this.finish();
                        } else if (EXIT == item.getRealValue()) {//退出
                            AppConfigHelper.clearCache();
                            NewMainActivity.this.finish();
                            Intent intent = new Intent(NewMainActivity.this, LoginMerchantRebuildActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else if (TESTWEXIN == item.getRealValue()) {//测试第三方支付
                        }
                    }
                });
            }
        });
    }

    private void goQ2Pay() {
        String collectTips = AppConfigHelper.getConfig(AppConfigDef.collectTips);
        if (!TextUtils.isEmpty(collectTips) && collectTips.equals(Constants.COLLETOFF) && receivablesFragment == null) {
            goUnionPay();
        } else if (receivablesFragment != null) {
            Intent intent = new Intent();
            intent.putExtra(Constants.realAmount, Calculater.formotYuan(payAmounts));
            intent.putExtra(Constants.tipsAmount, Calculater.formotYuan(tipAmount));
            intent.setClass(NewMainActivity.this, NewMicroActivity.class);
            startActivity(intent);
            finish();
        } else {
            receivablesFragment = new ReceivablesFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.flContent, receivablesFragment, TAG_RECEIVABLES_FRAGMENT).commit();

        }
    }

    private void goUnionPay() {
        String amount = "";
        if (DeviceManager.getInstance().getDeviceType() == DeviceManager.DEVICE_TYPE_WIZARHAND_Q1) {
        } else {
            amount = newQ2GatheringFragment.getAmount();
        }
        if (TextUtils.isEmpty(amount)) {
            Toast.makeText(this, getResources().getString(R.string.payamount_warn), Toast.LENGTH_SHORT).show();
            return;
        } else {
            Intent intent = new Intent();
            if (amount.equals("0.00")) {
                Toast.makeText(this, getResources().getString(R.string.payamount_warn), Toast.LENGTH_SHORT).show();
                return;
            }
            intent.putExtra(Constants.realAmount, Calculater.formotYuan(amount));
            intent.setClass(NewMainActivity.this, NewMicroActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void showForeverInfo(String msg) {
        CommonToastUtil.showForeverToast(this, msg, CommonToastUtil.LEVEL_INFO, CommonToastUtil.Y_BELOW_CENTER);
    }

    private void showProgressMsg(String msg) {
        CommonToastUtil.showMsgBelow(this, CommonToastUtil.LEVEL_INFO, msg);
    }

    private void showErrorMsg(String msg) {
        CommonToastUtil.showMsgBelow(this, CommonToastUtil.LEVEL_ERROR, msg);
    }

    private void showWarnMsg(String msg) {
        CommonToastUtil.showMsgBelow(this, CommonToastUtil.LEVEL_WARN, msg);
    }

    private void showNotifyMsg(String msg) {
        CommonToastUtil.showMsgBelow(this, CommonToastUtil.LEVEL_INFO, msg);
    }


    private void clearToast() {
        CommonToastUtil.stopAnyway();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (null != newQ2GatheringFragment) {
            newQ2GatheringFragment.reset();
        }
        super.onNewIntent(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlToolbarRightOwner:
                findViewById(R.id.tvSettingParams).performClick();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPayBtnClick(String payAmount, String tipsAmount) {
        Intent intent = new Intent();
        if (payAmount.equals("0.00")) {
            Toast.makeText(this, getResources().getString(R.string.payamount_warn), Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra(Constants.realAmount, Calculater.formotYuan(payAmount));
        intent.putExtra(Constants.tipsAmount, Calculater.formotYuan(tipsAmount));
        intent.setClass(NewMainActivity.this, NewMicroActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSave(String payAmount, String tipsAmount) {
        payAmounts = payAmount;
        tipAmount = tipsAmount;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TIP_SETTING_CODE && resultCode == RESULT_OK) {
            startActivity(new Intent(NewMainActivity.this, TipParameterSettingActivity.class));
            finish();
        }
    }
}
