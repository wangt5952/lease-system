package com.elextec.framework.plugins.image;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
import com.elextec.framework.utils.WzEncryptUtil;
import com.elextec.framework.utils.WzFileUtil;
import com.elextec.framework.utils.WzUniqueValUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * 图形验证码工具类.
 * Created by wangtao on 2018/1/16.
 * @deprecated
 */
@Component
public class WzImageClient {
    /** 日志. */
    private static final Logger logger = LoggerFactory.getLogger(WzImageClient.class);

    @Value("localsetting.upload-captcha-root")
    private String uploadCaptchaRoot;

    @Value("localsetting.download-captcha-prefix")
    private String downloadCaptchaPrefix;

    @Value("localsetting.upload-user-icon-root")
    private String uploadUserIconRoot;

    @Value("localsetting.download-user-icon-prefix")
    private String downloadUserIconPrefix;

    @Value("localsetting.upload-user-realname-root")
    private String uploadUserRealnameRoot;

    @Value("localsetting.download-user-realname-prefix")
    private String downloadUserRealnamePrefix;

    /** 默认宽度. */
    private static final int DEF_WIDTH_PX = 125;
    /** 默认高度. */
    private static final int DEF_HEIGHT_PX = 40;

    /**
     * 生成图形验证码并保存并返回访问地址.
     * @param captchaCode 图形验证码
     * @param widthPx 图形宽度
     * @param heightPx 图形高度
     * @return 图形访问地址
     */
    public String madeAndGetCapthaUrl(String captchaCode, int widthPx, int heightPx) {
        int usedWidthPx = DEF_WIDTH_PX;
        int usedHeightPx = DEF_HEIGHT_PX;
        Random localRandom = new Random();
        if (DEF_WIDTH_PX < widthPx) {
            usedWidthPx = widthPx;
        }
        if (DEF_HEIGHT_PX < heightPx) {
            usedHeightPx = heightPx;
        }
        // 创建图片
        BufferedImage bi = new BufferedImage(usedWidthPx, usedWidthPx, BufferedImage.TYPE_INT_RGB);
        // 获得图片
        Graphics localGraphics = bi.getGraphics();
        // 设置背景色
        localGraphics.setColor(getRandColor(200, 250));
        localGraphics.fillRect(0, 0, usedWidthPx, usedHeightPx);
        // 设置边框
        localGraphics.setColor(Color.BLUE);
        localGraphics.drawRect(1, 1, usedWidthPx - 2, usedHeightPx - 2);
        // 绘制随机线条，为图像添加噪音
        Random random = new Random();
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(usedWidthPx);
            int y = random.nextInt(usedHeightPx);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            localGraphics.drawLine(x, y, x + xl, y + yl);
        }
        // 写字
        for (int i = 0; i < captchaCode.length(); i++) {
            String ch = captchaCode.substring(i, i + 1);
            localGraphics.setColor(new Color(20 + localRandom.nextInt(110), 20 + localRandom.nextInt(110), 20 + localRandom.nextInt(110)));
            localGraphics.setFont(new Font("宋体", Font.PLAIN, 25));
            localGraphics.drawString(ch, 20 * i + 6, 20);
        }
        // 保存
        String imageName = WzUniqueValUtil.makeUniqueTimes();
        String imageFullName = WzFileUtil.makeFilePath(uploadCaptchaRoot, "", imageName, WzFileUtil.EXT_JPG);
        OutputStream out = null;
        try {
            out = new FileOutputStream(imageFullName);
            ImageIO.write(bi, "JPEG", out);
        } catch (Exception ex) {
            throw new BizException(RunningResult.IO_FAIL.code(), "保存图片验证码失败", ex);
        } finally {
            try {
                out.close();
            } catch (Exception ex) {
                throw new BizException(RunningResult.IO_FAIL.code(), "关闭输出流失败", ex);
            }
        }
        return WzFileUtil.makeRequestUrl(downloadCaptchaPrefix,"", imageName);
    }

    /**
     * 生成图形验证码并返回图形的Base64编码字符串.
     * @param captchaCode 图形验证码
     * @param widthPx 图形宽度
     * @param heightPx 图形高度
     * @return 图形验证码图像的Base64字符串
     */
    public String madeAndGetCapthaBase64(String captchaCode, int widthPx, int heightPx) {
        int usedWidthPx = DEF_WIDTH_PX;
        int usedHeightPx = DEF_HEIGHT_PX;
        Random localRandom = new Random();
        if (DEF_WIDTH_PX < widthPx) {
            usedWidthPx = widthPx;
        }
        if (DEF_HEIGHT_PX < heightPx) {
            usedHeightPx = heightPx;
        }
        // 创建图片
        BufferedImage bi = new BufferedImage(usedWidthPx, usedWidthPx, BufferedImage.TYPE_INT_RGB);
        // 获得图片
        Graphics localGraphics = bi.getGraphics();
        // 设置背景色
        localGraphics.setColor(getRandColor(200, 250));
        localGraphics.fillRect(0, 0, usedWidthPx, usedHeightPx);
        // 设置边框
        localGraphics.setColor(Color.BLUE);
        localGraphics.drawRect(1, 1, usedWidthPx - 2, usedHeightPx - 2);
        // 绘制随机线条，为图像添加噪音
        Random random = new Random();
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(usedWidthPx);
            int y = random.nextInt(usedHeightPx);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            localGraphics.drawLine(x, y, x + xl, y + yl);
        }
        // 写字
        for (int i = 0; i < captchaCode.length(); i++) {
            String ch = captchaCode.substring(i, i + 1);
            localGraphics.setColor(new Color(20 + localRandom.nextInt(110), 20 + localRandom.nextInt(110), 20 + localRandom.nextInt(110)));
            localGraphics.setFont(new Font("宋体", Font.BOLD, 25));
            localGraphics.drawString(ch, 20 * i + 6, 20);
        }
        // 返回Base64
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(bi, "JPEG", out);
            byte[] bytes = out.toByteArray();
            return WzEncryptUtil.byteArrToBase64(bytes);
        } catch (Exception ex) {
            throw new BizException(RunningResult.IO_FAIL.code(), "保存图片验证码失败", ex);
        } finally {
            try {
                out.close();
            } catch (Exception ex) {
                throw new BizException(RunningResult.IO_FAIL.code(), "关闭输出流失败", ex);
            }
        }
    }

    /**
     * 保存用户头像并保存并返回访问地址.
     * @param fileBase64Data 用户头像Base64编码
     * @return 用户头像访问路径
     */
    public String madeAndGetUserIconUrl(String fileBase64Data) {
        String imageName = WzUniqueValUtil.makeUniqueTimes();
        WzFileUtil.save(fileBase64Data, uploadUserIconRoot, "", imageName, WzFileUtil.EXT_JPG);
        return WzFileUtil.makeRequestUrl(downloadUserIconPrefix,"", imageName + WzFileUtil.EXT_JPG);
    }

    /**
     * 保存用户实名验证相关图片并保存并返回访问地址.
     * @param fileBase64Data 用户实名验证相关图片Base64编码
     * @return 用户实名验证相关图片访问路径
     */
    public String madeAndGetUserRealAuthUrl(String fileBase64Data) {
        String imageName = WzUniqueValUtil.makeUniqueTimes();
        WzFileUtil.save(fileBase64Data, uploadUserRealnameRoot, "", imageName, WzFileUtil.EXT_JPG);
        return WzFileUtil.makeRequestUrl(downloadUserRealnamePrefix,"", imageName + WzFileUtil.EXT_JPG);
    }

    /**
     * 随机获得颜色，RGB格式.
     * @param fc 前景色
     * @param bc 背景色
     * @return 颜色
     */
    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}
