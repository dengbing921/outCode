package com.adyl.logistics;

import com.adyl.logistics.framework.api.controller.ResultData;
import com.adyl.logistics.framework.gateway.filter.ApiFilter;
import com.adyl.logistics.framework.gateway.filter.CustomServletDetectionFilter;
import com.adyl.logistics.framework.gateway.filter.ErrorFilter;
import com.adyl.logistics.framework.gateway.filter.TokenFilter;
import com.adyl.logistics.framework.gateway.service.RefreshRouteService;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.Member;
import com.ecwid.consul.v1.agent.model.Service;
import com.ecwid.consul.v1.health.model.Check;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.web.ZuulHandlerMapping;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 网关服务启动应用程序
 *
 * @author Dengb
 * @date 20180827
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableZuulProxy
@MapperScan(basePackages = "com.adyl.logistics.framework.gateway.dao")
@RestController
public class GatewayApplication {
    @Autowired
    private RefreshRouteService refreshRouteService;
    @Autowired
    private ZuulHandlerMapping zuulHandlerMapping;
    @Autowired
    private ConsulClient consulClient;

    /**
     * 应用入口
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    /**
     * 第一个处理的过滤器
     *
     * @return
     */
    @Bean
    public CustomServletDetectionFilter customServletDetectionFilter() {
        return new CustomServletDetectionFilter();
    }

    /**
     * 验证筛选器
     *
     * @return
     */
    @Bean
    public TokenFilter tokenFilter() {
        TokenFilter tokenFilter = new TokenFilter();
        return tokenFilter;
    }

    /**
     * API权限过滤器
     *
     * @return
     */
    @Bean
    public ApiFilter apiFilter() {
        ApiFilter apiFilter = new ApiFilter();
        return apiFilter;
    }

    /**
     * 错误处理Filter
     *
     * @return
     */
    @Bean
    public ErrorFilter errorFilter() {
        return new ErrorFilter();
    }

    /**
     * 刷新路由
     *
     * @return
     */
    @RequestMapping(value = "/refreshRoute")
    public ResultData refresh() {
        refreshRouteService.refreshRoute();
        return ResultData.newSuccess();
    }

    /**
     * @return
     */
    @RequestMapping("/watchRoute")
    public ResultData watchNowRoute() {
        //可以用debug模式看里面具体是什么
        Map<String, Object> handlerMap = zuulHandlerMapping.getHandlerMap();
        return ResultData.newSuccess(handlerMap);
    }

    /**
     * 清理Consul失效服务
     *
     * @return
     */
    @RequestMapping(value = "/clearFailureServices")
    public ResultData clearFailureServices() {
        // 获取所有的members的信息
        List<Member> members = consulClient.getAgentMembers().getValue();
        for (int i = 0; i < members.size(); i++) {
            // 获取每个member的IP地址
            String address = members.get(i).getAddress();
            // 将IP地址传给ConsulClient的构造方法，获取对象
            ConsulClient clearClient = new ConsulClient(address);
            // 根据clearClient，获取当前IP下所有的服务 使用迭代方式 获取map对象的值
            Iterator<Map.Entry<String, Service>> it = clearClient.getAgentServices().getValue().entrySet().iterator();
            while (it.hasNext()) {
                // 迭代数据
                Map.Entry<String, Service> serviceMap = it.next();
                // 获得Service对象
                Service service = serviceMap.getValue();
                // 获取服务名称
                String serviceName = service.getService();
                // 获取服务ID
                String serviceId = service.getId();
                // 根据服务名称获取服务的健康检查信息
                Response<List<Check>> checkList = consulClient.getHealthChecksForService(serviceName, null);
                List<Check> checks = checkList.getValue();
                //获取健康状态值  PASSING：正常  WARNING  CRITICAL  UNKNOWN：不正常
                for (Check check : checks) {
                    Check.CheckStatus checkStatus = check.getStatus();
                    if (check.getServiceId().equals(serviceId) && checkStatus != Check.CheckStatus.PASSING) {
                        // 调用不了，PUT请求被封装成GET请求了
                        //clearClient.agentServiceDeregister(serviceId);
                        deleteInvalidService(address, serviceId);
                    }
                }
            }
        }
        return ResultData.newSuccess();
    }

    /**
     * 删除服务
     *
     * @param ip
     * @param serviceId
     * @return
     */
    private boolean deleteInvalidService(String ip, String serviceId) {
        String url = "http://" + ip + ":8500" + "/v1/agent/service/deregister/{serviceId}";
        Map<String, String> params = new HashMap<>();
        params.put("serviceId", serviceId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity httpEntity = new HttpEntity(null, headers);
        ResponseEntity<String> responseEntity = new RestTemplate().exchange(url, HttpMethod.PUT, httpEntity, String.class, params);
        int statusCode = responseEntity.getStatusCodeValue();
        if (200 == statusCode) {
            return true;
        } else {
            return false;
        }
    }
}
