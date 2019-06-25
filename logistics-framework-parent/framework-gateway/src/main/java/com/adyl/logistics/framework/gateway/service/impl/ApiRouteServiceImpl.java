package com.adyl.logistics.framework.gateway.service.impl;

import com.adyl.logistics.framework.gateway.dao.IApiRouteDao;
import com.adyl.logistics.framework.gateway.entity.ApiRoute;
import com.adyl.logistics.framework.gateway.entity.IgnoreUrl;
import com.adyl.logistics.framework.gateway.service.IApiRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 网关服务
 *
 * @author Dengb
 * @date 20180906
 */
@Service
public class ApiRouteServiceImpl implements IApiRouteService {
    @Autowired
    private IApiRouteDao apiRouteDao;

    /**
     * 加载路由信息
     *
     * @return
     */
    @Override
    public List<ApiRoute> getApiRoutes() {
        return apiRouteDao.getApiRoutes();
    }

    /**
     * 加载例外URL
     *
     * @param search
     * @return
     */
    @Override
    public List<IgnoreUrl> getIgnoreUrls(IgnoreUrl search) {
        return apiRouteDao.getIgnoreUrls(search);
    }
}
