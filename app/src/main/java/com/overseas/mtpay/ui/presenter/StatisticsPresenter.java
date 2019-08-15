package com.overseas.mtpay.ui.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.exception.DbException;
import com.overseas.mtpay.R;
import com.overseas.mtpay.app.App;
import com.overseas.mtpay.bean.DailyDetailResp;
import com.overseas.mtpay.bean.RefundDetailResp;
import com.overseas.mtpay.bean.TodayDetailBean;
import com.overseas.mtpay.bean.TranLogVoResp;
import com.overseas.mtpay.db.AppConfigDef;
import com.overseas.mtpay.db.Constants;
import com.overseas.mtpay.print.PrintServiceControllerProxy;
import com.overseas.mtpay.print.Q1PrintBuilder;
import com.overseas.mtpay.utils.Calculater;
import com.overseas.mtpay.utils.DateUtil;
import com.overseas.mtpay.utils.SharePreferenceUtil;
import com.overseas.mtpay.utils.Tools;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.overseas.mtpay.db.AppConfigHelper.getConfig;


/**
 * 查询
 *
 * @author wu
 */
public class StatisticsPresenter
//        extends BasePresenter
{

    private final SharePreferenceUtil spUtils;
    //    private GroupQueryResp handler;
    private Context context;
    private long count;
    private long totalPage = 0;// 总页数

    private List<String[]> detialRecords_private = new ArrayList<String[]>();// 交易明细,打印使用
    private List<String[]> detialRecords = new ArrayList<String[]>();// 交易明细




    public StatisticsPresenter(Context context) {
        this.context = context;
        spUtils = new SharePreferenceUtil(App.getInstance());

//        super(context);
    }

    public static String convertTimeRange2String(int timeRagnge) {
//		< "0", "今天">; < "1", "昨天">; < "2", "本周">; < "3", "上周">; < "4", "本月">; < "5", "上月">; < "6", "指定范围">
        if (timeRagnge == 0) {
            return "今天";
        } else if (timeRagnge == 1) {
            return "昨天";
        } else if (timeRagnge == 2) {
            return "本周";
        } else if (timeRagnge == 3) {
            return "上周";
        } else if (timeRagnge == 4) {
            return "本月";
        } else if (timeRagnge == 5) {
            return "上月";
        } else {
            return "指定范围";
        }
    }


    /**
     * 打印交易汇总查询
     */
//    public void printGroupQuery() {
//         GroupQueryResp handler = new GroupQueryResp();
//        List<String[]> recordListShow = handler.getRecordListShow();
//        List<String[]> recordTicketListSum = handler.getRecordTicketListSum();
//
//        if (recordListShow.size() < 1) {
//            return;
//        }
//        PrintServiceControllerProxy controller = new PrintServiceControllerProxy(context);
//        Q1PrintBuilder builder = new Q1PrintBuilder();
//        String printString = "";
//
//        printString += builder.center(builder.bold("交易汇总"));
//        printString += builder.branch();
//        printString += "SN号：" + getConfig(AppConfigDef.sn, "") + builder.br();
//        printString += "慧商户号：" + getConfig(AppConfigDef.mid, "") + builder.br();
//        printString += "商户名称：" + getConfig(AppConfigDef.merchantName, "") + builder.br();
//        printString += builder.branch();
//        // -----------------------------------------------------------------
//        List<String[]> recordListTickets = new ArrayList<String[]>();
//        List<String[]> recordListTickets2 = new ArrayList<String[]>();
//        List<String[]> recordListNoneTickets = new ArrayList<String[]>();
//        for (int i = 0; i < recordListShow.size(); i++) {
//            if ((recordListShow.get(i)[0]).contains("(券)")) {
//                String[] tickets = new String[]{recordListShow.get(i)[0], recordListShow.get(i)[1], recordListShow.get(i)[2],};
//                if (Tools.toIntMoney(recordListShow.get(i)[2]) > 0) {
//                    recordListTickets.add(tickets);
//                } else {
//                    recordListTickets2.add(tickets);
//                }
//            } else {
//                String[] nonetickets = new String[]{recordListShow.get(i)[0], recordListShow.get(i)[1], recordListShow.get(i)[2],};
//                recordListNoneTickets.add(nonetickets);
//            }
//        }
//        // -----------------------------------------------------------------
//        // 不带券
//        for (int i = 0; i < recordListNoneTickets.size(); i++) {
//            String[] recordPrinter = recordListNoneTickets.get(i);
//            printString += "交易类型：" + recordPrinter[0] + builder.br();
//            printString += "笔    数：" + recordPrinter[1] + builder.br();
//            printString += "交易金额：" + recordPrinter[2] + "元" + builder.br();
//            printString += builder.branch();
//        }
////        printString += builder.br();
//        // 带券
//        for (int i = 0; i < recordListTickets.size(); i++) {
//            String[] recordPrinter = recordListTickets.get(i);
//            printString += "交易类型：" + recordPrinter[0] + builder.br();
//            printString += "笔    数：" + recordPrinter[1] + builder.br();
//            printString += "交易金额：" + recordPrinter[2] + "元" + builder.br();
//            printString += builder.branch();
//        }
////        printString += builder.br();
//        // for(int i = 0; i < recordTicketUsed.size(); i++){
//        // String[] recordPrinter = recordTicketUsed.get(i);
//        // printString += "交易类型：" + recordPrinter[0] + pb.br();
//        // printString += "笔    数：" + recordPrinter[1] + pb.br();
//        // printString += "交易金额：" + recordPrinter[2] + pb.br();
//        // printString += pb.normal("--------------------------------") +
//        // pb.br() ;
//        // }
//        // printString += pb.br();
//        for (int i = 0; i < recordListTickets2.size(); i++) {
//            String[] recordPrinter = recordListTickets2.get(i);
//            printString += "交易类型：" + recordPrinter[0] + builder.br();
//            printString += "笔    数：" + recordPrinter[1] + builder.br();
//            printString += "交易金额：" + recordPrinter[2] + "元" + builder.br();
//            printString += builder.branch();
//        }
//
//        // for(int i = 0; i < recordTicketCancel.size(); i++){
//        // String[] recordPrinter = recordTicketCancel.get(i);
//        // printString += "交易类型：" + recordPrinter[0] + pb.br();
//        // printString += "笔    数：" + recordPrinter[1] + pb.br();
//        // printString += "交易金额：" + recordPrinter[2] + pb.br();
//        // printString += pb.normal("--------------------------------");
//        // }
//        // printString += pb.br() ;
//        for (int i = 0; i < recordTicketListSum.size(); i++) {
//            String[] recordPrinter = recordTicketListSum.get(i);
//            printString += "券 类 型：" + recordPrinter[0] + builder.br();
//            printString += "张    数：" + recordPrinter[1] + builder.br();
//            printString += "交易金额：" + recordPrinter[2] + "元" + builder.br();
//            printString += builder.branch();
//        }
//        if (Constants.APP_VERSION_NAME == Constants.APP_VERSION_LAWSON) {
//            printString += "开始时间:" + handler.getLastPrintTime() + builder.br();
//            printString += "结束时间:" + handler.getCurrentPrintTime() + builder.br();
//            printString += "操作员号:" + getConfig(AppConfigDef.operatorTrueName) + builder.br();
//            printString += builder.branch();
//        }
//        printString += builder.endPrint();
//        controller.print(printString);
//        controller.cutPaper();
//    }

    private String tranTypeChange(JSONObject jsonObject) {
        String type = Constants.TRAN_TYPE.get(jsonObject.get("tranCode"));
        int tranCode = jsonObject.getInteger("tranCode");
        switch (tranCode) {
            case 791:
                type = "组合支付|非会员券";
                break;

            case 792:
                type = "组合支付|会员券";
                break;

            case 793:
                type = "组合支付|微信卡券核销";
                break;

            case 795:
                type = "组合支付|抹零";
                break;

            case 796:
                type = "组合支付|折扣";
                break;

            default:
                break;
        }
        return type;
    }

    private void printDetial(JSONArray jobj) {
        PrintServiceControllerProxy controller = new PrintServiceControllerProxy(context);
        Q1PrintBuilder builder = new Q1PrintBuilder();
        String printString = "";

        printString += builder.center(builder.bold("交易明细")) + builder.br();
        for (int i = 0; i < jobj.size(); i++) {
            JSONObject job = jobj.getJSONObject(i);
            // JSONObject jobMerchantDef = job.getJSONObject("merchantDef");
            printString += builder.branch();
            printString += "流水号：" + job.getString("id") + builder.br();
//            printString += "支付宝交易号：" + builder.br() + job.getString("thirdTradeNo") + builder.br();
            printString += "慧商户号：" + job.getString("mid") + builder.br();
            printString += "终端设备号：" + getConfig(AppConfigDef.sn) + builder.br();
            String tranType = tranTypeChange(job);//解决组合支付部分trancode不识别的问题@yaosong
            printString += "消费类型：" + tranType + builder.br();
            printString += "交易日期：" + DateUtil.format(job.getLong("tranTime"), DateUtil.P2) + builder.br();
            printString += "交易金额：" + Calculater.formotFen(job.getString("tranAmount")) + " 元" + builder.br();

            printString += "补打" + builder.br();
            printString += builder.branch();
        }
        printString += builder.endPrint();
        controller.print(printString);
        controller.cutPaper();
    }

    public void printRefund(RefundDetailResp resp) {
        PrintServiceControllerProxy controller = new PrintServiceControllerProxy(context);
        Q1PrintBuilder builder = new Q1PrintBuilder();
        String printString = "";
        String merchant = getConfig(AppConfigDef.merchantName);
        printString += builder.center(builder.bold(merchant));
        printString += builder.center("228 Hunt Club Rd.");
        printString += builder.center("Ottawa,Ontario,K1V 1C1");
        printString += builder.center("(613)3196686");
        String address = "";
        String merchantId = getConfig(AppConfigDef.mid);
        printString += "Merchant ID:" + merchantId + builder.br();
        String terminalId = getConfig(AppConfigDef.sn);
        printString += "Terminal ID:" + terminalId + builder.br();
        String cahierId = getConfig(AppConfigDef.operatorTrueName);
        printString += "Cashier ID:" + cahierId + builder.br();
        printString += builder.br();
        printString += builder.center(builder.bold("REFUND")) + builder.br();

        printString += "Total:" + multipleSpaces(25 - Calculater.formotFen(resp.getRefundAmount()).length()) + "$" + Calculater.formotFen(resp.getRefundAmount()) + builder.br();
        String exchangeRate = getConfig(AppConfigDef.exchangeRate);
        if (TextUtils.isEmpty(exchangeRate)) {
            exchangeRate = "1";
        }
        String cnyAmount = String.format("%.2f", Float.parseFloat(Calculater.multiply(Calculater.formotFen(resp.getRefundAmount()), exchangeRate)));
        printString += multipleSpaces(28 - cnyAmount.length()) + "CNY " + cnyAmount + builder.br();
        printString += builder.br();
        String tranlogId = Tools.deleteMidTranLog(resp.getTranLogId(), getConfig(AppConfigDef.mid));
        printString += "Receipt#" + multipleSpaces(24 - tranlogId.length()) + tranlogId + builder.br();
        printString += DateUtil.format(new Date(), DateUtil.P1) + multipleSpaces(22 - DateUtil.format(new Date(), DateUtil.P12).length()) + DateUtil.format(new Date(), DateUtil.P12) + builder.br();
        String payType = resp.getTransKind();
        printString += "Type:" + multipleSpaces(27 - payType.length()) + payType + builder.br();
        String thirdTransOrder = resp.getThirdTradeNo();
        if (!TextUtils.isEmpty(thirdTransOrder)) {
            printString += "Trans#:" + builder.br();
            printString += multipleSpaces(32 - thirdTransOrder.length()) + thirdTransOrder + builder.br();
        }
        String acctName = resp.getThirdExtName();
        if (!TextUtils.isEmpty(acctName)) {
            printString += "Acct Name:" + multipleSpaces(22 - acctName.length()) + acctName + builder.br();
        }
        String acct = resp.getThirdExtId();
        if (!TextUtils.isEmpty(acct)) {
            printString += "Acct:" + multipleSpaces(27 - acct.length()) + acct + builder.br();
        }

        printString += builder.br();
        printString += builder.center(builder.bold("APPROVED"));
        printString += builder.br();
        printString += builder.center(builder.bold("MERCHANT COPY"));
        printString += builder.center(builder.bold("-important-"));
        printString += builder.center(builder.bold("Please retain this Copy"));
        printString += builder.center(builder.bold("for your records."));
        printString += builder.branch() + builder.endPrint();
        controller.print(printString);
        controller.cutPaper();
    }

    public void reprintCustomerRefund(DailyDetailResp resp) {
        try {
            PrintServiceControllerProxy controller = new PrintServiceControllerProxy(context);
            Q1PrintBuilder builder = new Q1PrintBuilder();
            String printString = "";
            String merchant = getConfig(AppConfigDef.merchantName);
            printString += builder.center(builder.bold(merchant + context.getString(R.string.print_reprint)));
            String address = getConfig(AppConfigDef.merchantAddr);
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
            String tel = getConfig(AppConfigDef.merchantTel);
            if (!TextUtils.isEmpty(tel)) {
                printString += builder.center(tel);
            }
            String merchantId = spUtils.getShopId();
            printString += context.getString(R.string.print_merchant_id) + merchantId + builder.br();
            /////////////
            String merchantAddr = spUtils.getMerchantAddr();
            printString += context.getString(R.string.print_merchant_addr) + merchantAddr + builder.br();
            String merchantTel = spUtils.getMerchantTel();
            printString += context.getString(R.string.print_merchant_tel) + merchantTel + builder.br();
            //////////////

            String terminalId = getConfig(AppConfigDef.sn);
            printString += context.getString(R.string.print_terminal_id) + terminalId + builder.br();
            String cahierId = spUtils.getOperId();
            printString += context.getString(R.string.print_cashier_id) + cahierId + builder.br();
            printString += builder.br() + builder.nBr();
            printString += builder.center(builder.bold(context.getString(R.string.refund_uppercase))) + builder.br() + builder.nBr();
            printString += "Total:" + multipleSpaces(25 - Calculater.formotFen(resp.getTrans_amount().replace("-", "").trim()).length()) + "$" + Calculater.formotFen(resp.getTrans_amount().replace("-", "").trim()) + builder.br();
            String exchangeRate = resp.getExchange_rate();
            if (TextUtils.isEmpty(exchangeRate)) {
                exchangeRate = "1";
            }
            String cnyAmount = Calculater.formotFen(resp.getCny_amount()).replace("-", "").trim();
            if (TextUtils.isEmpty(cnyAmount) || "0.00".equals(cnyAmount)) {
                cnyAmount = String.format("%.2f", Float.parseFloat(Calculater.multiply(Calculater.formotFen(resp.getTrans_amount().replace("-", "").trim()), exchangeRate)));
            }
            printString += multipleSpaces(28 - cnyAmount.length()) + "CNY " + cnyAmount + builder.br();
            String showCNY = "CAD 1.00=CNY " + Calculater.multiply("1", exchangeRate);
            String printFx = context.getString(R.string.print_fx_rate);
            printString += printFx + multipleSpaces(32 - printFx.getBytes("GBK").length - showCNY.length()) + showCNY + builder.br();
            printString += builder.br() + builder.nBr();
            String tranlogId = Tools.deleteMidTranLog(resp.getShop_trans_id(), getConfig(AppConfigDef.mid));
            String printRecepit = context.getString(R.string.print_receipt);
            printString += printRecepit + "# " + multipleSpaces(30 - printRecepit.getBytes("GBK").length - tranlogId.getBytes("GBK").length) + tranlogId + builder.br();
            printString += resp.getTran_time().substring(0, 10) + multipleSpaces(22 - resp.getTran_time().substring(10).length()) + resp.getTran_time().substring(10) + builder.br();
            String payType = resp.getTransName();
            String printType = context.getString(R.string.print_type);
            printString += printType + multipleSpaces(32 - printType.getBytes("GBK").length - payType.length()) + payType + builder.br();
            String thirdTransOrder = resp.getThird_trade_no();
            if (!TextUtils.isEmpty(thirdTransOrder)) {
                printString += context.getString(R.string.print_trans) + builder.br();
                printString += multipleSpaces(32 - thirdTransOrder.length()) + thirdTransOrder + builder.br();
            }
//            String acctName = resp.getThirdExtName();
//            if (!TextUtils.isEmpty(acctName)) {
//                String printAcctName = context.getString(R.string.print_acctName);
//                printString += printAcctName + multipleSpaces(32 - printAcctName.getBytes("GBK").length - acctName.getBytes("GBK").length) + acctName + builder.br();
//            }
//            String acct = resp.getThirdExtId();
//            if (!TextUtils.isEmpty(acct)) {
//                String printAcct = context.getString(R.string.print_acct);
//                printString += printAcct + multipleSpaces(32 - printAcct.getBytes("GBK").length - acct.getBytes("GBK").length) + acct + builder.br();
//            }
            printString += builder.br();
            printString += builder.center(builder.bold(context.getString(R.string.print_approved)));
            printString += builder.br() + builder.nBr();
            printString += builder.center(builder.bold(context.getString(R.string.print_customer_copy)));
            printString += builder.center(builder.bold(context.getString(R.string.print_important)));
            printString += builder.center(builder.bold(context.getString(R.string.print_hint1)));
            printString += builder.center(builder.bold(context.getString(R.string.print_hint2)));
            printString += builder.branch() + builder.endPrint();
            controller.print(printString);
            controller.cutPaper();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void reprintMerchantRefund(DailyDetailResp resp) {
        try {
            PrintServiceControllerProxy controller = new PrintServiceControllerProxy(context);
            Q1PrintBuilder builder = new Q1PrintBuilder();
            String printString = "";
            String merchant = getConfig(AppConfigDef.merchantName);
            printString += builder.center(builder.bold(merchant + context.getString(R.string.print_reprint)));
            String address = getConfig(AppConfigDef.merchantAddr);
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
            String tel = getConfig(AppConfigDef.merchantTel);
            if (!TextUtils.isEmpty(tel)) {
                printString += builder.center(tel);
            }
            String merchantId = spUtils.getShopId();
            printString += context.getString(R.string.print_merchant_id) + merchantId + builder.br();

            /////////////
            String merchantAddr = spUtils.getMerchantAddr();
            printString += context.getString(R.string.print_merchant_addr) + merchantAddr + builder.br();
            String merchantTel = spUtils.getMerchantTel();
            printString += context.getString(R.string.print_merchant_tel) + merchantTel + builder.br();
            //////////////

            String terminalId = getConfig(AppConfigDef.sn);
            printString += context.getString(R.string.print_terminal_id) + terminalId + builder.br();
            String cahierId = spUtils.getOperId();
            printString += context.getString(R.string.print_cashier_id) + cahierId + builder.br();
            printString += builder.br() + builder.nBr();
            printString += builder.center(builder.bold(context.getString(R.string.refund_uppercase))) + builder.br() + builder.nBr();
            printString += "Total:" + multipleSpaces(25 - Calculater.formotFen(resp.getTrans_amount().replace("-", "").trim()).length()) + "$" + Calculater.formotFen(resp.getTrans_amount().replace("-", "").trim()) + builder.br();
            String exchangeRate = resp.getExchange_rate();
            if (TextUtils.isEmpty(exchangeRate)) {
                exchangeRate = "1";
            }
            String cnyAmount = Calculater.formotFen(resp.getCny_amount()).replace("-", "").trim();
            if (TextUtils.isEmpty(cnyAmount) || "0.00".equals(cnyAmount)) {
                cnyAmount = String.format("%.2f", Float.parseFloat(Calculater.multiply(Calculater.formotFen(resp.getTrans_amount().replace("-", "").trim()), exchangeRate)));
            }
            printString += multipleSpaces(28 - cnyAmount.length()) + "CNY " + cnyAmount + builder.br();
            String showCNY = "CAD 1.00=CNY " + Calculater.multiply("1", exchangeRate);
            String printFx = context.getString(R.string.print_fx_rate);
            printString += printFx + multipleSpaces(32 - printFx.getBytes("GBK").length - showCNY.length()) + showCNY + builder.br();
            printString += builder.br() + builder.nBr();
            String tranlogId = Tools.deleteMidTranLog(resp.getShop_trans_id(), getConfig(AppConfigDef.mid));
            String printRecepit = context.getString(R.string.print_receipt);
            printString += printRecepit + "# " + multipleSpaces(30 - printRecepit.getBytes("GBK").length - tranlogId.getBytes("GBK").length) + tranlogId + builder.br();
            printString += resp.getTran_time().substring(0, 10) + multipleSpaces(22 - resp.getTran_time().substring(10).length()) + resp.getTran_time().substring(10) + builder.br();
            String payType = resp.getTransName();
            if ("Wechat".equals(payType)) {
                payType = "Wechat Pay";
            } else if (payType.contains("Union")) {
                payType = "Union Pay QR";
            }
            String printType = context.getString(R.string.print_type);
            printString += printType + multipleSpaces(32 - printType.getBytes("GBK").length - payType.length()) + payType + builder.br();
            String thirdTransOrder = resp.getThird_trade_no();
            if (!TextUtils.isEmpty(thirdTransOrder)) {
                printString += context.getString(R.string.print_trans) + builder.br();
                printString += multipleSpaces(32 - thirdTransOrder.length()) + thirdTransOrder + builder.br();
            }
//            String acctName = resp.getThirdExtName();
//            if (!TextUtils.isEmpty(acctName)) {
//                String printAcctName = context.getString(R.string.print_acctName);
//                printString += printAcctName + multipleSpaces(32 - printAcctName.getBytes("GBK").length - acctName.getBytes("GBK").length) + acctName + builder.br();
//            }
//            String acct = resp.getThirdExtId();
//            if (!TextUtils.isEmpty(acct)) {
//                String printAcct = context.getString(R.string.print_acct);
//                printString += printAcct + multipleSpaces(32 - printAcct.getBytes("GBK").length - acct.getBytes("GBK").length) + acct + builder.br();
//            }
            printString += builder.br() + builder.nBr();
            printString += builder.center(builder.bold(context.getString(R.string.print_approved)));
            printString += builder.br() + builder.nBr();
            printString += builder.center(builder.bold(context.getString(R.string.print_merchant_copy)));
            printString += builder.center(builder.bold(context.getString(R.string.print_important)));
            printString += builder.center(builder.bold(context.getString(R.string.print_hint1)));
            printString += builder.center(builder.bold(context.getString(R.string.print_hint2)));
            printString += builder.branch() + builder.endPrint();
            controller.print(printString);
            controller.cutPaper();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public void reprintMerchantSale(DailyDetailResp resp) {
        try {
            PrintServiceControllerProxy controller = new PrintServiceControllerProxy(context);
            Q1PrintBuilder builder = new Q1PrintBuilder();
            String printString = "";
            String merchant = getConfig(AppConfigDef.merchantName);
            printString += builder.center(builder.bold(merchant + context.getString(R.string.print_reprint)));
            String address = getConfig(AppConfigDef.merchantAddr);
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
            String tel = getConfig(AppConfigDef.merchantTel);
            if (!TextUtils.isEmpty(tel)) {
                printString += builder.center(tel);
            }
            String merchantId = spUtils.getShopId();
            printString += context.getString(R.string.print_merchant_id) + merchantId + builder.br();
            String terminalId = getConfig(AppConfigDef.sn);
            String merchantAddr = spUtils.getMerchantAddr();
            printString += context.getString(R.string.print_merchant_addr) + merchantAddr + builder.br();
            String merchantTel = spUtils.getMerchantTel();
            printString += context.getString(R.string.print_merchant_tel) + merchantTel + builder.br();
            printString += context.getString(R.string.print_terminal_id) + terminalId + builder.br();
            String cahierId = spUtils.getOperId();
            printString += context.getString(R.string.print_cashier_id) + cahierId + builder.br();
            printString += builder.br();
            printString += builder.center(builder.bold(context.getString(R.string.print_sale))) + builder.br();
            String totalAmount = resp.getTrans_amount();
            String tipsAmount = resp.getTip_amount();
            String printPurchase = context.getString(R.string.print_purchase);
            if (!TextUtils.isEmpty(tipsAmount) && !tipsAmount.equals("0")) {
                String purchaseAmount = Calculater.formotFen(Calculater.subtract(totalAmount, tipsAmount));
                printString += printPurchase + multipleSpaces(31 - printPurchase.getBytes("GBK").length - purchaseAmount.length()) + "$" + purchaseAmount + builder.br();
            } else {
                printString += printPurchase + multipleSpaces(31 - printPurchase.getBytes("GBK").length - Calculater.formotFen(resp.getTrans_amount()).length()) + "$" + Calculater.formotFen(resp.getTrans_amount()) + builder.br();
            }
            if (!TextUtils.isEmpty(tipsAmount) && !tipsAmount.equals("0")) {
                String printTip = context.getString(R.string.print_tip);
                printString += printTip + multipleSpaces(31 - printTip.getBytes("GBK").length - Calculater.formotFen(tipsAmount).length()) + "$" + Calculater.formotFen(tipsAmount) + builder.br();
            }
            printString += "Total:" + multipleSpaces(25 - Calculater.formotFen(resp.getTrans_amount()).length()) + "$" + Calculater.formotFen(resp.getTrans_amount()) + builder.br();
            String exchangeRate = resp.getExchange_rate();
            if (TextUtils.isEmpty(exchangeRate)) {
                exchangeRate = "1";
            }
            String cnyAmount = Calculater.formotFen(resp.getCny_amount()).replace("-", "").trim();
            if (TextUtils.isEmpty(cnyAmount) || "0.00".equals(cnyAmount)) {
                cnyAmount = String.format("%.2f", Float.parseFloat(Calculater.multiply(Calculater.formotFen(resp.getTrans_amount()), exchangeRate)));
            }
            printString += multipleSpaces(28 - cnyAmount.length()) + "CNY " + cnyAmount + builder.br();
            String showCNY = "CAD 1.00=CNY " + Calculater.multiply("1", exchangeRate);
            String printFx = context.getString(R.string.print_fx_rate);
            printString += printFx + multipleSpaces(32 - printFx.getBytes("GBK").length - showCNY.length()) + showCNY + builder.br();
            printString += builder.br();
            String tranlogId = Tools.deleteMidTranLog(resp.getShop_trans_id(), getConfig(AppConfigDef.mid));
            String printRecepit = context.getString(R.string.print_receipt);
            printString += printRecepit + "# " + multipleSpaces(30 - printRecepit.getBytes("GBK").length - tranlogId.getBytes("GBK").length) + tranlogId + builder.br();
            printString += resp.getTran_time().substring(0, 10) + multipleSpaces(22 - resp.getTran_time().substring(10).length()) + resp.getTran_time().substring(10) + builder.br();
            String payType = resp.getTransName();
            if ("Wechat".equals(payType)) {
                payType = "Wechat Pay";
            } else if (payType.contains("Union")) {
                payType = "Union Pay QR";
            }
            String printType = context.getString(R.string.print_type);
            printString += printType + multipleSpaces(32 - printType.getBytes("GBK").length - payType.getBytes("GBK").length) + payType + builder.br();
            String thirdTransOrder = resp.getThird_trade_no();
            if (!TextUtils.isEmpty(thirdTransOrder)) {
                printString += context.getString(R.string.print_trans) + builder.br();
                printString += multipleSpaces(32 - thirdTransOrder.getBytes("GBK").length) + thirdTransOrder + builder.br();
            }
//            String acctName = resp.getThirdExtName();
//            if (!TextUtils.isEmpty(acctName)) {
//                String printAcctName = context.getString(R.string.print_acctName);
//                printString += printAcctName + multipleSpaces(32 - printAcctName.getBytes("GBK").length - acctName.getBytes("GBK").length) + acctName + builder.br();
//            }
//            String acct = resp.getThirdExtId();
//            if (!TextUtils.isEmpty(acct)) {
//                String printAcct = context.getString(R.string.print_acct);
//                printString += printAcct + multipleSpaces(32 - printAcct.getBytes("GBK").length - acct.getBytes("GBK").length) + acct + builder.br();
//            }
            printString += builder.br();
            printString += builder.center(builder.bold(context.getString(R.string.print_approved)));
            printString += builder.br();
            printString += builder.center(builder.bold(context.getString(R.string.print_merchant_copy)));
            printString += builder.center(builder.bold(context.getString(R.string.print_important)));
            printString += builder.center(builder.bold(context.getString(R.string.print_hint1)));
            printString += builder.center(builder.bold(context.getString(R.string.print_hint2)));
            printString += builder.branch() + builder.endPrint();
            controller.print(printString);
            controller.cutPaper();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reprintCustomerSale(DailyDetailResp resp) {
        try {
            PrintServiceControllerProxy controller = new PrintServiceControllerProxy(context);
            Q1PrintBuilder builder = new Q1PrintBuilder();
            String printString = "";
            String merchant = getConfig(AppConfigDef.merchantName);
            printString += builder.center(builder.bold(merchant + context.getString(R.string.print_reprint)));
            String address = getConfig(AppConfigDef.merchantAddr);
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
            String tel = getConfig(AppConfigDef.merchantTel);
            if (!TextUtils.isEmpty(tel)) {
                printString += builder.center(tel);
            }
            String merchantId = spUtils.getShopId();
            printString += context.getString(R.string.print_merchant_id) + merchantId + builder.br();
            String terminalId = getConfig(AppConfigDef.sn);
            String merchantAddr = spUtils.getMerchantAddr();
            printString += context.getString(R.string.print_merchant_addr) + merchantAddr + builder.br();
            String merchantTel = spUtils.getMerchantTel();
            printString += context.getString(R.string.print_merchant_tel) + merchantTel + builder.br();
            printString += context.getString(R.string.print_terminal_id) + terminalId + builder.br();
            String cahierId = spUtils.getOperId();
            printString += context.getString(R.string.print_cashier_id) + cahierId + builder.br();
            printString += builder.br();
            printString += builder.center(builder.bold(context.getString(R.string.print_sale))) + builder.br();
            String totalAmount = resp.getTrans_amount();
            String tipsAmount = resp.getTip_amount();
            String printPurchase = context.getString(R.string.print_purchase);
            if (!TextUtils.isEmpty(tipsAmount) && !tipsAmount.equals("0")) {
                String purchaseAmount = Calculater.formotFen(Calculater.subtract(totalAmount, tipsAmount));
                printString += printPurchase + multipleSpaces(31 - printPurchase.getBytes("GBK").length - purchaseAmount.length()) + "$" + purchaseAmount + builder.br();
            } else {
                printString += printPurchase + multipleSpaces(31 - printPurchase.getBytes("GBK").length - Calculater.formotFen(resp.getTrans_amount()).length()) + "$" + Calculater.formotFen(resp.getTrans_amount()) + builder.br();
            }
            if (!TextUtils.isEmpty(tipsAmount) && !tipsAmount.equals("0")) {
                String printTip = context.getString(R.string.print_tip);
                printString += printTip + multipleSpaces(31 - printTip.getBytes("GBK").length - Calculater.formotFen(tipsAmount).length()) + "$" + Calculater.formotFen(tipsAmount) + builder.br();
            }
            printString += "Total:" + multipleSpaces(25 - Calculater.formotFen(resp.getTrans_amount()).length()) + "$" + Calculater.formotFen(resp.getTrans_amount()) + builder.br();
            String exchangeRate = resp.getExchange_rate();
            if (TextUtils.isEmpty(exchangeRate)) {
                exchangeRate = "1";
            }
            String cnyAmount = Calculater.formotFen(resp.getCny_amount()).replace("-", "").trim();
            if (TextUtils.isEmpty(cnyAmount) || "0.00".equals(cnyAmount)) {
                cnyAmount = String.format("%.2f", Float.parseFloat(Calculater.multiply(Calculater.formotFen(resp.getTrans_amount()), exchangeRate)));
            }
            printString += multipleSpaces(28 - cnyAmount.length()) + "CNY " + cnyAmount + builder.br();
            String showCNY = "CAD 1.00=CNY " + Calculater.multiply("1", exchangeRate);
            String printFx = context.getString(R.string.print_fx_rate);
            printString += printFx + multipleSpaces(32 - printFx.getBytes("GBK").length - showCNY.length()) + showCNY + builder.br();
            printString += builder.br();
            String tranlogId = Tools.deleteMidTranLog(resp.getShop_trans_id(), getConfig(AppConfigDef.mid));
            String printRecepit = context.getString(R.string.print_receipt);
            printString += printRecepit + "# " + multipleSpaces(30 - printRecepit.getBytes("GBK").length - tranlogId.getBytes("GBK").length) + tranlogId + builder.br();
            printString += resp.getTran_time().substring(0, 10) + multipleSpaces(22 - resp.getTran_time().substring(10).length()) + resp.getTran_time().substring(10) + builder.br();
            String payType = resp.getTransName();//            if ("Wechat".equals(payType)) {
//                payType = "Wechat Pay";
//            } else if (payType.contains("Union") || payType.contains("UNS")) {
//                payType = "Union Pay QR";
//            }
            String printType = context.getString(R.string.print_type);
            printString += printType + multipleSpaces(32 - printType.getBytes("GBK").length - payType.getBytes("GBK").length) + payType + builder.br();
            String thirdTransOrder = resp.getThird_trade_no();
            if (!TextUtils.isEmpty(thirdTransOrder)) {
                printString += context.getString(R.string.print_trans) + builder.br();
                printString += multipleSpaces(32 - thirdTransOrder.getBytes("GBK").length) + thirdTransOrder + builder.br();
            }
//            String acctName = resp.getThirdExtName();
//            if (!TextUtils.isEmpty(acctName)) {
//                String printAcctName = context.getString(R.string.print_acctName);
//                printString += printAcctName + multipleSpaces(32 - printAcctName.getBytes("GBK").length - acctName.getBytes("GBK").length) + acctName + builder.br();
//            }
//            String acct = resp.getThirdExtId();
//            if (!TextUtils.isEmpty(acct)) {
//                String printAcct = context.getString(R.string.print_acct);
//                printString += printAcct + multipleSpaces(32 - printAcct.getBytes("GBK").length - acct.getBytes("GBK").length) + acct + builder.br();
//            }
            printString += builder.br();
            printString += builder.center(builder.bold(context.getString(R.string.print_approved)));
            printString += builder.br();
            printString += builder.center(builder.bold(context.getString(R.string.print_customer_copy)));
            printString += builder.center(builder.bold(context.getString(R.string.print_important)));
            printString += builder.center(builder.bold(context.getString(R.string.print_hint1)));
            printString += builder.center(builder.bold(context.getString(R.string.print_hint2)));
            printString += builder.branch() + builder.endPrint();
            controller.print(printString);
            controller.cutPaper();
        } catch (Exception e) {
            e.printStackTrace();
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

    public void printTest() {
        PrintServiceControllerProxy controller = new PrintServiceControllerProxy(context);
        Q1PrintBuilder builder = new Q1PrintBuilder();
        String printString = "";
        printString += builder.center(builder.bold("DAILY REPORT"));
        printString += builder.center(builder.bold("SALES"));
        printString += builder.center("END OF REPORT") + builder.br();
        printString += builder.branch();

        printString += builder.endPrint();
        controller.print(printString);
        controller.cutPaper();
    }

    public void printTodaySumPlus(TranLogVoResp.TranLogVo tranLogVo) {
        PrintServiceControllerProxy controller = new PrintServiceControllerProxy(context);
        Q1PrintBuilder builder = new Q1PrintBuilder();
        String printString = "";
        printString += builder.center(builder.bold("DAILY REPORT"));
        printString += builder.center(builder.bold("SALES"));
        printString += builder.center(getConfig(AppConfigDef.merchantName, ""));
        printString += builder.center(getConfig(AppConfigDef.merchantAddr, ""));
        printString += builder.center(DateUtil.formatInternationalDate(new Date()));
        printString += "Summary Period:" + builder.br();
        printString += "" + multipleSpaces(32 - tranLogVo.getBeginTime().length()) + tranLogVo.getBeginTime() + builder.br();
        printString += "" + multipleSpaces(32 - tranLogVo.getEndTime().length()) + tranLogVo.getEndTime() + builder.br();
        printString += builder.br();

        printString += context.getString(R.string.print_merchant_id) +spUtils.getShopId() + builder.br();
        printString += context.getString(R.string.print_emplayee) + "All" + builder.br();
        if ("4".equals(getConfig(AppConfigDef.authFlag))) {
            printString += context.getString(R.string.print_terminal_id) + getConfig(AppConfigDef.sn, "") + builder.br();
        } else {
            printString += context.getString(R.string.print_terminal_id) + "All" + builder.br();
        }


        printString += builder.br();

        printString += builder.center(builder.bold("SUMMARY"));
        printString += "Gross Sales x " + tranLogVo.getDatas().getGross_sales_number() + multipleSpaces(31 - (("Gross Sales x " + tranLogVo.getDatas().getGross_sales_number()) + Tools.formatFen(tranLogVo.getDatas().getGross_sales_amount())).length()) + ("$" + Tools.formatFen(tranLogVo.getDatas().getGross_sales_amount())) + builder.br();
        printString += "Refunds x " + tranLogVo.getDatas().getRefunds_number() + multipleSpaces(31 - (("Refunds x " + tranLogVo.getDatas().getRefunds_number()) + Tools.formatFen(tranLogVo.getDatas().getRefunds_amount())).length()) + "$" + Tools.formatFen(tranLogVo.getDatas().getRefunds_amount()) + builder.br();
        printString += "Net Sales x " + tranLogVo.getDatas().getNet_sales_number() + multipleSpaces(31 - (("Net Sales x " + tranLogVo.getDatas().getNet_sales_number()) + Tools.formatFen(tranLogVo.getDatas().getNet_sales_amount())).length()) + ("$" + Tools.formatFen(tranLogVo.getDatas().getNet_sales_amount())) + builder.br();
        printString += "Tips x " + tranLogVo.getDatas().getTips_number() + multipleSpaces(31 - (("Tips x " + tranLogVo.getDatas().getTips_number()) + Tools.formatFen(tranLogVo.getDatas().getTips_amount())).length()) + ("$" + Tools.formatFen(tranLogVo.getDatas().getTips_amount())) + builder.br();
        printString += "Total Collected" + multipleSpaces(16 - Tools.formatFen(tranLogVo.getDatas().getTotal_collected()).length()) + ("$" + Tools.formatFen(tranLogVo.getDatas().getTotal_collected())) + builder.br();
        printString += builder.br();

//        printString += builder.center(builder.bold("WECHAT PAY SUMMARY"));
//        printString += "Net Sales x " + tranLogVo.getWechatNetSalesNumber() + multipleSpaces(32 - (("Net Sales x " + tranLogVo.getWechatNetSalesNumber()) + "$" + Tools.formatFen(tranLogVo.getWechatNetSalesAmount())).length()) + ("$" + Tools.formatFen(tranLogVo.getWechatNetSalesAmount())) + builder.br();
//        printString += builder.br();

        printString += builder.center(builder.bold("ALIPAY SUMMARY"));
        printString += "Net Sales x " + tranLogVo.getDatas().getAlipay_netSales_number() + multipleSpaces(32 - (("Net Sales x " + tranLogVo.getDatas().getAlipay_netSales_number()) + "$" + Tools.formatFen(tranLogVo.getDatas().getAlipay_netSales_amount())).length()) + ("$" + Tools.formatFen(tranLogVo.getDatas().getAlipay_netSales_amount())) + builder.br();
        printString += builder.br();

//        printString += builder.center(builder.bold("Union Pay QR SUMMARY"));
//        printString += "Net Sales x " + tranLogVo.getUnionPayNetSalesNumber() + multipleSpaces(32 - (("Net Sales x " + tranLogVo.getUnionPayNetSalesNumber()) + "$" + Tools.formatFen(tranLogVo.getUnionPayNetSalesAmount())).length()) + ("$" + Tools.formatFen(tranLogVo.getUnionPayNetSalesAmount())) + builder.br();
//        printString += builder.br();

        printString += builder.center("END OF REPORT") + builder.br();
        printString += builder.branch();

        printString += builder.endPrint();
        controller.print(printString);
        controller.cutPaper();
    }

    private String getShowCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date(System.currentTimeMillis());
        return format.format(currentDate);
    }

    public static String getFormatedDateTime(String pattern, long dateTime) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
        return sDateFormat.format(new Date(dateTime + 0));
    }
}
