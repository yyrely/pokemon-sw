package com.chuncongcong.sw.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "wxApi", url = "https://api.weixin.qq.com")
public interface WxFeignClient {

    @GetMapping("/sns/jscode2session")
    String getWxSession(
            @RequestParam("appid") String appId,
            @RequestParam("secret") String appSecret,
            @RequestParam("js_code") String code,
            @RequestParam("grant_type") String grantType);
}