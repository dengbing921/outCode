package com.adyl.logistics.framework.gateway.config;

import com.adyl.logistics.framework.core.utils.Tools;
import com.adyl.logistics.framework.gateway.entity.ApiRoute;
import com.adyl.logistics.framework.gateway.service.IApiRouteService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.util.RequestUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义路由配置
 *
 * @author Dengb
 * @date 20180906
 */
public class CustomRouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator {
    private ZuulProperties properties;
    @Autowired
    private IApiRouteService routeService;

    public CustomRouteLocator(String servletPath, ZuulProperties properties) {
        super(servletPath, properties);
        this.properties = properties;
    }

    @Override
    public void refresh() {
        doRefresh();
    }

    @Override
    protected Map<String, ZuulProperties.ZuulRoute> locateRoutes() {
        LinkedHashMap<String, ZuulProperties.ZuulRoute> routesMap = new LinkedHashMap<>();
        //从application.properties中加载路由信息
        routesMap.putAll(super.locateRoutes());
        //从db中加载路由信息
        routesMap.putAll(locateRoutesFromDB());
        //优化一下配置
        LinkedHashMap<String, ZuulProperties.ZuulRoute> values = new LinkedHashMap<>();
        for (Map.Entry<String, ZuulProperties.ZuulRoute> entry : routesMap.entrySet()) {
            String path = entry.getKey();
            // Prepend with slash if not already present.
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (Tools.isNotEmpty(this.properties.getPrefix())) {
                path = this.properties.getPrefix() + path;
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
            }
            values.put(path, entry.getValue());
        }
        return values;
    }

    /**
     * 从数据库加载路由配置
     *
     * @return
     */
    private Map<String, ZuulProperties.ZuulRoute> locateRoutesFromDB() {
        Map<String, ZuulProperties.ZuulRoute> routes = new LinkedHashMap<>();
        List<ApiRoute> listRoutes = routeService.getApiRoutes();
        // 加载路由
        for (ApiRoute result : listRoutes) {
            if (Tools.isEmpty(result.getPath())) {
                continue;
            }
            if (Tools.isEmpty(result.getServiceId()) && Tools.isEmpty(result.getUrl())) {
                continue;
            }
            ZuulProperties.ZuulRoute zuulRoute = new ZuulProperties.ZuulRoute();
            try {
                BeanUtils.copyProperties(result, zuulRoute);
            } catch (Exception e) {

            }
            routes.put(zuulRoute.getPath(), zuulRoute);
        }
        return routes;
    }

    @Override
    protected Route getSimpleMatchingRoute(final String path) {
        // 单独处理文件上传 // path.startsWith("/api/file/upload")
        if (RequestUtils.isZuulServletRequest()) {
            ZuulProperties.ZuulRoute route = this.getZuulRoute(path);
            return this.getRoute(route, path);
        } else {
            return super.getSimpleMatchingRoute(path);
        }
    }
}
