package com.overseas.mtpay.ui;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.log.Logger;
import com.overseas.mtpay.R;
import com.overseas.mtpay.app.App;
import com.overseas.mtpay.base.ProgressLayout;
import com.overseas.mtpay.bean.TranLogVoResp;
import com.overseas.mtpay.db.AppConfigDef;
import com.overseas.mtpay.db.AppConfigHelper;
import com.overseas.mtpay.http.NetCodeConstants;
import com.overseas.mtpay.http.NetConfig;
import com.overseas.mtpay.http.NetRequest;
import com.overseas.mtpay.ui.base.BaseViewActivity;
import com.overseas.mtpay.ui.presenter.StatisticsPresenter;
import com.overseas.mtpay.utils.DateUtil;
import com.overseas.mtpay.utils.MsgTipsDialog;
import com.overseas.mtpay.utils.SharePreferenceUtil;
import com.overseas.mtpay.utils.TodayTotalUtil;
import com.overseas.mtpay.utils.Tools;
import com.overseas.mtpay.utils.widget.PieChart02View;

import org.xclcharts.chart.PieData;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.overseas.mtpay.db.AppConfigHelper.getConfig;


/**
 * 今日汇总新版
 *
 * @author wu at 2019-06-08
 */
public class NewDailySumActivityPlus extends BaseViewActivity {
    private StatisticsPresenter statisticsPresenter;
    private TextView tvTimeRange, tvDevice;
    private TextView tvGrossSalesCount;
    private TextView tvGrossSalesAmount;
    private TextView tvRefundCount;
    private TextView tvRefundAmount;
    private TextView tvNetSaleCount;
    private TextView tvNetSaleAmount;

//    private TextView tvWechatNetSaleAmount;
//    private TextView tvWechatNetSaleCount;

    private TextView tvTipsAmount;
    private TextView tvTipsCount;

    private TextView tvAliPayNetSaleAmount;
    private TextView tvAliPayNetSaleCount;

//    private TextView tvUnionPayNetSalesAmount;
//    private TextView tvUnionPayNetSalesCount;

    private TextView tvTotalCollectedAmount;

    private ProgressLayout plDailyDetail;

    private TranLogVoResp.TranLogVo tranLogVo;
    private SharePreferenceUtil spUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statisticsPresenter = new StatisticsPresenter(this);
        spUtils = new SharePreferenceUtil(App.getInstance());
        initView();
        initSumData();
    }

    private void initSumData() {
        progresser.showProgress();
        JSONObject json = new JSONObject();
        json.put("time_range", "0");
        json.put("auth_flag", getConfig(AppConfigDef.authFlag));
        JSONObject sysParam = new JSONObject();
        sysParam.put("shop_id", spUtils.getShopId());
        sysParam.put("oper_id", spUtils.getOperId());
        sysParam.put("oper_name", spUtils.getOperName());
        sysParam.put("sn", App.getInstance().terminalSn());
        sysParam.put("language",  AppConfigHelper.getConfig(AppConfigDef.SWITCH_LANGUAGE));
        json.put("sysParam", sysParam);
//        String data = json.toJSONString();
        NetRequest.getInstance().fakePost(NetConfig.POST_QUERY_DAILY_SUMMARY, json, "2", new NetRequest.NetCallback() {
            @Override
            public void onSuccess(String resp) {
                progresser.showContent();
                TranLogVoResp tranLogVoRespTemp = JSONObject.parseObject(resp, TranLogVoResp.class);
                tranLogVo = tranLogVoRespTemp.getResult();
                tvTimeRange.setText(DateUtil.formatInternationalDate(tranLogVo.getBeginTime())
                        + " - "
                        + DateUtil.formatInternationalDate(tranLogVo.getEndTime()));

                tvGrossSalesCount.setText(String.valueOf(tranLogVo.getDatas().getGross_sales_number()));
                tvGrossSalesAmount.setText("$" + Tools.formatFen(tranLogVo.getDatas().getGross_sales_amount()));

                tvRefundCount.setText(String.valueOf(tranLogVo.getDatas().getRefunds_number()));
                tvRefundAmount.setText("$" + Tools.formatFen(tranLogVo.getDatas().getRefunds_amount()));

                tvNetSaleCount.setText(String.valueOf(tranLogVo.getDatas().getNet_sales_number()));
                tvNetSaleAmount.setText("$" + Tools.formatFen(tranLogVo.getDatas().getNet_sales_amount()));

                tvTipsCount.setText(String.valueOf(tranLogVo.getDatas().getTips_number()));
                tvTipsAmount.setText("$" + Tools.formatFen(tranLogVo.getDatas().getTips_amount()));

//                tvWechatNetSaleCount.setText(String.valueOf(tranLogVo.getWechatNetSalesNumber()));
//                tvWechatNetSaleAmount.setText("$" + Tools.formatFen(tranLogVo.getWechatNetSalesAmount()));

                tvAliPayNetSaleCount.setText(String.valueOf(tranLogVo.getDatas().getAlipay_netSales_number()));
                tvAliPayNetSaleAmount.setText("$" + Tools.formatFen(tranLogVo.getDatas().getAlipay_netSales_amount()));

//                tvUnionPayNetSalesCount.setText(String.valueOf(tranLogVo.getUnionPayNetSalesNumber()));
//                tvUnionPayNetSalesAmount.setText("$" + Tools.formatFen(tranLogVo.getUnionPayNetSalesAmount()));

                tvTotalCollectedAmount.setText("$" + Tools.formatFen(tranLogVo.getDatas().getTotal_collected()));

//                initPie(initPieData(tranLogVo));

                plDailyDetail.showContent();
            }

            @Override
            public void onFailed(String code, String message) {
                progresser.showContent();
                if (NetCodeConstants.MERCHANT_DISABLE_CODE_68.equals(code)) {
                    MsgTipsDialog.toLoginActivity(NewDailySumActivityPlus.this, NetCodeConstants.MERCHANT_DISABLE_MSG);
                } else if (NetCodeConstants.TERMINAL_REGISTER_CODE_53.equals(code)) {
                    MsgTipsDialog.toLoginActivity(NewDailySumActivityPlus.this, NetCodeConstants.TERMINAL_REGISTER_MSG);
                }else {

//                CommonToastUtil.showMsgAbove(NewDailySumActivityPlus.this, CommonToastUtil.LEVEL_WARN, message);
                plDailyDetail.showError(getResources().getString(R.string.no_data), R.drawable.nodata_new, false);
                }
                Logger.d("Login onFailed: " + code + "--------" + message);
            }
        });
    }

    private void initView() {
        setMainView(R.layout.activity_new_daily_sum_plus);
        showTitleBack();
        setTitleText(getResources().getString(R.string.daily_summary_international));
        setTitleRight(getResources().getString(R.string.print));
        tvTimeRange = findViewById(R.id.tvTimeRange);
        tvDevice = findViewById(R.id.tvDevice);
        plDailyDetail = findViewById(R.id.plDailyDetail);

        tvGrossSalesCount = findViewById(R.id.tvGrossSalesCount);
        tvGrossSalesAmount = findViewById(R.id.tvGrossSalesAmount);
        tvRefundCount = findViewById(R.id.tvRefundCount);
        tvRefundAmount = findViewById(R.id.tvRefundAmount);
        tvNetSaleCount = findViewById(R.id.tvNetSaleCount);
        tvNetSaleAmount = findViewById(R.id.tvNetSaleAmount);
        tvTipsCount = findViewById(R.id.tvTipsCount);
        tvTipsAmount = findViewById(R.id.tvTipsAmount);

//        tvWechatNetSaleAmount = findViewById(R.id.tvWechatNetSaleAmount);
//        tvWechatNetSaleCount = findViewById(R.id.tvWechatNetSaleCount);

        tvAliPayNetSaleAmount = findViewById(R.id.tvAliPayNetSaleAmount);
        tvAliPayNetSaleCount = findViewById(R.id.tvAliPayNetSaleCount);

//        tvUnionPayNetSalesAmount = findViewById(R.id.tvUnionPayNetSalesAmount);
//        tvUnionPayNetSalesCount = findViewById(R.id.tvUnionPayNetSalesCount);

        tvTotalCollectedAmount = findViewById(R.id.tvTotalCollectedAmount);
        if ("4".equals(getConfig(AppConfigDef.authFlag))) {
            tvDevice.setText("Device: " + AppConfigHelper.getConfig(AppConfigDef.sn, ""));
        } else {
            tvDevice.setText("Device: All");
        }
    }

    /**
     * 初始化饼图
     */
    private void initPie(ArrayList<PieData> chartData) {
        if (chartData == null) {
            return;
        }
//        饼图
        PieChart02View pie = new PieChart02View(this, chartData);
        FrameLayout flPie = findViewById(R.id.flPie);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        flPie.addView(pie);
    }

    /**
     * @return 当前日期
     */
    private String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date(System.currentTimeMillis());
        return format.format(currentDate);
    }


    private ArrayList<PieData> initPieData(TranLogVoResp.TranLogVo tranLogVo) {
        ArrayList<PieData> chartData = new ArrayList<>();
//        PieData wechatPieData = getPieData(tranLogVo, tranLogVo.getWechatSalesAmount(), "Wechat Pay", TodayTotalUtil.FLAG_WEXIN_COLOR);
        PieData alipayPieData;
        if(0 == tranLogVo.getDatas().getAlipay_sales_amount()){
             alipayPieData = new PieData("Alipay", "Alipay1", 100, getResources().getColor(R.color.gray));
        }else {
             alipayPieData = getPieData(tranLogVo, tranLogVo.getDatas().getAlipay_sales_amount(), "Alipay", TodayTotalUtil.FLAG_ALIPAY_COLOR);
        }
//        PieData alipayPieData = getPieData(tranLogVo, tranLogVo.getDatas().getAlipay_sales_amount(), "Alipay", TodayTotalUtil.FLAG_ALIPAY_COLOR);
//        PieData unionPieData = getPieData(tranLogVo, tranLogVo.getUnionPaySalesAmount(), "Union Pay QR", TodayTotalUtil.FLAG_UNION_COLOR);
//        chartData.add(wechatPieData);
        chartData.add(alipayPieData);
//        chartData.add(unionPieData);

        return chartData;
    }

    private PieData getPieData(TranLogVoResp.TranLogVo tranLogVo, int grossAmount, String key, int color) {
        BigDecimal totalAmountDec = new BigDecimal(tranLogVo.getDatas().getGross_sales_amount());
        BigDecimal scale = new BigDecimal(100);
        BigDecimal zero = new BigDecimal(0);
        if (totalAmountDec.compareTo(zero) == 0) {
            return null;
        }

        BigDecimal beanAmount = new BigDecimal(grossAmount);
        int beanCount = beanAmount.divide(totalAmountDec, 2, BigDecimal.ROUND_HALF_UP).multiply(scale).intValue();
        if (beanCount < 1 && beanAmount.compareTo(zero) > 0) {
            beanCount = 1;
        }
        float floatCount = beanAmount.multiply(scale).divide(totalAmountDec, 1, BigDecimal.ROUND_HALF_UP).floatValue();
        return new PieData(key + floatCount + "%", beanCount + "%", beanCount, color);
    }

    @Override
    protected void onTitleRightClicked() {
        super.onTitleRightClicked();
        printDay();
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

    /**
     * @Author: Huangweicai
     * @date 2015-11-4 下午6:59:33
     * @Description:打印交易汇总(日结单)
     */
    private void printDay() {
//        statisticsPresenter.printTest();
        if (tranLogVo != null) {
            try {
//                statisticsPresenter.printTest();
                statisticsPresenter.printTodaySumPlus(tranLogVo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
