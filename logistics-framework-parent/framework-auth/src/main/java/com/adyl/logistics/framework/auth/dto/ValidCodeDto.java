package com.adyl.logistics.framework.auth.dto;

import com.adyl.logistics.framework.api.controller.BaseController;
import com.adyl.logistics.framework.api.dto.RequestDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Description: 验证码请求对象
 * @Author: Dengb
 * @Date: 2018年11月27日 17:01
 */
public class ValidCodeDto extends RequestDto {
    @NotBlank(message = "手机号码不能为空", groups = {BaseController.GroupDefault.class})
    @Pattern(regexp = "^1\\d{10}$", message = "手机号码格式不正确", groups = {BaseController.GroupDefault.class})
    private String phone;
    private Integer scene = 0;
    @NotBlank(message = "验证码不能为空", groups = {BaseController.GroupUpdate.class})
    private String validCode;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getScene() {
        return scene;
    }

    public void setScene(Integer scene) {
        this.scene = scene;
    }

    public String getValidCode() {
        return validCode;
    }

    public void setValidCode(String validCode) {
        this.validCode = validCode;
    }
}
