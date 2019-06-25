package com.adyl.logistics.framework.fastdfs.service;

import com.adyl.logistics.framework.fastdfs.client.FileInfo;
import com.adyl.logistics.framework.fastdfs.client.StorageClient1;
import com.adyl.logistics.framework.fastdfs.common.MyException;
import com.adyl.logistics.framework.fastdfs.common.NameValuePair;
import com.adyl.logistics.framework.fastdfs.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Description: 文件上传服务
 * @Author: Dengb
 * @Date: 2018年10月25日 17:54
 */
@Component
public class FastdfsService {
    @Autowired
    private StorageClient1 storageClient;

    /**
     * 文件上传
     *
     * @param buff
     * @param fileName
     * @return
     */
    public String uploadFile(byte[] buff, String fileName) {
        return uploadFile(buff, fileName, null);
    }

    /**
     * 文件上传
     *
     * @param buff
     * @param fileName
     * @param metaList
     * @return
     */
    public String uploadFile(byte[] buff, String fileName, Map<String, String> metaList) {
        try {
            NameValuePair[] nameValuePairs = null;
            if (metaList != null) {
                nameValuePairs = new NameValuePair[metaList.size()];
                int index = 0;
                Iterator<Map.Entry<String, String>> iterator = metaList.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry) iterator.next();
                    String name = (String) entry.getKey();
                    String value = (String) entry.getValue();
                    nameValuePairs[(index++)] = new NameValuePair(name, value);
                }
            }
            return this.storageClient.upload_file1(buff, FileUtils.getExtensionName(fileName), nameValuePairs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件元信息
     *
     * @param fileId
     * @return
     */
    public Map<String, String> getFileMetadata(String fileId) {
        try {
            NameValuePair[] metaList = this.storageClient.get_metadata1(fileId);
            if (metaList != null) {
                HashMap<String, String> map = new HashMap();
                NameValuePair[] arrayOfNameValuePair1;
                int j = (arrayOfNameValuePair1 = metaList).length;
                for (int i = 0; i < j; i++) {
                    NameValuePair metaItem = arrayOfNameValuePair1[i];
                    map.put(metaItem.getName(), metaItem.getValue());
                }
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除文件
     *
     * @param fileId
     * @return
     */
    public int deleteFile(String fileId) {
        try {
            return this.storageClient.delete_file1(fileId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 文件下载
     *
     * @param fileId
     * @return
     */
    public byte[] downloadFile(String fileId) {
        try {
            return this.storageClient.download_file1(fileId);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件信息
     *
     * @param fileId
     * @return
     */
    public FileInfo getFileInfo(String fileId) {
        try {
            return this.storageClient.get_file_info1(fileId);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return null;
    }
}
