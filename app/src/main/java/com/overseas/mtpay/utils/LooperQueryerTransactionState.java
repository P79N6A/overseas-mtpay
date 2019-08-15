package com.overseas.mtpay.utils;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.log.Logger;
import com.overseas.mtpay.app.App;
import com.overseas.mtpay.bean.OrderPayResp;
import com.overseas.mtpay.db.AppConfigDef;
import com.overseas.mtpay.db.AppConfigHelper;
import com.overseas.mtpay.http.NetConfig;
import com.overseas.mtpay.http.NetRequest;


/**
 * 轮询查询订单状态接口 Created by wu on 2015/6/26 0026.
 */
public class LooperQueryerTransactionState extends LooperTask {

    private final SharePreferenceUtil spUtils;
    private String orderNo; // 流水号

    // 订单状态(1未支付2已支付3撤销4交易关闭)

    public LooperQueryerTransactionState(String orderNo) {
        this.orderNo = orderNo;
        spUtils = new SharePreferenceUtil(App.getInstance());
    }

    @Override
    protected void doTask() {
        if (TextUtils.isEmpty(orderNo)) {
            return;
        }

        JSONObject json = new JSONObject();
        json.put("terminal_trans_id", orderNo);
        JSONObject sysParam = new JSONObject();
        sysParam.put("head_shop_id", spUtils.getHeadShopId());
        sysParam.put("shop_id", spUtils.getShopId());
        sysParam.put("oper_id", spUtils.getOperId());
        sysParam.put("oper_name", spUtils.getOperName());
        sysParam.put("sn", App.getInstance().terminalSn());
        sysParam.put("language",  AppConfigHelper.getConfig(AppConfigDef.SWITCH_LANGUAGE));
        json.put("sysParam", sysParam);
//        String data = json.toJSONString();
        NetRequest.getInstance().fakePost(NetConfig.POST_ORDER_INQUIRE, json, "2", new NetRequest.NetCallback() {
            @Override
            public void onSuccess(String resp) {
                OrderPayResp resp1 = JSONObject.parseObject(resp, OrderPayResp.class);
//                int code = resp1.getCode();
//                String state = resp1.getResult().getState();
//                Log.d("===onSuccess==", "onSuccess: " + code + "  " + state);
//                if(code == 0 && "2".equals(state)){
//                    //支付成功 跳转到支付成功界面 并打印
//                    finish(resp);
//                }else if(code == 0 && "1".equals(state)){
//                    //继续轮询
//                    goon();
//                    waitForConfirm();
//                }else{
//                    toTip(resp1.getMessage());
//                }
                if ("2".equals( resp1.getResult().getState())) {
                    //支付成功 跳转到支付成功界面 并打印
                    finish(resp);
                }else if("1".equals(resp1.getResult().getState())){
                    // 继续轮询
                    goon();
                    waitForConfirm();
                }else {
                    toTip(resp1.getMessage());
                }
            }

            @Override
            public void onFailed(String code, String message) {
                if("-1".equals(code)){
                    goon();
                }else{
                    toTip(message);
                }
                Logger.d("Login onFailed: " + code + "--------" + message);
            }
        });
    }
}
