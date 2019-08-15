package com.overseas.mtpay.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.log.Logger;
import com.overseas.mtpay.R;
import com.overseas.mtpay.app.App;
import com.overseas.mtpay.bean.DailyDetailResp;
import com.overseas.mtpay.bean.OrderPayResp;
import com.overseas.mtpay.bean.QuerySummaryDetailResp;
import com.overseas.mtpay.bean.RefundResp;
import com.overseas.mtpay.bean.TransDetailResp;
import com.overseas.mtpay.db.AppConfigDef;
import com.overseas.mtpay.db.AppConfigHelper;
import com.overseas.mtpay.db.Constants;
import com.overseas.mtpay.http.NetCodeConstants;
import com.overseas.mtpay.http.NetConfig;
import com.overseas.mtpay.http.NetRequest;
import com.overseas.mtpay.print.Q1PrintBuilder;
import com.overseas.mtpay.ui.base.BaseViewActivity;
import com.overseas.mtpay.utils.Calculater;
import com.overseas.mtpay.utils.MsgTipsDialog;
import com.overseas.mtpay.utils.SharePreferenceUtil;
import com.overseas.mtpay.utils.Tools;
import com.overseas.mtpay.utils.widget.CommonToastUtil;
import com.ui.dialog.DialogHelper;
import com.ui.dialog.NoticeDialogFragment;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 * 撤销
 * Created by wu on 16/3/28.
 */
public class VoidTransActivity extends BaseViewActivity {

    public static final String EXTRA_TRANS_VOID_FAILD = "EXTRA_TRANS_VOID_FAILD";
    public static final String EXTRA_TRANS_DETIAL = "EXTRA_TRANS_DETIAL";
    public static final int REQUEST_VOID_CARD_LINK = 1001;
    private String refundAmount;
    private DailyDetailResp transDetail;
    private SharePreferenceUtil spUtils;

    public static Intent getStartIntent(Context context, DailyDetailResp transDetailResp, String refundAmount) {
        Intent intent = new Intent(context, VoidTransActivity.class);
        intent.putExtra(EXTRA_TRANS_DETIAL, transDetailResp);
        intent.putExtra("refundAmount", refundAmount);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transDetail = (DailyDetailResp) getIntent().getSerializableExtra(EXTRA_TRANS_DETIAL);
        refundAmount = getIntent().getStringExtra("refundAmount");
        if (transDetail == null) {
            finish();
        }
        spUtils = new SharePreferenceUtil(App.getInstance());

        transactionCancle(false);
        setTitleText(getResources().getString(R.string.issue_refund));
        showTitleBack();
    }

    private void transactionCancle(final boolean bankcardpay) {
        //TODO 先查询这笔订单是否存在  然后再退款
        doSearchOrder();


        //TODO 撤销
//        presenter.getTransactionDetial(Tools.deleteMidTranLog(transDetail.getTranLogId(), AppConfigHelper.getConfig(AppConfigDef.mid)), bankcardpay, new BasePresenter.ResultListener() {
//            @Override
//            public void onSuccess(Response response) {
//                payTranRsp = (PayTranRsp) response.result;
//                presenter.cancel(payTranRsp, bankcardpay, refundAmount, new BasePresenter.ResultListener() {
//                    @Override
//                    public void onSuccess(Response response) {
//                        if (response.getMsg().contains("success")) {
//                            response.setMsg("success");
//                        }
//                        CommonToastUtil.showMsgBelow(VoidTransActivity.this, CommonToastUtil.LEVEL_SUCCESS, response.getMsg());
////                        Toast.makeText(VoidTransActivity.this,response.getMsg(),Toast.LENGTH_SHORT).show();
//                        setResult(RESULT_OK);
//                        finish();
//                    }
//
//                    @Override
//                    public void onFaild(Response response) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(VoidTransActivity.this);
//                        builder.setMessage(response.getMsg());
//                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                                finish();
//                            }
//                        });
//                        AlertDialog alertDialog = builder.create();
//                        alertDialog.show();
//                        //      CommonToastUtil.showMsgBelow(VoidTransActivity.this, CommonToastUtil.LEVEL_ERROR, response.getMsg());
////                        Toast.makeText(VoidTransActivity.this,response.getMsg(),Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//            }
//
//            @Override
//            public void onFaild(Response response) {
//                Toast.makeText(VoidTransActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        });
    }

    private void doSearchOrder() {
        progresser.showProgress();
        JSONObject json = new JSONObject();
        json.put("tran_log_id", transDetail.getShop_trans_id());
        JSONObject sysParam = new JSONObject();
        sysParam.put("shop_id", spUtils.getShopId());
        sysParam.put("oper_id", spUtils.getOperId());
        sysParam.put("oper_name", spUtils.getOperName());
        sysParam.put("sn", App.getInstance().terminalSn());
        sysParam.put("language", AppConfigHelper.getConfig(AppConfigDef.SWITCH_LANGUAGE));
        json.put("sysParam", sysParam);
//        String data = json.toJSONString();
        NetRequest.getInstance().fakePost(NetConfig.POST_QUERY_DAILY_SUMMARY_DETAIL, json, "2", new NetRequest.NetCallback() {
            @Override
            public void onSuccess(String resp) {
                progresser.showContent();
                QuerySummaryDetailResp transDetailResp = JSONObject.parseObject(resp, QuerySummaryDetailResp.class);
                List<TransDetailResp> list = (List<TransDetailResp>) transDetailResp.getResult().getDatas();
                if (list.size() > 0 && list != null) {
                    doRefund();
                }
            }

            @Override
            public void onFailed(String code, String message) {
                progresser.showContent();
                if (NetCodeConstants.MERCHANT_DISABLE_CODE_68.equals(code)) {
                    MsgTipsDialog.toLoginActivity(VoidTransActivity.this, NetCodeConstants.MERCHANT_DISABLE_MSG);
                } else if (NetCodeConstants.TERMINAL_REGISTER_CODE_53.equals(code)) {
                    MsgTipsDialog.toLoginActivity(VoidTransActivity.this, NetCodeConstants.TERMINAL_REGISTER_MSG);
                } else {

                    progresser.showError(message, true);
                }
//                CommonToastUtil.showMsgAbove(NewDailySumActivityPlus.this, CommonToastUtil.LEVEL_WARN, message);
                Logger.d("Login onFailed: " + code + "--------" + message);
            }
        });
    }

    private void doRefund() {
        progresser.showProgress();
        String teminalOrderNo = App.getInstance().terminalSn() + System.currentTimeMillis();
        //终端订单号  以便轮询或者网络终端查询
        AppConfigHelper.setConfig(AppConfigDef.teminalOrderNo, teminalOrderNo);
        JSONObject json = new JSONObject();
        json.put("terminal_refund_trans_id", teminalOrderNo);
        json.put("refund_amount", refundAmount);
        json.put("shop_trans_id", transDetail.getShop_trans_id());
        JSONObject sysParam = new JSONObject();
        sysParam.put("head_shop_id", spUtils.getHeadShopId());
        sysParam.put("shop_id", spUtils.getShopId());
        sysParam.put("oper_id", spUtils.getOperId());
        sysParam.put("oper_name", spUtils.getOperName());
        sysParam.put("sn", App.getInstance().terminalSn());
        sysParam.put("language", AppConfigHelper.getConfig(AppConfigDef.SWITCH_LANGUAGE));
        json.put("sysParam", sysParam);
//        String data = json.toJSONString();
        NetRequest.getInstance().fakePost(NetConfig.POST_ORDER_REFUND, json, "2", new NetRequest.NetCallback() {
            @Override
            public void onSuccess(String resp) {
                progresser.showContent();
                RefundResp refundResp = JSONObject.parseObject(resp, RefundResp.class);
                AppConfigHelper.setConfig(AppConfigDef.PRINT_SALE_REFUND_CONTEXT, getRefundPrintContext(refundResp.getResult()));
                AppConfigHelper.setConfig(AppConfigDef.PRINT_CUSTOMER_REFUND_CONTEXT, getCustomerRefundPrintContext(refundResp.getResult()));
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailed(String code, String message) {
                progresser.showContent();
                if (NetCodeConstants.MERCHANT_DISABLE_CODE_68.equals(code)) {
                    MsgTipsDialog.toLoginActivity(VoidTransActivity.this, NetCodeConstants.MERCHANT_DISABLE_MSG);
                } else if (NetCodeConstants.TERMINAL_REGISTER_CODE_53.equals(code)) {
                    MsgTipsDialog.toLoginActivity(VoidTransActivity.this, NetCodeConstants.TERMINAL_REGISTER_MSG);
                } else {

                    Toast.makeText(VoidTransActivity.this, message, Toast.LENGTH_SHORT).show();
                    finish();
                    AlertDialog.Builder builder = new AlertDialog.Builder(VoidTransActivity.this);
                    builder.setMessage(message);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                Logger.d("Login onFailed: " + code + "--------" + message);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VOID_CARD_LINK) {
            if (resultCode == RESULT_OK) {
                transactionCancle(true);
            } else {
//                String errorMsg = data.getStringExtra(EXTRA_TRANS_VOID_FAILD);
                this.finish();
            }
        }
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

    public String getRefundPrintContext(RefundResp.ResultBean resp) {
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
            String merchantAddr = spUtils.getMerchantAddr();
            printString += getString(R.string.print_merchant_addr) + merchantAddr + builder.br();
            String merchantTel = spUtils.getMerchantTel();
            printString += getString(R.string.print_merchant_tel) + merchantTel + builder.br();
            String terminalId = AppConfigHelper.getConfig(AppConfigDef.sn);
            printString += getString(R.string.print_terminal_id) + terminalId + builder.br();
            String cahierId = spUtils.getOperId();
            printString += getString(R.string.print_cashier_id) + cahierId + builder.br();
            printString += builder.br();
            printString += builder.center(builder.bold(getString(R.string.refund_uppercase))) + builder.br();

            printString += "Total:" + multipleSpaces(26 - Calculater.formotFen(resp.getTran_amount()).length()) + "$" + Calculater.formotFen("" + Math.abs(Integer.parseInt(resp.getTran_amount()))) + builder.br();
            String exchangeRate = resp.getExchange_rate();
            if (TextUtils.isEmpty(exchangeRate)) {
                exchangeRate = "1";
            }
            String cnyAmount = Calculater.formotFen("" + Math.abs(Integer.parseInt(resp.getCny_amount()))).replace("-", "").trim();
            if (TextUtils.isEmpty(cnyAmount) || "0.00".equals(cnyAmount)) {
                cnyAmount = String.format("%.2f", Math.abs(Float.parseFloat(Calculater.multiply(Calculater.formotFen(resp.getTran_amount()), exchangeRate))));
            }
            printString += multipleSpaces(28 - cnyAmount.length()) + "CNY " + cnyAmount + builder.br();
            String showCNY = "CAD 1.00=CNY " + Calculater.multiply("1", exchangeRate);
            String printFx = getString(R.string.print_fx_rate);
            printString += printFx + multipleSpaces(32 - printFx.getBytes("GBK").length - showCNY.length()) + showCNY + builder.br();
            printString += builder.br() + builder.nBr();
            String tranlogId = Tools.deleteMidTranLog(resp.getRefund_shop_tran_id(), AppConfigHelper.getConfig(AppConfigDef.mid));
            String printRecepit = getString(R.string.print_receipt);
            printString += printRecepit + "# " + multipleSpaces(30 - printRecepit.getBytes("GBK").length - tranlogId.getBytes("GBK").length) + tranlogId + builder.br();
//            printString += printRecepit + "#" + multipleSpaces(31 - printRecepit.getBytes("GBK").length - tranlogId.length()) + tranlogId + builder.br();
            printString += resp.getTran_time().substring(0, 10) + multipleSpaces(22 - resp.getTran_time().substring(10).length()) + resp.getTran_time().substring(10) + builder.br();
            String payType = Constants.TRAN_TYPE.get(transDetail.getTrans_type());
            if ("WechatPay".equals(payType)) {
                payType = "Wechat Pay";
            } else if (payType.contains("Union")) {
                payType = "Union Pay QR";
            }
            String printType = getString(R.string.print_type);
            printString += printType + multipleSpaces(32 - printType.getBytes("GBK").length - payType.getBytes("GBK").length) + payType + builder.br();
            String thirdTransOrder = resp.getThird_trade_no();
            if (!TextUtils.isEmpty(thirdTransOrder)) {
                printString += getString(R.string.print_trans) + builder.br();
                printString += multipleSpaces(32 - thirdTransOrder.getBytes("GBK").length) + thirdTransOrder + builder.br();
            }
            String acctName = resp.getThird_ext_name();
            if (!TextUtils.isEmpty(acctName)) {
                String printAcctName = getString(R.string.print_acctName);
                printString += printAcctName + multipleSpaces(32 - printAcctName.getBytes("GBK").length - acctName.getBytes("GBK").length) + acctName + builder.br();
            }
//            String acct = resp.getThird_ext_id();
//            if (!TextUtils.isEmpty(acct)) {
//                String printAcct = getString(R.string.print_acct);
//                printString += printAcct + multipleSpaces(32 - printAcct.getBytes("GBK").length - acct.getBytes("GBK").length) + acct + builder.br();
//            }

            printString += builder.br();
            printString += builder.center(builder.bold(getString(R.string.print_approved)));
            printString += builder.br();
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

    public String getCustomerRefundPrintContext(RefundResp.ResultBean resp) {
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
//            String merchantId = AppConfigHelper.getConfig(AppConfigDef.mid);

            String merchantId = spUtils.getShopId();
            printString += getString(R.string.print_merchant_id) + merchantId + builder.br();
            /////////////
            String merchantAddr = spUtils.getMerchantAddr();
            printString += getString(R.string.print_merchant_addr) + merchantAddr + builder.br();
            String merchantTel = spUtils.getMerchantTel();
            printString += getString(R.string.print_merchant_tel) + merchantTel + builder.br();
            //////////////

            String terminalId = AppConfigHelper.getConfig(AppConfigDef.sn);
            printString += getString(R.string.print_terminal_id) + terminalId + builder.br();
//            String cahierId = AppConfigHelper.getConfig(AppConfigDef.operatorTrueName);
            String cahierId = spUtils.getOperId();
            printString += getString(R.string.print_cashier_id) + cahierId + builder.br();
            printString += builder.br();
            printString += builder.center(builder.bold(getString(R.string.refund_uppercase))) + builder.br();

            printString += "Total:" + multipleSpaces(26 - Calculater.formotFen(resp.getTran_amount()).length()) + "$" + Calculater.formotFen("" + Math.abs(Integer.parseInt(resp.getTran_amount()))) + builder.br();
            String exchangeRate = resp.getExchange_rate();
            if (TextUtils.isEmpty(exchangeRate)) {
                exchangeRate = "1";
            }
            String cnyAmount = Calculater.formotFen("" + Math.abs(Integer.parseInt(resp.getCny_amount()))).replace("-", "").trim();
            if (TextUtils.isEmpty(cnyAmount) || "0.00".equals(cnyAmount)) {
                cnyAmount = String.format("%.2f", Float.parseFloat(Calculater.multiply(Calculater.formotFen("" + Integer.parseInt(resp.getTran_amount())), exchangeRate)));
            }
            printString += multipleSpaces(28 - cnyAmount.length()) + "CNY " + cnyAmount + builder.br();
            String showCNY = "CAD 1.00=CNY " + Calculater.multiply("1", exchangeRate);
            String printFx = getString(R.string.print_fx_rate);
            printString += printFx + multipleSpaces(32 - printFx.getBytes("GBK").length - showCNY.length()) + showCNY + builder.br();
            printString += builder.br() + builder.nBr();
            String tranlogId = Tools.deleteMidTranLog(resp.getRefund_shop_tran_id(), AppConfigHelper.getConfig(AppConfigDef.mid));
            String printRecepit = getString(R.string.print_receipt);
            printString += printRecepit + "# " + multipleSpaces(30 - printRecepit.getBytes("GBK").length - tranlogId.getBytes("GBK").length) + tranlogId + builder.br();
//            printString += printRecepit + "#" + multipleSpaces(31 - printRecepit.getBytes("GBK").length - tranlogId.length()) + tranlogId + builder.br();
            printString += resp.getTran_time().substring(0, 10) + multipleSpaces(22 - resp.getTran_time().substring(10).length()) + resp.getTran_time().substring(10) + builder.br();
            String payType = Constants.TRAN_TYPE.get(transDetail.getTrans_type());
            if ("WechatPay".equals(payType)) {
                payType = "Wechat Pay";
            } else if (payType.contains("Union")) {
                payType = "Union Pay QR";
            }
            String printType = getString(R.string.print_type);
            printString += printType + multipleSpaces(32 - printType.getBytes("GBK").length - payType.getBytes("GBK").length) + payType + builder.br();
            String thirdTransOrder = resp.getThird_trade_no();
            if (!TextUtils.isEmpty(thirdTransOrder)) {
                printString += getString(R.string.print_trans) + builder.br();
                printString += multipleSpaces(32 - thirdTransOrder.getBytes("GBK").length) + thirdTransOrder + builder.br();
            }
            String acctName = resp.getThird_ext_name();
            if (!TextUtils.isEmpty(acctName)) {
                String printAcctName = getString(R.string.print_acctName);
                printString += printAcctName + multipleSpaces(32 - printAcctName.getBytes("GBK").length - acctName.getBytes("GBK").length) + acctName + builder.br();
            }
//            String acct = resp.getThird_ext_id();
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
