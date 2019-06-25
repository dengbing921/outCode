package com.adyl.logistics.platform.resource.config;

import com.adyl.logistics.platform.auth.api.ResServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * 资源服务配置
 *
 * @author Dengb
 * @date 20180911
 */
@Configuration
public class ResourceServer01Configuration extends ResServerConfig {
    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .and()
                .authorizeRequests()
                .antMatchers("/**").authenticated();
    }
}
