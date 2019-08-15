package com.overseas.mtpay.bean;

import java.io.Serializable;
import java.util.List;

public class TransDetailResp implements Serializable {
    private String amount_sum;
    private String trans_date;
    private List<DailyDetailResp> tranLogDailyDetail;

    public String getAmount_sum() {
        return amount_sum;
    }

    public void setAmount_sum(String amount_sum) {
        this.amount_sum = amount_sum;
    }

    public String getTrans_date() {
        return trans_date;
    }

    public void setTrans_date(String trans_date) {
        this.trans_date = trans_date;
    }

    public List<DailyDetailResp> getTranLogDailyDetail() {
        return tranLogDailyDetail;
    }

    public void setTranLogDailyDetail(List<DailyDetailResp> tranLogDailyDetail) {
        this.tranLogDailyDetail = tranLogDailyDetail;
    }
}
