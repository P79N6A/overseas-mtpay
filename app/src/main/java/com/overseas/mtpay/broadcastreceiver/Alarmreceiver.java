package com.overseas.mtpay.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.log.Logger;
import com.overseas.mtpay.R;
import com.overseas.mtpay.app.App;
import com.overseas.mtpay.bean.SysTipsResp;
import com.overseas.mtpay.bean.TranLogVoResp;
import com.overseas.mtpay.db.AppConfigDef;
import com.overseas.mtpay.db.AppConfigHelper;
import com.overseas.mtpay.db.AppStateDef;
import com.overseas.mtpay.db.AppStateManager;
import com.overseas.mtpay.db.Constants;
import com.overseas.mtpay.http.NetConfig;
import com.overseas.mtpay.http.NetRequest;
import com.overseas.mtpay.utils.DateUtil;
import com.overseas.mtpay.utils.SharePreferenceUtil;
import com.overseas.mtpay.utils.Tools;

import java.util.Calendar;

/**
 * Created by blue_sky on 2016/11/8.
 */

public class Alarmreceiver extends BroadcastReceiver {
    public static final String ACTION_GET_EXCHANGE = "action_get_exchange";
    private SharePreferenceUtil spUtils;

    @Override
    public void onReceive(final Context context, Intent intent) {
        spUtils = new SharePreferenceUtil(App.getInstance());
        if (intent.getAction().equals(Constants.UPDATE)) {
//            Toast.makeText(context, "update", Toast.LENGTH_LONG).show();
            Calendar cal = Calendar.getInstance();// 当前日期
            int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
            int minute = cal.get(Calendar.MINUTE);// 获取分钟
            int minuteOfDay = hour * 60 + minute;// 从0:00分开是到目前为止的分钟数
            final int time = 10 * 60;// 结束时间 10:00的分钟数
            if ((minuteOfDay > time && Constants.TRUE.equals(AppConfigHelper.getConfig(AppConfigDef.isLogin))) ||
                    (TextUtils.isEmpty(AppConfigHelper.getConfig(AppConfigDef.exchangeRate)) &&
                            Constants.TRUE.equals(AppConfigHelper.getConfig(AppConfigDef.isLogin)))) {
                JSONObject json = new JSONObject();
                JSONObject sysParam = new JSONObject();
                sysParam.put("head_shop_id", spUtils.getHeadShopId());
                sysParam.put("shop_id", spUtils.getShopId());
                sysParam.put("sn", App.getInstance().terminalSn());
                sysParam.put("language",  AppConfigHelper.getConfig(AppConfigDef.SWITCH_LANGUAGE));
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
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
                            }
                        }
                    }

                    @Override
                    public void onFailed(String code, String message) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        Logger.d("Login onFailed: " + code + "--------" + message);
                    }
                });
            }
        }
    }
}
