package com.adyl.logistics.platform.auth.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;


public class CustomClientDetailsService implements ClientDetailsService {
    @Autowired
    private IClientDetailsDao clientDetailsDao;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        CustomClientDetails clientDetails = clientDetailsDao.getClientByClientId(clientId);
        if (clientDetails == null) {
            throw new ClientRegistrationException("客户端【" + clientId + "】未注册");
        }
        // 设置密码加密
        clientDetails.setClientSecret(passwordEncoder.encode(clientDetails.getClientSecret()));
        return clientDetails;
    }
}
