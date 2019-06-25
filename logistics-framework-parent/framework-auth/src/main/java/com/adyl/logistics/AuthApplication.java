package com.adyl.logistics;

import com.adyl.logistics.framework.api.application.BaseApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 认证服务应用启动程序
 *
 * @author Dengb
 * @date 20180821
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class AuthApplication extends BaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
