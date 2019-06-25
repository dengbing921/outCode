package com.adyl.logistics.framework.auth.dto;

/**
 * 用于存放Token对象
 *
 * @author Dengb
 * @date 20180823
 */
public class TokenResponse {
    private String accessToken;
    private String refreshToken;

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

    /**
     * 创建一个Token
     * @param accessToken
     * @param refreshToken
     * @return
     */
    public static TokenResponse create(String accessToken, String refreshToken) {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        return tokenResponse;
    }
}
