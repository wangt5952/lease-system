package com.elextec.framework.common.constants;

/**
 * 处理结果信息.
 * Created by wangtao on 2018/1/17.
 */
public enum RunningResult {
    SUCCESS("200", "成功"),
    BAD_REQUEST("400", "请求失败"),
    UNAUTHORIZED("401", "用户名或密码错"),
    FORBIDDEN("403", "禁止访问"),
    SERVER_ERROR("500", "服务器错误");

    /** Code. */
    private final String code;
    /** Value. */
    private final String info;

    /**
     * 构造方法.
     * @param code Code
     * @param info Value
     */
    private RunningResult(String code, String info) {
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
