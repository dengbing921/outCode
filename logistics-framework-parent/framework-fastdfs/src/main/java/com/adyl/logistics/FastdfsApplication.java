package com.adyl.logistics;

import com.adyl.logistics.framework.api.application.BaseApplication;
import com.adyl.logistics.framework.fastdfs.client.*;
import com.adyl.logistics.framework.fastdfs.common.FastdfsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;


/**
 * @Description: 文件服务应用程序
 * @Author: Dengb
 * @Date: 2018年10月25日 17:27
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableCircuitBreaker
@EnableHystrix
public class FastdfsApplication extends BaseApplication {
    @Autowired
    FastdfsProperties fastdfsProperties;

    public static void main(String[] args) {
        SpringApplication.run(FastdfsApplication.class, args);
    }

    @Bean
    public StorageClient1 initStorageClient() {
        StorageClient1 storageClient = null;
        try {
            //String filePath = ResourceUtils.getFile("com/adyl/logistics/framework/fastdfs/fastdfs.conf").getPath();
            ClientGlobal.init(fastdfsProperties);
            TrackerClient trackerClient = new TrackerClient(ClientGlobal.g_tracker_group);
            TrackerServer trackerServer = trackerClient.getConnection();
            if (trackerServer == null) {
                throw new IllegalStateException("getConnection return null");
            }
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            if (storageServer == null) {
                throw new IllegalStateException("getStoreStorage return null");
            }
            storageClient = new StorageClient1(trackerServer, storageServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return storageClient;
    }
}
