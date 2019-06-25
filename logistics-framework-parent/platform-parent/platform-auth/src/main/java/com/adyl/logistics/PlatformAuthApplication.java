package com.adyl.logistics;

import com.adyl.logistics.platform.auth.api.annotation.EnableAuthJwtTokenStore;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 平台授权应用服务
 *
 * @author Dengb
 * @date 20180831
 */
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = "com.adyl.logistics.platform.auth.client")
@EnableAuthJwtTokenStore
public class PlatformAuthApplication {
    /**
     * 入口
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(PlatformAuthApplication.class, args);
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
