package com.elextec.framework.filter;

import jodd.io.StreamUtil;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.net.URLDecoder;

/**
 * 自定义RequestWrapper.
 * 主要用于Filter读取参数后导致Controller无法再次接收参数的问题
 * Created by wangtao on 2018/3/6.
 */
public class WzHttpServletRequestWrapper extends HttpServletRequestWrapper {
    /** 用于保存Body参数. */
    private final byte[] reqBody;

    /**
     * 构造方法.
     * @param request HttpServletRequest
     */
    public WzHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        reqBody = StreamUtil.readBytes(request.getInputStream());
    }

    public String getBodyStr() {
        String s = null;
        try {
            s = new String(reqBody, "UTF-8");
            return URLDecoder.decode(s, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            return "读取参数异常";
        }
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(reqBody);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }
}
