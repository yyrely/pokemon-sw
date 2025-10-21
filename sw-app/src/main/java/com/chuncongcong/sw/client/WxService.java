package com.chuncongcong.sw.client;

import com.chuncongcong.framework.exception.ServiceException;
import com.chuncongcong.sw.client.dto.WxSessionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class WxService {

    @Resource
    private WxFeignClient wxFeignClient;
    @Resource
    private ObjectMapper objectMapper;

    public WxSessionDTO getWxSession(String appId, String appSecret, String code) {
        String wxSessionStr = wxFeignClient.getWxSession(appId, appSecret, code, "authorization_code");
        WxSessionDTO wxSession;
        try {
            wxSession = objectMapper.readValue(wxSessionStr, WxSessionDTO.class);
        } catch (JsonProcessingException e) {
            throw new ServiceException(e);
        }
        Integer errcode = wxSession.getErrcode();
        if (errcode != null && errcode != 0) {
            throw ServiceException.instance(wxSession.getErrcode(), wxSession.getErrmsg());
        }
        return wxSession;
    }
}
