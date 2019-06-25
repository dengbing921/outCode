package com.adyl.logistics.framework.gateway.filter;


import com.adyl.logistics.framework.api.controller.ResultData;
import com.adyl.logistics.framework.core.utils.Tools;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;


/**
 * 错误过滤器
 *
 * @author Dengb
 * @date 20180826
 */
public class ErrorFilter extends AbstractFilter {
    private static final String ERROR_STATUS_CODE_KEY = "error.status_code";
    private static final String DEFAULT_ERR_MSG = "系统异常,请稍后再试";

    @Override
    public String filterType() {
        return "error";
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
        RequestContext context = RequestContext.getCurrentContext();
        try {
            if (context.containsKey(ERROR_STATUS_CODE_KEY)) {
                int statusCode = (Integer) context.get(ERROR_STATUS_CODE_KEY);
                String message = (String) context.get("error.message");
                if (context.containsKey("error.exception")) {
                    Throwable e = (Exception) context.get("error.exception");
                    Throwable re = getOriginException(e);
                    if (re instanceof java.net.ConnectException) {
                        message = "Real Service Connection refused";
                    } else if (re instanceof java.net.SocketTimeoutException) {
                        message = "Real Service Timeout";
                    } else if (re instanceof com.netflix.client.ClientException) {
                        message = re.getMessage();
                    } else {
                    }
                }
                // 没有特定的错误信息
                if (Tools.isEmpty(message)) {
                    message = DEFAULT_ERR_MSG;
                }
                //request.setAttribute("javax.servlet.error.status_code", statusCode);
                //request.setAttribute("javax.servlet.error.message", message);
                processResult(ResultData.newFailure(String.valueOf(statusCode), message), context);
            } else {
                ZuulException exception = this.findZuulException(context.getThrowable());
                String errorCode = String.valueOf(exception.nStatusCode);
                ResultData resultData = null;
                if (exception.getCause() != null) {
                    resultData = ResultData.newFailure(errorCode, exception.getCause().getMessage());
                } else {
                    resultData = ResultData.newFailure(errorCode, exception.getMessage());
                }
                processResult(resultData, context);
            }
        } catch (Exception ex) {
            processResult(ResultData.newFailure("500", "DEFAULT_ERR_MSG"), context);
        }
        return null;
    }

    private Throwable getOriginException(Throwable e) {
        e = e.getCause();
        while (e.getCause() != null) {
            e = e.getCause();
        }
        return e;
    }

    private ZuulException findZuulException(Throwable throwable) {
        if (throwable.getCause() instanceof ZuulRuntimeException) {
            return (ZuulException) throwable.getCause().getCause();
        } else if (throwable.getCause() instanceof ZuulException) {
            return (ZuulException) throwable.getCause();
        } else {
            return throwable instanceof ZuulException ? (ZuulException) throwable : new ZuulException(throwable, 500, DEFAULT_ERR_MSG);
        }
    }
}
