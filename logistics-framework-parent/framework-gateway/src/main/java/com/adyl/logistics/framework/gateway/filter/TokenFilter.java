package com.adyl.logistics.framework.gateway.filter;

import com.adyl.logistics.framework.api.controller.ResultData;
import com.adyl.logistics.framework.api.dto.UserDto;
import com.adyl.logistics.framework.api.jwt.JWTConstant;
import com.adyl.logistics.framework.api.jwt.JWTUtils;
import com.adyl.logistics.framework.api.utils.CacheUtils;
import com.adyl.logistics.framework.core.utils.Tools;
import com.adyl.logistics.framework.gateway.feign.AuthServiceFeign;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * 网关Token过滤器
 *
 * @author Dengb
 * @date 20180816
 */
public class TokenFilter extends AbstractFilter {
    @Autowired
    private AuthServiceFeign authServiceFeign;

    /**
     * 表示路由之前进行过滤
     *
     * @return
     */
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        // 加载例外URL
        loadIgnoreList("1");
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        // 从Header里面获取Token
        String token = request.getHeader(JWTConstant.JWT_Auth_Token);
        // 没有Token的情况下，返回1000
        if (Tools.isEmpty(token)) {
            // 如果是没有带Token的请求，自动计算出一个Token
            String requestUri = request.getRequestURI();
            if (containerIgnoreUrl(requestUri)) {
                String accessToken = JWTUtils.generateAccessToken(new HashMap<>());
                context.addZuulRequestHeader(JWTConstant.JWT_Auth_Token, accessToken);
            } else {
                processResult(ResultData.newFailure(JWTConstant.JWT_Verify_Token_Valid, "没有Token信息，检查Auth-Token参数"), context);
            }
        } else {
            // 检查是否被登录
            UserDto userDto = CacheUtils.getUserInfoByToken(token);
            if (userDto == null) {
                processResult(ResultData.newFailure(JWTConstant.JWT_Verify_Token_Valid, "账户在其他地方登录，请重新登录"), context);
            }
            // 验证Token是否正确
            ResultData resultData = authServiceFeign.verify(token);
            processResult(resultData, context);
        }
        return null;
    }
}
