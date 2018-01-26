package com.elextec.framework.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 唯一值工具类.
 * Created by wangtao on 2018/1/16.
 */
public class WzUniqueValUtil {
    /**
     * UUID生成方法.
     * @return UUID字符串（无"-"）
     **/
    public synchronized static String makeUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /*
     * 获得唯一时间码.
     */
    /** 种子时间. */
    private static long L = System.currentTimeMillis();

    /**
     * 唯一时间生成器.
     * @return 唯一时间字符串
     */
    public synchronized static final String makeUniqueTimes() {
        long l = System.currentTimeMillis();
        while(l == L) {
            l = System.currentTimeMillis();
        }
        L = l;
        return String.valueOf(l);
    }
}
