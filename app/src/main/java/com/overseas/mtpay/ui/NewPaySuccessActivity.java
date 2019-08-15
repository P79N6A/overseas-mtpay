package com.overseas.mtpay.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.overseas.mtpay.R;
import com.overseas.mtpay.db.AppConfigDef;
import com.overseas.mtpay.db.AppConfigHelper;
import com.overseas.mtpay.print.PrintServiceControllerProxy;
import com.overseas.mtpay.ui.base.BaseViewActivity;
import com.overseas.mtpay.utils.Tools;
import com.overseas.mtpay.utils.widget.CommonToastUtil;
import com.ui.dialog.DialogHelper;
import com.ui.dialog.NoticeDialogFragment;

/**
 * Created by blue_sky on 2016/3/23.
 */
public class NewPaySuccessActivity extends BaseViewActivity {
    ImageView ivPaySuccess;
    TextView tvAmount;
    private String payDes = "";

    public static final String EXTRA_PAY_TYPE = "payType";
    public static final String EXTRA_PAY_AMOUNT = "realAmount";

    public static Intent getStartIntent(Context context, String payType, String amount) {
        Intent intent = new Intent(context, NewPaySuccessActivity.class);
        intent.putExtra(EXTRA_PAY_TYPE, payType);
        intent.putExtra(EXTRA_PAY_AMOUNT, amount);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        print();
    }

    private void initView() {
        setMainView(R.layout.activity_new_paysuccess);
        showTitleBack();
        setTitleText(getResources().getString(R.string.payment_success));
        findViewById(R.id.btnContiuePay).setOnClickListener(this);
        ivPaySuccess = (ImageView) findViewById(R.id.ivPaySuccess);
        tvAmount = (TextView) findViewById(R.id.tvAmount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.btnContiuePay).requestFocus();
    }

    private void initData() {
        Intent payIntent = getIntent();
        if (null == payIntent) {
            CommonToastUtil.showMsgBelow(this, CommonToastUtil.LEVEL_WARN, "未获取到支付数据");
//            UIHelper.ToastMessage(this, "未获取到支付数据", Toast.LENGTH_SHORT);
            return;
        }
        String payType =  payIntent.getStringExtra(EXTRA_PAY_TYPE);
        if ("2".equals(payType)) {
            payDes = "支付宝";
            ivPaySuccess.setImageResource(R.drawable.success_zhifubao);
        } else if ("1".equals(payType)) {
            payDes = " 微信";
            ivPaySuccess.setImageResource(R.drawable.success_weixin);
        }
        String realAmount = payIntent.getStringExtra(EXTRA_PAY_AMOUNT);
        if (!TextUtils.isEmpty(realAmount)) {
            try {
                tvAmount.setText("$" + Tools.formatFen(Long.parseLong(realAmount)));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Log.e("error", "Long.parseLong(String) error");
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnContiuePay:
                startActivity(NewMainActivity.getStartIntent(this));
                finish();
                break;
        }
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

    private void print() {
        int printNumber = 1;
        if (!TextUtils.isEmpty(AppConfigHelper.getConfig(AppConfigDef.print_number))) {
            printNumber = Integer.parseInt(AppConfigHelper.getConfig(AppConfigDef.print_number));
        }
        final PrintServiceControllerProxy controller = new PrintServiceControllerProxy(this);
        switch (printNumber) {
            case 1:
//                if (AppConfigHelper.getConfig(AppConfigDef.SWITCH_LANGUAGE).equals("fr")) {
////                    PaymentApplication.getInstance().startActivity(WebPrintActivity.getStartIntent(PaymentApplication.getInstance(), AppConfigHelper.getConfig(AppConfigDef.PRINT_CONTEXT)));
//                    WebPrintHelper.getInstance().print(AppConfigHelper.getConfig(AppConfigDef.PRINT_CONTEXT));
//                } else {
                    controller.print(AppConfigHelper.getConfig(AppConfigDef.PRINT_CONTEXT));
//                }
                break;
            case 2:
//                if (AppConfigHelper.getConfig(AppConfigDef.SWITCH_LANGUAGE).equals("fr")) {
//                    WebPrintHelper.getInstance().print(AppConfigHelper.getConfig(AppConfigDef.PRINT_CONTEXT));
////                    PaymentApplication.getInstance().startActivity(WebPrintActivity.getStartIntent(PaymentApplication.getInstance(), AppConfigHelper.getConfig(AppConfigDef.PRINT_CONTEXT)));
//                } else {
                    controller.print(AppConfigHelper.getConfig(AppConfigDef.PRINT_CONTEXT));
//                }
                final NoticeDialogFragment dialogFragment = NoticeDialogFragment.newInstance("INFORMATION", "Customer Copy?", "YES", "NO");
                dialogFragment.setListener(new DialogHelper.DialogCallbackAndNo() {
                    @Override
                    public void callback() {
//                        if (AppConfigHelper.getConfig(AppConfigDef.SWITCH_LANGUAGE).equals("fr")) {
//                            WebPrintHelper.getInstance().print(AppConfigHelper.getConfig(AppConfigDef.PRINT_CUSTOMER_CONTEXT));
////                            PaymentApplication.getInstance().startActivity(WebPrintActivity.getStartIntent(PaymentApplication.getInstance(), AppConfigHelper.getConfig(AppConfigDef.PRINT_CUSTOMER_CONTEXT)));
//                        } else {
                            controller.print(AppConfigHelper.getConfig(AppConfigDef.PRINT_CUSTOMER_CONTEXT));
//                        }
                    }

                    @Override
                    public void callbackNo() {
                        dialogFragment.dismiss();
                    }
                });
                dialogFragment.show(getSupportFragmentManager(), "SimpleMsgDialogFragment");
                break;
            case 3:
//                if (AppConfigHelper.getConfig(AppConfigDef.SWITCH_LANGUAGE).equals("fr")) {
//                    WebPrintHelper.getInstance().print(AppConfigHelper.getConfig(AppConfigDef.PRINT_CONTEXT));
////                    PaymentApplication.getInstance().startActivity(WebPrintActivity.getStartIntent(PaymentApplication.getInstance(), AppConfigHelper.getConfig(AppConfigDef.PRINT_CONTEXT)));
//                } else {
                    controller.print(AppConfigHelper.getConfig(AppConfigDef.PRINT_CONTEXT));
//                }
                final NoticeDialogFragment fragmentDialog = NoticeDialogFragment.newInstance("INFORMATION", "Customer Copy?", "YES", "NO");
                fragmentDialog.setListener(new DialogHelper.DialogCallbackAndNo() {
                    @Override
                    public void callback() {
//                        if (AppConfigHelper.getConfig(AppConfigDef.SWITCH_LANGUAGE).equals("fr")) {
//                            WebPrintHelper.getInstance().print(AppConfigHelper.getConfig(AppConfigDef.PRINT_CUSTOMER_CONTEXT));
////                            PaymentApplication.getInstance().startActivity(WebPrintActivity.getStartIntent(PaymentApplication.getInstance(), AppConfigHelper.getConfig(AppConfigDef.PRINT_CUSTOMER_CONTEXT)));
//                        } else {
                            controller.print(AppConfigHelper.getConfig(AppConfigDef.PRINT_CUSTOMER_CONTEXT));
//                        }
                        final NoticeDialogFragment dialog = NoticeDialogFragment.newInstance("INFORMATION", "Customer Copy?", "YES", "NO");
                        dialog.setListener(new DialogHelper.DialogCallbackAndNo() {
                            @Override
                            public void callback() {
//                                if (AppConfigHelper.getConfig(AppConfigDef.SWITCH_LANGUAGE).equals("fr")) {
////                                    PaymentApplication.getInstance().startActivity(WebPrintActivity.getStartIntent(PaymentApplication.getInstance(), AppConfigHelper.getConfig(AppConfigDef.PRINT_CUSTOMER_CONTEXT)));
//                                    WebPrintHelper.getInstance().print(AppConfigHelper.getConfig(AppConfigDef.PRINT_CUSTOMER_CONTEXT));
//                                } else {
                                    controller.print(AppConfigHelper.getConfig(AppConfigDef.PRINT_CUSTOMER_CONTEXT));
//                                }
                            }

                            @Override
                            public void callbackNo() {
                                dialog.dismiss();
                            }
                        });
                        dialog.show(getSupportFragmentManager(), "SimpleMsgDialogFragment2");
                    }

                    @Override
                    public void callbackNo() {
                        fragmentDialog.dismiss();
                    }
                });
                fragmentDialog.show(getSupportFragmentManager(), "SimpleMsgDialogFragment1");
                break;
        }
    }
}
