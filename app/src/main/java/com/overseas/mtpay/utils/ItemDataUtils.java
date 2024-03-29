package com.overseas.mtpay.utils;

import android.content.Context;


import com.overseas.mtpay.R;
import com.overseas.mtpay.app.App;
import com.overseas.mtpay.bean.entity.ArrayItem;
import com.overseas.mtpay.bean.entity.ItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by blue_sky on 2016/3/22.
 */
public class ItemDataUtils {
    public static List<ItemBean> getItemBeans(Context mContext){
        List<ItemBean> itemBeans=new ArrayList<>();
//        itemBeans.add(new ItemBean(R.drawable.ic_write_off,"卡券核销",false,0));
        itemBeans.add(new ItemBean(R.drawable.ic_record,mContext.getResources().getString(R.string.trans_detail),false,1));
//        itemBeans.add(new ItemBean(R.drawable.ic_check_ticket,"卡券核销记录",false,2));
        itemBeans.add(new ItemBean(R.drawable.ic_history,mContext.getResources().getString(R.string.daily_summary),false,3));
        itemBeans.add(new ItemBean(R.drawable.nav_icon_setting,mContext.getResources().getString(R.string.setting),false,4));
//        itemBeans.add(new ItemBean(R.drawable.ic_xiaopiao,mContext.getResources().getString(R.string.tips_setting),false,5));
//        itemBeans.add(new ItemBean(R.drawable.nav_icon_safesetting,"CHANGE SECURITY CODE",false,8));
        itemBeans.add(new ItemBean(R.drawable.ic_logout,mContext.getResources().getString(R.string.exit),false,6));
//        itemBeans.add(new ItemBean(R.drawable.ic_logout,"移动支付",false,7));
        return  itemBeans;
    }

    public static List<ArrayItem> getTimeRanges(){
        List<ArrayItem> timeRanges=new ArrayList<>();
        timeRanges.add(new ArrayItem(0, App.getInstance().getResources().getString(R.string.today)));
        timeRanges.add(new ArrayItem(1,App.getInstance().getResources().getString(R.string.yestoday)));
        timeRanges.add(new ArrayItem(2,App.getInstance().getResources().getString(R.string.this_week)));
        timeRanges.add(new ArrayItem(3,App.getInstance().getResources().getString(R.string.before_week)));
        timeRanges.add(new ArrayItem(4,App.getInstance().getResources().getString(R.string.this_month)));
        timeRanges.add(new ArrayItem(5,App.getInstance().getResources().getString(R.string.before_month)));
        timeRanges.add(new ArrayItem(6,App.getInstance().getResources().getString(R.string.custom)));
        return  timeRanges;
    }

    public static List<ArrayItem> getTranTypes(){
        List<ArrayItem> tranTypes=new ArrayList<>();
//        tranTypes.add(new ArrayItem("1", "充值"));

//        tranTypes.add(new ArrayItem(3,App.getInstance().getResources().getString(R.string.sale)));
//        tranTypes.add(new ArrayItem(2, App.getInstance().getResources().getString(R.string.refund)));
        tranTypes.add(new ArrayItem("pay",App.getInstance().getResources().getString(R.string.sale)));
        tranTypes.add(new ArrayItem("refund", App.getInstance().getResources().getString(R.string.refund)));
        tranTypes.add(new ArrayItem("", App.getInstance().getResources().getString(R.string.All)));
        return  tranTypes;
    }

    public static List<ArrayItem> getPrintNums(){
        List<ArrayItem> printNums=new ArrayList<>();
        printNums.add(new ArrayItem(1, "1"));
        printNums.add(new ArrayItem(2, "2"));
        printNums.add(new ArrayItem(3, "3"));
        return  printNums;
    }
}
