package com.adyl.logistics.framework.gateway.service;

import com.adyl.logistics.framework.gateway.entity.ApiRoute;
import com.adyl.logistics.framework.gateway.entity.IgnoreUrl;

import java.util.List;

/**
 * 网关路由接口服务
 *
 * @author Dengb
 * @date 20180906
 */
public interface IApiRouteService {
    /**
     * 加载路由
     *
     * @return
     */
    List<ApiRoute> getApiRoutes();

    /**
     * 加载例外URL
     *
     * @return
     */
    List<IgnoreUrl> getIgnoreUrls(IgnoreUrl search);
}
