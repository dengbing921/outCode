package com.adyl.logistics.framework.gateway.feign;

import com.adyl.logistics.framework.api.controller.ResultData;
import com.adyl.logistics.framework.api.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 授权鉴权Feign
 *
 * @author Dengb
 * @date 20180822
 */
@FeignClient(name = "${feign.authService}", fallback = AuthServiceHystrix.class, configuration = FeignConfig.class)
public interface AuthServiceFeign {
    @RequestMapping(value = "/auth/verify", method = RequestMethod.POST)
    ResultData verify(@RequestParam(name = "token") String token);

    @RequestMapping(value = "/auth/verifyPerm", method = RequestMethod.POST)
    ResultData verifyPerm(@RequestParam("apiUrl") String apiUrl, @RequestParam("token") String token);
}
