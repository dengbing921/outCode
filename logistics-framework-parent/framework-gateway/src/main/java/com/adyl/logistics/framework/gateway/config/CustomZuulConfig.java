package com.adyl.logistics.framework.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义路由配置
 *
 * @author Dengb
 * @date 20180906
 */
@Configuration
public class CustomZuulConfig {
    @Autowired
    ZuulProperties zuulProperties;

    @Bean
    public CustomRouteLocator routeLocator() {
        CustomRouteLocator routeLocator = new CustomRouteLocator("", this.zuulProperties);
        return routeLocator;
    }
}
