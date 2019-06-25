package com.adyl.logistics.platform.auth.config;

import com.adyl.logistics.platform.auth.api.AuthServerConfig;
import com.adyl.logistics.platform.auth.client.CustomClientDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;

import java.util.concurrent.TimeUnit;


/**
 * 自定义配置授权服务器
 * 配置授权的相关信息，配置的核心都在这里 在这里进行配置客户端，配置token存储方式等
 *
 * @author Dengb
 * @date 20180904
 */
@Configuration
public class CustomAuthorizationServerConfiguration extends AuthServerConfig {
    public CustomAuthorizationServerConfiguration() {
        super((int) TimeUnit.DAYS.toSeconds(1), 0, false, false);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(getClientDetailsService());
    }

    @Bean
    public ClientDetailsService getClientDetailsService() {
        return new CustomClientDetailsService();
    }
}
