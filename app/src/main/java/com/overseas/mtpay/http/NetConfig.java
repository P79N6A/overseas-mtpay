package com.overseas.mtpay.http;

public class NetConfig {
//    public static final String HOST = "https://guigu.wizarpos.com/motionTradeSystem"; // 测试
    public static final String HOST = "https://demo.alipaycanada.com/motionTradeSystem"; // beta
//    public static final String HOST = "https://pos.alipaycanada.com/motionTradeSystem"; // 生产
    //    public static final String HOST = "http://api.huangweicai.com/mock/53"; // 预生产
//    public static final String HOST = "https://nqapp.prlife.com.cn"; // 生产
    public static final String POST_LOGIN_INIT_INFO = "/login/initInfo";// 终端激活初始化
    public static final String POST_LOGIN = "/login/terminalLogin";// 用户登录

    public static final String POST_QUERY_DAILY_SUMMARY = "/queryData/dailySummary";// 终端汇总查询
    public static final String POST_QUERY_DAILY_SUMMARY_DETAIL = "/queryData/dailySummaryDetail";// 交易明细查询
    public static final String POST_SYS_QUERY_TIPS = "/sys/queryTpis";// 费率查询

    public static final String POST_SYS_UPDATE_TIPS = "/sys/updateTpis";// 费率更新
    public static final String POST_ORDER_PAYMENT = "/order/payment";// 移动支付统一下单接口
    public static final String POST_ORDER_INQUIRE = "/order/inquire";// 移动支付订单查询
    public static final String POST_ORDER_REFUND = "/order/refund";// 交易退款(支持部分退款)
    public static final String POST_ORDER_CANCEL = "/order/cancel";// 取消交易接口
    public static final String POST_VERIFY_PWD = "/login/verifyPwd";// 验证密码

//    public static final String GET_ACTIVE_C004 = "/api/merchant/device/active";
//    public static final String GET_TOKEN_C042 = "/api/merchant/device/token";
//    public static final String POST_LOGIN_C005 = "/api/merchant/device/login";//登录
//    public static final String GET_CARD_INFO_C034 = "/api/card/getInfo";//会员卡号详情
//    public static final String GET_PHONE_INFO_C180 = "/api/member/getInfoByIdType";//会员卡号详情
//    public static final String POST_RECHARGE_A002 = "/api/card/rechargeUnShopCard";//去充值
//    public static final String POST_CONSUME_A001 = "/api/card/defray";//去消费
//    public static final String GET_COUPON_C075 = "/api/coupon/couponList";//得到批量券
//    public static final String POST_BATCH_COUPON_A041 = "/api/coupon/batchConsume";//电子券批量核销
//    public static final String GET_TRANSLOG_C172 = "/api/transLog/list";//门店交易记录查询
//    public static final String GET_COUPON_COUNT_C040 = "/api/coupon/couponCount2";//获取会员券数量
//    public static final String POST_BIG_RECHARGE_A049 = "/api/member/rechargeWithBig";// 大额充值
//    public static final String POST_BIG_CONSUME_A050 = "/api/member/defrayWithBig";// 大额消费
}
