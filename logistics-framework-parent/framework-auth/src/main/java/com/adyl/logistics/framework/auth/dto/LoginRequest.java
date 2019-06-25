package com.adyl.logistics.framework.auth.dto;

import com.adyl.logistics.framework.api.controller.BaseController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * 登录信息
 *
 * @author Dengb
 * @date 20180822
 */
public class LoginRequest {
    /**
     * 账号
     */
    @NotBlank(message = "登录名不能为空")
    private String username;
    /**
     * 密码
     */
    @NotBlank(message = "登录密码不能为空")
    private String password;
    /**
     * 类型
     */
    private Byte type = 0;
    /**
     * APP设备注册ID
     */
    private String registrationId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
}
