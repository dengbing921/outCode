package com.adyl.logistics.framework.gateway.filter;

import com.adyl.logistics.framework.api.controller.ResultData;
import com.adyl.logistics.framework.api.jwt.JWTConstant;
import com.adyl.logistics.framework.api.utils.RequestUtils;
import com.adyl.logistics.framework.gateway.feign.AuthServiceFeign;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

import javax.servlet.http.HttpServletRequest;

/**
 * API权限过滤器
 *
 * @author Dengb
 * @date 20180906
 */
public class ApiFilter extends AbstractFilter {
    @Autowired
    private AuthServiceFeign authServiceFeign;
    @Autowired
    private ZuulProperties zuulProperties;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        return context.sendZuulResponse();
    }

    @Override
    public Object run() throws ZuulException {
        // 加载例外URL
        loadIgnoreList("2");
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String requestUri = request.getRequestURI();
        // 不是排除的URL，不是App请求
        if (containerIgnoreUrl(requestUri) || RequestUtils.isAppRequest(request)) {
            return null;
        }
        String token = request.getHeader(JWTConstant.JWT_Auth_Token);
        // 如果requestUri是api开头的，把api干掉
        if (requestUri.startsWith(zuulProperties.getPrefix())) {
            requestUri = requestUri.substring(zuulProperties.getPrefix().length());
        }
        ResultData resultData = authServiceFeign.verifyPerm(requestUri, token);
        processResult(resultData, context);
        return null;
    }
}
