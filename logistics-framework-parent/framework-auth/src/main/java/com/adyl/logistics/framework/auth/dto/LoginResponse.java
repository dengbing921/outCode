package com.adyl.logistics.framework.auth.dto;

import com.adyl.logistics.framework.api.dto.TokenDto;
import com.adyl.logistics.framework.api.dto.UserDto;

import java.util.List;

/**
 * 登录响应对象
 *
 * @author Dengb
 * @date 20180823
 */
public class LoginResponse {
    /**
     * Token信息
     */
    private TokenDto token;
    /**
     * 用户信息
     */
    private UserDto user;
    /**
     * 权限信息
     */
    private List<String> code;
    /**
     * 菜单集合
     */
    private List<MenuInfo> menuList;

    public UserDto getUser() {
        return user;
    }

    public List<String> getCode() {
        return code;
    }

    public void setCode(List<String> code) {
        this.code = code;
    }

    public List<MenuInfo> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<MenuInfo> menuList) {
        this.menuList = menuList;
    }

    public TokenDto getToken() {
        return token;
    }

    public void setToken(TokenDto token) {
        this.token = token;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}
