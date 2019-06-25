package com.adyl.logistics.platform.auth.api.service;

import com.adyl.logistics.framework.api.controller.ResultData;
import com.adyl.logistics.framework.core.utils.Tools;
import com.adyl.logistics.platform.auth.api.constant.AuthConstant;
import com.adyl.logistics.platform.auth.api.dto.AuthorizationDto;
import com.adyl.logistics.platform.auth.api.dto.OAuth2Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2ClientProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 授权API接口（客户端使用），若使用，请在应用配置里面配置以下相关参数：
 * security.oauth2.client.client-id
 * security.oauth2.client.client-secret
 * security.oauth2.client.access-token-uri
 * @Author: Dengb
 * @Date: 2018年09月13日 15:05
 */
@Service
public class AuthorizationService {
    @Autowired
    private OAuth2ClientProperties clientProperties;
    @Value("${security.oauth2.client.access-token-uri}")
    private String accessTokenUri;

    /**
     * 客户端模式
     *
     * @return
     */
    public ResultData authorizationClient() {
        return authorization(new AuthorizationDto().setGrantType("client_credentials")
                .setClientId(clientProperties.getClientId())
                .setClientSecret(clientProperties.getClientSecret()));
    }

    /**
     * 密码授权模式（暂未实现）
     *
     * @return
     */
    public ResultData authorizationPassword() {
        return null;
    }

    /**
     * 授权码模式（暂未实现）
     *
     * @return
     */
    public ResultData authorizationCode() {
        return null;
    }

    /**
     * 授权
     *
     * @param authorizationDto
     * @return
     */
    private ResultData authorization(AuthorizationDto authorizationDto) {
        // 配置参数校验
        if (Tools.isEmpty(authorizationDto.getClientId()) || Tools.isEmpty(authorizationDto.getClientSecret())
                || Tools.isEmpty(authorizationDto.getGrantType())) {
            return ResultData.newFailure(AuthConstant.Error_Code_02, AuthConstant.Error_Code_02_Msg);
        }
        if (Tools.isEmpty(accessTokenUri)) {
            return ResultData.newFailure(AuthConstant.Error_Code_02, AuthConstant.Error_Code_02_Msg);
        }
        // 构造参数
        Map<String, String> map = new HashMap<>();
        map.put("grant_type", authorizationDto.getGrantType());
        map.put("client_id", authorizationDto.getClientId());
        map.put("client_secret", authorizationDto.getClientSecret());
        // 请求授权接口
        ResponseEntity<OAuth2AccessToken> responseEntity = new RestTemplate().getForEntity(accessTokenUri, OAuth2AccessToken.class, map);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            OAuth2AccessToken oAuth2AccessToken = responseEntity.getBody();
            OAuth2Token auth2Token = new OAuth2Token();
            auth2Token.setAccessToken(oAuth2AccessToken.getValue());
            auth2Token.setExpiresIn(oAuth2AccessToken.getExpiresIn());
            auth2Token.setTokenType(oAuth2AccessToken.getTokenType());
            auth2Token.setScope(oAuth2AccessToken.getScope());
            if (oAuth2AccessToken.getRefreshToken() != null) {
                auth2Token.setRefreshToken(oAuth2AccessToken.getRefreshToken().getValue());
            }
            return ResultData.newSuccess(auth2Token);
        }
        return ResultData.newFailure(AuthConstant.Error_Code_01, AuthConstant.Error_Code_01_Msg);
    }
}
