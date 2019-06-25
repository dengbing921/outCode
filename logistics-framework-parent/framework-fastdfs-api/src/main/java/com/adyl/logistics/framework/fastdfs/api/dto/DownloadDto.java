package com.adyl.logistics.framework.fastdfs.api.dto;

import java.util.List;

/**
 * @Description: 文件下载对象
 * @Author: Dengb
 * @Date: 2018年11月08日 09:37
 */
public class DownloadDto {
    private List<String> files;

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
