package com.elextec.framework.common.constants;

/**
 * 处理结果信息.
 * Created by wangtao on 2018/1/17.
 */
public enum RunningResult {
    SUCCESS("200", "成功"),
    BAD_REQUEST("400", "请求失败"),
    UNAUTHORIZED("401", "非法请求"),
    NAME_OR_PASSWORD_WRONG("40101", "用户名或密码错"),
    NO_USER("40102", "未查询到用户"),
    NO_PARAM("40103", "请求参数为空"),
    PARAM_ANALYZE_ERROR("40104", "请求参数解析失败"),
    PARAM_VERIFY_ERROR("40105", "请求参数验证失败"),
    AUTH_OVER_TIME("40106", "认证已超时"),
    NO_PERMISSION("40107", "您无使用权限"),
    MULTIPLE_RECORD("40108", "记录已存在"),
    NO_ROLE("40109", "未查询到角色"),
    NO_RESOURCE("40110", "未查询到资源"),
    NO_DIRECTORY("40111", "资源目录不存在"),
    NO_FUNCTION_PERMISSION("40112", "您无权执行该操作"),
    HAVE_BIND("40113", "绑定未解除"),
    FORBIDDEN("403", "禁止访问"),
    NOT_FOUND("404", "服务器无法找到被请求的页面或资源"),
    SERVER_ERROR("500", "服务器错误"),
    DB_ERROR("50001", "数据库处理错误"),
    SMS_SEND_FAIL("50002", "短信发送失败"),
    IO_FAIL("50003", "读写文件是发生错误");

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
