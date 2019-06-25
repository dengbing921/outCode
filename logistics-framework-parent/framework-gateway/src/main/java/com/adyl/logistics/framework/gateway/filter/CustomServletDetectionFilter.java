package com.adyl.logistics.framework.gateway.filter;

import com.adyl.logistics.framework.core.utils.Tools;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.HttpServletRequest;

/**
 * 处理文件上传，把请求交给ZuulServlet处理
 *
 * @author Dengb
 * @date 20180907
 */
public class CustomServletDetectionFilter extends ZuulFilter {
    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String contentType = request.getContentType();
        if (Tools.isNotEmpty(contentType) && contentType.toLowerCase().contains("multipart")) {
            ctx.set("isDispatcherServletRequest", false);
        }

        return null;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return -2;
    }
}
