package com.adyl.logistics.framework.auth.dto;

import javax.validation.constraints.NotBlank;

/**
 * @Description: 注册请求对象
 * @Author: Dengb
 * @Date: 2019年01月09日 18:40
 */
public class RegisterRequest {
    @NotBlank(message = "手机号码不能为空")
    private String mobilePhone;
    @NotBlank(message = "验证码不能为空")
    private String validCode;

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getValidCode() {
        return validCode;
    }

    public void setValidCode(String validCode) {
        this.validCode = validCode;
    }
}
