package com.overseas.mtpay.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 本地配置工具
 *
 * @author harlen
 */
public class SharePreferenceUtil {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    public String fileName = "handsale";

    public SharePreferenceUtil(Context context) {
        sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public static final String setting_pic_url = "pic_url";
    public static final String setting_service_host = "service_host";
    public static final String setting_service_port = "service_port";
    public static final String setting_product_down = "product_down";

    public static final String setting_department_code = "departmentCode";
    public static final String setting_machine_department_code = "machineDepartmentCode";
    public static final String setting_payment_platform = "paymentPlatform";
    public static final String setting_safe_password = "safePassword";

    public final static String CODE = "CODE";
    //第一次登录  0未配置基础信息  1已配置
    public final static String IS_FIRST_LOGIN = "IS_FIRST_LOGIN";
    //token
    public final static String BASE_HOST = "BASE_HOST";

    public final static String BASE_SHOP_ID = "BASE_SHOP_ID";
    public final static String BASE_HEAD_SHOP_ID = "BASE_HEAD_SHOP_ID";

    public final static String BASE_OPER_ID = "BASE_OPER_ID";
    public final static String BASE_OPER_NAME = "BASE_OPER_NAME";
    public static final String AES_KEY = "AES_KEY";
    public static final String RSA_KEY = "RSA_KEY";

    public static final String MERCHANT_NAME = "MERCHANT_NAME";
    public static final String MERCHANT_ADDR = "MERCHANT_ADDR";
    public static final String MERCHANT_TEL = "MERCHANT_TEL";

    /**
     * 末经授权标识符
     */
    public final static String UNAUTHORIZED = "UNAUTHORIZED";

    public String getIsFirstLogin() {
        return sp.getString(IS_FIRST_LOGIN, "");
    }

    public void setIsFirstLogin(String firstLogin) {
        editor.putString(IS_FIRST_LOGIN, firstLogin);
        editor.commit();
    }

    public String getHost() {
        return sp.getString(BASE_HOST, "");
    }

    public void setHost(String host) {
        editor.putString(BASE_HOST, host);
        editor.commit();
    }

    public String getShopId() {
        return sp.getString(BASE_SHOP_ID, "");
    }

    public void setShopId(String shopNo) {
        editor.putString(BASE_SHOP_ID, shopNo);
        editor.commit();
    }

    public String getHeadShopId() {
        return sp.getString(BASE_HEAD_SHOP_ID, "");
    }

    public void setHeadShopId(String headShopId) {
        editor.putString(BASE_HEAD_SHOP_ID, headShopId);
        editor.commit();
    }

    public String getOperId() {
        return sp.getString(BASE_OPER_ID, "");
    }

    public void setOperId(String operId) {
        editor.putString(BASE_OPER_ID, operId);
        editor.commit();
    }

    public String getOperName() {
        return sp.getString(BASE_OPER_NAME, "");
    }

    public void setOpeName(String operName) {
        editor.putString(BASE_OPER_NAME, operName);
        editor.commit();
    }

    public String getRSA() {
        return sp.getString(RSA_KEY, "");
    }

    public void setRSA(String rsa) {
        editor.putString(RSA_KEY, rsa);
        editor.commit();
    }

    public String getMerchantName() {
        return sp.getString(MERCHANT_NAME, "");
    }

    public void setMerchantName(String rsa) {
        editor.putString(MERCHANT_NAME, rsa);
        editor.commit();
    }

    public String getMerchantAddr() {
        return sp.getString(MERCHANT_ADDR, "");
    }

    public void setMerchantAddr(String rsa) {
        editor.putString(MERCHANT_ADDR, rsa);
        editor.commit();
    }


    public String getMerchantTel() {
        return sp.getString(MERCHANT_TEL, "");
    }

    public void setMerchantTel(String rsa) {
        editor.putString(MERCHANT_TEL, rsa);
        editor.commit();
    }


}
