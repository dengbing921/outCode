package com.adyl.logistics.platform.resource;

import com.adyl.logistics.platform.auth.api.annotation.EnableResJwtTokenStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 资源服务器
 *
 * @author Dengb
 * @date 20180911
 */
@SpringBootApplication
@EnableResJwtTokenStore
@EnableDiscoveryClient
public class ResourceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResourceApplication.class, args);
    }
}
