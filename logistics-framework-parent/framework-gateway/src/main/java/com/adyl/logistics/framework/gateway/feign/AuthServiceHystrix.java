package com.adyl.logistics.framework.gateway.feign;

import com.adyl.logistics.framework.api.controller.ResultData;
import org.springframework.stereotype.Component;

/**
 * 授权服务的降级处理（断路器）
 *
 * @author Dengb
 * @date 20180826
 */
@Component
public class AuthServiceHystrix implements AuthServiceFeign {
    @Override
    public ResultData verify(String token) {
        return null;
    }

    @Override
    public ResultData verifyPerm(String apiUrl, String token) {
        return null;
    }
}
