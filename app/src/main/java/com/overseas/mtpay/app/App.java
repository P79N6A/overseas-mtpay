package com.overseas.mtpay.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.nexgo.oaf.apiv3.APIProxy;
import com.nexgo.oaf.apiv3.DeviceEngine;
import com.overseas.mtpay.db.AppConfig;
import com.overseas.mtpay.db.AppConfigDef;
import com.overseas.mtpay.db.AppConfigHelper;
import com.overseas.mtpay.db.AppConfigInitUtil;
import com.overseas.mtpay.db.AppState;
import com.overseas.mtpay.db.CashPayRepair;
import com.overseas.mtpay.db.Constants;
import com.overseas.mtpay.db.MerchantCardDef;
import com.overseas.mtpay.db.TicketCardDef;
import com.overseas.mtpay.db.UserBean;
import com.overseas.mtpay.device.DeviceManager;
import com.overseas.mtpay.print.MidFilterPrinterBuilder;
import com.overseas.mtpay.print.PrinterManager;
import com.overseas.mtpay.utils.LogEx;
import com.overseas.mtpay.utils.UuidUitl;


public class App extends Application {
    private static App app;
    private DbUtils dbController;
    public DeviceEngine deviceEngine;//N3或者N5打印原型

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        app = this;
        init();
    }

    public static App getInstance() {
        return app;
    }

    private void init() {
        LogEx.initData(this);
//        initPosApi();
//        initCrashHandler();
//        if (!Constants.UNIFIEDLOGIN_FLAG) {
            initDb();// 建库建表
//        }
        initConfig();// 初始化配置项
//        initState();// 初始化运行时状态
//        setConfig();// 子类实现 初始化DB参数
        initDevice();// 初始化驱动
//        initPushId();
//        initNetRequest();// 初始化网络请求
//		initPrintNetwork();//初始化网络打印
        AppConfigHelper.setConfig(AppConfigDef.sn,terminalSn());
        AppConfigHelper.setConfig(AppConfigDef.SWITCH_LANGUAGE,"en");
    }

    private void initDevice() {
        PrinterManager.getInstance().setPrinter(new MidFilterPrinterBuilder());
        if (DeviceManager.getInstance().getDeviceType() == DeviceManager.DEVICE_TYPE_N3_OR_N5) {
            deviceEngine = APIProxy.getDeviceEngine();
        }
    }

//    public void exit() {
//        Intent intent = new Intent();
//        intent.setAction(Configs.UNAUTHORIZED);
//        App.getInstance().sendBroadcast(intent);
//        MyLog.d("no System.exit");
//    }

    public DbUtils getDbController() {
        return dbController;
    }

    public boolean isWemengMerchant() {
        return Constants.merchantType_weimeng.equals(AppConfigHelper.getConfig(AppConfigDef.merchantType));
    }

    private void initDb() {
        try {
            dbController = DbUtils.create(this, "common.db");
            // 建表
            dbController.createTableIfNotExist(AppConfig.class);// 配置表
            dbController.createTableIfNotExist(AppState.class);// 状态表
            dbController.createTableIfNotExist(CashPayRepair.class);// 离线交易(现金)
            dbController.createTableIfNotExist(MerchantCardDef.class);// 会员卡定义表*
            // 这张表支付易中未使用到
            dbController.createTableIfNotExist(TicketCardDef.class);// 券定义表*
            // 这张表支付易中未使用到
            dbController.createTableIfNotExist(UserBean.class);// 券定义表*
            // 这张表支付易中未使用到
//            dbController.createTableIfNotExist(BankCardTransUploadReq.class);
        } catch (DbException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    /**
     * 初始化配置表
     */
    private void initConfig() {
        AppConfigInitUtil.init(this);
    }

    public String terminalSn(){
//        String terminalUniqNo = getImei();
        String terminalUniqNo ;//WP17391K20000082
        if (DeviceManager.getInstance().isWizarDevice() || DeviceManager.getInstance().getDeviceType() == DeviceManager.DEVICE_TYPE_SHENGTENG_M10) {
            terminalUniqNo = android.os.Build.SERIAL;//终端序列号
        } else {
            terminalUniqNo = DeviceManager.getImei(getApplicationContext());//IMEI地址
        }
        if (TextUtils.isEmpty(terminalUniqNo)) {
            terminalUniqNo = UuidUitl.getUuid();
        }
        Log.d("Application","sn:"+terminalUniqNo);
        return terminalUniqNo;
//        return "WP15461Q00001350";
    }

    //获取IMEI地址
    public String getImei() {
        String sn = "";
        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        sn = mTelephonyMgr.getDeviceId();
        return sn;
    }

}

