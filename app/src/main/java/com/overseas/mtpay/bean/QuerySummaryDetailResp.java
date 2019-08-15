package com.overseas.mtpay.bean;

import java.util.List;

public class QuerySummaryDetailResp {

    private boolean success;
    private String message;
    private int code;
    private QuerySummaryDetailRespInfo result;
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


    public QuerySummaryDetailRespInfo getResult() {
        return result;
    }

    public void setResult(QuerySummaryDetailRespInfo result) {
        this.result = result;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static class QuerySummaryDetailRespInfo {
        private int totalPage;
        private List<TransDetailResp> datas;

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public List<TransDetailResp> getDatas() {
            return datas;
        }

        public void setDatas(List<TransDetailResp> datas) {
            this.datas = datas;
        }
    }
}
