package com.overseas.mtpay.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.overseas.mtpay.R;
import com.overseas.mtpay.ui.LoginMerchantRebuildActivity;

/**
 * Created by jack on 2019/8/7.
 */

public class MsgTipsDialog extends Dialog implements View.OnClickListener {
    private TextView tvContents;

    private RoundTextView sureBtn;
    private SureOnclick sureOnclick;

    /**
     * 弹出dialog，跳转到log界面
     *
     * @param context
     * @param content
     */
    public static void toLoginActivity(final Context context, String content) {
        MsgTipsDialog msgTipsDialog = new MsgTipsDialog(context);
        msgTipsDialog.show();
        if (!TextUtils.isEmpty(content)) {
            msgTipsDialog.setContent(content);
        }
        msgTipsDialog.setSureOnclick(new SureOnclick() {
            @Override
            public void onSureClick() {
                //跳转到登陆界面
                LoginMerchantRebuildActivity.reloadLoginActivity(context);
            }
        });
    }


    private MsgTipsDialog(@NonNull Context context) {
        super(context, R.style.WarnDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_close_auth);
        tvContents = this.findViewById(R.id.tvContents);
        sureBtn = this.findViewById(R.id.okBtn);
        sureBtn.setOnClickListener(this);

    }

    /**
     * 显示按钮
     *
     * @param okVisible
     */
    public void setBtnVisible(int okVisible) {
        sureBtn.setVisibility(okVisible);
    }

    public void setContent(String text) {
        tvContents.setText(text);
    }

    public void setBtnText(int textOk) {
        sureBtn.setText(textOk);

    }


    public void setSureOnclick(SureOnclick sureOnclick) {
        this.sureOnclick = sureOnclick;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.okBtn:
                if (sureOnclick != null) {
                    sureOnclick.onSureClick();
                }
                this.dismiss();
                break;
            default:
                break;
        }
    }

    interface SureOnclick {
        void onSureClick();
    }


}
