package com.adyl.logistics.framework.fastdfs.api.feign;

import com.adyl.logistics.framework.api.controller.ResultData;
import com.adyl.logistics.framework.fastdfs.api.dto.DownloadDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description:
 * @Author: Dengb
 * @Date: 2018年11月20日 14:49
 */
@Service
public class FastdfsFallback implements FastdfsFeign {
    @Override
    public ResultData downloadFile(DownloadDto downloadDto) {
        return null;
    }

    @Override
    public ResultData uploadFile(MultipartFile file) {
        return null;
    }

    @Override
    public ResultData uploadBatch() {
        return null;
    }
}
