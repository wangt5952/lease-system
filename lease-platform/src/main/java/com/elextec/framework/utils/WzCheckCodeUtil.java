package com.elextec.framework.utils;

import java.util.Random;
import java.util.UUID;

/**
 * 随机码工具类.
 * Created by wangtao on 2018/1/16.
 */
public class WzCheckCodeUtil {
    /** 随机码种子. */
    private static final String[] SEED = {
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
        "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
        "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    /**
     * 随机验证码生成器.
     * @param len 随机码长度
     * @return 随机码字符串
     **/
    public synchronized static String makeCheckCode(int len) {
        Random rd = new Random();
        StringBuffer sb = new StringBuffer("");
        for (int i = 0;i < len; i++) {
            sb.append(SEED[rd.nextInt(62)]);
        }
        return sb.toString();
    }

}
