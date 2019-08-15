package com.overseas.mtpay.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.overseas.mtpay.R;
import com.overseas.mtpay.db.AppConfigDef;
import com.overseas.mtpay.db.AppConfigHelper;
import com.overseas.mtpay.db.AppStateManager;
import com.overseas.mtpay.db.Constants;
import com.overseas.mtpay.ui.base.BaseViewActivity;
import com.overseas.mtpay.utils.LanguageUtils;
import com.overseas.mtpay.utils.LogEx;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

/**
 * 入口activity 闪屏
 *
 * @author wu
 */
public class StartupActivity extends BaseViewActivity {

    private static final int REQUEST_CODE_SETTING = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        doRequestPermissions();
        LanguageUtils.init(this);
        super.onCreate(savedInstanceState);
        LogEx.d("START APP");
        setContentView(R.layout.layout_startup);
//        DeviceManager.getInstance().getDeviceType();
//        findViewById(R.id.btnTestModel).setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
////                startActivity(new Intent(StartupActivity.this, TestStartMenuActivity.class));
////                StartupActivity.this.finish();
//            }
//        });
//        presenter = new StartupPresenter(this);
//        progresser.showProgress();
        go();
    }

    private void go() {
        if (Constants.TRUE.equals(AppConfigHelper.getConfig(AppConfigDef.isLogin))) {
//			startActivity(new Intent(this, MainFragmentActivity2.class));
            startActivity(new Intent(this, NewMainActivity.class));
        } else {
            startActivity(new Intent(this, LoginMerchantRebuildActivity.class));
        }
        this.finish();
        // startActivity(new Intent(this, TestBaiduMicroPayActivity.class));
    }

    private void doRequestPermissions() {
        // 先判断是否有权限。
        if (AndPermission.hasPermission(this, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WAKE_LOCK)) {
            // 有权限，直接do anything.
        } else {
            // 申请权限。
            AndPermission.with(this)
                    .requestCode(100)
                    .permission(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WAKE_LOCK)
                    .send();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 只需要调用这一句，其它的交给AndPermission吧，最后一个参数是PermissionListener。
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            if (requestCode == 100) {
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。
            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(StartupActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
//                AndPermission.defaultSettingDialog(LoginActivity.this, REQUEST_CODE_SETTING).show();
                // 第二种：用自定义的提示语。
                AndPermission.defaultSettingDialog(StartupActivity.this, REQUEST_CODE_SETTING)
                        .setTitle("权限申请失败")
                        .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
                        .setPositiveButton("好，去设置")
                        .show();
            }
        }
    };

}





