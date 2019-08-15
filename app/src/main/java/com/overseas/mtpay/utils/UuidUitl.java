package com.overseas.mtpay.utils;

import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.overseas.mtpay.app.App;
import com.overseas.mtpay.db.AppConfigDef;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * @Author: yaosong
 * @date 2016-2-17 下午2:19:09
 * @Description:推送id使用的UUID相关方法 
 */
public class UuidUitl {
	static String fileName = "";
	static public void createUuid() {
		final TelephonyManager tm = (TelephonyManager) App.getInstance()
				.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, tmPhone, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(App
				.getInstance().getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();
		Log.i("UUID","CREATED:"+uniqueId);
		saveUuidInSdcard(uniqueId);
	}

	static private boolean saveUuidInSdcard(String uuid) {
		try {
			FileUtil.write2File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/" + AppConfigDef.appPushId,uuid.getBytes("UTF-8"));
			Log.i("UUID","Saved in sdCard");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return false;
	}

	static public String getUuid() {
		File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + AppConfigDef.appPushId);
		if (f.exists()) {
			String res = "";
			try {
				FileInputStream fin = new FileInputStream(f);
				int length = fin.available();
				byte[] buffer = new byte[length];
				fin.read(buffer);
				res = new String(buffer, "UTF-8");
				Log.i("UUID","get uuid:" + res);
				fin.close();
				return res;
			}
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}
}
