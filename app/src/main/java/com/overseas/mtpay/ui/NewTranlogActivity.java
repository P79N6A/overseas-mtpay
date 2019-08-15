package com.overseas.mtpay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.log.Logger;
import com.overseas.mtpay.R;
import com.overseas.mtpay.adapter.TranlogDetailAdapter;
import com.overseas.mtpay.app.App;
import com.overseas.mtpay.bean.DailyDetailResp;
import com.overseas.mtpay.bean.QuerySummaryDetailResp;
import com.overseas.mtpay.bean.TransDetailResp;
import com.overseas.mtpay.db.AppConfigDef;
import com.overseas.mtpay.db.AppConfigHelper;
import com.overseas.mtpay.db.Constants;
import com.overseas.mtpay.http.NetCodeConstants;
import com.overseas.mtpay.http.NetConfig;
import com.overseas.mtpay.http.NetRequest;
import com.overseas.mtpay.print.PrintServiceControllerProxy;
import com.overseas.mtpay.ui.fragment.QueryFragment;
import com.overseas.mtpay.ui.fragment.RefundDialogFragment;
import com.overseas.mtpay.ui.presenter.StatisticsPresenter;
import com.overseas.mtpay.utils.Calculater;
import com.overseas.mtpay.utils.LogEx;
import com.overseas.mtpay.utils.MsgTipsDialog;
import com.overseas.mtpay.utils.SharePreferenceUtil;
import com.overseas.mtpay.utils.Tools;
import com.overseas.mtpay.utils.UIHelper;
import com.overseas.mtpay.utils.widget.SwipyRefreshLayout;
import com.overseas.mtpay.utils.widget.SwipyRefreshLayoutDirection;
import com.ui.dialog.DialogHelper;
import com.ui.dialog.NoticeDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class NewTranlogActivity extends NewBaseTranlogActivity implements SwipyRefreshLayout.OnRefreshListener, QueryFragment.QueryFragmentListener, View.OnClickListener, RefundDialogFragment.OnSaveListener {
    private ExpandableListView expandableListView;
    private TranlogDetailAdapter adapter;
    private DrawerLayout dlMain;
    private String alreadyAmount;
    private int currentPage = 1;// 当前页数 从1开始
    private int mTotalPage;// 总页数
    private String mTranType = "pay";// 交易类型
    private String mTimeRange;// 时间范围 默认为今天(0)
    private String mStartTime = null;// 起始时间
    private String mEndTime = null;// 终止时间
    private String mOrderNo = null;// 订单号
    private SwipyRefreshLayout mSwipyRefreshLayout;
    //右侧抽屉相关数据
    private QueryFragment queryFragment;


    private StatisticsPresenter statisticsPresenter;
    private String TODAY = "0";
    private String YESTODAY = "1";
    private String THISWEEK = "2";
    private String RECHARGEON = "1";
    private String UNRECHARGEON = "1";
    private String DEFAULTNUM = "0";
    private static String TAG = NewTranlogActivity.class.getSimpleName();
    private static final int REQUEST_PAY_CANCEL = 2001;
    private static final int REQUEST_INPUT_PASSWORD = 2002;
    private DailyDetailResp dailyDetailResp;
    private List<DailyDetailResp> respList = new ArrayList<>();
    private List<DailyDetailResp> relist = new ArrayList<>();
    private SharePreferenceUtil spUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spUtils = new SharePreferenceUtil(App.getInstance());
        initView();
        getData(THISWEEK, UNRECHARGEON, null, "", 1, "", "", false);
        initDrawerLayout();
    }

    private void initView() {
//        setMainView(R.layout.activity_tranlog_detail_new);
        statisticsPresenter = new StatisticsPresenter(this);
        setTitleTxt(getResources().getString(R.string.trans_detail));
//        setTitleRightImage(R.drawable.ic_nav_search);
        adapter = new TranlogDetailAdapter(NewTranlogActivity.this);

        mSwipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);
        mSwipyRefreshLayout.setOnRefreshListener(this);

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListView.setAdapter(adapter);
        expandableListView.setGroupIndicator(null); // 去掉默认带的箭头
        expandableListView.setSelection(0);// 设置默认选中项
        expandableListView.setOnGroupClickListener(onGroupClickListener);
        adapter.setOnTranLogDetialListener(new TranlogDetailAdapter.OnTranLogDetialListener() {
            @Override
            public void onPrint(final DailyDetailResp resp) {
                String masterTranlogId;
                //服务器返回的 是 Refund非 refund
                if (resp.getTrans_kind().contains("Refund")) {
                    masterTranlogId = resp.getShop_trans_id();
                } else {
                    masterTranlogId = resp.getMaster_tran_log_id();
                }
                getDetailData("", "", "", "", 1, "", masterTranlogId);
            }

            @Override
            public void onRevoke(DailyDetailResp resp) {
                dailyDetailResp = resp;
                alreadyAmount = String.valueOf(Integer.parseInt(resp.getTrans_amount()) - Integer.parseInt(resp.getRefund_amount()));
                toInputPasswordActivity(REQUEST_INPUT_PASSWORD);
            }
        });
    }

    /**
     * 能否加载更多
     *
     * @return
     */
    private boolean isNoMorePage() {
        return currentPage >= mTotalPage;
    }

    /**
     * 停止加载
     */
    private void stopRefresh() {
        mSwipyRefreshLayout.setRefreshing(false);
        progresser.showContent();
    }

    private void rePrintCustomer() {
        for (final DailyDetailResp detailResp : respList) {
            if (getString(R.string.pay_tag).equals(detailResp.getTrans_kind())) {
                detailResp.setTrans_amount(detailResp.getTrans_amount());
                detailResp.setTransName(Constants.TRAN_TYPE.get(detailResp.getTrans_type()));
                detailResp.setTran_time(detailResp.getTran_time());
                detailResp.setMaster_tran_log_id(detailResp.getMaster_tran_log_id());
                detailResp.setShop_trans_id(detailResp.getShop_trans_id());
                detailResp.setRefund_amount(detailResp.getRefund_amount());
                detailResp.setOperate_name(detailResp.getOperate_name());
                detailResp.setTip_amount(detailResp.getTip_amount());
                detailResp.setShop_order_no(detailResp.getShop_order_no());
                detailResp.setCny_amount(detailResp.getCny_amount());
                detailResp.setThird_trade_no(detailResp.getThird_trade_no());
//                if (!TextUtils.isEmpty(detailResp.getThirdExtName())) {
//                    detailResp.setThirdExtName(detailResp.getThirdExtName());
//                }
//                if (!TextUtils.isEmpty(detailResp.getThirdExtId())) {
//                    detailResp.setThirdExtId(detailResp.getThirdExtId());
//                }


                statisticsPresenter.reprintCustomerSale(detailResp);
            }
            if (getString(R.string.refund_tag).equals(detailResp.getTrans_kind())) {
                detailResp.setTrans_amount(detailResp.getTrans_amount());
                detailResp.setTransName(Constants.TRAN_TYPE.get(detailResp.getTrans_type()));
                detailResp.setTran_time(detailResp.getTran_time());
                detailResp.setMaster_tran_log_id(detailResp.getMaster_tran_log_id());
                detailResp.setShop_trans_id(detailResp.getShop_trans_id());
                detailResp.setRefund_amount(detailResp.getRefund_amount());
                detailResp.setOperate_name(detailResp.getOperate_name());
                detailResp.setTip_amount(detailResp.getTip_amount());
                detailResp.setShop_order_no(detailResp.getShop_order_no());
                detailResp.setCny_amount(detailResp.getCny_amount());
                detailResp.setThird_trade_no(detailResp.getThird_trade_no());
//                if (!TextUtils.isEmpty(detailResp.getThirdExtName())) {
//                    detailResp.setThirdExtName(detailResp.getThirdExtName());
//                }
//                if (!TextUtils.isEmpty(detailResp.getThirdExtId())) {
//                    detailResp.setThirdExtId(detailResp.getThirdExtId());
//                }
                statisticsPresenter.reprintCustomerRefund(detailResp);
            }
        }
        respList.clear();
    }

    private void rePrintMerchant() {
        for (final DailyDetailResp detailResp : respList) {
            if (getString(R.string.pay_tag).equals(detailResp.getTrans_kind())) {
                detailResp.setTrans_amount(detailResp.getTrans_amount());
                detailResp.setTransName(Constants.TRAN_TYPE.get(detailResp.getTrans_type()));
                detailResp.setTran_time(detailResp.getTran_time());
                detailResp.setMaster_tran_log_id(detailResp.getMaster_tran_log_id());
                detailResp.setShop_trans_id(detailResp.getShop_trans_id());
                detailResp.setRefund_amount(detailResp.getRefund_amount());
                detailResp.setOperate_name(detailResp.getOperate_name());
                detailResp.setTip_amount(detailResp.getTip_amount());
                detailResp.setShop_order_no(detailResp.getShop_order_no());
                detailResp.setCny_amount(detailResp.getCny_amount());
                detailResp.setThird_trade_no(detailResp.getThird_trade_no());
//                if (!TextUtils.isEmpty(detailResp.getThirdExtName())) {
//                    detailResp.setThirdExtName(detailResp.getThirdExtName());
//                }
//                if (!TextUtils.isEmpty(detailResp.getThirdExtId())) {
//                    detailResp.setThirdExtId(detailResp.getThirdExtId());
//                }
                statisticsPresenter.reprintMerchantSale(detailResp);
            }
            if (getString(R.string.refund_tag).equals(detailResp.getTrans_kind())) {
                detailResp.setTrans_amount(detailResp.getTrans_amount());
                detailResp.setTransName(Constants.TRAN_TYPE.get(detailResp.getTrans_type()));
                detailResp.setTran_time(detailResp.getTran_time());
                detailResp.setMaster_tran_log_id(detailResp.getMaster_tran_log_id());
                detailResp.setShop_trans_id(detailResp.getShop_trans_id());
                detailResp.setRefund_amount(detailResp.getRefund_amount());
                detailResp.setOperate_name(detailResp.getOperate_name());
                detailResp.setTip_amount(detailResp.getTip_amount());
                detailResp.setShop_order_no(detailResp.getShop_order_no());
                detailResp.setCny_amount(detailResp.getCny_amount());
                detailResp.setThird_trade_no(detailResp.getThird_trade_no());
//                if (!TextUtils.isEmpty(detailResp.getThirdExtName())) {
//                    detailResp.setThirdExtName(detailResp.getThirdExtName());
//                }
//                if (!TextUtils.isEmpty(detailResp.getThirdExtId())) {
//                    detailResp.setThirdExtId(detailResp.getThirdExtId());
//                }
                statisticsPresenter.reprintMerchantRefund(detailResp);
            }
        }
        respList.clear();
    }

    public void toInputPasswordActivity(int requestCode) {
        Intent intent = new Intent(NewTranlogActivity.this, InputPassWordActivity.class);
        this.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_INPUT_PASSWORD) {
                RefundDialogFragment refundDialogFragment = RefundDialogFragment.newInstance(getString(R.string.refund), alreadyAmount);
                refundDialogFragment.show(getFragmentManager(), null);
            } else if (requestCode == REQUEST_PAY_CANCEL) {
                getData(THISWEEK, UNRECHARGEON, null, "", 1, "", "", false);
                printRefund();
            }

        }
    }

    private void printRefund() {
        int printNumber = 1;
        if (!TextUtils.isEmpty(AppConfigHelper.getConfig(AppConfigDef.print_number))) {
            printNumber = Integer.parseInt(AppConfigHelper.getConfig(AppConfigDef.print_number));
        }
        final PrintServiceControllerProxy controller = new PrintServiceControllerProxy(this);
        switch (printNumber) {
            case 1:
                Log.d("NetR", "内容：" + AppConfigHelper.getConfig(AppConfigDef.PRINT_SALE_REFUND_CONTEXT));
                controller.print(AppConfigHelper.getConfig(AppConfigDef.PRINT_SALE_REFUND_CONTEXT));
                break;
            case 2:
                controller.print(AppConfigHelper.getConfig(AppConfigDef.PRINT_SALE_REFUND_CONTEXT));
                final NoticeDialogFragment dialogFragment = NoticeDialogFragment.newInstance("INFORMATION", "Customer Copy?", "YES", "NO");
                dialogFragment.setListener(new DialogHelper.DialogCallbackAndNo() {
                    @Override
                    public void callback() {
                        controller.print(AppConfigHelper.getConfig(AppConfigDef.PRINT_CUSTOMER_REFUND_CONTEXT));
                    }

                    @Override
                    public void callbackNo() {
                        dialogFragment.dismiss();
                    }
                });
                dialogFragment.show(getSupportFragmentManager(), "SimpleMsgDialogFragment");
                break;
            case 3:
                controller.print(AppConfigHelper.getConfig(AppConfigDef.PRINT_SALE_REFUND_CONTEXT));
                final NoticeDialogFragment fragmentDialog = NoticeDialogFragment.newInstance("INFORMATION", "Customer Copy?", "YES", "NO");
                fragmentDialog.setListener(new DialogHelper.DialogCallbackAndNo() {
                    @Override
                    public void callback() {
                        controller.print(AppConfigHelper.getConfig(AppConfigDef.PRINT_CUSTOMER_REFUND_CONTEXT));
                        final NoticeDialogFragment dialog = NoticeDialogFragment.newInstance("INFORMATION", "Customer Copy?", "YES", "NO");
                        dialog.setListener(new DialogHelper.DialogCallbackAndNo() {
                            @Override
                            public void callback() {
                                controller.print(AppConfigHelper.getConfig(AppConfigDef.PRINT_CUSTOMER_REFUND_CONTEXT));
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

    /**
     * 右侧查询相关控件 Song
     * 封装为Fragment
     */
    private void initDrawerLayout() {
        dlMain = (DrawerLayout) findViewById(R.id.dl_main);
        dlMain.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

        });
        queryFragment = QueryFragment.newInstance();
        if (!queryFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.inRight, queryFragment, "query").commit();
        }
    }

    @Override
    public void onQuery(String timeRange, String tranType, String startDate, String endDate, String tranlogId) {
        getData(timeRange, UNRECHARGEON, tranType, startDate, 1, endDate, tranlogId, false);
        if (dlMain.isDrawerOpen(Gravity.RIGHT)) {
            dlMain.closeDrawer(Gravity.RIGHT);
        }
    }

    private void setTitleTxt(String title) {
        Toolbar toolbarOwner = (Toolbar) findViewById(R.id.toolbarOwner);
        if (toolbarOwner != null) {
            toolbarOwner.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbarOwner);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            toolbarOwner.setNavigationIcon(R.drawable.back);
        }
        TextView tvTitleOwner = (TextView) findViewById(R.id.tvTitleOwner);
        RelativeLayout rlToolbarRightOwner = (RelativeLayout) findViewById(R.id.rlToolbarRightOwner);
        if (tvTitleOwner != null) {
            tvTitleOwner.setText(title);
            tvTitleOwner.setVisibility(View.VISIBLE);
        }
        TextView tvSettingParams = (TextView) findViewById(R.id.tvSettingParams);
        if (tvSettingParams != null && rlToolbarRightOwner != null) {
            rlToolbarRightOwner.setVisibility(View.VISIBLE);
            rlToolbarRightOwner.setOnClickListener(this);
            tvSettingParams.setBackgroundResource(R.drawable.ic_nav_search);
            tvSettingParams.setOnClickListener(this);
        }
  /*      ImageView ivLeftIcon = (ImageView) findViewById(R.id.ivLeftIcon);
        if (ivLeftIcon != null) {
            ivLeftIcon.setVisibility(View.VISIBLE);
            ivLeftIcon.setImageResource(R.id.);
            ivLeftIcon.setOnClickListener(this);
        }*/
    }

    /**
     * 根据主流水号查询相关的所有订单并打印小票
     */
    private void getDetailData(String timeRange, String rechargeOn, String transType, String startTime, int pageNumber, String endTime, String tranlogId) {

        progresser.showProgress();
        JSONObject json = new JSONObject();
        if (!TextUtils.isEmpty(timeRange)) {
            json.put("time_range", timeRange);
        }
        json.put("tran_log_id", tranlogId);
        if (!TextUtils.isEmpty(transType)) {
            json.put("tran_type", transType);
        }
        json.put("page_no", pageNumber);
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
                    for (int i = 0; i < list.size(); i++) {
                        relist = list.get(i).getTranLogDailyDetail();
                        respList.addAll(relist);
                    }
                }
                if (respList.size() > 0) {
                    NoticeDialogFragment dialogFragment = NoticeDialogFragment.newInstance("REPRINT", "Make your choice", "Customer Copy", "Merchant Copy");
                    dialogFragment.setListener(new DialogHelper.DialogCallbackAndNo() {
                        @Override
                        public void callback() {
                            rePrintCustomer();
                        }

                        @Override
                        public void callbackNo() {
                            rePrintMerchant();
                        }
                    });
                    dialogFragment.show(getSupportFragmentManager(), "SimpleMsgDialogFragment");
                }
            }

            @Override
            public void onFailed(String code, String message) {
                progresser.showContent();

                if (NetCodeConstants.MERCHANT_DISABLE_CODE_68.equals(code)) {
                    MsgTipsDialog.toLoginActivity(NewTranlogActivity.this, NetCodeConstants.MERCHANT_DISABLE_MSG);
                } else if (NetCodeConstants.TERMINAL_REGISTER_CODE_53.equals(code)) {
                    MsgTipsDialog.toLoginActivity(NewTranlogActivity.this, NetCodeConstants.TERMINAL_REGISTER_MSG);
                } else {
                    progresser.showError(message, true);

                }
//                CommonToastUtil.showMsgAbove(NewDailySumActivityPlus.this, CommonToastUtil.LEVEL_WARN, message);
                Logger.d("Login onFailed: " + code + "--------" + message);
            }
        });


    }

    /**
     * 查询方法
     *
     * @param timeRange  //0 今天 1 昨天 2本周 3上周 4本月 5上月 6时间段 必须传
     * @param rechargeOn //0不含充值 1含充值
     * @param transType
     * @param startTime
     * @param pageNumber //必须传，查询几天数据
     * @param endTime
     * @param tranlogId  //流水号
     */
    private void getData(String timeRange, String rechargeOn, String transType, String startTime, int pageNumber, String endTime, String tranlogId, final boolean isRefresh) {
        mTranType = transType;
        mTimeRange = timeRange;
        mStartTime = startTime;
        mEndTime = endTime;
        mOrderNo = tranlogId;
        currentPage = pageNumber;
        progresser.showProgress();
        JSONObject json = new JSONObject();
        json.put("time_range", timeRange);
        if (!TextUtils.isEmpty(tranlogId)) {
            json.put("tran_log_id", tranlogId);
        }
        if (!TextUtils.isEmpty(transType)) {
            json.put("tran_type", transType);
        }
        json.put("page_no", pageNumber);
        if ("6".equals(timeRange)) {
            json.put("start_time", startTime);
            json.put("end_time", endTime);
        }
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

                mTotalPage = transDetailResp.getResult().getTotalPage();// 总页数

                if (list == null || list.isEmpty()) {
                    if (currentPage == 1) {
                        adapter.clear();
                        showErrorView("暂无数据");
                    } else {
                        stopRefresh();
                    }
                } else {
                    if (currentPage == 1) {
                        adapter.setDataChanged(list);
                    } else {
                        stopRefresh();
                        adapter.addRefreshDataChanged(list);
                    }
                }
//                adapter.setDataChanged(list);
                // 遍历所有group,将所有项设置成默认展开
                int groupCount = expandableListView.getCount();
                for (int i = 0; i < groupCount; i++) {
                    expandableListView.expandGroup(i);
                }
                if (groupCount == 0) {
                    progresser.showContent();
                    progresser.showError(getResources().getString(R.string.no_data), R.drawable.nodata_new, false);
                }
            }

            @Override
            public void onFailed(String code, String message) {
                progresser.showContent();
                if (NetCodeConstants.MERCHANT_DISABLE_CODE_68.equals(code)) {
                    MsgTipsDialog.toLoginActivity(NewTranlogActivity.this, NetCodeConstants.MERCHANT_DISABLE_MSG);
                } else if (NetCodeConstants.TERMINAL_REGISTER_CODE_53.equals(code)) {
                    MsgTipsDialog.toLoginActivity(NewTranlogActivity.this, NetCodeConstants.TERMINAL_REGISTER_MSG);
                } else {
                    progresser.showError(message, true);
                }
//                CommonToastUtil.showMsgAbove(NewDailySumActivityPlus.this, CommonToastUtil.LEVEL_WARN, message);
                Logger.d("Login onFailed: " + code + "--------" + message);
            }
        });

    }

    private void showErrorView(String msg) {
        if (progresser != null) {
            if (TextUtils.isEmpty(msg) == false) {
                progresser.showError(msg, false);
            } else {
                progresser.showError("未知异常", false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(NewMainActivity.getStartIntent(this));
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvSettingParams:
                if (!dlMain.isDrawerOpen(Gravity.RIGHT)) {
                    dlMain.openDrawer(Gravity.RIGHT);
                } else {
                    dlMain.closeDrawer(Gravity.RIGHT);
                }
                break;
            case R.id.rlToolbarRightOwner:
                findViewById(R.id.tvSettingParams).performClick();
                break;
            case R.id.ivLeftIcon:
                //back
                onBackPressed();
                break;
            default:
                break;
        }
    }

    ExpandableListView.OnGroupClickListener onGroupClickListener = new ExpandableListView.OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            return true;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home || id == R.drawable.back) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSave(String amount) {
        startActivityForResult(VoidTransActivity.getStartIntent(NewTranlogActivity.this, dailyDetailResp, Calculater.formotYuan(amount)), REQUEST_PAY_CANCEL);
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        Log.d("direction", direction.name());
        Log.d("onRefresh", "onRefresh");

        if (SwipyRefreshLayoutDirection.TOP == direction) {
            stopRefresh();
//            currentPage = 1;
//            // initParams();
//            loadDetialData(true);
        } else if (SwipyRefreshLayoutDirection.BOTTOM == direction) {
            if (isNoMorePage()) {
                UIHelper.ToastMessage(this, "没有更多数据了");
                stopRefresh();
                // cannotLoadMore();
            } else {
                currentPage++;
                loadDetialData(false);
            }
        }
    }

    private void canLoadMore() {
        mSwipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
    }

    /**
     * 开始加载
     */
    private void startRefresh() {
        Log.d("recordfragment", "开始加载");
        mSwipyRefreshLayout.setRefreshing(true);
    }

    /**
     * 加载数据
     */
    private void loadDetialData(final boolean isRefresh) {
        LogEx.i("loadDetialData", "loadDetialData");
        startRefresh();
        canLoadMore();
//		if (!TextUtils.isEmpty(orderNo)) {// 根据订单好来查询@hong
//			getTransactionDetial(isRefresh);
//		} else {
//        transactionDetialQuery(isRefresh);

        getData(mTimeRange, UNRECHARGEON, mTranType, mStartTime, currentPage, mEndTime, mOrderNo, isRefresh);

//		}
    }
}
