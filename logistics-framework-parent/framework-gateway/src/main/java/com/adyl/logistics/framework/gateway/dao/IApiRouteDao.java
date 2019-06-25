package com.adyl.logistics.framework.gateway.dao;

import com.adyl.logistics.framework.gateway.entity.ApiRoute;
import com.adyl.logistics.framework.gateway.entity.IgnoreUrl;

import java.util.List;

public interface IApiRouteDao {
    /**
     * 获取所有路由配置
     *
     * @return
     */
    List<ApiRoute> getApiRoutes();

    /**
     * 加载例外处理URL
     *
     * @return
     */
    List<IgnoreUrl> getIgnoreUrls(IgnoreUrl search);
}
