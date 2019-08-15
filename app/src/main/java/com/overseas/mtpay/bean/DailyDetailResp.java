package com.overseas.mtpay.bean;

import java.io.Serializable;

public class DailyDetailResp implements Serializable {

    /**
     "trans_type": "913",
     "trans_amount": 1,
     "master_tran_log_id": "PM2019071846115596366491156485900",
     "shop_trans_id": "PM2019071846115596366491156485900",
     "operate_name": "0005",
     "refund_amount": 0,
     "trans_kind": "pay",
     "tran_time": "2019-07-18 05:33:45",
     "tip_amount": 0,
     "cny_amount": 5,
     "shop_order_no": "022019071846115596363806801937954",
     "exchange_rate": 5.28195500
     */

    private String trans_type;
    private String trans_amount;
    private String master_tran_log_id;
    private String shop_trans_id;
    private String operate_name;
    private String refund_amount;
    private String trans_kind;
    private String tran_time;
    private String tip_amount;
    private String cny_amount;
    private String shop_order_no;
    private String exchange_rate;
    private String third_trade_no;

    public String getThird_trade_no() {
        return third_trade_no;
    }

    public void setThird_trade_no(String third_trade_no) {
        this.third_trade_no = third_trade_no;
    }

    private String transName;

    public boolean isExpand = false;


    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public String getTransName() {
        return transName;
    }

    public void setTransName(String transName) {
        this.transName = transName;
    }

    public String getExchange_rate() {
        return exchange_rate;
    }

    public void setExchange_rate(String exchange_rate) {
        this.exchange_rate = exchange_rate;
    }

    public String getTrans_type() {
        return trans_type;
    }

    public void setTrans_type(String trans_type) {
        this.trans_type = trans_type;
    }


    public String getTrans_amount() {
        return trans_amount;
    }

    public void setTrans_amount(String trans_amount) {
        this.trans_amount = trans_amount;
    }

    public String getMaster_tran_log_id() {
        return master_tran_log_id;
    }

    public void setMaster_tran_log_id(String master_tran_log_id) {
        this.master_tran_log_id = master_tran_log_id;
    }


    public String getOperate_name() {
        return operate_name;
    }

    public void setOperate_name(String operate_name) {
        this.operate_name = operate_name;
    }

    public String getRefund_amount() {
        return refund_amount;
    }

    public void setRefund_amount(String refund_amount) {
        this.refund_amount = refund_amount;
    }

    public String getTrans_kind() {
        return trans_kind;
    }

    public void setTrans_kind(String trans_kind) {
        this.trans_kind = trans_kind;
    }

    public String getTran_time() {
        return tran_time;
    }

    public void setTran_time(String tran_time) {
        this.tran_time = tran_time;
    }

    public String getTip_amount() {
        return tip_amount;
    }

    public void setTip_amount(String tip_amount) {
        this.tip_amount = tip_amount;
    }

    public String getCny_amount() {
        return cny_amount;
    }

    public void setCny_amount(String cny_amount) {
        this.cny_amount = cny_amount;
    }

    public String getShop_trans_id() {
        return shop_trans_id;
    }

    public void setShop_trans_id(String shop_trans_id) {
        this.shop_trans_id = shop_trans_id;
    }

    public String getShop_order_no() {
        return shop_order_no;
    }

    public void setShop_order_no(String shop_order_no) {
        this.shop_order_no = shop_order_no;
    }
}
