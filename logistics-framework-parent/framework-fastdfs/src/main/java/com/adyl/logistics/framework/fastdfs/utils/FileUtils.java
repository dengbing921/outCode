package com.adyl.logistics.framework.fastdfs.utils;

/**
 * @Description: 文件操作工具类
 * @Author: Dengb
 * @Date: 2018年10月25日 17:57
 */
public class FileUtils {

    public static String getExtensionName(String fileName) {
        String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
        return prefix;
    }

    public static String getOriginalFilename(String filename) {
        if (filename == null) {
            return "";
        }
        int pos = filename.lastIndexOf("/");
        if (pos == -1) {
            pos = filename.lastIndexOf("\\");
        }
        if (pos != -1) {
            return filename.substring(pos + 1);
        }
        return filename;
    }
}
