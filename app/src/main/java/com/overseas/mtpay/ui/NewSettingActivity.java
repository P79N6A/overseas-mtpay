package com.overseas.mtpay.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.overseas.mtpay.R;
import com.overseas.mtpay.adapter.BaseLogicAdapter;
import com.overseas.mtpay.bean.entity.ArrayItem;
import com.overseas.mtpay.db.AppConfigDef;
import com.overseas.mtpay.db.AppConfigHelper;
import com.overseas.mtpay.ui.base.BaseViewActivity;
import com.overseas.mtpay.utils.ItemDataUtils;
import com.overseas.mtpay.utils.ViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewSettingActivity extends BaseViewActivity {
    @BindView(R.id.llSettingPrint)
    LinearLayout llSettingPrint;//打印设置
    @BindView(R.id.llAuthCode)
    LinearLayout llAuthCode;//设置验证码
    @BindView(R.id.llKeyDownload)
    LinearLayout llKeyDownload;//密钥下载
    @BindView(R.id.llSettingSignAgain)
    LinearLayout llSettingSignAgain;//重新签到
    @BindView(R.id.llPasswordManager)
    LinearLayout llPasswordManager;//密码管理
    @BindView(R.id.llAbout)
    LinearLayout llAbout;//关于
    @BindView(R.id.llCancel)
    LinearLayout llCancel;//交易撤销
    private Dialog cardlinkDialog;
    private TextView tvNumber;
    private String DEFAULTNUMBER = "1";
    private int TIP_SETTING_CODE = 102;

    BaseLogicAdapter<ArrayItem> printItemAdapter = null;
    MaterialDialog printDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {//初始化数据
    }

    private void initView() {
        setMainView(R.layout.activity_setting_layout);
        showTitleBack();
        setTitleText(getResources().getString(R.string.setting));
        tvNumber = (TextView) findViewById(R.id.tvNumber);
        LinearLayout llChangeCode = (LinearLayout) findViewById(R.id.llChangeCode);
        String number = AppConfigHelper.getConfig(AppConfigDef.print_number);
        if (!TextUtils.isEmpty(number)) {
            tvNumber.setText(number);
        } else {
            tvNumber.setText(DEFAULTNUMBER);
        }
        ButterKnife.bind(this);
//        llChangeCode.setVisibility(View.GONE);
    }

    @Override
    protected void onTitleBackClikced() {
        startActivity(NewMainActivity.getStartIntent(this));
        super.onTitleBackClikced();
    }

    @Override
    public void onBackPressed() {
        startActivity(NewMainActivity.getStartIntent(this));
        super.onBackPressed();
    }

    @OnClick({R.id.llSettingPrint, R.id.llAuthCode, R.id.llKeyDownload, R.id.llSettingSignAgain, R.id.llPasswordManager, R.id.llAbout, R.id.llCancel, R.id.llTipSetting, R.id.llChangeCode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llSettingPrint://打印设置
                if (printDialog == null || (!printDialog.isShowing() && printDialog.isCancelled())) {
                    showPrintItems();
                }
                break;
//            case R.id.llAuthCode://验证码
//                startActivity(NewAuthCodeSettingActivity.getStartIntent(this));
//                break;
//            case R.id.llKeyDownload://密钥下载
//                doDownloadKeyAction();
//                break;
//            case R.id.llSettingSignAgain://再次签到
//                doSignOnAction();
//                break;
//            case R.id.llPasswordManager://收银员管理
//                startActivity(new Intent(NewSettingActivity.this, NewCashierManagerActivity.class));
////                startActivity(new Intent(NewSettingActivity.this, NewPasswordSettingActivity.class));
//                break;
            case R.id.llAbout://关于
                startActivity(new Intent(NewSettingActivity.this, NewAboutActivity.class));
                break;
//            case R.id.llCancel://交易撤销
//                voidTrans();
//                break;
            case R.id.llTipSetting:
                startActivityForResult(new Intent(NewSettingActivity.this, InputPassWordActivity.class), TIP_SETTING_CODE);
                break;
            case R.id.llChangeCode:
                startActivity(new Intent(NewSettingActivity.this, ModifyPasswordActivity.class));
                break;

        }
    }

    private void showPrintItems() {
        final List<ArrayItem> printItems = ItemDataUtils.getPrintNums();
        printItemAdapter = new BaseLogicAdapter<ArrayItem>(this, printItems, R.layout.item_print) {

            @Override
            public void convert(ViewHolder helper, final ArrayItem item, final int position) {
                helper.setText(R.id.tvPrintItem, item.getShowValue());
                final ImageView ivPrintItem = helper.getView(R.id.ivPrintItem);
                String num = AppConfigHelper.getConfig(AppConfigDef.print_number, "1");
                ivPrintItem.setPressed(item.getShowValue().equals(num));
                helper.getView(R.id.ivPrintItemDivider).setPressed(item.getShowValue().equals(num));
                helper.getView(R.id.llPrintItem).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvNumber.setText(printItems.get(position).getShowValue());
                        AppConfigHelper.setConfig(AppConfigDef.print_number, printItems.get(position).getShowValue());
                        if (null != printItemAdapter) {
                            printItemAdapter.notifyDataSetChanged();
                        }
                        if (null != printDialog && printDialog.isShowing()) {
                            printDialog.dismiss();
                        }
                    }
                });
            }

        };
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_item_print, null);
        dialogView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        ListView lvPrintItem = (ListView) dialogView.findViewById(R.id.lvPrintItem);
        lvPrintItem.setAdapter(printItemAdapter);
        MaterialDialog.Builder printDialogBuilder = new MaterialDialog.Builder(this);
        printDialog = printDialogBuilder.customView(dialogView, false).build();
        printDialog.show();
    }

    private void voidTrans() {
//        startActivity(NewVoidTransActivity.getStartIntent(this, ));
    }

//    private void doSignOnAction() {
//        startActivity(NewSettleActivity.getStartIntnet(this));
//    }
//
//    private void doDownloadKeyAction() {
//        startActivity(NewDownloadKeyActivity.getStartIntnet(this));
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TIP_SETTING_CODE && resultCode == RESULT_OK) {
            startActivity(new Intent(NewSettingActivity.this, TipParameterSettingActivity.class));
            finish();
        }
    }


}
