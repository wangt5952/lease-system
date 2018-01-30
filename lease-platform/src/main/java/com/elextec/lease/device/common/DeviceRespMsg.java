package com.elextec.lease.device.common;

/**
 * 设备相关处理结果信息.
 * Created by wangtao on 2018/1/17.
 */
public enum DeviceRespMsg {
    SUCCESS("0", "ok"),
    NONE_ID_AND_TYPE("1", "参数deviceid和devicetype不能为空"),
    INVALID_DEVICE("2", "无效的设备类别"),
    NO_DEVICE("3", "无此设备"),
    NO_PARAM("40103", "请求参数为空"),
    PARAM_ANALYZE_ERROR("40104", "请求参数解析失败"),
    PARAM_VERIFY_ERROR("40105", "请求参数验证失败"),
    AUTH_OVER_TIME("40106", "认证已超时"),
    NO_PERMISSION("40107", "您无权使用该系统"),
    MULTIPLE_RECORD("40108", "记录已存在"),
    FORBIDDEN("403", "禁止访问"),
    SERVER_ERROR("500", "服务器错误"),
    DB_ERROR("50001", "数据库处理错误"),
    SMS_SEND_FAIL("50002", "短信发送失败");

    /** Code. */
    private final String code;
    /** Value. */
    private final String info;

    /**
     * 构造方法.
     * @param code Code
     * @param info Value
     */
    private DeviceRespMsg(String code, String info) {
        this.code = code;
        this.info = info;
    }

    /*
     * Getter 及 Setter方法.
     */
    public String code() {
        return code;
    }
    public String getInfo() {
        return info;
    }
}
