package com.overseas.mtpay.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.overseas.mtpay.db.Constants;

public class GetSnHelper {
	public GetSnHelper(Context context) {
	}
	public static String getMacAndSn (Context context) {
		String sn = "";
		try{
			if (Constants.WIZARPOS_FLAG) {
				sn = android.os.Build.SERIAL;
			} else if (Constants.DZXPAD_FLAG){
				WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = wifi.getConnectionInfo();
				sn = info.getMacAddress().replace(":", "");
			}
		}catch(Exception ex){}
		return sn;
	}
}
