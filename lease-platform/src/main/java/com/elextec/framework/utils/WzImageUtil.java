package com.elextec.framework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;

/**
 * 图片上传工具类.
 * Created by wangtao on 2018/1/16.
 */
public class WzImageUtil {

    /** 日志对象 */
    private static final Logger logger = LoggerFactory.getLogger(WzImageUtil.class);

     /*
     * 图片上传地址
     */
    public static String UPLODE_PATH = "";

    /**
     * 上传图片固定转换格式
     * */
    public static String IMAGE_UPDATE_TYPE = ".jpg";


    /**
     * 将Base64位字符串转换为图片并保存到指到文件夹
     * */
    public static String base64StringToImage(String base64String,String imageType) {
        try
        {
            //Base64解码
            byte[] b = WzEncryptUtil.base64ToByteArr(base64String);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {
                    //调整异常数据
                    b[i]+=256;
                }
            }
            String imgFileName = WzImageUtil.UPLODE_PATH + "\\" + imageType + WzUniqueValUtil.makeUUID() + WzImageUtil.IMAGE_UPDATE_TYPE;
            OutputStream out = new FileOutputStream(imgFileName);
            out.write(b);
            out.flush();
            out.close();
            return imgFileName;
        }
        catch (Exception e)
        {
            if (logger.isErrorEnabled()) {
                logger.error(imageType + "图片保存失败.", e);
            }
            return "";
        }
    }

}
