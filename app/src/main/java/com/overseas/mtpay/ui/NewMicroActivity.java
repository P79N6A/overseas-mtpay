package com.overseas.mtpay.ui;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.flyco.roundview.RoundTextView;
import com.log.Logger;
import com.overseas.mtpay.R;
import com.overseas.mtpay.app.App;
import com.overseas.mtpay.bean.OrderPayResp;
import com.overseas.mtpay.bean.SysTipsResp;
import com.overseas.mtpay.db.AppConfigDef;
import com.overseas.mtpay.db.AppConfigHelper;
import com.overseas.mtpay.db.Constants;
import com.overseas.mtpay.http.NetCodeConstants;
import com.overseas.mtpay.http.NetConfig;
import com.overseas.mtpay.http.NetRequest;
import com.overseas.mtpay.print.Q1PrintBuilder;
import com.overseas.mtpay.ui.base.BaseViewActivity;
import com.overseas.mtpay.utils.Calculater;
import com.overseas.mtpay.utils.Configs;
import com.overseas.mtpay.utils.LooperQueryerTransactionState;
import com.overseas.mtpay.utils.LooperTask;
import com.overseas.mtpay.utils.MsgTipsDialog;
import com.overseas.mtpay.utils.SharePreferenceUtil;
import com.overseas.mtpay.utils.Tools;
import com.overseas.mtpay.utils.widget.CommonToastUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */

public class NewMicroActivity extends BaseViewActivity implements OnClickListener {
    //    private BatCommonTransaction batTransaction;
    private TextView tvScanResult;
    private int REQUEST_CODE_SCAN = 112;
    private SharePreferenceUtil spUtils;
    private String mRealAmount;
    private String mTipAmount;
    private LooperQueryerTransactionState queryer;
    private boolean isShowTip = false;

    String teminalOrderNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mRealAmount = intent.getStringExtra(Constants.realAmount); //增加realAmount验证,如果没有realAmount,则将initAmount设为realAmount wu
        mTipAmount = intent.getStringExtra(Constants.tipsAmount);
        initView();
        spUtils = new SharePreferenceUtil(App.getInstance());
    }

    private void initView() {
//        CommonToastUtil.showMsgAbove(this, CommonToastUtil.LEVEL_WARN, "支付扫描");
        doScan();
    }


    private void doScan() {
        Intent i = new Intent(this, ScanActivity.class);
        startActivityForResult(i, REQUEST_CODE_SCAN);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isShowTip) {
            return;
        }
        popWindowDialog();
//        startActivity(NewMainActivity.getReturnIntent(this));
//        super.onBackPressed();
    }

    public void tipPopWindowDialog(String message) {
        isShowTip = true;
        final PopupWindow mPopupWindow;
        View viewPopupView;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewPopupView = inflater.inflate(R.layout.popup_user_mange_submit, null);
        TextView tvTips = viewPopupView.findViewById(R.id.tvTips);
        tvTips.setText(TextUtils.isEmpty(message) ? "" : message);
        viewPopupView.setFocusable(true);
        mPopupWindow = new PopupWindow(viewPopupView,
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAtLocation(viewPopupView, Gravity.CENTER, 0, 0);
        mPopupWindow.update();
        RoundTextView btnCancelSubmit = viewPopupView.findViewById(R.id.btnCancelSubmit);
        btnCancelSubmit.setVisibility(View.GONE);

        RoundTextView btnOKSubmit = viewPopupView
                .findViewById(R.id.btnOKSubmit);
        btnOKSubmit.setText("Return");
        btnOKSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                onCancelClick();
            }
        });
    }


    public void popWindowDialog() {
        final PopupWindow mPopupWindow;
        View viewPopupView;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewPopupView = inflater.inflate(R.layout.popup_user_mange_submit, null);
//        TextView tvTips = viewPopupView.findViewById(R.id.tvTips);
//        tvTips.setText("是否取消交易？");
        viewPopupView.setFocusable(true);
        mPopupWindow = new PopupWindow(viewPopupView,
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAtLocation(viewPopupView, Gravity.CENTER, 0, 0);
        mPopupWindow.update();
        RoundTextView btnCancelSubmit = viewPopupView.findViewById(R.id.btnCancelSubmit);
        RoundTextView btnOKSubmit = viewPopupView
                .findViewById(R.id.btnOKSubmit);
        btnOKSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                cancel();
            }
        });
        btnCancelSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
    }

    private void cancel() {
        JSONObject json = new JSONObject();
        json.put("terminal_trans_id", teminalOrderNo);
        JSONObject sysParam = new JSONObject();
        sysParam.put("head_shop_id", spUtils.getHeadShopId());
        sysParam.put("shop_id", spUtils.getShopId());
        sysParam.put("oper_id", spUtils.getOperId());
        sysParam.put("oper_name", spUtils.getOperName());
        sysParam.put("sn", App.getInstance().terminalSn());
        sysParam.put("language", AppConfigHelper.getConfig(AppConfigDef.SWITCH_LANGUAGE));
        json.put("sysParam", sysParam);
        progresser.showProgress();
        NetRequest.getInstance().fakePost(NetConfig.POST_ORDER_CANCEL, json, "2", new NetRequest.NetCallback() {
            @Override
            public void onSuccess(String resp) {
                progresser.showContent();
                onCancelClick();
            }

            @Override
            public void onFailed(String code, String message) {
                progresser.showContent();
                if (NetCodeConstants.MERCHANT_DISABLE_CODE_68.equals(code)) {
                    MsgTipsDialog.toLoginActivity(NewMicroActivity.this, NetCodeConstants.MERCHANT_DISABLE_MSG);
                } else if (NetCodeConstants.TERMINAL_REGISTER_CODE_53.equals(code)) {
                    MsgTipsDialog.toLoginActivity(NewMicroActivity.this, NetCodeConstants.TERMINAL_REGISTER_MSG);
                } else {

                    onCancelClick();
                }
            }
        });
    }

    private void onCancelClick() {
        if (queryer != null) {
            queryer.onDestory();
        }
        startActivity(NewMainActivity.getReturnIntent(NewMicroActivity.this));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            String noStr = data.getStringExtra(Configs.CODE);
            if (!TextUtils.isEmpty(noStr)) {
//                CommonToastUtil.showMsgAbove(this, CommonToastUtil.LEVEL_WARN, Configs.CODE + ":" + noStr);
                doPay(noStr);
            }
        } else {
            finish();
            startActivity(NewMainActivity.getReturnIntent(NewMicroActivity.this));
        }
    }

    private void showTips(String msg) {
        progresser.showProgress(msg);
    }

    private void doPay(String noStr) {
        progresser.showProgress();
        teminalOrderNo = App.getInstance().terminalSn() + System.currentTimeMillis();
        //终端订单号  以便轮询或者网络终端查询
        AppConfigHelper.setConfig(AppConfigDef.teminalOrderNo, teminalOrderNo);
        JSONObject json = new JSONObject();
        json.put("terminal_trans_id", teminalOrderNo);
        json.put("auth_code", noStr);
        json.put("tran_amount", mRealAmount);
        json.put("tip_amount", TextUtils.isEmpty(mTipAmount) ? "" : mTipAmount);
        json.put("scan_type", "");
        json.put("goods_info", "第三方支付");
        JSONObject sysParam = new JSONObject();
        sysParam.put("head_shop_id", spUtils.getHeadShopId());
        sysParam.put("shop_id", spUtils.getShopId());
        sysParam.put("oper_id", spUtils.getOperId());
        sysParam.put("oper_name", spUtils.getOperName());
        sysParam.put("sn", App.getInstance().terminalSn());
        sysParam.put("language", AppConfigHelper.getConfig(AppConfigDef.SWITCH_LANGUAGE));
        json.put("sysParam", sysParam);
//        String data = json.toJSONString();
        NetRequest.getInstance().fakePost(NetConfig.POST_ORDER_PAYMENT, json, "2", new NetRequest.NetCallback() {
            @Override
            public void onSuccess(String resp) {
                progresser.showContent();
                OrderPayResp resp1 = JSONObject.parseObject(resp, OrderPayResp.class);

                int code = resp1.getCode();
                String state = resp1.getResult().getState();
                Log.d("===onSuccess==", "onSuccess: " + code + "  " + state);
                if ("2".equals(state)) {
                    showTips("");
                    //支付成功 跳转到支付成功界面 并打印
                    AppConfigHelper.setConfig(AppConfigDef.teminalOrderNo, "");
                    if (!TextUtils.isEmpty(resp1.getResult().getExchange_rate())) {
                        AppConfigHelper.setConfig(AppConfigDef.exchangeRate, resp1.getResult().getExchange_rate());
                        Log.e("NewMicroActivity: ", "exchangeRate:" + AppConfigHelper.getConfig(AppConfigDef.exchangeRate));
                    }
                    if (!TextUtils.isEmpty(resp1.getResult().getCny_amount())) {
                        AppConfigHelper.setConfig(AppConfigDef.CNY_AMOUNT, resp1.getResult().getCny_amount());
                        Log.e("NewMicroActivity: ", "CNY_AMOUNT:" + AppConfigHelper.getConfig(AppConfigDef.CNY_AMOUNT));
                    }
                    doPrint(resp1.getResult());
                } else if ("1".equals(state)) {
                    //继续轮询
                    //1 未支付  需轮询
                    doLooper();
                    showTips("Please wait for customer to confirm");
                } else {
                    progresser.showContent();
                    showTips("");
                    tipPopWindowDialog(resp1.getMessage());
                }


//                if ("2".equals(resp1.getResult().getState())) {
//                    //支付成功 跳转到支付成功界面 并打印
//                    AppConfigHelper.setConfig(AppConfigDef.teminalOrderNo, "");
//                    if (!TextUtils.isEmpty(resp1.getResult().getExchange_rate())) {
//                        AppConfigHelper.setConfig(AppConfigDef.exchangeRate, resp1.getResult().getExchange_rate());
//                        Log.e("NewMicroActivity: ", "exchangeRate:" + AppConfigHelper.getConfig(AppConfigDef.exchangeRate));
//                    }
//                    if (!TextUtils.isEmpty(resp1.getResult().getCny_amount())) {
//                        AppConfigHelper.setConfig(AppConfigDef.CNY_AMOUNT, resp1.getResult().getCny_amount());
//                        Log.e("NewMicroActivity: ", "CNY_AMOUNT:" + AppConfigHelper.getConfig(AppConfigDef.CNY_AMOUNT));
//                    }
//                    doPrint(resp1.getResult());
//                } else {
//                    //1 未支付  需轮询
//                    doLooper();
//                }
            }

            @Override
            public void onFailed(String code, String message) {
                progresser.showContent();
                if (NetCodeConstants.MERCHANT_DISABLE_CODE_68.equals(code)) {
                    MsgTipsDialog.toLoginActivity(NewMicroActivity.this, NetCodeConstants.MERCHANT_DISABLE_MSG);
                } else if (NetCodeConstants.TERMINAL_REGISTER_CODE_53.equals(code)) {
                    MsgTipsDialog.toLoginActivity(NewMicroActivity.this, NetCodeConstants.TERMINAL_REGISTER_MSG);
                } else {

                    CommonToastUtil.showMsgAbove(NewMicroActivity.this, CommonToastUtil.LEVEL_WARN, message);
                    Logger.d("Login onFailed: " + code + "--------" + message);
                    if ("-1".equals(code)) {
                        doLooper();
                    } else {
                        showTips("");
                        tipPopWindowDialog(message);
                    }
                }
            }
        });
    }

    private void doLooper() {
        progresser.showProgress();
        queryer = new LooperQueryerTransactionState(AppConfigHelper.getConfig(AppConfigDef.teminalOrderNo));
        queryer.setListener(new LooperTask.LooperFinishListener() {

            @Override
            public void onFinish(String object) {
                showTips("");
                //支付成功 跳转到支付成功界面 并打印
                progresser.showContent();
                OrderPayResp resp1 = JSONObject.parseObject(object, OrderPayResp.class);
                AppConfigHelper.setConfig(AppConfigDef.teminalOrderNo, "");
                doPrint(resp1.getResult());
            }

            @Override
            public void toTip(String message) {
                showTips("");
                progresser.showContent();
                tipPopWindowDialog(message);
            }

            @Override
            public void waitForConfirm() {
                showTips("Please wait for customer to confirm");
            }
        });
        queryer.start();
    }

    private void doPrint(OrderPayResp.ResultBean result) {
        AppConfigHelper.setConfig(AppConfigDef.PRINT_CONTEXT, getPrintContext(result));
        AppConfigHelper.setConfig(AppConfigDef.PRINT_CUSTOMER_CONTEXT, getCustomerPrintContext(result));
//        AppConfigHelper.setConfig(AppConfigDef.tran_type, result.getPay_type());
        Log.e("NewMicroActivity: ", "doPrint:");
        startActivity(NewPaySuccessActivity.getStartIntent(this, result.getPay_type(), mRealAmount));
        finish();
    }

    public String getPrintContext(OrderPayResp.ResultBean result) {
        String printString = null;
        try {
            Q1PrintBuilder builder = new Q1PrintBuilder();
            printString = "";
            String merchant = AppConfigHelper.getConfig(AppConfigDef.merchantName);
            printString += builder.center(builder.bold(merchant));
            String address = AppConfigHelper.getConfig(AppConfigDef.merchantAddr);
            if (!TextUtils.isEmpty(address)) {
                if (address.getBytes("GBK").length <= 32) {
                    printString += builder.center(address);
                } else if (address.getBytes("GBK").length <= 64) {
                    printString += builder.center(address.substring(0, 32));
                    printString += builder.center(address.substring(32));
                } else if (address.getBytes("GBK").length <= 96) {
                    printString += builder.center(address.substring(0, 32));
                    printString += builder.center(address.substring(32, 64));
                    printString += builder.center(address.substring(64));
                }
            }
            String tel = AppConfigHelper.getConfig(AppConfigDef.merchantTel);
            if (!TextUtils.isEmpty(tel)) {
                printString += builder.center(tel);
            }
            String merchantId = spUtils.getShopId();
            printString += getString(R.string.print_merchant_id) + merchantId + builder.br();
            String terminalId = AppConfigHelper.getConfig(AppConfigDef.sn);
            String merchantAddr = spUtils.getMerchantAddr();
            printString += getString(R.string.print_merchant_addr) + merchantAddr + builder.br();
            String merchantTel = spUtils.getMerchantTel();
            printString += getString(R.string.print_merchant_tel) + merchantTel + builder.br();
            printString += getString(R.string.print_terminal_id) + terminalId + builder.br();
            String cahierId = spUtils.getOperId();
            printString += getString(R.string.print_cashier_id) + cahierId + builder.br();
            printString += builder.br() + builder.nBr();
            printString += builder.center(builder.bold(getString(R.string.print_sale))) + builder.br() + builder.nBr();
            String totalAmount = Calculater.formotFen(mRealAmount);
            String tipsAmount = mTipAmount;
            String printPurchase = getString(R.string.print_purchase);
            if (!TextUtils.isEmpty(tipsAmount) && !tipsAmount.equals("0")) {
                String purchaseAmount = Calculater.formotFen(Calculater.subtract(mRealAmount, tipsAmount));
                printString += printPurchase + multipleSpaces(31 - printPurchase.getBytes("GBK").length - purchaseAmount.length()) + "$" + purchaseAmount + builder.br();
            } else {
                printString += printPurchase + multipleSpaces(31 - printPurchase.getBytes("GBK").length - totalAmount.length()) + "$" + totalAmount + builder.br();
            }
            String printTip = getString(R.string.print_tip);
            if (!TextUtils.isEmpty(tipsAmount) && !tipsAmount.equals("0")) {
                printString += printTip + multipleSpaces(31 - printTip.getBytes("GBK").length - Calculater.formotFen(mRealAmount).length()) + "$" + Calculater.formotFen(tipsAmount) + builder.br();
            }
            printString += "Total:" + multipleSpaces(25 - totalAmount.length()) + "$" + totalAmount + builder.br();
//            String exchangeRate = AppConfigHelper.getConfig(AppConfigDef.exchangeRate);
            String exchangeRate = result.getExchange_rate();
            if (TextUtils.isEmpty(exchangeRate)) {
                exchangeRate = "1";
            }
//            String cnyAmount = Calculater.formotFen(AppConfigHelper.getConfig(AppConfigDef.CNY_AMOUNT)).trim();
            String cnyAmount = Calculater.formotFen(result.getCny_amount());

            if (TextUtils.isEmpty(cnyAmount) || "0.00".equals(cnyAmount)) {
                cnyAmount = String.format("%.2f", Float.parseFloat(Calculater.multiply(totalAmount, exchangeRate)));
            }
            printString += multipleSpaces(28 - cnyAmount.length()) + "CNY " + cnyAmount + builder.br();
            String showCNY = "CAD 1.00=CNY " + Calculater.multiply("1", exchangeRate);
            String printFx = getString(R.string.print_fx_rate);
            printString += printFx + multipleSpaces(32 - printFx.getBytes("GBK").length - showCNY.length()) + showCNY + builder.br();
            printString += builder.br() + builder.nBr();
            String tranlogId = Tools.deleteMidTranLog(result.getShop_tran_id(), AppConfigHelper.getConfig(AppConfigDef.mid));
            String printRecepit = getString(R.string.print_receipt);
            printString += printRecepit + "# " + multipleSpaces(30 - printRecepit.getBytes("GBK").length - tranlogId.getBytes("GBK").length) + tranlogId + builder.br();
            printString += result.getTran_time().substring(0, 10) + multipleSpaces(22 - result.getTran_time().substring(10).length()) + result.getTran_time().substring(10) + builder.br();
            String printType = getString(R.string.print_type);
            if (Constants.ALIPAYFLAG2.equals(result.getPay_type())) {
                printString += printType + multipleSpaces(26 - printType.getBytes("GBK").length) + "Alipay" + builder.br();
            } else if (Constants.WEPAYFLAG2.equals(result.getPay_type())) {
                printString += printType + multipleSpaces(22 - printType.getBytes("GBK").length) + "Wechat Pay" + builder.br();
            }


            String thirdTransOrder = result.getThird_trade_no();
            if (!TextUtils.isEmpty(thirdTransOrder)) {
                printString += getString(R.string.print_trans) + builder.br();
                printString += multipleSpaces(32 - thirdTransOrder.getBytes("GBK").length) + thirdTransOrder + builder.br();
            }
            String acctName = result.getThird_ext_name();
            if (!TextUtils.isEmpty(acctName)) {
                String printAcctName = getString(R.string.print_acctName);
                printString += printAcctName + multipleSpaces(32 - printAcctName.getBytes("GBK").length - acctName.getBytes("GBK").length) + acctName + builder.br();
            }
//            String acct = result.getThird_ext_id();
//            if (!TextUtils.isEmpty(acct)) {
//                String printAcct = getString(R.string.print_acct);
//                printString += printAcct + multipleSpaces(32 - printAcct.getBytes("GBK").length - acct.getBytes("GBK").length) + acct + builder.br();
//            }
            printString += builder.br();
            printString += builder.center(builder.bold(getString(R.string.print_approved)));
            printString += builder.br() + builder.nBr();
            printString += builder.center(builder.bold(getString(R.string.print_merchant_copy)));
            printString += builder.center(builder.bold(getString(R.string.print_important)));
            printString += builder.center(builder.bold(getString(R.string.print_hint1)));
            printString += builder.center(builder.bold(getString(R.string.print_hint2)));
            printString += builder.branch() + builder.endPrint();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return printString;
    }

    /**
     * 打印空格
     *
     * @param n
     * @return
     */
    public String multipleSpaces(int n) {
        String output = "";
        for (int i = 0; i < n; i++)
            output += " ";
        return output;
    }

    public String getCustomerPrintContext(OrderPayResp.ResultBean result) {
        String printString = null;
        try {
            Q1PrintBuilder builder = new Q1PrintBuilder();
            printString = "";
            String merchant = AppConfigHelper.getConfig(AppConfigDef.merchantName);
            printString += builder.center(builder.bold(merchant));
            String address = AppConfigHelper.getConfig(AppConfigDef.merchantAddr);
            if (!TextUtils.isEmpty(address)) {
                if (address.getBytes("GBK").length <= 32) {
                    printString += builder.center(address);
                } else if (address.getBytes("GBK").length <= 64) {
                    printString += builder.center(address.substring(0, 32));
                    printString += builder.center(address.substring(32));
                } else if (address.getBytes("GBK").length <= 96) {
                    printString += builder.center(address.substring(0, 32));
                    printString += builder.center(address.substring(32, 64));
                    printString += builder.center(address.substring(64));
                }
            }
            String tel = AppConfigHelper.getConfig(AppConfigDef.merchantTel);
            if (!TextUtils.isEmpty(tel)) {
                printString += builder.center(tel);
            }
            String merchantId = spUtils.getShopId();
            printString += getString(R.string.print_merchant_id) + merchantId + builder.br();

            /////////
            String merchantAddr = spUtils.getMerchantAddr();
            printString += getString(R.string.print_merchant_addr) + merchantAddr + builder.br();
            String merchantTel = spUtils.getMerchantTel();
            printString += getString(R.string.print_merchant_tel) + merchantTel + builder.br();
            ////////

            String terminalId = AppConfigHelper.getConfig(AppConfigDef.sn);
            printString += getString(R.string.print_terminal_id) + terminalId + builder.br();
            String cahierId = spUtils.getOperId();
            printString += getString(R.string.print_cashier_id) + cahierId + builder.br();
            printString += builder.br();
            printString += builder.center(builder.bold(getString(R.string.print_sale))) + builder.br();
            String totalAmount = Calculater.formotFen(mRealAmount);
            String tipsAmount = mTipAmount;
            String printPurchase = getString(R.string.print_purchase);
            if (!TextUtils.isEmpty(tipsAmount) && !tipsAmount.equals("0")) {
                String purchaseAmount = Calculater.formotFen(Calculater.subtract(mRealAmount, tipsAmount));
                printString += printPurchase + multipleSpaces(31 - printPurchase.getBytes("GBK").length - purchaseAmount.length()) + "$" + purchaseAmount + builder.br();
            } else {
                printString += printPurchase + multipleSpaces(31 - printPurchase.getBytes("GBK").length - totalAmount.length()) + "$" + totalAmount + builder.br();
            }
            String printTip = getString(R.string.print_tip);
            if (!TextUtils.isEmpty(tipsAmount) && !tipsAmount.equals("0")) {
                printString += printTip + multipleSpaces(31 - printTip.getBytes("GBK").length - Calculater.formotFen(mRealAmount).length()) + "$" + Calculater.formotFen(tipsAmount) + builder.br();
            }
            printString += "Total:" + multipleSpaces(25 - totalAmount.length()) + "$" + totalAmount + builder.br();
//            String exchangeRate = AppConfigHelper.getConfig(AppConfigDef.exchangeRate);
            String exchangeRate = result.getExchange_rate();
            if (TextUtils.isEmpty(exchangeRate)) {
                exchangeRate = "1";
            }
//            String cnyAmount = Calculater.formotFen(AppConfigHelper.getConfig(AppConfigDef.CNY_AMOUNT)).trim();
            String cnyAmount = Calculater.formotFen(result.getCny_amount());
            if (TextUtils.isEmpty(cnyAmount) || "0.00".equals(cnyAmount)) {
                cnyAmount = String.format("%.2f", Float.parseFloat(Calculater.multiply(totalAmount, exchangeRate)));
            }
            printString += multipleSpaces(28 - cnyAmount.length()) + "CNY " + cnyAmount + builder.br();
            String showCNY = "CAD 1.00=CNY " + Calculater.multiply("1", exchangeRate);
            String printFx = getString(R.string.print_fx_rate);
            printString += printFx + multipleSpaces(32 - printFx.getBytes("GBK").length - showCNY.length()) + showCNY + builder.br();
            printString += builder.br();
            String tranlogId = Tools.deleteMidTranLog(result.getShop_tran_id(), AppConfigHelper.getConfig(AppConfigDef.mid));
            String printRecepit = getString(R.string.print_receipt);
            printString += printRecepit + "# " + multipleSpaces(30 - printRecepit.getBytes("GBK").length - tranlogId.getBytes("GBK").length) + tranlogId + builder.br();
            printString += result.getTran_time().substring(0, 10) + multipleSpaces(22 - result.getTran_time().substring(10).length()) + result.getTran_time().substring(10) + builder.br();
            String printType = getString(R.string.print_type);
            if (Constants.ALIPAYFLAG2.equals(result.getPay_type())) {
                printString += printType + multipleSpaces(26 - printType.getBytes("GBK").length) + "Alipay" + builder.br();
            } else if (Constants.WEPAYFLAG2.equals(result.getPay_type())) {
                printString += printType + multipleSpaces(22 - printType.getBytes("GBK").length) + "Wechat Pay" + builder.br();
            }
            String thirdTransOrder = result.getThird_trade_no();
            if (!TextUtils.isEmpty(thirdTransOrder)) {
                printString += getString(R.string.print_trans) + builder.br();
                printString += multipleSpaces(32 - thirdTransOrder.getBytes("GBK").length) + thirdTransOrder + builder.br();
            }
            String acctName = result.getThird_ext_name();
            if (!TextUtils.isEmpty(acctName)) {
                String printAcctName = getString(R.string.print_acctName);
                printString += printAcctName + multipleSpaces(32 - printAcctName.getBytes("GBK").length - acctName.getBytes("GBK").length) + acctName + builder.br();
            }
//            String acct = result.getThird_ext_id();
//            if (!TextUtils.isEmpty(acct)) {
//                String printAcct = getString(R.string.print_acct);
//                printString += printAcct + multipleSpaces(32 - printAcct.getBytes("GBK").length - acct.getBytes("GBK").length) + acct + builder.br();
//            }
            printString += builder.br();
            printString += builder.center(builder.bold(getString(R.string.print_approved)));
            printString += builder.br();
            printString += builder.center(builder.bold(getString(R.string.print_customer_copy)));
            printString += builder.center(builder.bold(getString(R.string.print_important)));
            printString += builder.center(builder.bold(getString(R.string.print_hint1)));
            printString += builder.center(builder.bold(getString(R.string.print_hint2)));
            printString += builder.branch() + builder.endPrint();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return printString;
    }
}
