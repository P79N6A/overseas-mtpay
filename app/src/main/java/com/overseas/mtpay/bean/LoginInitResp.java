package com.overseas.mtpay.bean;

public class LoginInitResp {


    /**
     * success : true
     * message : 操作成功！
     * code : 0
     * result : {"shop_id":"1001000200","public_key":"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDVWiY6ozyWMWGc73BcSvGQHaQpXosFagIDFIhJ\nArimdIXJdsz3iuaUMgtiGCLXTjnwwq70MZtgjlRU9PelkwQd8IR6jY89HCEdsTTuDf4Fbop2gIs6\n5vHlWQa5d5b83J+shtkxo7/ldE9YsBSeizO6/XCIYK21iAHOEKGIHVd+mQIDAQAB","head_shop_id":"1001000200"}
     * timestamp : 1563015625761
     */

    private boolean success;
    private String message;
    private String code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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
         * shop_id : 1001000200
         * public_key : MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDVWiY6ozyWMWGc73BcSvGQHaQpXosFagIDFIhJ
         ArimdIXJdsz3iuaUMgtiGCLXTjnwwq70MZtgjlRU9PelkwQd8IR6jY89HCEdsTTuDf4Fbop2gIs6
         5vHlWQa5d5b83J+shtkxo7/ldE9YsBSeizO6/XCIYK21iAHOEKGIHVd+mQIDAQAB
         * head_shop_id : 1001000200
         */

        private String shop_id;
        private String public_key;
        private String head_shop_id;
        private String merchant_name;
        private String merchant_addr;
        private String merchant_tel;

        public String getShop_id() {
            return shop_id;
        }

        public void setShop_id(String shop_id) {
            this.shop_id = shop_id;
        }

        public String getPublic_key() {
            return public_key;
        }

        public void setPublic_key(String public_key) {
            this.public_key = public_key;
        }

        public String getHead_shop_id() {
            return head_shop_id;
        }

        public void setHead_shop_id(String head_shop_id) {
            this.head_shop_id = head_shop_id;
        }

        public String getMerchant_name() {
            return merchant_name;
        }

        public void setMerchant_name(String merchant_name) {
            this.merchant_name = merchant_name;
        }

        public String getMerchant_addr() {
            return merchant_addr;
        }

        public void setMerchant_addr(String merchant_addr) {
            this.merchant_addr = merchant_addr;
        }

        public String getMerchant_tel() {
            return merchant_tel;
        }

        public void setMerchant_tel(String merchant_tel) {
            this.merchant_tel = merchant_tel;
        }
    }
}
