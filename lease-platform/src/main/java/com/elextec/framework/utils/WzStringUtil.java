package com.elextec.framework.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * 字符串工具类.
 * Created by wangtao on 2018/1/16.
 */
public class WzStringUtil extends StringUtils {
    /*
     * 各类型与16进制字符串的相互转换
     */
    /** char数组. */
    public final static char[] digits = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
            'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
            't', 'u', 'v', 'w', 'x', 'y', 'z', '_', '-', '~', '!', '#', '$', '%', '^', '&', '*', '(', ')', '=', '+',
            '/', '{', '}', '[', ']', '|', '\\', ':', ';', '"', '\"', '<', '>', ',', '.', '?'};

    /**
     * 将普通的文本字符串转换成16进制字符串.
     * @param str 文本字符串
     * @param splitStr 分割字符串
     * @return 16进制字符串
     */
    public static String parse16(String str, String splitStr) {
        return parse16(str.getBytes(), null, splitStr, 0);
    }

    /**
     * 将byte数组转换成16进制字符串.
     * @param data byte数组
     * @return 16进制字符串
     */
    public static String parse16(byte[] data) {
        return parse16(data, null, null, -1);
    }

    /**
     * 将byte数组转换成16进制字符串.
     * @param data byte数组
     * @param prefix 前缀
     * @return 16进制字符串
     */
    public static String parse16(byte[] data, String prefix) {
        return parse16(data, prefix, null, -1);
    }

    /**
     * 将byte数组转换成16进制字符串.
     * @param data byte数组
     * @param prefix 前缀
     * @param splitStr 分隔符
     * @return 16进制字符串
     */
    public static String parse16(byte[] data, String prefix, String splitStr) {
        return parse16(data, prefix, splitStr, -1);
    }

    /**
     * 将byte数组转换成16进制字符串，如数组{0x78, 0x2A, 0x5C}将转换成字符串"78 2A 5C"。
     * @param data byte数组
     * @param prefix 前缀
     * @param splitStr 分隔符
     * @param lineNum 一行显示条数
     * @return 16进制字符串
     */
    public static String parse16(byte[] data, String prefix, String splitStr, int lineNum) {
        if (data == null || data.length == 0) {
            return null;
        }
        StringBuffer buf = new StringBuffer();
        for (int i = 0, n = data.length; i < n;) {
            byte b = data[i];
            if (splitStr != null && i > 0) {
                buf.append(splitStr);
            }
            if (prefix != null) {
                buf.append(prefix);
            }
            buf.append(digits[(b >>> 4) & 0x0f]);
            buf.append(digits[b & 0x0f]);
            i++;
            if (lineNum > 0 && i % lineNum == 0) {
                buf.append("\r\n");
            }
        }
        return buf.toString();
    }

    /**
     * 将16进制文本字符串转换成普通字符串.
     * @param hex 16进制字符串
     * @return 原字符串
     */
    public static String hex2String(String hex) {
        Pattern pattern = Pattern.compile("^0(x|X)");
        boolean find = pattern.matcher(hex).find();
        if (find) {
            hex = hex.substring(2);
        }
        hex = hex.replaceAll("\\s", "");

        char[] chs = hex.toCharArray();
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (char2byte(chs[i * 2]) << 4 | char2byte(chs[i * 2 + 1]));
        }
        return new String(bytes);
    }

    /**
     * 将long型转换成16进制表现形式.
     * @param num 数字
     * @param pix 是否带0x前缀
     * @return 16进制字符串
     */
    public static String parse16(long num, boolean pix) {
        return parse16(num, 16, pix);
    }

    /**
     * 将int型转换成16进制表现形式.
     * @param num 数字
     * @param pix 是否带0x前缀
     * @return 16进制字符串
     */
    public static String parse16(int num, boolean pix) {
        return parse16(num, 8, pix);
    }

    /**
     * 将short型转换成16进制表现形式.
     * @param num 数字
     * @param pix 是否带0x前缀
     * @return 16进制字符串
     */
    public static String parse16(short num, boolean pix) {
        return parse16(num, 4, pix);
    }

    /**
     * 将byte型转换成16进制表现形式.
     * @param num 数字
     * @param pix 是否带0x前缀
     * @return 16进制字符串
     */
    public static String parse16(byte num, boolean pix) {
        return parse16(num, 2, pix);
    }

    /**
     * 将数值转换成指定长度的16进制表现形式.
     * @param num 数字
     * @param index 开始位置
     * @param pix 是否带0x前缀
     * @return 16进制字符串
     */
    public static String parse16(long num, int index, boolean pix) {
        char[] chs;
        if (pix) {
            chs = new char[index + 2];
            chs[0] = '0';
            chs[1] = 'x';
            for (int i = index - 1; i >= 0; i--) {
                int _t = (int) (num >>> (i * 4) & 0x0f);
                chs[index + 1 - i] = digits[_t];
            }
        } else {
            chs = new char[index];
            for (int i = index - 1; i >= 0; i--) {
                int _t = (int) (num >>> (i * 4) & 0x0f);
                chs[index - 1 - i] = digits[_t];
            }
        }
        return new String(chs);
    }

    /**
     * 将16进制的char字符转换成byte.
     * @param ch 字符
     * @return byte
     */
    private static byte char2byte(char ch) {
        int result = "0123456789ABCDEF".indexOf(Character.toUpperCase(ch));
        if (result == -1) {
            throw new RuntimeException("不能将字符:  \"" + ch + "\"转换成16进制数");
        }
        return (byte) result;
    }

    /**
     * 将字符串转成Long.
     * @param str Long字符串
     */
    public static Long str2Long(String str) {
        if (isBlank(str)) {
            return null;
        }
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            return null;
        }
    }
}
