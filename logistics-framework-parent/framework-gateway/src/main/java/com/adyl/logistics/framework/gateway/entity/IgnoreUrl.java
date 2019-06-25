package com.adyl.logistics.framework.gateway.entity;

/**
 * @Description:
 * @Author: Dengb
 * @Date: 2018年11月30日 16:08
 */
public class IgnoreUrl {
    private Long ignoreId;
    private String ignoreUrl;
    private String ignoreType;

    public Long getIgnoreId() {
        return ignoreId;
    }

    public void setIgnoreId(Long ignoreId) {
        this.ignoreId = ignoreId;
    }

    public String getIgnoreUrl() {
        return ignoreUrl;
    }

    public void setIgnoreUrl(String ignoreUrl) {
        this.ignoreUrl = ignoreUrl;
    }

    public String getIgnoreType() {
        return ignoreType;
    }

    public void setIgnoreType(String ignoreType) {
        this.ignoreType = ignoreType;
    }
}
