package com.adyl.logistics.platform.auth.api.dto;

import java.util.Set;

/**
 * @Description: OAuth2授权Token对象
 * @Author: Dengb
 * @Date: 2018年09月13日 15:10
 */
public class OAuth2Token {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private int expiresIn;
    private Set<String> scope;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Set<String> getScope() {
        return scope;
    }

    public void setScope(Set<String> scope) {
        this.scope = scope;
    }
}
