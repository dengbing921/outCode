package com.adyl.logistics.framework.fastdfs.api.feign;

import com.adyl.logistics.framework.api.controller.ResultData;
import com.adyl.logistics.framework.api.feign.FeignConfig;
import com.adyl.logistics.framework.fastdfs.api.dto.DownloadDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description:
 * @Author: Dengb
 * @Date: 2018年11月20日 14:47
 */
@FeignClient(name = "${feign.fastdfsService}", fallback = FastdfsFallback.class, configuration = FeignConfig.class)
public interface FastdfsFeign {
    @RequestMapping(value = "/file/download", method = RequestMethod.POST)
    ResultData downloadFile(@RequestBody DownloadDto downloadDto);

    @RequestMapping(value = "/file/upload", method = RequestMethod.POST)
    ResultData uploadFile(@RequestParam(name = "file") MultipartFile file);

    @RequestMapping(value = "/file/upload/batch", method = RequestMethod.POST)
    ResultData uploadBatch();
}
