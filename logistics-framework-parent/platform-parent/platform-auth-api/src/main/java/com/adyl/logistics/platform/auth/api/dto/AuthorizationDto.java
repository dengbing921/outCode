package com.adyl.logistics.platform.auth.api.dto;

/**
 * @Description: 授权请求对象
 * @Author: Dengb
 * @Date: 2018年09月13日 17:20
 */
public class AuthorizationDto {
    private String grantType;
    private String clientId;
    private String clientSecret;
    private String username;
    private String password;

    public String getGrantType() {
        return grantType;
    }

    public AuthorizationDto setGrantType(String grantType) {
        this.grantType = grantType;
        return this;
    }

    public String getClientId() {
        return clientId;
    }

    public AuthorizationDto setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public AuthorizationDto setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public AuthorizationDto setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public AuthorizationDto setPassword(String password) {
        this.password = password;
        return this;
    }
}
