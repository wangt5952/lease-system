package com.elextec.framework.utils;

import com.elextec.framework.common.constants.RunningResult;
import com.elextec.framework.exceptions.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * 文件工具类.
 * Created by wangtao on 2018/1/16.
 */
public class WzFileUtil {
    /** 日志. */
    private static final Logger logger = LoggerFactory.getLogger(WzFileUtil.class);
    /** 扩展名 jpg. */
    public static final String EXT_JPG = ".jpg";
    /** 扩展名 png. */
    public static final String EXT_PNG = ".png";
    /** 扩展名 gif. */
    public static final String EXT_GIF = ".gif";
    /** 扩展名 txt. */
    public static final String EXT_TXT = ".txt";
    /** 扩展名 xls. */
    public static final String EXT_XLS = ".xls";
    /** 扩展名 xlsx. */
    public static final String EXT_XLSX = ".xlsx";
    /** 扩展名 doc. */
    public static final String EXT_DOC = ".doc";
    /** 扩展名 docx. */
    public static final String EXT_DOCX = ".docx";
    /** 扩展名 pdf. */
    public static final String EXT_PDF = "pdf";

    /**
     * 保存文件.
     * @param fileBase64Data 文件内容Base64字符串
     * @param saveRoot 保存根目录
     * @param saveDir 保存相对目录
     * @param fileName 文件名（不带扩展名）
     * @param ext 扩展名
     */
    public static void save(String fileBase64Data, String saveRoot, String saveDir, String fileName, String ext) {
        String filePath = makeFilePath(saveRoot, saveDir, fileName, ext);
        byte[] fileBytes = WzEncryptUtil.base64ToByteArr(fileBase64Data);
        if (null == fileBytes) {
            throw new BizException(RunningResult.PARAM_ANALYZE_ERROR.code(), "文件内容解码失败");
        }
        save(filePath, fileBytes);
    }

    /**
     * 保存文件.
     * @param fileData 文件内容
     * @param saveRoot 保存根目录
     * @param saveDir 保存相对目录
     * @param fileName 文件名（不带扩展名）
     * @param ext 扩展名
     */
    public static void save(byte[] fileData, String saveRoot, String saveDir, String fileName, String ext) {
        String filePath = makeFilePath(saveRoot, saveDir, fileName, ext);
        save(filePath, fileData);
    }

    /**
     * 保存文件.
     * @param fileFullPath 文件保存全路径
     * @param fileData 文件内容
     */
    public static void save(String fileFullPath, byte[] fileData) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(fileFullPath);
            out.write(fileData);
            out.flush();
            out.close();
        } catch (Exception ex) {
            logger.error("文件保存失败", ex);
            throw new BizException(RunningResult.IO_FAIL.code(), "文件保存失败", ex);
        } finally {
            try {
                out.close();
                out = null;
            } catch (Exception ex) {
                logger.error("关闭输出流失败", ex);
                throw new BizException(RunningResult.IO_FAIL.code(), "关闭输出流失败", ex);
            }
        }
    }

    /**
     * 组合文件全路径.
     * @param saveRoot 保存根目录
     * @param relativePath 保存相对目录
     * @param fileName 文件名（不带扩展名）
     * @param ext 扩展名
     * @return 全路径
     */
    public static String makeFilePath(String saveRoot, String relativePath, String fileName, String ext) {
        StringBuffer savedPath = new StringBuffer("");
        if (WzStringUtil.isBlank(saveRoot)
                || WzStringUtil.isBlank(fileName)
                || WzStringUtil.isBlank(ext)) {
            throw new BizException(RunningResult.IO_FAIL.code(), "文件保存目录有误");
        }
        // 处理保存Root，并去掉末尾的/
        String usedSaveRoot = saveRoot.replace("\\", "/");
        if (usedSaveRoot.endsWith("/")) {
            usedSaveRoot = usedSaveRoot.substring(0, usedSaveRoot.length() - 1);
        }
        savedPath.append(usedSaveRoot);
        // 处理相对目录，在开始加/并去掉末尾的/
        String usedSaveDir = null;
        if (WzStringUtil.isNotBlank(relativePath)) {
            usedSaveDir = relativePath.replace("\\", "/");
            if (!usedSaveDir.startsWith("/")) {
                usedSaveDir = "/" + usedSaveDir;
            }
            if (usedSaveDir.endsWith("/")) {
                usedSaveDir = usedSaveDir.substring(0, usedSaveDir.length() - 1);
            }
            savedPath.append(usedSaveDir);
        }
        File f = new File(savedPath.toString());
        if (!f.exists() || !f.isDirectory()) {
            f.mkdirs();
        }
        // 处理文件名，在开始加/并如果存在.xx的扩展名则去掉
        String usedFileName = fileName;
        if (!usedFileName.startsWith("/")) {
            usedFileName = "/" + usedFileName;
        }
        if (-1 < usedFileName.indexOf(".")) {
            usedFileName = usedFileName.substring(0, usedFileName.indexOf("."));
        }
        savedPath.append(usedFileName);
        // 处理扩展名，在开始加 .
        String usedExt = ext;
        if (!ext.startsWith(".")) {
            usedExt = "." + usedExt;
        }
        savedPath.append(usedExt);
        return savedPath.toString();
    }

    /**
     * 组合文件全路径.
     * @param saveRoot 保存根目录
     * @param relativePath 保存相对目录
     * @param fileName 文件名（不带扩展名）
     * @return 全路径
     */
    public static String makeFilePath(String saveRoot, String relativePath, String fileName) {
        StringBuffer savedPath = new StringBuffer("");
        if (WzStringUtil.isBlank(saveRoot)
                || WzStringUtil.isBlank(fileName)) {
            throw new BizException(RunningResult.IO_FAIL.code(), "文件保存目录有误");
        }
        // 处理保存Root，并去掉末尾的/
        String usedSaveRoot = saveRoot.replace("\\", "/");
        if (usedSaveRoot.endsWith("/")) {
            usedSaveRoot = usedSaveRoot.substring(0, usedSaveRoot.length() - 1);
        }
        savedPath.append(usedSaveRoot);
        // 处理相对目录，在开始加/并去掉末尾的/
        String usedSaveDir = null;
        if (WzStringUtil.isNotBlank(relativePath)) {
            usedSaveDir = relativePath.replace("\\", "/");
            if (!usedSaveDir.startsWith("/")) {
                usedSaveDir = "/" + usedSaveDir;
            }
            if (usedSaveDir.endsWith("/")) {
                usedSaveDir = usedSaveDir.substring(0, usedSaveDir.length() - 1);
            }
            savedPath.append(usedSaveDir);
        }
        File f = new File(savedPath.toString());
        if (!f.exists() || !f.isDirectory()) {
            f.mkdirs();
        }
        // 处理文件名，在开始加/并如果存在.xx的扩展名则去掉
        String usedFileName = fileName;
        if (!usedFileName.startsWith("/")) {
            usedFileName = "/" + usedFileName;
        }
        savedPath.append(usedFileName);
        return savedPath.toString();
    }

    /**
     * 组合文件访问Url.
     * @param prefix 访问根目录
     * @param relativePath 相对目录
     * @param fileName 文件名
     * @return 访问URL
     */
    public static String makeRequestUrl(String prefix, String relativePath, String fileName) {
        StringBuffer reqUrl = new StringBuffer("");
        if (WzStringUtil.isBlank(prefix)
                || WzStringUtil.isBlank(fileName)) {
            throw new BizException(RunningResult.IO_FAIL.code(), "访问路径有误");
        }
        // 处理前缀，并去掉末尾的/
        String usedPrefix = prefix.replace("\\", "/");
        if (usedPrefix.endsWith("/")) {
            usedPrefix = usedPrefix.substring(0, usedPrefix.length() - 1);
        }
        reqUrl.append(usedPrefix);
        // 处理相对目录，在开始加/并去掉末尾的/
        String usedRelativePath = null;
        if (WzStringUtil.isNotBlank(relativePath)) {
            usedRelativePath = relativePath.replace("\\", "/");
            if (!usedRelativePath.startsWith("/")) {
                usedRelativePath = "/" + usedRelativePath;
            }
            if (usedRelativePath.endsWith("/")) {
                usedRelativePath = usedRelativePath.substring(0, usedRelativePath.length() - 1);
            }
            reqUrl.append(usedRelativePath);
        }
        // 处理文件名，在开始加/并如果存在.xx的扩展名则去掉
        String usedFileName = fileName;
        if (!usedFileName.startsWith("/")) {
            usedFileName = "/" + usedFileName;
        }
//        if (-1 < usedFileName.indexOf(".")) {
//            usedFileName = usedFileName.substring(0, usedFileName.indexOf("."));
//        }
        reqUrl.append(usedFileName);
        return reqUrl.toString();
    }

    /**
     * 删除文件.
     * @param delDir 删除文件所在目录
     * @param fileName 删除文件名
     */
    public static void deleteFile(String delDir, String fileName) {
        String delPath = makeFilePath(delDir, "", fileName);
        File delFile  = new File(delPath);
        if (delFile.exists()) {
            delFile.delete();
        }
    }
}
