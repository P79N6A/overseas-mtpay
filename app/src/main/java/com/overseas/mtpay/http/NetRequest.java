package com.overseas.mtpay.http;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.overseas.mtpay.app.App;
import com.overseas.mtpay.bean.BaseResp;
import com.overseas.mtpay.bean.LoginInitResp;
import com.overseas.mtpay.tools.AesFun;
import com.overseas.mtpay.tools.RsaUtils;
import com.overseas.mtpay.utils.SharePreferenceUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.overseas.mtpay.http.NetConfig.HOST;

public class NetRequest implements NetCodeConstants {

    private Handler handler = new Handler(Looper.getMainLooper());

    private static final String TAG = "NetRequest";

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static NetRequest netRequest = new NetRequest();

    private OkHttpClient client;

    private SharePreferenceUtil sharePreferenceUtil;


    private NetRequest() {
        sharePreferenceUtil = new SharePreferenceUtil(App.getInstance());
//        client = new OkHttpClient();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(1, TimeUnit.MINUTES);
        builder.readTimeout(1, TimeUnit.MINUTES);
        builder.writeTimeout(1, TimeUnit.MINUTES);
        client = builder.build();
    }

    public static NetRequest getInstance() {
        return netRequest;
    }


    public void post(String url, Object req, final NetCallback callback) {
        String json = JSONObject.toJSONString(req);
        this.post(url, json, callback);
    }

    public void post(String url, String json, final NetCallback callback) {
        String shop_id = sharePreferenceUtil.getShopId();
        log("请求参数shop_id: " + shop_id);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().header("Content-Type", "application/json").header("shop_id", shop_id).url(url).post(body).build();
        Call call = client.newCall(request);
        call.enqueue(handleCallback(callback));
    }


    public void fakePost(String url, Object req, String str, final NetCallback callback) {
        url = HOST + url;
        String bodyKey = "";
        if ("0".equals(str)) {
            bodyKey = JSONObject.toJSONString(req);
        } else if ("1".equals(str)) {
            try {
                bodyKey = RsaUtils.encryptByPublicKey(JSONObject.toJSONString(req));
            } catch (Exception e) {
                e.printStackTrace();
            }
//            bodyKey =  RsaUtil.bodyToRsa(JSONObject.toJSONString(req));
        } else if ("2".equals(str)) {
            bodyKey = AesFun.bodyToAes(JSONObject.toJSONString(req));
        }
        log("请求地址: " + url);
        log("请求参数: " + JSONObject.toJSONString(req));
        log("请求参数加密: " + bodyKey);
        post(url, bodyKey, callback);
    }

    public void get(String url, final NetCallback callback) {
        log("请求地址: " + url);
        Request.Builder builder = new Request.Builder().url(url);
//        if (GlobalData.token != null) {
//            builder.addHeader("token", GlobalData.token);
//        }
        Request request = builder.build();
        Call call = client.newCall(request);
        call.enqueue(handleCallback(callback));
    }

    public void get(String url, Object req, final NetCallback callback) {
        url = HOST + url;
//        log("请求地址: " + url);
        get(url, callback);
    }


    @NonNull
    private Callback handleCallback(final NetCallback callback) {
        return new Callback() {//4.回调方法
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailed("-1", "Network failure");
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) {
                final String result;
                try {
                    result = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onFailed("-2", "data parse error");
                    return;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            log("返回参数: " + result);

                            BaseResp resp = JSONObject.parseObject(result, BaseResp.class);


                            if ("0".equals(resp.getCode())) {
                                callback.onSuccess(result);
                            } else if ("-1".equals(resp.getCode())) {
                                callback.onFailed("-1", "Server error");
                            } else if ("120".equals(resp.getCode())) {
                                callback.onFailed("120", "Alipay transaction failed");
                            } else if ("400".equals(resp.getCode())) {
                                callback.onFailed("120", "The request was refused");
                            } else if (MERCHANT_DISABLE_CODE_68.equals(resp.getCode())) {
                                callback.onFailed(MERCHANT_DISABLE_CODE_68, MERCHANT_DISABLE_MSG);
                            } else {
                                String message = TextUtils.isEmpty(resp.getMessage()) ? "Server error" : resp.getMessage();
                                callback.onFailed(resp.getCode(), message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            callback.onFailed("-2", "data parse error");
                        }
                    }
                });
            }
        };
    }

    private void log(String content) {
//        if (BuildConfig.DEBUG) {
        Log.e(TAG, content);
//        }
    }

    public interface NetCallback {

        void onSuccess(String resp);

        void onFailed(String code, String message);

    }

    public interface ProcessNetCallback extends NetCallback {

        void onProgress(String message);
    }
}
