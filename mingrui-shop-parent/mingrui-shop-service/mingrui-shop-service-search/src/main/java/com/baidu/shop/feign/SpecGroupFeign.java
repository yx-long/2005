package com.baidu.shop.feign;


import com.baidu.shop.service.SpceGroupService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "xxx-server",contextId = "SpceGroupService")
public interface SpecGroupFeign extends SpceGroupService {
}
