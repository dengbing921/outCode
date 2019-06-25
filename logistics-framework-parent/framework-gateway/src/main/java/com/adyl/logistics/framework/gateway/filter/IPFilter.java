package com.adyl.logistics.framework.gateway.filter;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * IP地址白名单过滤器
 *
 * @author Dengb
 * @date 20180826
 */
public class IPFilter extends AbstractFilter {
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
        RequestContext ctx = RequestContext.getCurrentContext();
        Object success = ctx.get("isSuccess");
        return success == null ? true : Boolean.parseBoolean(success.toString());
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        /*String ip = IpUtils.getIpAddr(ctx.getRequest());
        // 在黑名单中禁用
        if (StringUtils.isNotEmpty(ip) && basicConf != null && basicConf.getIpStr().contains(ip)) {
            ctx.set("isSuccess", false);
            ctx.setSendZuulResponse(false);
            ApiResponse data = ApiResponse.failure(500,"非法请求");
            ctx.setResponseBody(JsonUtils.toJson(data));
            ctx.getResponse().setContentType("application/json; charset=utf-8");
            return null;
        }*/
        return null;
    }
}
