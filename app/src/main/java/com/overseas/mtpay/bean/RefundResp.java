package com.overseas.mtpay.bean;

public class RefundResp {

    /**
     * success : true
     * message : 操作成功！
     * code : 0
     * result : {"third_trade_no":"2019071622001428841051771324","refund_shop_tran_id":"PM2019071668400591196095774735906","tran_time":"2019-07-16 07:36:19","exchange_rate":5.285,"tran_amount":-40,"tip_amount":0,"cny_amount":-211,"trans_currency":"CAD","third_ext_id":"2088702652128841","third_ext_name":"138120***56179"}
     * timestamp : 1563276970939
     */

    private boolean success;
    private String message;
    private int code;
    private ResultBean result;
    private long timestamp;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static class ResultBean {
        /**
         * third_trade_no : 2019071622001428841051771324
         * refund_shop_tran_id : PM2019071668400591196095774735906
         * tran_time : 2019-07-16 07:36:19
         * exchange_rate : 5.285
         * tran_amount : -40
         * tip_amount : 0
         * cny_amount : -211
         * trans_currency : CAD
         * third_ext_id : 2088702652128841
         * third_ext_name : 138120***56179
         */

        private String third_trade_no;
        private String refund_shop_tran_id;
        private String tran_time;
        private String exchange_rate;
        private String tran_amount;
        private int tip_amount;
        private String cny_amount;
        private String trans_currency;
        private String third_ext_id;
        private String third_ext_name;

        public String getThird_trade_no() {
            return third_trade_no;
        }

        public void setThird_trade_no(String third_trade_no) {
            this.third_trade_no = third_trade_no;
        }

        public String getRefund_shop_tran_id() {
            return refund_shop_tran_id;
        }

        public void setRefund_shop_tran_id(String refund_shop_tran_id) {
            this.refund_shop_tran_id = refund_shop_tran_id;
        }

        public String getTran_time() {
            return tran_time;
        }

        public void setTran_time(String tran_time) {
            this.tran_time = tran_time;
        }

        public String getExchange_rate() {
            return exchange_rate;
        }

        public void setExchange_rate(String exchange_rate) {
            this.exchange_rate = exchange_rate;
        }

        public String getTran_amount() {
            return tran_amount;
        }

        public void setTran_amount(String tran_amount) {
            this.tran_amount = tran_amount;
        }

        public int getTip_amount() {
            return tip_amount;
        }

        public void setTip_amount(int tip_amount) {
            this.tip_amount = tip_amount;
        }

        public String getCny_amount() {
            return cny_amount;
        }

        public void setCny_amount(String cny_amount) {
            this.cny_amount = cny_amount;
        }

        public String getTrans_currency() {
            return trans_currency;
        }

        public void setTrans_currency(String trans_currency) {
            this.trans_currency = trans_currency;
        }

        public String getThird_ext_id() {
            return third_ext_id;
        }

        public void setThird_ext_id(String third_ext_id) {
            this.third_ext_id = third_ext_id;
        }

        public String getThird_ext_name() {
            return third_ext_name;
        }

        public void setThird_ext_name(String third_ext_name) {
            this.third_ext_name = third_ext_name;
        }
    }
}
