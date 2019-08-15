package com.overseas.mtpay.bean;

public class LoginTerminalResp {


    /**
     * success : true
     * message : 操作成功！
     * code : 0
     * result : {"tips_percentage":"{\"p1\":\"10\",\"p2\":\"15\",\"p3\":\"20\"}","collect_tips":"OFF","tips_custom_allow":"F","tips_terminal_allow":"T","aes_secret":"1zDVFso8fqR317C3EXV55zLXSUwEfVL7","oper_name":"Henry.gao","tips_percentage_allow":"T","merchant_name":"test","oper_id":"0005"}
     * timestamp : 1563192033623
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
         * tips_percentage : {"p1":"10","p2":"15","p3":"20"}
         * collect_tips : OFF
         * tips_custom_allow : F
         * tips_terminal_allow : T
         * aes_secret : 1zDVFso8fqR317C3EXV55zLXSUwEfVL7
         * oper_name : Henry.gao
         * tips_percentage_allow : T
         * merchant_name : test
         * oper_id : 0005
         */

        private String tips_percentage;
        private String collect_tips;
        private String tips_custom_allow;
        private String tips_terminal_allow;
        private String aes_secret;
        private String oper_name;
        private String tips_percentage_allow;
        private String merchant_name;
        private String oper_id;
        private String admin_flag;//1管理员,2 财务，3店长,4操作员

        public String getAdmin_flag() {
            return admin_flag;
        }

        public void setAdmin_flag(String admin_flag) {
            this.admin_flag = admin_flag;
        }

        public String getTips_percentage() {
            return tips_percentage;
        }

        public void setTips_percentage(String tips_percentage) {
            this.tips_percentage = tips_percentage;
        }

        public String getCollect_tips() {
            return collect_tips;
        }

        public void setCollect_tips(String collect_tips) {
            this.collect_tips = collect_tips;
        }

        public String getTips_custom_allow() {
            return tips_custom_allow;
        }

        public void setTips_custom_allow(String tips_custom_allow) {
            this.tips_custom_allow = tips_custom_allow;
        }

        public String getTips_terminal_allow() {
            return tips_terminal_allow;
        }

        public void setTips_terminal_allow(String tips_terminal_allow) {
            this.tips_terminal_allow = tips_terminal_allow;
        }

        public String getAes_secret() {
            return aes_secret;
        }

        public void setAes_secret(String aes_secret) {
            this.aes_secret = aes_secret;
        }

        public String getOper_name() {
            return oper_name;
        }

        public void setOper_name(String oper_name) {
            this.oper_name = oper_name;
        }

        public String getTips_percentage_allow() {
            return tips_percentage_allow;
        }

        public void setTips_percentage_allow(String tips_percentage_allow) {
            this.tips_percentage_allow = tips_percentage_allow;
        }

        public String getMerchant_name() {
            return merchant_name;
        }

        public void setMerchant_name(String merchant_name) {
            this.merchant_name = merchant_name;
        }

        public String getOper_id() {
            return oper_id;
        }

        public void setOper_id(String oper_id) {
            this.oper_id = oper_id;
        }
    }
}
