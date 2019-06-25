package com.adyl.logistics.framework.gateway.filter;

import com.adyl.logistics.framework.api.controller.ResultData;
import com.adyl.logistics.framework.core.utils.JsonUtils;
import com.adyl.logistics.framework.gateway.entity.IgnoreUrl;
import com.adyl.logistics.framework.gateway.service.IApiRouteService;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 抽象的ZuulFilter基类
 *
 * @author Dengb
 * @date 20180906
 */
public abstract class AbstractFilter extends ZuulFilter {
    @Autowired
    protected IApiRouteService routeService;
    protected boolean isLoadIgnore = false;
    protected List<IgnoreUrl> ignoreUrlList;

    /**
     * 加载例外URL
     *
     * @param ignoreType
     */
    public void loadIgnoreList(String ignoreType) {
        if (isLoadIgnore) {
            return;
        }
        IgnoreUrl search = new IgnoreUrl();
        search.setIgnoreType(ignoreType);
        ignoreUrlList = routeService.getIgnoreUrls(search);
        isLoadIgnore = true;
    }

    /**
     * 处理结果
     *
     * @param resultData
     * @param context
     */
    protected void processResult(ResultData resultData, RequestContext context) {
        if (!ResultData.isSuccess(resultData)) {
            context.setSendZuulResponse(false);
            //context.setResponseStatusCode(200);
            context.setResponseBody(JsonUtils.toJson(resultData));
            context.getResponse().setContentType("application/json;charset=utf-8");
            // 把错误信息置空，这样响应filter才会处理
            context.set("throwable", null);
        }
    }

    /**
     * 是否包含排除的URL
     *
     * @param url
     * @return true：有，false：无
     */
    protected boolean containerIgnoreUrl(String url) {
        if (ignoreUrlList != null && ignoreUrlList.size() > 0) {
            for (IgnoreUrl ignoreUrl : ignoreUrlList) {
                if (ignoreUrl.getIgnoreUrl().equalsIgnoreCase(url)) {
                    return true;
                }
            }
        }
        return false;
    }
}
