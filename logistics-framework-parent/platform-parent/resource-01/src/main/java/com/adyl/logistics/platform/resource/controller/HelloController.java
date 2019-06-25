package com.adyl.logistics.platform.resource.controller;

import com.adyl.logistics.framework.api.controller.ResultData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/hello")
public class HelloController {

    @RequestMapping(value = "/say")
    public ResultData say() {
        return ResultData.newSuccess("say hello");
    }
}
