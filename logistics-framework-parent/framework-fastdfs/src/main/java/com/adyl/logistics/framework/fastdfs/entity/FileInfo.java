package com.adyl.logistics.framework.fastdfs.entity;

import java.io.Serializable;

/**
 * @Description: 文件信息
 * @Author: Dengb
 * @Date: 2018年10月26日 10:09
 */
public class FileInfo implements Serializable {
    private String fileUrl;

    public FileInfo(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
