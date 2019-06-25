package com.adyl.logistics.framework.auth.dto;

import java.util.LinkedList;
import java.util.List;

/**
 * 菜单对象
 *
 * @author Dengb
 * @date 20180831
 */
public class MenuInfo {
    /**
     * 菜单显示标题
     */
    private String title;
    /**
     * 菜单显示图标
     */
    private String icon;
    /**
     * 菜单对应的请求页面地址
     */
    private String url;
    /**
     * 权限
     */
    private String authCode;
    /**
     * 排序
     */
    private Byte sort;
    /**
     * 子集菜单
     */
    private List<MenuInfo> subNavList;

    public MenuInfo() {
        this.subNavList = new LinkedList<MenuInfo>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public List<MenuInfo> getSubNavList() {
        return subNavList;
    }

    public void setSubNavList(List<MenuInfo> subNavList) {
        this.subNavList = subNavList;
    }

    public Byte getSort() {
        return sort;
    }

    public void setSort(Byte sort) {
        this.sort = sort;
    }
}
