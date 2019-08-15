package com.overseas.mtpay.print;


import com.overseas.mtpay.db.AppConfigDef;
import com.overseas.mtpay.db.AppConfigHelper;

/**
 * 打印实现<br>
 * 移除商户号
 *
 * @author wu
 */
public class MidFilterPrinterBuilder extends DefaultPrinterImpl {

    @Override
    public void print(String str) {
        str = str.replaceAll("P" + AppConfigHelper.getConfig(AppConfigDef.mid),
                "P");// 移除商户号
        str = str.replaceAll("T" + AppConfigHelper.getConfig(AppConfigDef.mid), "T");//券号中去掉mid@hong 20151221
        super.print(str);
    }
}
