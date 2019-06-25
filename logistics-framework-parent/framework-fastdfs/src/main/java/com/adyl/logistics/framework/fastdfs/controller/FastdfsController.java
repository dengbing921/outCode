package com.adyl.logistics.framework.fastdfs.controller;

import com.adyl.logistics.framework.api.controller.BaseController;
import com.adyl.logistics.framework.api.controller.ResultData;
import com.adyl.logistics.framework.core.utils.Tools;
import com.adyl.logistics.framework.fastdfs.api.dto.DownloadDto;
import com.adyl.logistics.framework.fastdfs.entity.FileInfo;
import com.adyl.logistics.framework.fastdfs.service.FastdfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 控制器
 * @Author: Dengb
 * @Date: 2018年10月25日 17:37
 */
@RestController
@RequestMapping(value = "/file")
public class FastdfsController extends BaseController {
    private final String Err_Code_01 = "81301";
    private final String Err_Code_02 = "81302";
    private final String Err_Code_01_Msg = "上传失败";
    private final String Err_Code_02_Msg = "获取不到上传文件";

    @Autowired
    private FastdfsService fastdfsService;
    @Value("${fastdfs.httpPrefix}")
    private String fastdfsHttpPrefix;

    /**
     * 单文件上传
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResultData uploadFile(@RequestParam MultipartFile file) throws Exception {
        if (file != null) {
            String path = fastdfsService.uploadFile(file.getBytes(), file.getOriginalFilename());
            if (Tools.isNotEmpty(path)) {
                return ResultData.newSuccess(new FileInfo(fastdfsHttpPrefix + path));
            }
            // 上传失败重新再试一次，有个很怪的问题，第一次会失败，后面上传就好了
            path = fastdfsService.uploadFile(file.getBytes(), file.getOriginalFilename());
            if (Tools.isNotEmpty(path)) {
                return ResultData.newSuccess(new FileInfo(fastdfsHttpPrefix + path));
            }
            return ResultData.newFailure(Err_Code_01, Err_Code_01_Msg);
        }
        return ResultData.newFailure(Err_Code_02, Err_Code_02_Msg);
    }

    /**
     * 多文件上传
     *
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/upload/batch", method = RequestMethod.POST)
    public ResultData uploadBatch(HttpServletRequest request) throws Exception {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        if (files == null || files.size() == 0) {
            return ResultData.newFailure(Err_Code_02, Err_Code_02_Msg);
        }
        int succ = 0;
        List<String> succFileList = new ArrayList<>();
        for (MultipartFile file : files) {
            String path = fastdfsService.uploadFile(file.getBytes(), file.getOriginalFilename());
            if (Tools.isNotEmpty(path)) {
                succFileList.add(fastdfsHttpPrefix + path);
                succ++;
            }
        }
        if (succ > 0) {
            String msg = "文件上传成功";
            if (succ < files.size()) {
                msg = succ + "个文件上传成功，" + (files.size() - succ) + "个文件上传失败";
            }
            return ResultData.newSuccess(succFileList, msg);
        }
        return ResultData.newFailure(Err_Code_01, Err_Code_01_Msg);
    }

    /**
     * 单文件或多文件下载
     *
     * @param downloadDto
     * @return
     */
    @RequestMapping(value = "/download", method = RequestMethod.POST)
    public ResultData downloadFile(@RequestBody DownloadDto downloadDto) {
        if (downloadDto.getFiles() == null || downloadDto.getFiles().size() == 0) {
            return ResultData.newFailure();
        }
        // 文件内容
        Map<String, byte[]> mapFiles = new HashMap<>();
        for (String fileId : downloadDto.getFiles()) {
            byte[] fileBytes = fastdfsService.downloadFile(fileId);
            if (!Tools.isEmpty(fileBytes)) {
                mapFiles.put(fileId, fileBytes);
            }
        }
        return ResultData.newSuccess(mapFiles);
    }
}
