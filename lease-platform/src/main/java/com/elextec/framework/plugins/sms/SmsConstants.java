package com.elextec.framework.plugins.sms;

/**
 * 短信发送返回结果定义.
 * Created by wangtao on 2018/1/24.
 */
public class SmsConstants {
    /*
     * 发送短消息方法sendMsg()，返回结果.
     */
    public static final String CODE_SEND_MSG_0 = "0";
    public static final String CODE_SEND_MSG_100 = "100";
    public static final String CODE_SEND_MSG_101 = "101";
    public static final String CODE_SEND_MSG_102 = "102";
    public static final String CODE_SEND_MSG_103 = "103";
    public static final String CODE_SEND_MSG_105 = "105";
    public static final String CODE_SEND_MSG_106 = "106";
    public static final String CODE_SEND_MSG_108 = "108";
    public static final String CODE_SEND_MSG_109 = "109";
    public static final String CODE_SEND_MSG_110 = "110";
    public static final String CODE_SEND_MSG_114 = "114";
    public static final String CODE_SEND_MSG_115 = "115";
    public static final String CODE_SEND_MSG_116 = "116";
    public static final String CODE_SEND_MSG_117 = "117";

    /**
     * 根据Status获得消息内容.
     * @param code 消息Status
     * @return 消息内容
     */
    public static String getSendMsgMessage(String code) {
        switch (code) {
            case "0" : return "提交成功";
            case "100" : return "发送失败";
            case "101" : return "用户账号不存在或密码错误";
            case "102" : return "账号已禁用";
            case "103" : return "参数不正确";
            case "105" : return "短信内容超过300字或为空、或内容编码格式不正确";
            case "106" : return "手机号码超过100个或有错误号码";
            case "108" : return "余额不足";
            case "109" : return "ip错误";
            case "110" : return "短信内容存在系统保留关键词，可以登录客户端，查找具体的敏感词。";
            case "114" : return "模板短信序号不存在";
            case "115" : return "短信签名标签序号不存在";
            case "116" : return "认证码不正确";
            case "117" : return "未开通此接入方式";
            default: return "未知错误";
        }
    }
}
