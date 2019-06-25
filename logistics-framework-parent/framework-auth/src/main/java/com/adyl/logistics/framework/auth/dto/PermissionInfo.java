package com.adyl.logistics.framework.auth.dto;

import java.io.Serializable;

/**
 * 权限信息对象
 *
 * @author Dengb
 * @date 20180902
 */
public class PermissionInfo implements Serializable {
    //权限id
    private Long resourceId;
    //父权限id
    private Long resourcePid;
    //资源名称
    private String resourceName;
    //资源类型
    private Byte resourceType;
    //资源排序序号
    private Byte resourceSort;
    //资源编码
    private String resourceCode;
    //资源状态
    private Byte resourceState;
    //资源描述
    private String resourceDes;
    //前端路由
    private String resourceApi;
    //请求地址
    private String resourceUrl;
    //资源图标
    private String resourceIcon;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getResourcePid() {
        return resourcePid;
    }

    public void setResourcePid(Long resourcePid) {
        this.resourcePid = resourcePid;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Byte getResourceType() {
        return resourceType;
    }

    public void setResourceType(Byte resourceType) {
        this.resourceType = resourceType;
    }

    public Byte getResourceSort() {
        return resourceSort;
    }

    public void setResourceSort(Byte resourceSort) {
        this.resourceSort = resourceSort;
    }

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    public Byte getResourceState() {
        return resourceState;
    }

    public void setResourceState(Byte resourceState) {
        this.resourceState = resourceState;
    }

    public String getResourceDes() {
        return resourceDes;
    }

    public void setResourceDes(String resourceDes) {
        this.resourceDes = resourceDes;
    }

    public String getResourceApi() {
        return resourceApi;
    }

    public void setResourceApi(String resourceApi) {
        this.resourceApi = resourceApi;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getResourceIcon() {
        return resourceIcon;
    }

    public void setResourceIcon(String resourceIcon) {
        this.resourceIcon = resourceIcon;
    }
}
