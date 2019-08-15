package com.overseas.mtpay.bean;

/**
 * 今日汇总接口返回
 *
 * @author wu at 2019-06-08
 */
public class TranLogVoResp {

    /**
     * success : true
     * message : 操作成功！
     * code : 0
     * result : {"datas":{"shop_id":"1001000200","net_sales_number":1,"net_sales_amount":4,"total_collected":5,"gross_sales_number":1,"gross_sales_amount":4,"refunds_number":0,"refunds_amount":0,"tips_number":1,"tips_amount":1,"alipay_sales_number":1,"alipay_sales_amount":5,"alipay_refunds_number":0,"alipay_refunds_amount":0,"alipay_netSales_number":1,"alipay_netSales_amount":4},"beginTime":"2019-07-11 00:00:00","endTime":"2019-07-11 23:59:59"}
     * timestamp : 1562912022725
     */

    private boolean success;
    private String message;
    private int code;
    private TranLogVo result;
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

    public TranLogVo getResult() {
        return result;
    }

    public void setResult(TranLogVo result) {
        this.result = result;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static class TranLogVo {
        /**
         * datas : {"shop_id":"1001000200","net_sales_number":1,"net_sales_amount":4,"total_collected":5,"gross_sales_number":1,"gross_sales_amount":4,"refunds_number":0,"refunds_amount":0,"tips_number":1,"tips_amount":1,"alipay_sales_number":1,"alipay_sales_amount":5,"alipay_refunds_number":0,"alipay_refunds_amount":0,"alipay_netSales_number":1,"alipay_netSales_amount":4}
         * beginTime : 2019-07-11 00:00:00
         * endTime : 2019-07-11 23:59:59
         */

        private DatasBean datas;
        private String beginTime;
        private String endTime;

        public DatasBean getDatas() {
            return datas;
        }

        public void setDatas(DatasBean datas) {
            this.datas = datas;
        }

        public String getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public static class DatasBean {
            /**
             * shop_id : 1001000200
             * net_sales_number : 1
             * net_sales_amount : 4
             * total_collected : 5
             * gross_sales_number : 1
             * gross_sales_amount : 4
             * refunds_number : 0
             * refunds_amount : 0
             * tips_number : 1
             * tips_amount : 1
             * alipay_sales_number : 1
             * alipay_sales_amount : 5
             * alipay_refunds_number : 0
             * alipay_refunds_amount : 0
             * alipay_netSales_number : 1
             * alipay_netSales_amount : 4
             */

            private String shop_id;
            private int net_sales_number;
            private int net_sales_amount;
            private int total_collected;
            private int gross_sales_number;
            private int gross_sales_amount;
            private int refunds_number;
            private int refunds_amount;
            private int tips_number;
            private int tips_amount;
            private int alipay_sales_number;
            private int alipay_sales_amount;
            private int alipay_refunds_number;
            private int alipay_refunds_amount;
            private int alipay_netSales_number;
            private int alipay_netSales_amount;

            public String getShop_id() {
                return shop_id;
            }

            public void setShop_id(String shop_id) {
                this.shop_id = shop_id;
            }

            public int getNet_sales_number() {
                return net_sales_number;
            }

            public void setNet_sales_number(int net_sales_number) {
                this.net_sales_number = net_sales_number;
            }

            public int getNet_sales_amount() {
                return net_sales_amount;
            }

            public void setNet_sales_amount(int net_sales_amount) {
                this.net_sales_amount = net_sales_amount;
            }

            public int getTotal_collected() {
                return total_collected;
            }

            public void setTotal_collected(int total_collected) {
                this.total_collected = total_collected;
            }

            public int getGross_sales_number() {
                return gross_sales_number;
            }

            public void setGross_sales_number(int gross_sales_number) {
                this.gross_sales_number = gross_sales_number;
            }

            public int getGross_sales_amount() {
                return gross_sales_amount;
            }

            public void setGross_sales_amount(int gross_sales_amount) {
                this.gross_sales_amount = gross_sales_amount;
            }

            public int getRefunds_number() {
                return refunds_number;
            }

            public void setRefunds_number(int refunds_number) {
                this.refunds_number = refunds_number;
            }

            public int getRefunds_amount() {
                return refunds_amount;
            }

            public void setRefunds_amount(int refunds_amount) {
                this.refunds_amount = refunds_amount;
            }

            public int getTips_number() {
                return tips_number;
            }

            public void setTips_number(int tips_number) {
                this.tips_number = tips_number;
            }

            public int getTips_amount() {
                return tips_amount;
            }

            public void setTips_amount(int tips_amount) {
                this.tips_amount = tips_amount;
            }

            public int getAlipay_sales_number() {
                return alipay_sales_number;
            }

            public void setAlipay_sales_number(int alipay_sales_number) {
                this.alipay_sales_number = alipay_sales_number;
            }

            public int getAlipay_sales_amount() {
                return alipay_sales_amount;
            }

            public void setAlipay_sales_amount(int alipay_sales_amount) {
                this.alipay_sales_amount = alipay_sales_amount;
            }

            public int getAlipay_refunds_number() {
                return alipay_refunds_number;
            }

            public void setAlipay_refunds_number(int alipay_refunds_number) {
                this.alipay_refunds_number = alipay_refunds_number;
            }

            public int getAlipay_refunds_amount() {
                return alipay_refunds_amount;
            }

            public void setAlipay_refunds_amount(int alipay_refunds_amount) {
                this.alipay_refunds_amount = alipay_refunds_amount;
            }

            public int getAlipay_netSales_number() {
                return alipay_netSales_number;
            }

            public void setAlipay_netSales_number(int alipay_netSales_number) {
                this.alipay_netSales_number = alipay_netSales_number;
            }

            public int getAlipay_netSales_amount() {
                return alipay_netSales_amount;
            }

            public void setAlipay_netSales_amount(int alipay_netSales_amount) {
                this.alipay_netSales_amount = alipay_netSales_amount;
            }
        }
    }
}